package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.User;
import com.wiilisten.service.CallerProfileService;

import jakarta.annotation.PostConstruct;

@Service
public class CallerProfileServiceImpl extends BaseServiceImpl<CallerProfile, Long> implements CallerProfileService {

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getCallerProfileRepository();
	}

	@Override
	public CallerProfile findByUserAndActiveTrue(User user) {
		return getDaoFactory().getCallerProfileRepository().findByUserAndActiveTrue(user);
	}

	@Override
	public CallerProfile findByUser(User user) {
		// TODO Auto-generated method stub
		return getDaoFactory().getCallerProfileRepository().findByUser(user);
	}

	@Override
	public CallerProfile findByIdAndActiveTrue(Long id) {
		// TODO Auto-generated method stub
		return getDaoFactory().getCallerProfileRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public List<CallerProfile> findAllByOrderByIdDesc() {
		// TODO Auto-generated method stub
		return getDaoFactory().getCallerProfileRepository().findAllByOrderByIdDesc();
	}

	@Override
	public List<CallerProfile> findByActiveTrueOrderByIdDesc() {
		// TODO Auto-generated method stub
		return getDaoFactory().getCallerProfileRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public CallerProfile findByUserAndSearchSubscriptionStatusAndActiveTrue(User user, String status) {
		return getDaoFactory().getCallerProfileRepository().findByUserAndSearchSubscriptionStatusAndActiveTrue(user,
				status);
	}

}
