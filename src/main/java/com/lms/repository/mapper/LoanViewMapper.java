package com.lms.repository.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.lms.model.LoanDashboard;
import com.lms.repository.model.LoanView;

@Mapper
public interface LoanViewMapper {
	
	@Mapping(source = "dateDue", target = "dateDue", dateFormat = "MM/dd/yyyy")
	public LoanDashboard toLoanDashboard(LoanView view);
	
	public List<LoanDashboard> toListDashboard(List<LoanView> view);

}
