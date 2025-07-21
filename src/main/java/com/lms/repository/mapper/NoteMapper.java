package com.lms.repository.mapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.jpa.core.util.DateUtils;
import com.lms.model.Note;
import com.lms.model.NoteDto;

@Mapper
public abstract class NoteMapper {

	public abstract NoteDto toDto(Note note);

	@AfterMapping
	public void updatePosted(Note note, @MappingTarget NoteDto dto) {
		dto.setPosted(DateUtils.prettify(note.getDatePosted()));
	}

	public List<NoteDto> toDto(List<Note> notes, String nickname) {
		return Optional.ofNullable(notes).map(
				n -> n.stream().sorted(Comparator.reverseOrder()).map(note -> toDto(note.formatted(nickname))).collect(Collectors.toList()))
				.orElse(new ArrayList<>());

	}
	
}
