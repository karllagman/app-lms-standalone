package com.lms.model.date;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateGenerator {

	private final int field, amount;

	public DateGenerator(int field, int amount) {
		super();
		this.field = field;
		this.amount = amount;
	}

	public List<Date> generate(Date startDate, int expected, int skips) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);

		List<Date> result = new ArrayList<>();
		while (result.size() < expected) {
			result.add(cal.getTime());
			cal.add(field, amount * skips);
		}
		return result;
	}
	
	public Date nextDate(Date date, int skips) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(field, amount * skips);
		return cal.getTime();
	}

}
