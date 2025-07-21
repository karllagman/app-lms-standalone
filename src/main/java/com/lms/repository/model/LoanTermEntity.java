package com.lms.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.jpa.core.persistence.model.AbstractEntity;
import com.lms.model.InterestType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "loan_terms")
public class LoanTermEntity extends AbstractEntity {
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "from_amt")
	private Double fromAmount;
	
	@Column(name = "to_amt")
	private Double toAmount;
	
	@Column(name = "int_type")
	@Enumerated(EnumType.STRING)
	private InterestType interestType;
}
