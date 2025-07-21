package com.lms.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.lms.model.date.DateGenerator;

public enum PaymentFrequency {
	DAILY("Daily", Calendar.DAY_OF_MONTH, 1), WEEKLY("Weekly", Calendar.DAY_OF_MONTH, 7), MONTHLY("Monthly", Calendar.MONTH, 1),
	ANNUAL("Annual", Calendar.YEAR, 1);

	private DateGenerator generator;
	private String display;

	private PaymentFrequency(String display, DateGenerator generator) {
		this.generator = generator;
		this.display = display;
	}

	private PaymentFrequency(String display, int field, int amount) {
		this(display, new DateGenerator(field, amount));
	}

	public List<Date> generate(Date startDate, int expected, int skips) {
		return generator.generate(startDate, expected, skips);
	}
	
	public Date nextDate(Date date, int skips) {
		return generator.nextDate(date, skips);
	}
	
	public String getDisplay() {
		return display;
	}

}
