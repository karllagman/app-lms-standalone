package com.lms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.lms.model.Status;
import com.lms.repository.model.LoanView;
import com.lms.repository.model.SummaryView;

@Repository
public interface SummaryViewRepository  extends JpaRepository<SummaryView, Integer>, JpaSpecificationExecutor<SummaryView>{

	
}
