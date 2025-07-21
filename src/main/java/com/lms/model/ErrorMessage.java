package com.lms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@JsonInclude(Include.NON_EMPTY)
public class ErrorMessage {
	
	private boolean result;
	private final String message;
	private String newLoanId;
	private String newLoanCode;
}
