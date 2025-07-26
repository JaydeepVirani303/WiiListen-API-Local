package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.Subscription;

@Repository
public interface SubscriptionRepository extends BaseRepository<Subscription, Long>{
	
	List<Subscription> findByIsDeletedFalseOrderByIdDesc();
	
	Subscription findByIdAndIsDeletedFalse(Long id);
	
	Subscription findByIdAndIsDeletedFalseAndActiveTrue(Long id);
	
	List<Subscription> findByIsDeletedFalseAndActiveTrueOrderByIdDesc();
	
	List<Subscription> findByDeviceOsAndIsDeletedFalseAndActiveTrueOrderByIdDesc(String deviceType);

    List<Subscription> findByDeviceOsAndIsDeletedFalseAndActiveTrueAndCategoryOrderByIdDesc(String deviceType,
            String type);

}
