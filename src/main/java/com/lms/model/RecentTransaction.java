package com.lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecentTransaction {
	
	private String date;
	private String loanCode;
	private String customer;
	private String amount;
	private String runningBalance;
	private boolean deposit;

}
