package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.AdministrativeNotification;
import com.wiilisten.service.AdministrativeNotificationService;

import jakarta.annotation.PostConstruct;

@Service
public class AdministrativeNotificationServiceImpl extends BaseServiceImpl<AdministrativeNotification, Long> implements AdministrativeNotificationService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getAdministrativeNotificationRepository();
	}

	@Override
	public List<AdministrativeNotification> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getAdministrativeNotificationRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public AdministrativeNotification findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getAdministrativeNotificationRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public Page<AdministrativeNotification> findByUserId(Long userId, Pageable pageable) {
		return getDaoFactory().getAdministrativeNotificationRepository().findByUserId(userId, pageable);
	}

	
}
