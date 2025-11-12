package com.wiilisten.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;
import com.wiilisten.service.ListenerProfileService;

import jakarta.annotation.PostConstruct;

@Service
public class ListenerProfileServiceImpl extends BaseServiceImpl<ListenerProfile, Long>
		implements ListenerProfileService {

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getListenerProfileRepository();
	}

	@Override
	public ListenerProfile findByUserNameAndActiveTrue(String userName) {
		return getDaoFactory().getListenerProfileRepository().findByUserNameAndActiveTrue(userName);
	}

	@Override
	public ListenerProfile findByUserAndActiveTrue(User user) {
		return getDaoFactory().getListenerProfileRepository().findByUserAndActiveTrue(user);
	}

	@Override
	public ListenerProfile findByIdAndActiveTrue(Long listenerId) {
		return getDaoFactory().getListenerProfileRepository().findByIdAndActiveTrue(listenerId);
	}

	@Override
	public List<ListenerProfile> findByProfileStatusAndUserNameContainingAndActiveTrue(String status, String username) {
		return getDaoFactory().getListenerProfileRepository()
				.findByProfileStatusAndUserNameContainingAndActiveTrue(status, username);
	}

	@Override
	public ListenerProfile findByUser(User user) {
		return getDaoFactory().getListenerProfileRepository().findByUser(user);
	}

	@Override
	public List<ListenerProfile> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getListenerProfileRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public List<ListenerProfile> findByProfileStatusAndActiveTrueOrderByIdDesc(String status) {
		return getDaoFactory().getListenerProfileRepository().findByProfileStatusAndActiveTrueOrderByIdDesc(status);
	}

	@Override
	public Page<ListenerProfile> findByProfileStatusAndActiveTrue(String status, Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository().findByProfileStatusAndActiveTrue(status, pageable);
	}

	@Override
	public List<ListenerProfile> findByLocationAndActiveTrueOrderByCreatedAtDesc(String location) {
		return getDaoFactory().getListenerProfileRepository().findByLocationAndActiveTrueOrderByCreatedAtDesc(location);
	}

	@Override
	public List<ListenerProfile> findByLocationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
			String location) {
		return getDaoFactory().getListenerProfileRepository()
				.findByLocationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(location);
	}

	@Override
	public List<ListenerProfile> findByEducationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
			String education) {
		return getDaoFactory().getListenerProfileRepository()
				.findByEducationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(education);
	}

	@Override
	public List<ListenerProfile> findByGenderAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
			String gender) {
		return getDaoFactory().getListenerProfileRepository()
				.findByGenderAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(gender);
	}

	@Override
	public List<ListenerProfile> findByLanguageNameForPremiumListener(String languageName) {
		return getDaoFactory().getListenerProfileRepository().findByLanguageNameForPremiumListener(languageName);
	}

	@Override
	public List<ListenerProfile> findByDateOfBirthBetweenAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
			LocalDate startDate, LocalDate endDate) {
		return getDaoFactory().getListenerProfileRepository()
				.findByDateOfBirthBetweenAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(startDate, endDate);
	}

	@Override
	public List<ListenerProfile> findByActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByIdDesc() {
		return getDaoFactory().getListenerProfileRepository()
				.findByActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByIdDesc();
	}

	@Override
	public List<ListenerProfile> findByLocationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByIdDesc(
			String location) {
		return getDaoFactory().getListenerProfileRepository()
				.findByLocationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByIdDesc(location);
	}

	@Override
	public List<ListenerProfile> findByLanguageNameListener(String languageName) {
		return getDaoFactory().getListenerProfileRepository().findByLanguageNameListener(languageName);
	}

	@Override
	public List<ListenerProfile> findByEducationAndActiveTrue(String education) {
		return getDaoFactory().getListenerProfileRepository().findByEducationAndActiveTrue(education);
	}

	@Override
	public List<ListenerProfile> findByGenderAndActiveTrue(String gender) {
		return getDaoFactory().getListenerProfileRepository().findByGenderAndActiveTrue(gender);
	}

	@Override
	public List<ListenerProfile> findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(LocalDate startDate,
			LocalDate endDate) {
		return getDaoFactory().getListenerProfileRepository()
				.findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(startDate, endDate);
	}

	@Override
	public List<ListenerProfile> findByLocationAndActiveTrue(String location) {
		return getDaoFactory().getListenerProfileRepository().findByLocationAndActiveTrue(location);
	}

	@Override
	public Page<ListenerProfile> findByUserNameContainingAndProfileStatusAndActiveTrue(String userName, String status,
			Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository()
				.findByUserNameContainingAndProfileStatusAndActiveTrue(userName, status, pageable);
	}

	@Override
	public Page<ListenerProfile> findByUserIdInAndProfileStatusAndActiveTrue(List<Long> ids, String status,
			Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository().findByUserIdInAndProfileStatusAndActiveTrue(ids, status,
				pageable);
	}

	@Override
	public Page<ListenerProfile> findByEducationContainingIgnoreCaseAndActiveTrue(String education, Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository()
				.findByEducationContainingIgnoreCaseAndActiveTrue(education, pageable);
	}

	@Override
	public Page<ListenerProfile> findByGenderIgnoreCaseAndActiveTrue(String gender, Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository().findByGenderIgnoreCaseAndActiveTrue(gender, pageable);
	}

	@Override
	public Page<ListenerProfile> findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(LocalDate startDate,
			LocalDate endDate, Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository()
				.findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(startDate, endDate, pageable);
	}

	@Override
	public Page<ListenerProfile> findByLocationContainingIgnoreCaseAndActiveTrue(String location, Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository().findByLocationContainingIgnoreCaseAndActiveTrue(location,
				pageable);
	}

	@Override
	public ListenerProfile findFirstByAppActiveStatusTrueAndActiveTrueOrderByCreatedAtDesc() {
		return getDaoFactory().getListenerProfileRepository()
				.findFirstByAppActiveStatusTrueAndActiveTrueOrderByCreatedAtDesc();
	}

	@Override
	public Optional<ListenerProfile> findRandomListenerProfile() {
		return getDaoFactory().getListenerProfileRepository().findRandomListenerProfile();
	}

	@Override
	public ListenerProfile findByuserNameAndProfileStatusAndActiveTrue(String name, String status) {
		return getDaoFactory().getListenerProfileRepository().findByuserNameAndProfileStatusAndActiveTrue(name, status);
	}

	@Override
	public ListenerProfile findByUserNameIgnoringSpaces(String userName, String profileStatus) {
		return getDaoFactory().getListenerProfileRepository().findByUserNameIgnoringSpaces(userName, profileStatus);
	}

	@Override
	public Page<ListenerProfile> findByIsAdvertisementActiveTrueAndActiveTrue(Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository().findByIsAdvertisementActiveTrueAndActiveTrue(pageable);
	}

	@Override
	public Page<ListenerProfile> findActiveAdvertisementListenersWithActiveSubscription(Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository().findActiveAdvertisementListenersWithActiveSubscription(pageable);
	}

	@Override
	public List<ListenerProfile> findTop10ByIsAdvertisementActiveTrueAndActiveTrue() {
		return getDaoFactory().getListenerProfileRepository().findTop10ByIsAdvertisementActiveTrueAndActiveTrue();
	}

	@Override
	public List<ListenerProfile> findTop10ActiveAdvertisementListeners(Pageable pageable) {
		return getDaoFactory().getListenerProfileRepository().findTop10ActiveAdvertisementListeners(pageable);
	}

	@Override
	public List<String> findUniqueLocations() {
		return getDaoFactory().getListenerProfileRepository().findUniqueLocations();
	}

	@Override
	public List<ListenerProfile> findAllByActiveAndAppActiveStatusAndUserNotIn(Boolean true1, Boolean true2,
			List<Long> listenerIds) {
		return getDaoFactory().getListenerProfileRepository().findByActiveAndAppActiveStatusAndUserIdNotIn(true1, true2, listenerIds);
	}

	@Override
	public List<ListenerProfile> findProfilesByCreatedAtBetweenAndMinEarning(LocalDateTime startDate, LocalDateTime endDate, Double amount) {
		return getDaoFactory().getListenerProfileRepository().findProfilesByCreatedAtBetweenAndMinEarning(startDate, endDate, amount);
	}

    public List<ListenerProfile> getAllListenersWithEarnings() {
        return getDaoFactory().getListenerProfileRepository().findAllWithRemainingEarnings();
    }

}
