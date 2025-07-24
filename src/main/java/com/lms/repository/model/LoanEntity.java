package com.lms.repository.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.jpa.core.persistence.model.CodedEntity;
import com.lms.model.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "loans")
public class LoanEntity extends CodedEntity {
		
//	@Column(name = "client_id")
//	private String clientId;
	
	@ManyToOne
	@JoinColumn(name = "client_id")
	private ClientEntity client;
	
	@Column(name = "loan_amt")
	private Double loanAmount;
	
	@Column(name = "date_due")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDue;
	
	@Column(name = "date_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateCreated;
	
	@Column(name = "date_start")
	@Temporal(TemporalType.DATE)
	private Date dateStart;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@OneToMany(mappedBy = "loan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<PayableEntity> payableEntities;

}
