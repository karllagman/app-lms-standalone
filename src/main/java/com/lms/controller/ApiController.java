package com.lms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.model.Client;
import com.lms.model.DashboardDetails;
import com.lms.model.DashboardFilter;
import com.lms.model.Loan;
import com.lms.model.LoanParam;
import com.lms.model.ListResponse;
import com.lms.model.LoanTerm;
import com.lms.model.NoteData;
import com.lms.model.Payment;
import com.lms.model.RecentTransaction;
import com.lms.model.StatusView;
import com.lms.repository.model.LoanView;
import com.lms.service.AccountService;
import com.lms.service.ClientService;
import com.lms.service.DashboardService;
import com.lms.service.LoanService;
import com.lms.service.LoanTermService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class ApiController {

	private LoanService loanService;
	private LoanTermService termService;
	private ClientService clientService;
	private AccountService accountService;
	private DashboardService dashboardService;

	@GetMapping("/api/loans")
	public List<LoanView> getLoans(@RequestParam(value = "search", required = false) String search,
			@RequestParam(value = "status", required = false) StatusView status) {
		return loanService.getList(search, status);
	}

	@PostMapping("/api/loans")
	public ListResponse<LoanView> getLoans(@RequestBody LoanParam param) {
		return loanService.getList(param);
	}

	@GetMapping("/api/loans/{id}")
	public Loan getLoan(@PathVariable("id") String id) {
		return loanService.get(id);
	}

	@DeleteMapping("/api/loans/{id}")
	public String deleteLoan(@PathVariable("id") String id) {
		loanService.delete(id);
		return id;
	}

	@GetMapping("/api/loans/create")
	public Loan createLoan() {
		return loanService.createLoan();
	}

	@PostMapping("/api/loans/populate")
	public Loan populateLoan(@RequestParam(value = "reload", required = false) Boolean reload, @RequestBody Loan loan) {
		return loanService.populatePayables(loan, reload != null ? reload : false);
	}

	@PutMapping("/api/loans/note")
	public String addLoanNote(@RequestBody NoteData data) {
		loanService.addNote(data);
		return "Success";
	}

	@PutMapping("/api/loans")
	public Loan saveLoan(@RequestBody Loan loan) {
		return loanService.save(loan);
	}

	@PostMapping("/api/loans/submit")
	public Loan submitLoan(@RequestBody Loan loan) {
		return loanService.submit(loan);
	}

	@PostMapping("/api/loans/pay")
	public Loan pay(@RequestBody Payment payment) {
		return loanService.pay(payment);
	}

	@PostMapping("/api/loans/cancel")
	public Loan cancel(@RequestBody Payment payment) {
		return loanService.cancelLoan(payment);
	}

	@GetMapping("/api/loan/terms")
	public List<?> getLoanTerms(@RequestParam(value = "search", required = false) String search) {
		return termService.getList(search);
	}

	@GetMapping("/api/loan/terms/{id}")
	public LoanTerm getLoanTerm(@PathVariable("id") String id) {
		return termService.get(id);
	}

	@PostMapping("/api/loan/terms")
	public LoanTerm save(@RequestBody LoanTerm term) {
		return termService.save(term);
	}

	@DeleteMapping("/api/loan/terms/{id}")
	public String deleteLoanTerm(@PathVariable("id") String id) {
		termService.delete(id);
		return id;
	}

	@GetMapping("/api/clients")
	public List<?> getClients(@RequestParam(value = "search", required = false) String search) {
		return clientService.getList(search);
	}

	@PostMapping("/api/clients")
	public Client save(@RequestBody Client client) {
		return clientService.save(client);
	}

	@GetMapping("/api/clients/{id}")
	public Client getClient(@PathVariable("id") String id) {
		return clientService.get(id);
	}

	@DeleteMapping("/api/clients/{id}")
	public String deleteClient(@PathVariable("id") String id) {
		clientService.delete(id);
		return id;
	}

	@PostMapping("/api/account/deposit")
	public String deposit(@RequestBody Payment payment) {
		accountService.deposit(null, payment.getAmount(), payment.isInvestment());
		return "Success";
	}

	@PostMapping("/api/account/withdraw")
	public String withdraw(@RequestBody Payment payment) {
		accountService.withdraw(null, payment.getAmount(), payment.isInvestment());
		return "Success";
	}

	@GetMapping("/api/account/transactions")
	public ListResponse<RecentTransaction> getTransactions(@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "filter", required = false) Object filter) {
		return accountService.getList(Optional.ofNullable(page).orElse(1), DashboardFilter.getYear(filter));
	}
	
	@GetMapping("/api/dashboard/summary")
	public DashboardDetails getDashboardSummary(@RequestParam(value = "filter", required = false) Object filter) {
		return dashboardService.getSummaryDetails(filter);
	}
}
