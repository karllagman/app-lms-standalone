package com.lms.model;

import java.util.function.Function;

import org.springframework.util.ObjectUtils;

public enum InterestType {
	STAGGERED("Staggered", loan -> {
		if (!ObjectUtils.isEmpty(loan) && !ObjectUtils.isEmpty(loan.getTerm())
				&& !ObjectUtils.isEmpty(loan.getLoanAmount()) && !ObjectUtils.isEmpty(loan.getTerm().getInterestRate()))
			return loan.getLoanAmount() * loan.getTerm().getInterestRate();
		return 0d;
	}),
	REPEATED("Repeated", loan -> {
		if (!ObjectUtils.isEmpty(loan) && !ObjectUtils.isEmpty(loan.getTerm())
				&& !ObjectUtils.isEmpty(loan.getLoanAmount()) && !ObjectUtils.isEmpty(loan.getTerm().getInterestRate())
				&& !ObjectUtils.isEmpty(loan.getTerm().getPayableIn())) {
			double interest = loan.getLoanAmount() * loan.getTerm().getInterestRate();
			return interest
					* (loan.getPayables().size() > 0 ? loan.getPayables().size() : loan.getTerm().getPayableIn());
		}
		return 0d;
	});

	private Function<Loan, Double> func;
	private String display;

	InterestType(String display, Function<Loan, Double> func) {
		this.func = func;
		this.display = display;
	}

	public double getExpectedInterest(Loan loan) {
		return func.apply(loan);
	}

	public String getDisplay() {
		return display;
	}

}
