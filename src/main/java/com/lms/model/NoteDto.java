package com.lms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteDto {
	private String message;
	private String posted;
	private String postedBy;
}
