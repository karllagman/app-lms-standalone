package com.lms.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.jpa.core.util.PojoUtils;
import com.lms.model.Loan;
import com.lms.model.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "due_payables")
public class DuePayable {

	@Id
	private Integer id;

	@Column(name = "loan_id")
	private String loanId;

	@Column
	@Enumerated(EnumType.STRING)
	private Status status;


}
