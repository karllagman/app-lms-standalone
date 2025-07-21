package com.lms.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lms.model.CodeCounter;
import com.lms.repository.model.CodeCounterEntity;

@Mapper
public interface CodeCounterEntityMapper {

	public CodeCounterEntity toEntity(CodeCounter dto);
	public CodeCounter toDto(CodeCounterEntity entity);
}
