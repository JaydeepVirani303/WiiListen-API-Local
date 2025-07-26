package com.wiilisten.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.NotificationHistory;
import com.wiilisten.entity.User;
import com.wiilisten.service.NotificationHistoryService;

import jakarta.annotation.PostConstruct;

@Service
public class NotificationHistoryServiceImpl extends BaseServiceImpl<NotificationHistory, Long>
		implements NotificationHistoryService {

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getNotificationHistoryRepository();
	}

	@Override
	public Page<NotificationHistory> findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(User user, Pageable pageable) {
		// TODO Auto-generated method stub
		return getDaoFactory().getNotificationHistoryRepository()
				.findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(user, pageable);
	}

	@Override
	public Page<NotificationHistory> findByRecipientIdAndEventAndActiveTrueOrderByCreatedAtDesc(User user,
			Pageable pageable, String event) {
		return getDaoFactory().getNotificationHistoryRepository()
				.findByRecipientIdAndEventAndActiveTrueOrderByCreatedAtDesc(user, pageable, event);
	}
}
