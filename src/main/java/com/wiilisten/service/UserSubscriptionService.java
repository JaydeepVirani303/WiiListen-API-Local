package com.wiilisten.service;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserSubscription;

public interface UserSubscriptionService extends BaseService<UserSubscription, Long>{
	
	UserSubscription findByUserAndActiveTrue(User user);

}
