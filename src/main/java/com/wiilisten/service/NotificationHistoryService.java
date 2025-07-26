package com.wiilisten.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.NotificationHistory;
import com.wiilisten.entity.User;

public interface NotificationHistoryService extends BaseService<NotificationHistory, Long> {

	Page<NotificationHistory> findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(User user, Pageable pageable);

	Page<NotificationHistory> findByRecipientIdAndEventAndActiveTrueOrderByCreatedAtDesc(User user, Pageable pageable,
			String event);

}
