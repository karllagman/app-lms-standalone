package com.lms.exception;

import com.lms.model.Loan;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewLoanException extends RuntimeException {
	
	private Loan loan;
	private Loan newLoan;

}
