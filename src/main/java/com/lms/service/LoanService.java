package com.lms.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.jpa.core.service.AbstractCodedService;
import com.jpa.core.specification.SpecificationsBuilder;
import com.lms.exception.LoanException;
import com.lms.model.CodeCounter;
import com.lms.model.ListResponse;
import com.lms.model.Loan;
import com.lms.model.LoanParam;
import com.lms.model.Note;
import com.lms.model.NoteData;
import com.lms.model.NoteType;
import com.lms.model.Payable;
import com.lms.model.Payment;
import com.lms.model.PaymentFrequency;
import com.lms.model.Status;
import com.lms.model.StatusView;
import com.lms.model.Term;
import com.lms.repository.DuePayableRepository;
import com.lms.repository.LoanViewRepository;
import com.lms.repository.PayableRepository;
import com.lms.repository.SummaryViewRepository;
import com.lms.repository.mapper.PayableMapper;
import com.lms.repository.model.DuePayable;
import com.lms.repository.model.LoanEntity;
import com.lms.repository.model.LoanView;
import com.lms.repository.model.SummaryView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoanService extends AbstractCodedService<Loan, LoanEntity, Integer, CodeCounter> {

	private final static DecimalFormat FORMATTER = new DecimalFormat("#,##0.00");

	private final SummaryViewRepository summaryRepo;
	private final LoanViewRepository viewRepo;
	private final PayableRepository payableRepo;
	private final DuePayableRepository dueRepo;
	private final PayableMapper payableMapper;
	private final LoanTermService termService;
	private final AccountService accountService;
	private final ClientService clientService;

	@Value("${application.dashboard.loan.page-size}")
	private Double loanSize;

	@Value("${application.loan.prefix}")
	private String prefix;

	public Loan createLoan() {
		Loan loan = new Loan();
		loan.setCode(getAvailableCode());
		loan.setDateCreated(Calendar.getInstance().getTime());
		loan.setDateLoaned(Calendar.getInstance().getTime());
		return loan;
	}

	public SummaryView getSummary() {
		return summaryRepo.findAll().stream().findFirst().orElse(null);
	}

	public int countDueLoans() {
		return Math.toIntExact(viewRepo
				.count(SpecificationsBuilder.init(LoanView.class).with("status", ":", Status.DUE.name()).build()));
	}

//	private Specification<LoanView> buildSpec(String search, StatusView status) {
//
//		List<String> andList = new ArrayList<>();
//		if (!ObjectUtils.isEmpty(search))
//			andList.add(Arrays.asList("code~" + search, "customer:" + search, "nickname~" + search).stream()
//					.collect(Collectors.joining(" OR ")));
//		if (!ObjectUtils.isEmpty(status) && !ObjectUtils.isEmpty(status.getStatusList()))
//			andList.add(status.getStatusList().stream().map(s -> "status:" + s.toString())
//					.collect(Collectors.joining(" OR ")));
//
//		String q = andList.stream().collect(Collectors.joining(" AND "));
//
//		return SpecificationsBuilder.build(q);
//	}

	private Specification<LoanView> spec(String search, StatusView status) {
		return (root, query, cb) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (!ObjectUtils.isEmpty(search))
				predicates.add(cb.or(cb.like(root.get("code"), "%" + search + "%"),
						cb.like(root.get("customer"), "%" + search + "%"),
						cb.like(root.get("nickname"), "%" + search + "%")));
			if (!ObjectUtils.isEmpty(status) && !ObjectUtils.isEmpty(status.getStatusList()))
				predicates.add(root.get("status")
						.in(status.getStatusList().stream().map(Status::toString).collect(Collectors.toList())));
			return !ObjectUtils.isEmpty(predicates) ? cb.and(predicates.toArray(new Predicate[predicates.size()]))
					: cb.and();
		};
	}

	public List<LoanView> getList(String search, StatusView status) {
		return viewRepo.findAll(spec(search, status));
	}

	public ListResponse<LoanView> getList(LoanParam param) {
		Specification<LoanView> spec = spec(param.getSearch(), param.getStatus());
		int count = (int) Math.ceil(Math.toIntExact(viewRepo.count(spec)) / loanSize);
		ListResponse<LoanView> response = new ListResponse<>(count > 0 ? count : 1, viewRepo.findAll(spec,
				PageRequest.of(param.getPage() > 0 ? param.getPage() - 1 : 0, loanSize.intValue(), param.getSortObj()))
				.getContent());

		return response;
	}

	public List<DuePayable> getDueLoans() {
		return dueRepo.findAll();
	}

	public Loan populatePayables(Loan loan, boolean reloadTerm) {
		if (!ObjectUtils.isEmpty(loan.getLoanAmount()) && !ObjectUtils.isEmpty(loan.getDateStart())
				&& !ObjectUtils.isEmpty(loan.getTerm()) && !loan.getTerm().isEmpty()) {
			if (reloadTerm || !loan.getTerm().isReady())
				loan.setTerm(termService.getByRange(loan));

			if (loan.getTerm().isReady()) {
				loan.getPayables().clear();
				double principal = loan.getLoanAmount() / loan.getTerm().getPayableIn();
				double interest = loan.getExpectedInterest() / loan.getTerm().getPayableIn();
				List<Payable> payables = loan.getTerm().generateDates(loan.getDateStart()).stream()
						.map(d -> new Payable(d, principal, interest)).collect(Collectors.toList());
				if (payables.size() > 1 && !ObjectUtils.isEmpty(loan.getDateEnd()))
					payables.stream().skip(payables.size() - 1).findFirst()
							.ifPresent(p -> p.setDateDue(loan.getDateEnd()));
				loan.setPayables(payables);
			}
		}
		return loan;

	}

	public Loan validate(Loan loan) {
		if (ObjectUtils.isEmpty(loan))
			throw new LoanException("Please submit a loan.");
		if (ObjectUtils.isEmpty(loan.getTerm()) || loan.getTerm().isEmpty())
			throw new LoanException("Term must not be empty.");
		if (ObjectUtils.isEmpty(loan.getPayables()))
			throw new LoanException("Must contain payables.");
		if (loan.getTotalPrincipal() < loan.getLoanAmount())
			throw new LoanException("Invalid principal amount. Missing "
					+ FORMATTER.format(loan.getLoanAmount() - loan.getTotalPrincipal()));
		double interest = loan.getExpectedInterest();
		if (loan.getTotalInterest() < interest)
			throw new LoanException(
					"Invalid interest amount. Missing " + FORMATTER.format(interest - loan.getTotalInterest()));

		Optional.ofNullable(loan).map(l -> l.getPayables())
				.ifPresent(list -> loan.setPayables(list.stream().sorted().collect(Collectors.toList())));
		return loan;
	}

	@Override
	public Loan save(Loan dto) {
		if (ObjectUtils.isEmpty(dto.getStatus()))
			dto.setStatus(Status.DRAFT);
		if (ObjectUtils.isEmpty(dto.getDateCreated()))
			dto.setDateCreated(Calendar.getInstance().getTime());

		return super.save(validate(dto));
	}

	public Loan submit(Loan dto) {

		if (!ObjectUtils.isEmpty(dto.getClient()))
			dto.setClient(clientService.get(dto.getClient().getId()));
		dto = save(dto);

		if (ObjectUtils.isEmpty(dto.getContinuedFrom()))
			accountService.withdraw(dto.getId(), dto.getLoanAmount(), false);

		Payable payable = dto.getPayableByStatus(Status.PENDING).findFirst().orElse(null);
		payable.setStatus(Status.TO_PAY);
		dto.setStatus(Status.TO_PAY);
		dto.setDateDue(payable.getDateDue());

		return super.save(dto);
	}

	public Loan cancelLoan(Payment payment) {
		return Optional.ofNullable(payment).map(pay -> get(pay.getLoanId())).map(loan -> {
			loan.setStatus(Status.CANCELLED);
			loan.getPayableByStatus(Status.PENDING, Status.TO_PAY, Status.DUE)
					.forEach(payable -> payable.setStatus(Status.CANCELLED));
			return super.save(loan);
		}).orElse(null);
	}

	public Loan pay(Payment payment) {
		return Optional.ofNullable(payment).filter(pay -> pay.getAmount() > 0).map(pay -> get(pay.getLoanId()))
				.map(loan -> {
					accountService.deposit(loan.getId(), payment.getAmount(), false);

					loan.addNote(Note.build(NoteType.PAID).datePosted(Calendar.getInstance().getTime())
							.message(new DecimalFormat("#,##0.00").format(payment.getAmount())).build());

					loan.pay(payment.getAmount());

					// check and update status if eligible for lowering interest if principal is
					// paid early
//					if (loan.isPrincipalPaid()) { // TODO
//						Term term = termService.getByRange(loan, Calendar.getInstance().getTime());
//						Optional.ofNullable(term).filter(t -> t.getInterestRate() < loan.getTerm().getInterestRate())
//								.map(t -> t.getInterestType().getExpectedInterest(loan))
//								.filter(expectedInt -> expectedInt <= loan.getTotalInterestPaid()).ifPresent(expectedInt -> {
//									loan.getTerm().setInterestRate(term.getInterestRate());
//									double interest = loan.getTotalInterestPaid() / loan.getTerm().getPayableIn();
//									
//								});
//					}

					loan.updateStatus();
					return super.save(loan);
				}).orElse(null);
	}

