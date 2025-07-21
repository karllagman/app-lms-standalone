package com.lms.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.jpa.core.service.UniqueCodeHandler;
import com.lms.model.CodeCounter;
import com.lms.model.Loan;
import com.lms.repository.CodeCounterRepository;
import com.lms.repository.mapper.CodeCounterEntityMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoanCounterHandler implements UniqueCodeHandler<Loan, Integer, CodeCounter> {

	private final CodeCounterEntityMapper mapper;
	private final CodeCounterRepository repository;

	@Value("${application.loan.prefix}")
	private String prefix;

	@Override
	public CodeCounter getUniqueCode() {
		return repository.findByCode(prefix).map(e -> mapper.toDto(e)).orElse(null);
	}

	@Override
	public CodeCounter save(CodeCounter uniqueCode) {
		return Optional.ofNullable(uniqueCode).map(unique -> {
			repository.save(mapper.toEntity(unique));
			return unique;
		}).orElse(uniqueCode);
	}


}
