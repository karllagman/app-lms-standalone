package com.lms.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DashboardDetails {
	private String onLoan;
	private String interestGained;
	private String interestIncreased;
	private String balance;
	private String expectedReturn;
	private String lost;
	private String investment;
	private StatusView initialStatus;
	private Integer loanPageCount;
	private Integer transactionPageCount;
	private List<RecentTransaction> recentTransactions;
	private List<LoanDashboard> loans;
	private List<Status> status;
	private String filterDisplay;

	public DashboardDetails(StatusView initialStatus) {
		super();
		this.initialStatus = initialStatus;
	}
}
