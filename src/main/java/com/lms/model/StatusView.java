package com.lms.model;

import java.util.Arrays;
import java.util.List;

public enum StatusView {
	ALL("All"),
	DUE("Due", Status.DUE),
	ACTIVE("Active", Status.TO_PAY, Status.PARTIAL, Status.DUE),
	PAID("Paid", Status.PAID),
	CANCELLED("Cancelled", Status.CANCELLED),
	DRAFT("Draft", Status.DRAFT);
	
	List<Status> statusList;
	private String display;
	
	StatusView(String display, Status...status) {
		this.display = display;
		statusList = Arrays.asList(status);
	}

	public List<Status> getStatusList() {
		return statusList;
	}

	public String getDisplay() {
		return display;
	}
	
}
