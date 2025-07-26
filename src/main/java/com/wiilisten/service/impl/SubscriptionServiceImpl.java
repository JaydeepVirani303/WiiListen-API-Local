package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.Subscription;
import com.wiilisten.service.SubscriptionService;

import jakarta.annotation.PostConstruct;

@Service
public class SubscriptionServiceImpl extends BaseServiceImpl<Subscription, Long> implements SubscriptionService {

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getSubscriptionRepository();
	}

	@Override
	public List<Subscription> findByIsDeletedFalseOrderByIdDesc() {
		return getDaoFactory().getSubscriptionRepository().findByIsDeletedFalseOrderByIdDesc();
	}

	@Override
	public Subscription findByIdAndIsDeletedFalse(Long id) {
		return getDaoFactory().getSubscriptionRepository().findByIdAndIsDeletedFalse(id);
	}

	@Override
	public Subscription findByIdAndIsDeletedFalseAndActiveTrue(Long id) {
		return getDaoFactory().getSubscriptionRepository().findByIdAndIsDeletedFalseAndActiveTrue(id);
	}

	@Override
	public List<Subscription> findByIsDeletedFalseAndActiveTrueOrderByIdDesc() {
		return getDaoFactory().getSubscriptionRepository().findByIsDeletedFalseAndActiveTrueOrderByIdDesc();
	}

	@Override
	public List<Subscription> findByDeviceOsAndIsDeletedFalseAndActiveTrueOrderByIdDesc(String deviceType) {
		return getDaoFactory().getSubscriptionRepository()
				.findByDeviceOsAndIsDeletedFalseAndActiveTrueOrderByIdDesc(deviceType);
	}

	@Override
	public List<Subscription> findByDeviceOsAndIsDeletedFalseAndActiveTrueAndCategoryOrderByIdDesc(String deviceType,
			String type) {
				return getDaoFactory().getSubscriptionRepository()
				.findByDeviceOsAndIsDeletedFalseAndActiveTrueAndCategoryOrderByIdDesc(deviceType, type);
	}

}
