package com.wiilisten.repo;

import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserSubscription;

import java.util.List;

@Repository
public interface UserSubscriptionRepository extends BaseRepository<UserSubscription, Long>{
	
	UserSubscription findByUserAndActiveTrue(User user);

    UserSubscription findTopByUserAndTypeAndActiveTrueOrderByIdDesc(User user, String type);

    List<UserSubscription> findByActiveTrueAndExpiryDateBefore(LocalDateTime dateTime);

}
