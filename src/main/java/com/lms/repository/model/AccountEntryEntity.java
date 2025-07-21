package com.lms.repository.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.jpa.core.persistence.model.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "accnt_entries")
public class AccountEntryEntity extends AbstractEntity {

	@Column(name = "loan_id")
	private String loanId;
	
	@Column(name = "date_entry")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateEntry;
	
	@Column(name = "debit")
	private Double debit;
	
	@Column(name = "credit")
	private Double credit;
	
	@Column(name = "is_investment")
	private Boolean investment;
	
}
