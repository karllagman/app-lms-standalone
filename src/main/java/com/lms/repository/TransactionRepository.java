package com.lms.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.repository.model.TransactionViewEntity;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionViewEntity, Integer> {
	
	public Page<TransactionViewEntity> findByYear(Integer year, Pageable page);
	public long countByYear(Integer year);

}
