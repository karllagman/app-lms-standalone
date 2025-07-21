package com.lms.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.model.Status;
import com.lms.repository.model.LoanView;

@Repository
public interface LoanViewRepository extends JpaRepository<LoanView, String>, JpaSpecificationExecutor<LoanView> {

	public long countByStatus(Status status);

	@Query(value = "select * from loans_view where (status = 'TO_PAY' or status 'PARTIAL') and datediff(date_due, current_timestamp()) <= :days", nativeQuery = true)
	public List<LoanView> getLoansAlmostDue(@Param("days") int days);

}
