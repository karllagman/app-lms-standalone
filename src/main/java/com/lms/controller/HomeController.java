package com.lms.controller;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.lms.model.Client;
import com.lms.model.DashboardDetails;
import com.lms.model.DashboardFilter;
import com.lms.model.Loan;
import com.lms.model.Note;
import com.lms.model.NoteDto;
import com.lms.model.NoteType;
import com.lms.model.Payable;
import com.lms.model.Status;
import com.lms.model.StatusView;
import com.lms.repository.mapper.NoteMapper;
import com.lms.service.ClientService;
import com.lms.service.DashboardService;
import com.lms.service.LoanService;
import com.lms.service.LoanTermService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final DashboardService dashboardService;
	private final ClientService clientService;
	private final LoanService loanService;
	private final LoanTermService termService;
	private final NoteMapper noteMapper;

	@Value("${application.base-url}")
	private String baseUrl;
	
	@Value("${application.dashboard.default-filter}")
	private String defaultFilter;

	@GetMapping({ "", "/" })
	public String index(Model model, @RequestParam(value = "status", required = false) StatusView status) {
		addDefaultAttributes(model);
		model.addAttribute("dashboard", "active");
		model.addAttribute("defaultFilter", defaultFilter);

		model.addAttribute("details", dashboardService.getDetails(status, defaultFilter));
		model.addAttribute("dashboardActive", "active");
		return "dashboard";
	}

	@GetMapping("/loan")
	public String loan(Model model, @RequestParam(value = "id", required = false) String id) {
		addDefaultAttributes(model);
		model.addAttribute("dashboard", "active");
		Loan loan = loanService.get(id);

		if (ObjectUtils.isEmpty(loan))
			loan = loanService.createLoan();
		model.addAttribute("loan", loan);
		model.addAttribute("notes", addSystemUpdates(loan));

		if (ObjectUtils.isEmpty(loan) || Status.DRAFT.equals(loan.getStatus()))
			return "loan_edit";
		else
			return "loan_view";
	}

	private List<NoteDto> addSystemUpdates(Loan loan) {
		DecimalFormat fmt = new DecimalFormat("#,##0.00");
		List<Note> notes = new ArrayList<>(loan.getNotes());
		notes.add(Note.build(NoteType.SYSTEM).datePosted(loan.getDateCreated()).message("Loan created"
				+ (!ObjectUtils.isEmpty(loan.getContinuedFrom()) ? " as renewal for " + loan.getContinuedFrom() : "")
				+ ".").build());

		Optional.ofNullable(loan.getContinueTo()).filter(to -> !ObjectUtils.isEmpty(to)).ifPresent(to -> {
			loan.getPayables().stream().max(Comparator.comparing(Payable::getDatePaid)).map(p -> p.getDatePaid())
					.map(date -> {
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						cal.add(Calendar.SECOND, 1);
						return cal.getTime();
					}).ifPresent(date -> {
						notes.add(Note.build(NoteType.SYSTEM).datePosted(date).message("Unpaid amount of "
								+ fmt.format(loan.getUnpaidAmount()) + " has been renewed to " + loan.getContinueTo())
								.build());
					});
		});
		return noteMapper.toDto(notes, loan.getClientNickname());
	}

	@GetMapping("/clients")
	public String clients(Model model) {
		addDefaultAttributes(model);
		model.addAttribute("clients", clientService.getList(null));
		model.addAttribute("clientsActive", "active");
		return "clients";
	}

	@GetMapping("/terms")
	public String terms(Model model) {
		addDefaultAttributes(model);
		model.addAttribute("terms", termService.getList(null));
		model.addAttribute("termsActive", "active");
		return "terms";
	}

	private void addDefaultAttributes(Model model) {
		model.addAttribute("baseUrl", baseUrl);
		model.addAttribute("dueCount", loanService.countDueLoans());
	}

}
