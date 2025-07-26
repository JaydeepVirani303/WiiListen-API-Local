package com.wiilisten.repo;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserSubscription;

@Repository
public interface UserSubscriptionRepository extends BaseRepository<UserSubscription, Long>{
	
	UserSubscription findByUserAndActiveTrue(User user);

}
