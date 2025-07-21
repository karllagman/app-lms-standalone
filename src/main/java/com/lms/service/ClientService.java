package com.lms.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.jpa.core.service.AbstractService;
import com.jpa.core.specification.SpecificationsBuilder;
import com.lms.model.Client;
import com.lms.repository.model.ClientEntity;

@Service
public class ClientService extends AbstractService<Client, ClientEntity> {

	public List<?> getList(String search) {
		Specification<ClientEntity> spec = SpecificationsBuilder.init(ClientEntity.class).with("name", "~", search)
				.build();
		return Optional.ofNullable(repository.getIfAvailable()).map(repo -> {
			List<ClientEntity> list = repo.findAll(spec, Sort.by("name"));
			return list.stream().map(e -> {
				e.setData(null);
				return e;
			}).collect(Collectors.toList());
		}).orElse(new ArrayList<>());
	}

	@Override
	public Class<Client> getDtoClass() {
		return Client.class;
	}

}
