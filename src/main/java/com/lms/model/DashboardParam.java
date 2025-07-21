package com.lms.model;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardParam {
	private DashboardFilter filter;
	private Integer filterYear;
	
	public Integer getYear() {
		return Optional.ofNullable(filterYear).orElse(DashboardFilter.getYear(filter));
	}
}
