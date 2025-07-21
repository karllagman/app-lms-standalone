package com.lms.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "code_counters")
public class CodeCounterEntity  {
	
	@Id
	@Column
	private String code;

	@Column
	private String format;
	
	@Column
	private Integer counter;



}
