package com.lms.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ocpsoft.prettytime.PrettyTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.repository.model.SummaryView;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Payable implements Comparable<Payable> {

	private Integer id;
	private Date dateDue;
	@JsonFormat(pattern = "MM/dd/yyyy HH:mm:ss")
	private Date datePaid;
	private double principal;
	private double interest;
	private double principalPaid;
	private double interestPaid;
	private Status status = Status.PENDING;

	public Payable(Date dateDue, double principal, double interest) {
		this.dateDue = dateDue;
		this.principal = principal;
		this.interest = interest;
	}

	@JsonIgnore
	public boolean isTotallyPaid() {
		return (principal <= principalPaid) && (interest <= interestPaid);
	}

	@JsonIgnore
	public double getAmountPaid() {
		return principalPaid + interestPaid;
	}

	@Override
	public int compareTo(Payable o) {
		return dateDue.compareTo(o.dateDue);
	}

	public double payPrincipal(double amount) {
		double newValue = principal > 0 ? principalPaid + amount : amount;
		double overflow = newValue - principal;

		setPrincipalPaid(overflow > 0 ? principal : newValue);
		return overflow < 0 ? 0 : overflow;
	}

	public double payInterest(double amount) {
		double newValue = interest > 0 ? interestPaid + amount : amount;
		double overflow = newValue - interest;

		setInterestPaid(overflow > 0 ? interest : newValue);
		return overflow < 0 ? 0 : overflow;
	}

	public double pay(double amount) {
		return payInterest(payPrincipal(amount));
	}

	public double getBalance() {
		return getTotal() - getAmountPaid();
	}

	public double getTotal() {
		return principal + interest;
	}

	public void updateStatus() {
		if (isTotallyPaid()) {
			setStatus(Status.PAID);
			setDatePaid(Calendar.getInstance().getTime());
		} else if (!Status.DUE.equals(status) && getAmountPaid() > 0 && getTotal() > getAmountPaid())
			setStatus(Status.TO_PAY);
	}

	public static void main(String fg[]) {
		List<SummaryView> list = new ArrayList<SummaryView>();
//		list.add(new SummaryView(2022,100d,101d,102d,103d,104d,105d,null));
//		list.add(new SummaryView(2023,200d,201d,202d,203d,204d,205d,null));
//		list.add(new SummaryView(2023,null,201d,202d,203d,204d,205d,null));


		Map map = list.stream()
				.collect(Collectors.groupingBy(s -> 1,
						Collectors.collectingAndThen(Collectors.reducing(SummaryView::new),
								Optional::get)));
		System.out.println();
	}

}
