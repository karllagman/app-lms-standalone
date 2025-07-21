package com.lms.repository.mapper;

import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.mapstruct.Mapper;
import org.springframework.util.ObjectUtils;

import com.jpa.core.util.DateUtils;
import com.lms.exception.LoanException;
import com.lms.model.LoanTerm;
import com.lms.model.Term;
import com.lms.model.TermRate;

@Mapper
public abstract class TermMapper {

	public Term apply(int days, Term term, LoanTerm loanTerm) {
		return Optional.ofNullable(loanTerm).map(l -> {
			Term newTerm = toTerm(l);
			TermRate rate = l.getRates().stream().filter(r -> r.isDefault()).findFirst().orElse(null);
			if (!ObjectUtils.isEmpty(term)) {
				rate = l.getRates().stream().filter(r -> days > 0 && days <= r.getDays()).findFirst().orElse(rate);
				Optional.ofNullable(rate).ifPresent(r -> {
					newTerm.setInterestRate(r.getInterestRate());
					newTerm.setInterval(r.getInterval());
					newTerm.setFrequency(r.getFrequency());
					newTerm.setPayableIn(r.getPayableIn());
				});
			}
			return newTerm;
		}).orElseThrow(() -> new LoanException(
				"Loan Term not found! Please setup a term that meets this criteria to proceed."));
	}
	
	public Term apply(Date start, Date end, Term term, LoanTerm loanTerm) {
		return apply(DateUtils.countDiff(start, end, TimeUnit.DAYS), term, loanTerm);
	}

	public abstract Term toTerm(LoanTerm loanTerm);

}
