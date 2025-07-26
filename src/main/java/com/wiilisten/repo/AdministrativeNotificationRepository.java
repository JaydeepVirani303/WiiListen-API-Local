package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.AdministrativeNotification;

@Repository
public interface AdministrativeNotificationRepository extends BaseRepository<AdministrativeNotification, Long> {

	List<AdministrativeNotification> findByActiveTrueOrderByIdDesc();

	AdministrativeNotification findByIdAndActiveTrue(Long id);

	@Query("SELECT a FROM AdministrativeNotification a JOIN a.users u WHERE u.id = :userId AND a.active = true AND u.active = true ORDER BY a.createdAt DESC")
	Page<AdministrativeNotification> findByUserId(Long userId, Pageable pageable);

}
