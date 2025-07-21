package com.lms.repository.mapper;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.springframework.util.ObjectUtils;

import com.lms.model.RecentTransaction;
import com.lms.repository.model.TransactionViewEntity;

@Mapper
public interface TransactionMapper {

	@Mappings({ @Mapping(source = "dateEntry", target = "date", dateFormat = "MMM-dd"),
			@Mapping(source = "runningBalance", target = "runningBalance", numberFormat = "₱ #,##0.00") })
	public RecentTransaction toDto(TransactionViewEntity view);

	
	@AfterMapping
	default void updateCode(TransactionViewEntity view, @MappingTarget RecentTransaction transaction) {
		DecimalFormat fmt = new DecimalFormat("#,##0.00");
		if (view.getCredit() > 0) {
			transaction.setAmount("₱ " + fmt.format(-view.getCredit()));
		} else {
			transaction.setAmount("₱ " + fmt.format(view.getDebit()));
			transaction.setDeposit(true);
		}

		if (ObjectUtils.isEmpty(view.getLoanCode())) {
			transaction.setCustomer(transaction.isDeposit() ? "DEPOSIT" : "WITHDRAWAL");
			transaction.setLoanCode(Optional.ofNullable(view.getInvestment()).filter(i -> i).map(i -> "Investment").orElse(""));
		}
	}
	
	public List<RecentTransaction> toListDto(List<TransactionViewEntity> view);

}
