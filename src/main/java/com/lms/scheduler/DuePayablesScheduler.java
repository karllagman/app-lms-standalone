package com.lms.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lms.model.Loan;
import com.lms.model.Status;
import com.lms.repository.model.DuePayable;
import com.lms.service.LoanService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class DuePayablesScheduler {

	private LoanService loanService;

	@Scheduled(initialDelay = 1000, fixedDelay = 43200000)
	public void run() {
		runTask();
	}

	@Scheduled(cron = "0 5 0 * * *")
	public void runCron() {
		runTask();
	}

	private void runTask() {
		List<DuePayable> list = loanService.getDueLoans();
		Map<String, List<Integer>> map = new HashMap<>();

		list.forEach(due -> {
			List<Integer> payableIds = map.get(due.getLoanId());
			if (payableIds == null) {
				payableIds = new ArrayList<>();
				map.put(due.getLoanId(), payableIds);
			}
			payableIds.add(due.getId());
		});

		map.forEach((loanId, payableIds) -> Optional.ofNullable(loanService.get(loanId)).ifPresent(loan -> {
			payableIds.stream()
					.map(id -> loan.getPayables().stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null))
					.filter(p -> p != null).forEach(p -> p.setStatus(Status.DUE));
			loan.updateStatus();
			loanService.save(loan);
		}));
	}

}
