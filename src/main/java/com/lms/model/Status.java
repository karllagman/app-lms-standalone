package com.lms.model;

public enum Status {
	DRAFT("Draft"), PENDING("Pending"), TO_PAY("To Pay"), PARTIAL("Partially Paid"), PAID("Paid"), RENEWED("Renewed"), DUE("Due"), CANCELLED("Cancelled");

	private String name;

	private Status(String name) {
		this.name = name;
	}

	public String getDisplay() {
		return name;
	}
}
