package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.ListenerAvailability;
import com.wiilisten.entity.User;
import com.wiilisten.service.ListenerAvailabilityService;

import jakarta.annotation.PostConstruct;

@Service
public class ListenerAvailabilityServiceImpl extends BaseServiceImpl<ListenerAvailability, Long> implements ListenerAvailabilityService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getListenerAvailabilityRepository();
	}

	@Override
	public List<ListenerAvailability> findByUserAndActiveTrue(User user) {
		return getDaoFactory().getListenerAvailabilityRepository().findByUserAndActiveTrue(user);
	}

	@Override
	public List<ListenerAvailability> findByUserAndWeekDayAndActiveTrue(User user, String day) {
		return getDaoFactory().getListenerAvailabilityRepository().findByUserAndWeekDayAndActiveTrue(user, day);
	}
}
