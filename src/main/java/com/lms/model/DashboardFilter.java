package com.lms.model;

import java.util.Calendar;
import java.util.Optional;

import org.springframework.util.ObjectUtils;

import com.lms.exception.DashboardException;

public enum DashboardFilter {

	THIS_YEAR("This Year"), LAST_YEAR("Last Year"), OVERALL("Overall");

	private String display;

	private DashboardFilter(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}

	public Integer getYear() {
		return getYear(this);
	}

	public static Integer getYear(DashboardFilter filter) {
		Calendar cal = Calendar.getInstance();
		switch (filter) {
		case THIS_YEAR:
			return cal.get(Calendar.YEAR);
		case LAST_YEAR:
			cal.add(Calendar.YEAR, -1);
			return cal.get(Calendar.YEAR);
		case OVERALL:
		default:
			return null;
		}
	}

	public static Integer getYear(Object filter) {
		return Optional.ofNullable(filter).filter(f -> !ObjectUtils.isEmpty(f)).map(f -> {
			Integer year = null;
			if (f instanceof String) {
				DashboardFilter d = DashboardFilter.valueOfName((String) f);
				year = d != null ? d.getYear() : parse((String) f);
			} else if (f instanceof Number)
				year = ((Number) f).intValue();
			return year;
		}).orElse(null);
	}

	private static Integer parse(String f) {
		try {
			return Integer.parseInt(f);
		} catch (Exception e) {
			throw new DashboardException("Specified year is invalid.");
		}
	}

	public static String getDisplay(String filter) {
		return Optional.ofNullable(filter).filter(f -> !ObjectUtils.isEmpty(f)).map(
				f -> Optional.ofNullable(DashboardFilter.valueOfName(f)).map(d -> d.getDisplay()).orElse("Year " + f))
				.orElse("");
	}

	public static DashboardFilter valueOfName(String value) {
		try {
			return DashboardFilter.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

}
