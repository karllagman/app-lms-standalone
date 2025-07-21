package com.lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanDashboard {
	
	private String id;
	private String code;
	private String customer;
	private String nickname;
	private String dateDue;
	private String status;
	private Double balance;

}
