package com.lms.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jpa.core.persistence.model.AbstractDto;
import com.jpa.core.persistence.model.CodedDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Loan extends CodedDto {

	private String continuedFrom;
	private String continueTo;
	private Client client;
	private Double loanAmount;

	@JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
	private Date dateCreated;

	private Date dateLoaned;
	private Date dateStart;
	private Date dateEnd;
	private Date dateDue;
	private Date datePaid;
	private Status status = Status.DRAFT;
	private Term term = new Term();
	private List<Payable> payables = new ArrayList<>();
	private List<Note> notes = new ArrayList<>();

	@JsonIgnore
	public double getTotalPaid() {
		return getTotalInterestPaid() + getTotalPrincipalPaid();
	}

	@JsonIgnore
	public double getTotal() {
		return getTotalInterest() + getTotalPrincipal();
	}

	@JsonIgnore
	public double getTotalInterestPaid() {
		return getTotal(Payable::getInterestPaid);
	}

	@JsonIgnore
	public double getTotalPrincipalPaid() {
		return getTotal(Payable::getPrincipalPaid);
	}

	public double getTotalInterest() {
		return getTotal(Payable::getInterest);
	}

	public double getTotalPrincipal() {
		return getTotal(Payable::getPrincipal);
	}

	public double getAmountToPay() {
		return getPayableByStatus(Status.TO_PAY, Status.DUE).map(Payable::getBalance)
				.collect(Collectors.summingDouble(Double::doubleValue));
	}

	@JsonIgnore
	public String getClientNickname() {
		return Optional.ofNullable(client)
				.map(c -> !ObjectUtils.isEmpty(c.getNickname()) ? c.getNickname() : c.getName()).orElse("");
	}

	@JsonIgnore
	public boolean isTotallyPaid() {
		return getUnpaidAmount() <= 0;
	}

	public double getUnpaidAmount() {
		return payables.stream().map(Payable::getBalance).collect(Collectors.summingDouble(Double::doubleValue));
	}

	@JsonIgnore
	public boolean isMarkedPaid() {
		return !Optional.ofNullable(payables)
				.map(list -> list.stream().anyMatch(p -> !Status.PAID.equals(p.getStatus()))).orElse(false);
	}

	@JsonIgnore
	private double getTotal(Function<Payable, Double> func) {
		return Optional.ofNullable(payables)
				.map(list -> list.stream().map(func).collect(Collectors.summingDouble(Double::doubleValue)))
				.orElse(0.0);
	}

	@JsonIgnore
	public Stream<Payable> getPayableByStatus(Status... status) {
		return payables.stream().filter(p -> Arrays.stream(status).anyMatch(s -> p.getStatus().equals(s)));
	}

	@JsonIgnore
	public double getExpectedInterest() {
		if (!ObjectUtils.isEmpty(term) && !ObjectUtils.isEmpty(term.getInterestType()))
			return term.getInterestType().getExpectedInterest(this);
		return 0;
	}
	
	public boolean isPrincipalPaid() {
		return getTotalPrincipal() <= getTotalPrincipalPaid();
	}

	public void addNote(Note note) {
		notes.add(note);
	}

	public void addNote(String postedBy, String message) {
		addNote(Note.build(NoteType.MESSAGE).message(message).postedBy(postedBy)
				.datePosted(Calendar.getInstance().getTime()).build());
	}

	public void pay(double amount) {
		double overflow = amount;
		for (Payable p : getPayables()) {
			if (overflow <= 0) {
				if (Status.PENDING.equals(p.getStatus()) && getPayableByStatus(Status.TO_PAY).count() <= 0)
					p.setStatus(Status.TO_PAY);
				break;
			}
			if (!Status.PAID.equals(p.getStatus())) {
				overflow = p.pay(overflow);
				p.updateStatus();
			}
		}
	}

	public void updateStatus() {
		if (isMarkedPaid()) {
			setStatus(Status.PAID);
			setDatePaid(Calendar.getInstance().getTime());
		} else if (getPayableByStatus(Status.DUE).count() > 0)
			setStatus(Status.DUE);
		else if (getTotalPaid() > 0 && getTotal() > getTotalPaid())
			setStatus(Status.PARTIAL);

		setDateDue(getPayableByStatus(Status.TO_PAY, Status.DUE).map(o -> o.getDateDue()).findFirst().orElse(null));
	}

}
