package com.lms.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.repository.model.CodeCounterEntity;

@Repository
public interface CodeCounterRepository extends JpaRepository<CodeCounterEntity, String> {
	
	public Optional<CodeCounterEntity> findByCode(String code);

}
