package com.lms.repository.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.lms.model.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "loans_view")
public class LoanView {
	
	@Id
	@Column
	private String id;
	
	@Column(name = "code")
	private String code;
	
	@Column(name = "name")
	private String customer;
	
	@Column(name = "nickname")
	private String nickname;
	
	@Column(name = "date_due")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateDue;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "balance")
	private Double balance;

}
