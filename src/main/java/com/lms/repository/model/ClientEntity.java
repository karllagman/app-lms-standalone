package com.lms.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.jpa.core.persistence.model.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clients")
public class ClientEntity extends AbstractEntity {
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "nickname")
	private String nickname;
}
