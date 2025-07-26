package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.Administration;

public interface AdministrationService extends BaseService<Administration, Long> {

	Administration findByEmailAndActiveTrue(String email);

	Administration findByContactAndActiveTrueAndCountryCode(String contact,String countryCode);
	
	Administration findByIdAndActiveTrue(Long id);
	
	Boolean existsByEmailAndActiveTrue(String email);
	
	List<Administration> findByRoleAndActiveTrueOrderByIdDesc(String role);


}
