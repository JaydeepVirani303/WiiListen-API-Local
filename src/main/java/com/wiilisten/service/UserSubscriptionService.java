package com.wiilisten.service;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserSubscription;

import java.time.LocalDateTime;
import java.util.List;

public interface UserSubscriptionService extends BaseService<UserSubscription, Long>{
	
	UserSubscription findByUserAndActiveTrue(User user);

    UserSubscription findTopByUserAndTypeAndActiveTrueOrderByIdDesc(User user, String type);

    List<UserSubscription> findByActiveTrueAndExpiryDateBefore(LocalDateTime dateTime);

    List<UserSubscription> findAllByUserAndActiveTrue(User user);

}
