package com.wiilisten.service.impl;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.EarningHistory;
import com.wiilisten.entity.User;
import com.wiilisten.service.EarningHistoryService;

import jakarta.annotation.PostConstruct;

@Service
public class EarningHistoryServiceImpl extends BaseServiceImpl<EarningHistory, Long> implements EarningHistoryService {

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getEarningHistoryRepository();
	}

	@Override
	public List<EarningHistory> findByListenerProfileEducation(String education) {
		return getDaoFactory().getEarningHistoryRepository().findByListenerProfileEducation(education);
	}

	@Override
	public List<EarningHistory> findByListenerProfileLocation(String location) {
		return getDaoFactory().getEarningHistoryRepository().findByListenerProfileLocation(location);
	}

	@Override
	public List<EarningHistory> findByListenerProfileGender(String gender) {
		return getDaoFactory().getEarningHistoryRepository().findByListenerProfileGender(gender);
	}

	@Override
	public List<EarningHistory> findByListenerProfileLanguage(String languageName) {
		return getDaoFactory().getEarningHistoryRepository().findByListenerProfileLanguage(languageName);
	}

	@Override
	public List<EarningHistory> findByActiveTrueAndUserOrderByCreatedAtDesc(User user) {
		return getDaoFactory().getEarningHistoryRepository().findByActiveTrueAndUserOrderByCreatedAtDesc(user);
	}

	@Override
	public List<EarningHistory> findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(Date startDate,
			Date endDate, User user) {
		return getDaoFactory().getEarningHistoryRepository()
				.findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(startDate, endDate, user);
	}

	@Override
	public List<EarningHistory> findEarningHistoryByListenerDateOfBirthBetween(LocalDate startDate, LocalDate endDate) {
		return getDaoFactory().getEarningHistoryRepository().findEarningHistoryByListenerDateOfBirthBetween(startDate,
				endDate);
	}

	@Override
	public Integer countDistinctUserBy() {
		return getDaoFactory().getEarningHistoryRepository().countDistinctUserBy();
	}

	@Override
	public List<EarningHistory> findByUserIdInAndCreatedAtBetweenAndActiveTrue(List<Long> userIds, Date startDate,
			Date endDate) {
		return getDaoFactory().getEarningHistoryRepository().findByUserIdInAndCreatedAtBetweenAndActiveTrue(userIds,
				startDate, endDate);
	}

	@Override
	public List<EarningHistory> findByUserIdInAndActiveTrue(List<Long> userIds) {
		return getDaoFactory().getEarningHistoryRepository().findByUserIdInAndActiveTrue(userIds);
	}

}
