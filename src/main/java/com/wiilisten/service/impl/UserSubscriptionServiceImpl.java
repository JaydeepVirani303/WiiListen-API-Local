package com.wiilisten.service.impl;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserSubscription;
import com.wiilisten.service.UserSubscriptionService;

import jakarta.annotation.PostConstruct;

import java.util.List;

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

    @Override
    public UserSubscription findTopByUserAndTypeAndActiveTrueOrderByIdDesc(User user, String type) {
        return getDaoFactory().getUserSubscriptionRepository().findTopByUserAndTypeAndActiveTrueOrderByIdDesc(user, type);
    }

    @Override
    public UserSubscription findTopByUserAndTypeAndActiveTrueAndExpiryDateAfterOrderByIdDesc(User user, String type, LocalDateTime now) {
        return getDaoFactory().getUserSubscriptionRepository().findTopByUserAndTypeAndActiveTrueAndExpiryDateAfterOrderByIdDesc(user, type, now);
    }

    @Override
    public List<UserSubscription> findByActiveTrueAndExpiryDateBefore(LocalDateTime dateTime) {
        return getDaoFactory().getUserSubscriptionRepository().findByActiveTrueAndExpiryDateBefore(dateTime);
    }

    @Override
    public List<UserSubscription> findAllByUserAndActiveTrue(User user) {
        return getDaoFactory().getUserSubscriptionRepository().findAllByUserAndActiveTrue(user);
    }

    @Override
    public List<UserSubscription> findByActiveTrue() {
        return getDaoFactory().getUserSubscriptionRepository().findByActiveTrue();
    }

}
