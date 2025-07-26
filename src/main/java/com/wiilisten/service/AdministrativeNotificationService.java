package com.wiilisten.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.AdministrativeNotification;

public interface AdministrativeNotificationService extends BaseService<AdministrativeNotification, Long> {

	List<AdministrativeNotification> findByActiveTrueOrderByIdDesc();

	AdministrativeNotification findByIdAndActiveTrue(Long id);

	Page<AdministrativeNotification> findByUserId(Long userId, Pageable pageable);

}
