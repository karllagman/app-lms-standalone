package com.lms.repository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.repository.model.PayableEntity;

@Repository
public interface PayableRepository extends JpaRepository<PayableEntity, Integer> {

//	public Optional<PayableEntity> findByLoanIdAndDueDate(String loanId, Date dueDate);
}
