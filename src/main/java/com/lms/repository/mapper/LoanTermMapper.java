package com.lms.repository.mapper;

import org.mapstruct.Mapper;

import com.jpa.core.persistence.mapper.EntityMapper;
import com.lms.model.LoanTerm;
import com.lms.repository.model.LoanTermEntity;

@Mapper
public interface LoanTermMapper extends EntityMapper<LoanTerm, LoanTermEntity>{

}
