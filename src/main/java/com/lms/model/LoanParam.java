package com.lms.model;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.ObjectUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoanParam {
	private String search;
	private StatusView status;
	private Integer page;
	private String sort;
	
	public Sort getSortObj() {
		if (!ObjectUtils.isEmpty(sort)) {
			String[] split = sort.split(":");
			return Sort.by(Direction.fromString(split[1]), split[0]);
		}
		return Sort.unsorted();
	}
}
