package com.lms.model;

import java.util.function.BiFunction;

public enum NoteType {
	SYSTEM((note, name) -> Note.builder().datePosted(note.getDatePosted()).postedBy("System").message(note.getMessage())
			.build()),
	PAID((note, name) -> Note.builder().datePosted(note.getDatePosted()).postedBy("System")
			.message(name + " paid " + note.getMessage() + ".").build()),
	MESSAGE((note, name) -> Note.builder().datePosted(note.getDatePosted()).postedBy(note.getPostedBy())
			.message(note.getMessage()).build());

	private BiFunction<Note, String, Note> func;

	private NoteType(BiFunction<Note, String, Note> func) {
		this.func = func;
	}

	public Note format(Note note, String name) {
		return func.apply(note, name);
	}
}
