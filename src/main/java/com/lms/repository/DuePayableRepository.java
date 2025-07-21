package com.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lms.model.Status;
import com.lms.repository.model.DuePayable;
import com.lms.repository.model.LoanView;

@Repository
public interface DuePayableRepository  extends JpaRepository<DuePayable, Integer>, JpaSpecificationExecutor<DuePayable>{

	public long countByStatus(Status status);
	
	
}
