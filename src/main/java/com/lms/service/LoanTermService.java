package com.lms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.jpa.core.service.AbstractService;
import com.jpa.core.specification.SpecificationsBuilder;
import com.lms.exception.LoanException;
import com.lms.model.InterestType;
import com.lms.model.Loan;
import com.lms.model.LoanTerm;
import com.lms.model.Term;
import com.lms.repository.mapper.TermMapper;
import com.lms.repository.model.LoanTermEntity;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoanTermService extends AbstractService<LoanTerm, LoanTermEntity> {

	private TermMapper termMapper;

	public LoanTerm getByRange(double amount, InterestType interestType) {
		if (!ObjectUtils.isEmpty(interestType))
			return Optional.ofNullable(repository.getIfAvailable())
					.map(repo -> repo
							.findOne(SpecificationsBuilder.init(LoanTermEntity.class).with("fromAmount", "<=", amount)
									.with("toAmount", ">=", amount).with("interestType", ":", interestType).build())
							.map(e -> convert(e)).orElse(null))
					.orElse(null);
		return null;

	}

	public Term getByRange(Loan loan) {
		return getByRange(loan, loan.getDateEnd());
	}
	
	public Term getByRange(Loan loan, Date dateEnd) {
		return termMapper.apply(loan.getDateLoaned(), dateEnd, loan.getTerm(),
				getByRange(loan.getLoanAmount(), loan.getTerm().getInterestType()));
	}

	public List<?> getList(String search) {
		Specification<LoanTermEntity> spec = SpecificationsBuilder.init(LoanTermEntity.class).with("name", "~", search)
				.build();
		return Optional.ofNullable(repository.getIfAvailable()).map(repo -> {
			List<LoanTermEntity> list = repo.findAll(spec, Sort.by("name"));
			return list.stream().map(e -> {
				e.setData(null);
				return e;
			}).collect(Collectors.toList());
		}).orElse(new ArrayList<>());
	}

	@Override
	public Class<LoanTerm> getDtoClass() {
		return LoanTerm.class;
	}

	@Override
	public LoanTerm save(LoanTerm dto) {
		dto.setRates(Optional.ofNullable(dto).filter(t -> !ObjectUtils.isEmpty(t.getRates())).map(l -> l.getRates())
				.map(list -> list.stream().sorted().collect(Collectors.toList()))
				.orElseThrow(() -> new LoanException("Rates must not be empty.")));
		if (!Optional.ofNullable(dto).map(LoanTerm::getRates).map(rates -> rates.stream().anyMatch(r -> r.isDefault()))
				.orElse(false))
			Optional.ofNullable(dto).map(LoanTerm::getRates).map(rates -> rates.stream())
					.ifPresent(s -> s.findFirst().ifPresent(r -> r.setDefault(true)));

		return super.save(dto);
	}

}
