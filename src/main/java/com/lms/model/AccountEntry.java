package com.lms.model;

import java.util.Date;

import com.jpa.core.persistence.model.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountEntry extends AbstractDto {

	private String loanId;
	private Date dateEntry;
	private double debit;
	private double credit;
	private boolean investment;
}
