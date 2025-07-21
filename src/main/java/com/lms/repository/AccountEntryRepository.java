package com.lms.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.jpa.core.persistence.repository.CoreRepository;
import com.lms.repository.model.AccountEntryEntity;

@Repository
public interface AccountEntryRepository extends CoreRepository<AccountEntryEntity> {
	
	@Query(value="select (sum(debit)-sum(credit)) from accnt_entries", nativeQuery = true)
	public Double getBalance();
	
}
