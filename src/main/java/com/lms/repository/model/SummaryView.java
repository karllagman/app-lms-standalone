package com.lms.repository.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jpa.core.util.PojoUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "summary_view")
public class SummaryView {
	
	@Id
	@Column
	private Integer year;
	
	@Column(name = "on_loan")
	private Double onLoan;

	@Column(name = "interest_gained")
	private Double interestGained;
	
	@Column(name = "balance")
	private Double balance;
	
	@Column(name = "invested")
	private Double invested;
	
	@Column(name = "expected_interest")
	private Double expectedInterest;
	
	@Column(name = "lost")
	private Double lost;
	
	@Transient
	private List<LoanView> loans;
	
	public SummaryView(SummaryView a, SummaryView b) {
		this.onLoan = PojoUtils.sumDouble(a::getOnLoan, b::getOnLoan);
		this.interestGained = PojoUtils.sumDouble(a::getInterestGained, b::getInterestGained);
		this.balance = a.getBalance();
		this.invested = a.getInvested();
		this.expectedInterest = PojoUtils.sumDouble(a::getExpectedInterest, b::getExpectedInterest);
		this.lost = PojoUtils.sumDouble(a::getLost, b::getLost);
	}
	
	@JsonIgnore
	public double getLostPrimitive() {
		return lost != null ? lost.doubleValue() : 0d;
	}
}
