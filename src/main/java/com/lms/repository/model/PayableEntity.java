package com.lms.repository.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.lms.model.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payables")
public class PayableEntity {
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "loan_id")
	private LoanEntity loan;
	
	@Column(name = "principal")
	private Double principal;
	
	@Column(name = "interest")
	private Double interest;
	
	@Column(name = "principal_pd")
	private Double principalPaid;
	
	@Column(name = "interest_pd")
	private Double interestPaid;
	
	@Column(name = "date_due")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDue;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;

}
