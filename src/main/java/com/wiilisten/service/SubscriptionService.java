package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.Subscription;

public interface SubscriptionService extends BaseService<Subscription, Long> {

	List<Subscription> findByIsDeletedFalseOrderByIdDesc();

	Subscription findByIdAndIsDeletedFalse(Long id);
	
	Subscription findByIdAndIsDeletedFalseAndActiveTrue(Long id);
	
	List<Subscription> findByIsDeletedFalseAndActiveTrueOrderByIdDesc();
	
	List<Subscription> findByDeviceOsAndIsDeletedFalseAndActiveTrueOrderByIdDesc(String deviceType);

    List<Subscription> findByDeviceOsAndIsDeletedFalseAndActiveTrueAndCategoryOrderByIdDesc(String string, String type);

}