//	public Loan pay(Payment payment) {
//		return Optional.ofNullable(payment).map(pay -> get(pay.getLoanId()))
//				.map(loan -> loan.getPayableByStatus(Status.TO_PAY, Status.DUE).findFirst().map(p -> {
//					double overflow = p.pay(payment.getAmount());
//
//					Calendar cal = Calendar.getInstance();
//					p.setStatus(Status.PAID);
//					p.setDatePaid(cal.getTime());
//
//					accountService.deposit(loan.getId(), p, p.getDatePaid());
//
//					List<Payable> pendingPayables = loan.getPayableByStatus(Status.PENDING)
//							.collect(Collectors.toList());
//					Loan newLoan = null;
//					if (!ObjectUtils.isEmpty(pendingPayables)) {
//						loan.setStatus(Status.PARTIAL);
//						pendingPayables.get(0).setStatus(Status.TO_PAY);
//
//						boolean nextToPay = false;
//						int add = 1;
//						for (Payable pending : pendingPayables) {
//							if (nextToPay) {
//								pending.setStatus(Status.TO_PAY);
//								nextToPay = false;
//							}
//							overflow = pending.pay(overflow);
//							if (pending.getAmountPaid() > 0) {
//								cal.add(Calendar.SECOND, add);
//								accountService.deposit(loan.getId(), pending, cal.getTime());
//
//								pending.setStatus(Status.PAID);
//								pending.setDatePaid(cal.getTime());
//
//								if (!pending.isTotallyPaid()) {
//									pending.setPrincipal(pending.getPrincipalPaid());
//									pending.setInterest(pending.getInterestPaid());
//									updatePendingAmounts(loan);
//								}
//
//								nextToPay = true;
//								add++;
//							} else
//								break;
//
//						}
//
//					}
//
//					if (loan.isMarkedPaid()) {
//						loan.setStatus(Status.PAID);
//						loan.setDatePaid(cal.getTime());
//						if (!loan.isTotallyPaid())
//							newLoan = createLoan(loan);
//					}
//
//					loan.setDateDue(loan.getPayableByStatus(Status.TO_PAY, Status.DUE).map(o -> o.getDateDue())
//							.findFirst().orElse(null));
//					Loan saved = saveAll(loan);
//					if (!ObjectUtils.isEmpty(newLoan))
//						throw new NewLoanException(saved, newLoan);
//					return saved;
//				}).orElse(null)).orElse(null);
//	}

	public void updatePendingAmounts(Loan loan) {
		List<Payable> pendingPayables = loan.getPayableByStatus(Status.PENDING).collect(Collectors.toList());

		int validPrincipals = Math.toIntExact(pendingPayables.stream().filter(pen -> pen.getPrincipal() > 0).count());
		int validInterests = Math.toIntExact(pendingPayables.stream().filter(pen -> pen.getInterest() > 0).count());

		double remainingLoanAmount = (loan.getLoanAmount() - loan.getTotalPrincipalPaid());
		double remainingInterest = loan.getExpectedInterest() - loan.getTotalInterestPaid();
		pendingPayables.forEach(pending -> {
			if (validPrincipals <= 0 || pending.getPrincipal() > 0)
				pending.setPrincipal(
						remainingLoanAmount / (validPrincipals > 0 ? validPrincipals : pendingPayables.size()));

			if (validInterests <= 0 || pending.getInterest() > 0)
				pending.setInterest(remainingInterest / (validInterests > 0 ? validInterests : pendingPayables.size()));
		});
	}

	public Loan createLoan(Loan loan) {
		Loan newLoan = createLoan();
		newLoan.setContinuedFrom(loan.getCode());
		newLoan.setClient(loan.getClient());
		newLoan.setTerm(new Term(loan.getTerm()));
		newLoan.setDateStart(PaymentFrequency.WEEKLY.nextDate(Calendar.getInstance().getTime(), 2));
		newLoan.setLoanAmount(loan.getUnpaidAmount());
		return save(populatePayables(newLoan, true));
	}

	public Loan renewLoan(Payment payment) {
		return Optional.ofNullable(payment).map(p -> get(payment.getLoanId())).map(loan -> {
			Loan renewed = createLoan(loan);
			loan.setContinueTo(renewed.getCode());
			loan.setStatus(Status.RENEWED);
			loan.getPayableByStatus(Status.PENDING, Status.TO_PAY, Status.DUE)
					.forEach(payable -> payable.setStatus(Status.RENEWED));
			super.save(loan);
			return renewed;
		}).orElse(null);
	}


	@Override
	public Class<Loan> getDtoClass() {
		return Loan.class;
	}

	@Override
	public void delete(String id) {
		Loan loan = get(id);
		if (Status.DRAFT.equals(loan.getStatus()))
			super.delete(id);
	}

	public void addNote(NoteData data) {
		Optional.ofNullable(data).map(d -> get(data.getId())).ifPresent(l -> {
			l.addNote("Admin", data.getMessage());
			super.save(l);
		});
	}

}
