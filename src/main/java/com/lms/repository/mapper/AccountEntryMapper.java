package com.lms.repository.mapper;

import org.mapstruct.Mapper;

import com.jpa.core.persistence.mapper.EntityMapper;
import com.lms.model.AccountEntry;
import com.lms.repository.model.AccountEntryEntity;

@Mapper
public interface AccountEntryMapper extends EntityMapper<AccountEntry, AccountEntryEntity> {

}
