package com.lms.model;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.springframework.util.ObjectUtils;

import com.jpa.core.persistence.model.UniqueCode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeCounter implements UniqueCode<Integer> {

	private String format;
	private int counter;
	private String code;

	@Override
	public Integer increment() {
		return ++counter;
	}

	@Override
	public String formatCode() {
		NumberFormat formatter = new DecimalFormat(format);
		return formatter.format(counter);
	}

//	@Override
//	public boolean isFormatted(String code) {
//		if (!ObjectUtils.isEmpty(code)) {
//			String replaced = code.replace(this.code, "");
//			return code.startsWith(this.code) && replaced.length() == format.length() && parseable(replaced);
//		}
//		return false;
//	}
//	
//	private boolean parseable(String value)  {
//		NumberFormat formatter = new DecimalFormat(format);
//		try {
//			return formatter.parse(value) != null;
//		} catch (ParseException e) {
//			return false;
//		}
//	}

}
