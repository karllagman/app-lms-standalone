package com.lms.model;

import java.util.List;

import com.jpa.core.persistence.model.AbstractDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Client extends AbstractDto {
	private String name;
	private String nickname;
	private String address;
	private String email;
	private String fb;
	private String contactNo;
	private List<ContactNumber> contactNos;

}
