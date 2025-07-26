package com.wiilisten.service.impl;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserSubscription;
import com.wiilisten.service.UserSubscriptionService;

import jakarta.annotation.PostConstruct;

@Service
public class UserSubscriptionServiceImpl extends BaseServiceImpl<UserSubscription, Long> implements UserSubscriptionService{
	
	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getUserSubscriptionRepository();
	}

	@Override
	public UserSubscription findByUserAndActiveTrue(User user) {
		return getDaoFactory().getUserSubscriptionRepository().findByUserAndActiveTrue(user);
	}

}
