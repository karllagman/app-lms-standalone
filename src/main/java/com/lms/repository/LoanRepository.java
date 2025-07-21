package com.lms.repository;

import org.springframework.stereotype.Repository;

import com.jpa.core.persistence.repository.CodedRepository;
import com.lms.repository.model.LoanEntity;

@Repository
public interface LoanRepository extends CodedRepository<LoanEntity>{

}
