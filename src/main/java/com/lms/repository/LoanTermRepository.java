package com.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jpa.core.persistence.repository.CoreRepository;
import com.lms.repository.model.LoanTermEntity;

@Repository
public interface LoanTermRepository extends CoreRepository<LoanTermEntity> {

	@Query(value="select * from loan_terms where from_amt <= :amount and to_amt >= :amount", nativeQuery = true)
	public Optional<LoanTermEntity> getTermInRange(@Param("amount") Double	 amount);
}
