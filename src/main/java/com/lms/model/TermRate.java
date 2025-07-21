package com.lms.model;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TermRate implements Comparable<TermRate> {
	private boolean isDefault;
	private PaymentFrequency frequency;
	private Double interestRate;
	private Integer payableIn;
	private Integer interval;
	private Integer days;

	public Integer getInterestRateInt() {
		return Optional.ofNullable(interestRate).map(i -> i*100).map(Double::intValue).orElse(null);
	}

	@Override
	public int compareTo(TermRate o) {
		return days.compareTo(o.days);
	}
}
