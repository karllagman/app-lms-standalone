package com.lms.model;

import java.util.ArrayList;
import java.util.List;

import com.jpa.core.persistence.model.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanTerm extends AbstractDto {
	
	private String name;
	private double fromAmount;
	private double toAmount;
	private InterestType interestType;
	private List<TermRate> rates = new ArrayList<>();
		

}
