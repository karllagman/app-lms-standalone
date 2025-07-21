package com.lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payment {
	private String loanId;
	private double amount;
	private boolean investment;
}
