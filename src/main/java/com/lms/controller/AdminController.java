package com.lms.controller;

import java.text.DecimalFormat;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jpa.core.util.PojoUtils;
import com.lms.model.CodeCounter;
import com.lms.model.Note;
import com.lms.model.NoteType;
import com.lms.repository.CodeCounterRepository;
import com.lms.repository.LoanRepository;
import com.lms.repository.mapper.LoanMapper;
import com.lms.repository.model.LoanEntity;
import com.lms.service.LoanService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class AdminController {

	private CodeCounterRepository repo;
//	private CodeCounterService service;
	private LoanRepository loanRepo;
	private LoanService loanService;
	private LoanMapper loanMapper;

	@PostMapping("/admin/update-id")
	public CodeCounter update(@RequestBody CodeCounter data) {
//				if (data != null) {
//					data.setCounter(0);
//					return service.save(repo.findByCode(data.getCode()).map(e -> {
//						CodeCounter found = Optional.ofNullable(e.getData())
//								.map(d -> PojoUtils.toObject(new String(d), CodeCounter.class)).orElse(new CodeCounter());
//						found.setFormat(data.getFormat());
//						return found;
//					}).orElse(data));
//				}

		return null;
	}

	@GetMapping("/admin/update-loans")
	public String update() {
		//		List<Loan> list = loanRepo.findAll().stream().map(e -> loanService.convert(e)).collect(Collectors.toList());
		loanRepo.findAll().stream().map(e -> loanService.convert(e)).forEach(l -> {
			l.getPayables().forEach(p -> {
				double paid = p.getAmountPaid();
				if (paid > 0 && p.getDatePaid() != null) {
					l.addNote(Note.build(NoteType.PAID).datePosted(p.getDatePaid())
							.message(new DecimalFormat("#,##0.00").format(paid)).build());
				}
			});

			LoanEntity e = loanMapper.toEntity(l);
			loanRepo.save(e);
		});
		return "";
	}

}
