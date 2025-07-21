package com.lms.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Note implements Comparable<Note> {
	@JsonFormat(pattern="MM/dd/yyyy HH:mm:ss")
	private Date datePosted;
	
	private String message;
	
	private String postedBy;
	
	private NoteType type;

	@Override
	public int compareTo(Note o) {
		return datePosted.compareTo(o.datePosted);
	}
	
	public static NoteBuilder build(NoteType type) {
		return Note.builder().type(type);
	}
	
	@JsonIgnore
	public Note formatted(String name) {
		return type != null ? type.format(this, name) : this;
	}
}
