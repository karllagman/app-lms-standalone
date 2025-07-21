package com.lms.repository.mapper;

import org.mapstruct.Mapper;

import com.jpa.core.persistence.mapper.EntityMapper;
import com.lms.model.Client;
import com.lms.repository.model.ClientEntity;

@Mapper
public interface ClientMapper extends EntityMapper<Client, ClientEntity>{

}
