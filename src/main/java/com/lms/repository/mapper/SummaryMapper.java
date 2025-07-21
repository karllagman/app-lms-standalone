package com.lms.repository.mapper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.util.ObjectUtils;

import com.lms.model.DashboardDetails;
import com.lms.model.Status;
import com.lms.repository.model.SummaryView;

@Mapper(uses = { TransactionMapper.class, LoanViewMapper.class })
public interface SummaryMapper {

	@Mappings({ @Mapping(source = "onLoan", target = "onLoan", numberFormat = "₱ #,##0.00"),
			@Mapping(source = "interestGained", target = "interestGained", numberFormat = "₱ #,##0.00"),
			@Mapping(source = "balance", target = "balance", numberFormat = "₱ #,##0.00"),
			@Mapping(source = "lost", target = "lost", numberFormat = "₱ #,##0.00"),
			@Mapping(source = "invested", target = "investment", numberFormat = "₱ #,##0.00")})
	public DashboardDetails toDto(SummaryView view);

	@BeforeMapping
	default void update(SummaryView view, @MappingTarget DashboardDetails summary) {
		if (!ObjectUtils.isEmpty(view.getInterestGained()) && !ObjectUtils.isEmpty(view.getInvested()))
			summary.setInterestIncreased(
					NumberFormat.getPercentInstance().format(view.getInterestGained() / view.getInvested()));
		if (!ObjectUtils.isEmpty(view.getExpectedInterest()) && !ObjectUtils.isEmpty(view.getOnLoan())
				&& !ObjectUtils.isEmpty(view.getLostPrimitive()))
			summary.setExpectedReturn(new DecimalFormat("₱ #,##0.00")
					.format(view.getExpectedInterest() + view.getOnLoan() - view.getLostPrimitive()));
	}

}
