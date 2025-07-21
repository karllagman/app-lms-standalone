package com.lms.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.jpa.core.service.AbstractService;
import com.lms.exception.AccountException;
import com.lms.model.AccountEntry;
import com.lms.model.ListResponse;
import com.lms.model.Payable;
import com.lms.model.RecentTransaction;
import com.lms.repository.AccountEntryRepository;
import com.lms.repository.TransactionRepository;
import com.lms.repository.mapper.TransactionMapper;
import com.lms.repository.model.AccountEntryEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService extends AbstractService<AccountEntry, AccountEntryEntity> {

	private final TransactionRepository transactionRepo;
	private final TransactionMapper transactionMapper;

	@Value("${application.dashboard.transaction.page-size}")
	private Double transactionSize;

	public void deposit(String loanId, double amount, Date dateEntry, boolean investment) {
		AccountEntry entry = create(loanId, dateEntry, investment);
		entry.setDebit(amount);
		save(entry);
	}

	public void deposit(String loanId, double amount, boolean investment) {
		deposit(loanId, amount, null, investment);
	}

	public void withdraw(String loanId, double amount, boolean investment) {
		if (amount > getBalance())
			throw new AccountException("Insufficient funds.");
		AccountEntry entry = create(loanId, null, investment);
		entry.setCredit(amount);
		save(entry);
	}

	private AccountEntry create(String loanId, Date dateEntry, boolean investment) {
		AccountEntry entry = new AccountEntry();
		entry.setLoanId(loanId);
		entry.setInvestment(investment);
		entry.setDateEntry(Optional.ofNullable(dateEntry).orElse(new Date()));
		return entry;
	}

	public void deposit(String loanId, Payable payable, Date dateEntry, boolean investment) {
		double toDeposit = payable.getAmountPaid();
		if (toDeposit > 0)
			deposit(loanId, toDeposit, dateEntry, investment);
	}

	public double getBalance() {
		return Optional.ofNullable(repository.getIfAvailable()).map(repo -> (AccountEntryRepository) repo)
				.map(repo -> repo.getBalance()).orElse(0d);
	}

	@Override
	public Class<AccountEntry> getDtoClass() {
		return AccountEntry.class;
	}

	public ListResponse<RecentTransaction> getList(Integer page, Integer year) {
		int count = (int) Math.ceil(Math.toIntExact(
				Optional.ofNullable(year).map(y -> transactionRepo.countByYear(year)).orElse(transactionRepo.count()))
				/ transactionSize);
		PageRequest pageRequest = PageRequest.of(page > 0 ? page - 1 : 0, transactionSize.intValue());
		ListResponse<RecentTransaction> response = new ListResponse<>(count > 0 ? count : 1,
				transactionMapper
						.toListDto(Optional.ofNullable(year).map(y -> transactionRepo.findByYear(year, pageRequest))
								.orElse(transactionRepo.findAll(pageRequest)).getContent()));
		return response;
	}
}
