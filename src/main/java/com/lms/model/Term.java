package com.lms.model;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Term {

	private Double interestRate;
	private InterestType interestType;
	private PaymentFrequency frequency;
	private Integer payableIn;
	private Integer interval;

	public Term(Term orig) {
		this(null, orig.interestType, null, null, null);
	}

	@JsonIgnore
	public List<Date> generateDates(Date startDate) {
		return frequency.generate(startDate, payableIn, interval);
	}

	public Integer getInterestRateInt() {
		return Optional.ofNullable(interestRate).map(i -> i * 100).map(Double::intValue).orElse(null);
	}

	@JsonIgnore
	public boolean isEmpty() {
		return ObjectUtils.isEmpty(payableIn) && ObjectUtils.isEmpty(interval) && ObjectUtils.isEmpty(frequency)
				&& ObjectUtils.isEmpty(interestType) && ObjectUtils.isEmpty(interestRate);
	}

	@JsonIgnore
	public boolean isReady() {
		return !ObjectUtils.isEmpty(payableIn) && !ObjectUtils.isEmpty(interval) && !ObjectUtils.isEmpty(frequency)
				&& !ObjectUtils.isEmpty(interestType) && !ObjectUtils.isEmpty(interestRate) && interestRate > 0
				&& payableIn > 0 && interval > 0;
	}

}
