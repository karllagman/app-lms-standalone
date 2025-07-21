package com.lms.repository.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "summary_transactions")
public class TransactionViewEntity {
	
	@Id
	@Column
	private String id;
	
	@Column(name = "code")
	private String loanCode;
	
	@Column(name = "customer")
	private String customer;
	
	@Column(name = "date_entry")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEntry;
	
	@Column(name = "debit")
	private Double debit;
	
	@Column(name = "credit")
	private Double credit;
	
	@Column(name = "running_balance")
	private Double runningBalance;
	
	@Column(name = "year")
	private Integer year;
	
	@Column(name = "is_investment")
	private Boolean investment;
	
}
