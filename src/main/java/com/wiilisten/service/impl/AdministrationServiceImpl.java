package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.Administration;
import com.wiilisten.service.AdministrationService;

import jakarta.annotation.PostConstruct;

@Service
public class AdministrationServiceImpl extends BaseServiceImpl<Administration, Long> implements AdministrationService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getAdministrationRepository();
	}

	@Override
	public Administration findByEmailAndActiveTrue(String email) {
		return getDaoFactory().getAdministrationRepository().findByEmailAndActiveTrue(email);
	}

	@Override
	public Administration findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getAdministrationRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public Boolean existsByEmailAndActiveTrue(String email) {
		return getDaoFactory().getAdministrationRepository().existsByEmailAndActiveTrue(email);
	}

	@Override
	public List<Administration> findByRoleAndActiveTrueOrderByIdDesc(String role) {
		return getDaoFactory().getAdministrationRepository().findByRoleAndActiveTrueOrderByIdDesc(role);
	}

	@Override
	public Administration findByContactAndActiveTrueAndCountryCode(String contact, String countryCode) {
		return getDaoFactory().getAdministrationRepository().findByContactAndActiveTrueAndCountryCode(contact, countryCode);
	}

}
