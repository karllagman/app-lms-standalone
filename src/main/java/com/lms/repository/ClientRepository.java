package com.lms.repository;

import org.springframework.stereotype.Repository;

import com.jpa.core.persistence.repository.CoreRepository;
import com.lms.repository.model.ClientEntity;

@Repository
public interface ClientRepository extends CoreRepository<ClientEntity>{

}
