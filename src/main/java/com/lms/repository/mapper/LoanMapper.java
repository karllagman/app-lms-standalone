package com.lms.repository.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import com.jpa.core.persistence.mapper.EntityMapper;
import com.jpa.core.util.PojoUtils;
import com.lms.model.Loan;
import com.lms.model.Payable;
import com.lms.repository.model.LoanEntity;
import com.lms.repository.model.PayableEntity;

@Mapper(uses = { ClientMapper.class })
public abstract class LoanMapper implements EntityMapper<Loan, LoanEntity> {

	@Autowired
	PayableMapper payableMapper;

	@Autowired
	ClientMapper clientMapper;

	public abstract LoanEntity toEntity(Loan data);

	@AfterMapping
	void updateData(@MappingTarget LoanEntity entity, Loan loan) {
		loan.setClient(null);
		loan.setCode(null);
		loan.setLoanAmount(null);
		loan.setDateCreated(null);
		loan.setDateStart(null);
		loan.setDateDue(null);
		loan.setStatus(null);
		if (entity.getPayableEntities() == null)
			entity.setPayableEntities(new ArrayList<>());
		entity.getPayableEntities().clear();
		if (!ObjectUtils.isEmpty(loan.getPayables())) {
			loan.getPayables().forEach(p -> {
				PayableEntity pe = payableMapper.toEntity(p);
				pe.setLoan(entity);

				entity.getPayableEntities().add(pe);

				p.setPrincipal(0);
				p.setInterest(0);
				p.setPrincipalPaid(0);
				p.setInterestPaid(0);
				p.setDateDue(null);
				p.setStatus(null);
			});
		}
		entity.setData(PojoUtils.toJsonBytes(loan));
	}

	public Loan toDto(Loan loan, LoanEntity source) {
		if (source == null) {
			return null;
		}

		loan.setId(source.getId());
		loan.setCode(source.getCode());
		loan.setClient(clientMapper.toDto(source.getClient()));
		loan.setLoanAmount(source.getLoanAmount());
		loan.setDateDue(source.getDateDue());
		loan.setDateCreated(source.getDateCreated());
		loan.setDateStart(source.getDateStart());
		loan.setStatus(source.getStatus());

		List<Payable> targetTemp = new ArrayList<>(loan.getPayables());
		loan.setPayables(new ArrayList<>());
		if (!ObjectUtils.isEmpty(source.getPayableEntities())) {
			source.getPayableEntities().forEach(p -> {
				loan.getPayables().add(Optional.ofNullable(findById(targetTemp, p.getId()))
						.map(pd -> payableMapper.toDto(pd, p)).orElse(payableMapper.toDto(p)));
			});
		}

		Optional.ofNullable(loan.getPayables())
				.ifPresent(list -> loan.setPayables(list.stream().sorted().collect(Collectors.toList())));

		return loan;
	}

	private Payable findById(List<Payable> source, Integer id) {
		return Optional.ofNullable(source).filter(l -> !ObjectUtils.isEmpty(l))
				.map(l -> l.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null)).orElse(null);
	}

	private PayableEntity findEntityById(List<PayableEntity> source, Integer id) {
		return Optional.ofNullable(source).filter(l -> !ObjectUtils.isEmpty(l))
				.map(l -> l.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null)).orElse(null);
	}
}
