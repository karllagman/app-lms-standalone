package com.lms.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.lms.model.DashboardDetails;
import com.lms.model.DashboardFilter;
import com.lms.model.ListResponse;
import com.lms.model.LoanParam;
import com.lms.model.RecentTransaction;
import com.lms.model.StatusView;
import com.lms.repository.SummaryViewRepository;
import com.lms.repository.mapper.SummaryMapper;
import com.lms.repository.model.LoanView;
import com.lms.repository.model.SummaryView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {
	
	private final LoanService loanService;
	private final AccountService accountService;
	private final SummaryViewRepository summaryRepo;
	private final SummaryMapper summaryMapper;
	
	@Value("${application.dashboard.transaction.page-size}")
	private Double transactionSize;
	
	@Value("${application.dashboard.loan.status}")
	private StatusView initialStatus;
	
	@Value("${application.dashboard.default-filter}")
	private String defaultFilter;
	
	
	public SummaryView getSummaryView(Integer year) {
		if (!ObjectUtils.isEmpty(year)) 
			return summaryRepo.findById(year).orElse(new SummaryView());
		return getOverallSummary();
	}
	
	private SummaryView getOverallSummary() {
		Integer KEY = 1;
		Map<Integer, SummaryView> map = summaryRepo.findAll().stream()
				.collect(Collectors.groupingBy(s -> KEY,
						Collectors.collectingAndThen(Collectors.reducing(SummaryView::new),
								Optional::get)));
		return Optional.ofNullable(map.get(KEY)).orElse(new SummaryView());
	}

	public DashboardDetails getDetails(StatusView paramStatus, Object filter) {
		StatusView status = !ObjectUtils.isEmpty(paramStatus) ? paramStatus : initialStatus;
		Integer year = DashboardFilter.getYear(filter);
		return Optional.ofNullable(getSummaryView(year)).map(view -> {
			ListResponse<LoanView> response = loanService.getList(new LoanParam(null, status, 1, null));
			ListResponse<RecentTransaction> transactions = accountService.getList(1, year);
			view.setLoans(response.getList());
			
			DashboardDetails details = summaryMapper.toDto(view);
			details.setInitialStatus(status);
			details.setRecentTransactions(transactions.getList());
			details.setTransactionPageCount(transactions.getTotalCount());
			details.setLoanPageCount(response.getTotalCount());
			details.setFilterDisplay(DashboardFilter.getDisplay(filter.toString()));
			return details;
		}).orElse(new DashboardDetails(status));
	}
	
	public DashboardDetails getSummaryDetails(Object filter) {
		Integer year = DashboardFilter.getYear(filter);
		return Optional.ofNullable(getSummaryView(year)).map(view -> {
			DashboardDetails details = summaryMapper.toDto(view);
			ListResponse<RecentTransaction> transactions = accountService.getList(1, year);
			details.setRecentTransactions(transactions.getList());
			details.setTransactionPageCount(transactions.getTotalCount());
			details.setFilterDisplay(DashboardFilter.getDisplay(filter.toString()));
			return details;
		}).orElse(new DashboardDetails());
	}
	

}
