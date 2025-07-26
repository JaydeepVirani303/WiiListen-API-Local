package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.User;
import com.wiilisten.service.UserService;

import jakarta.annotation.PostConstruct;

@Service
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getUserRepository();
	}

	@Override
	public User findByEmailAndActiveTrue(String email) {
		return getDaoFactory().getUserRepository().findByEmailAndActiveTrue(email);
	}

	@Override
	public User findByReferralCodeAndActiveTrue(String referralCode) {
		return getDaoFactory().getUserRepository().findByReferralCodeAndActiveTrue(referralCode);
	}

	@Override
	public User findByCountryCodeAndContactNumberAndActiveTrue(String countryCode, String contact) {
		return getDaoFactory().getUserRepository().findByCountryCodeAndContactNumberAndActiveTrue(countryCode, contact);
	}

	@Override
	public User findByEmailAndIsSuspendedTrueAndActiveTrue(String email) {
		return getDaoFactory().getUserRepository().findByEmailAndIsSuspendedTrueAndActiveTrue(email);
	}

	@Override
	public Boolean existsByEmailAndActiveTrue(String email) {
		return getDaoFactory().getUserRepository().existsByEmailAndActiveTrue(email);
	}

	@Override
	public List<User> findByActiveTrueAndIsSuspendedFalseOrderByIdDesc() {
		return getDaoFactory().getUserRepository().findByActiveTrueAndIsSuspendedFalseOrderByIdDesc();
	}

	@Override
	public List<User> findByRoleAndActiveTrueAndIsSuspendedFalseOrderByIdDesc(String role) {
		return getDaoFactory().getUserRepository().findByRoleAndActiveTrueAndIsSuspendedFalseOrderByIdDesc(role);
	}

	@Override
	public User findByIdAndActiveTrueAndIsSuspendedFalse(Long id) {
		return getDaoFactory().getUserRepository().findByIdAndActiveTrueAndIsSuspendedFalse(id);
	}

	@Override
	public Page<User> findByCallNameContainingIgnoreCaseAndActiveTrueAndRole(String name, Pageable pageable,String role) {
		return getDaoFactory().getUserRepository().findByCallNameContainingIgnoreCaseAndActiveTrueAndRole(name, pageable,role);
	}

	@Override
	public Page<User> findByCallNameIgnoreCaseAndActiveTrueAndRole(String name, Pageable pageable, String role) {
		return getDaoFactory().getUserRepository().findByCallNameIgnoreCaseAndActiveTrueAndRole(name, pageable,role);
	}

	
}
