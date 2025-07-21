package com.lms.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.lms.model.Payable;
import com.lms.repository.model.PayableEntity;

@Mapper
public abstract class PayableMapper {
	
	public abstract PayableEntity toEntity(Payable payable);
	
	public abstract Payable toDto(PayableEntity entity);
	
	public abstract Payable toDto(@MappingTarget Payable payable, PayableEntity entity);

}
