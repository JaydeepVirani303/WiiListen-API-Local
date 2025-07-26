package com.wiilisten.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.NotificationHistory;
import com.wiilisten.entity.User;

@Repository
public interface NotificationHistoryRepository extends BaseRepository<NotificationHistory, Long> {

	Page<NotificationHistory> findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(User user, Pageable pageable);

	Page<NotificationHistory> findByRecipientIdAndEventAndActiveTrueOrderByCreatedAtDesc(User user, Pageable pageable,
			String event);

}
