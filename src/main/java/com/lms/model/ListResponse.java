package com.lms.model;

import java.util.List;

import com.lms.repository.model.LoanView;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ListResponse<T> {
	
	private Integer totalCount;
	private List<T> list;

}
