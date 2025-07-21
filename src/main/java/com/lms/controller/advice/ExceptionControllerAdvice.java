package com.lms.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.lms.exception.AccountException;
import com.lms.exception.DashboardException;
import com.lms.exception.LoanException;
import com.lms.exception.NewLoanException;
import com.lms.model.ErrorMessage;

@ControllerAdvice
public class ExceptionControllerAdvice {

	@ExceptionHandler({ AccountException.class, LoanException.class,  DashboardException.class })
	public ResponseEntity<ErrorMessage> handleCustomerExceptions(Exception e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(new ErrorMessage(e.getMessage()));
	}

	@ExceptionHandler(NewLoanException.class)
	public ResponseEntity<ErrorMessage> handleCustomerExceptions(NewLoanException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorMessage.builder().message(e.getMessage())
				.result(true).newLoanId(e.getNewLoan().getId()).newLoanCode(e.getNewLoan().getCode()).build());
	}

}
