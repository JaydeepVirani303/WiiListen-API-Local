package com.wiilisten.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;

public interface ListenerProfileService extends BaseService<ListenerProfile, Long> {

	ListenerProfile findByUserNameAndActiveTrue(String userName);

	ListenerProfile findByUserAndActiveTrue(User user);

	ListenerProfile findByIdAndActiveTrue(Long listenerId);

	List<ListenerProfile> findByProfileStatusAndUserNameContainingAndActiveTrue(String status, String username);

	ListenerProfile findByUser(User user);

	List<ListenerProfile> findByActiveTrueOrderByIdDesc();

	List<ListenerProfile> findByProfileStatusAndActiveTrueOrderByIdDesc(String status);

	Page<ListenerProfile> findByProfileStatusAndActiveTrue(String status, Pageable pageable);

	List<ListenerProfile> findByLocationAndActiveTrueOrderByCreatedAtDesc(String location);

	List<ListenerProfile> findByLocationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
			String location);

	List<ListenerProfile> findByEducationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
			String education);

	List<ListenerProfile> findByGenderAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
			String gender);

	List<ListenerProfile> findByDateOfBirthBetweenAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
			LocalDate startDate, LocalDate endDate);

	List<ListenerProfile> findByLanguageNameForPremiumListener(String languageName);

	List<ListenerProfile> findByActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByIdDesc();

	List<ListenerProfile> findByLocationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByIdDesc(
			String location);

	List<ListenerProfile> findByLanguageNameListener(String languageName);

	List<ListenerProfile> findByEducationAndActiveTrue(String education);

	List<ListenerProfile> findByGenderAndActiveTrue(String gender);

	List<ListenerProfile> findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(LocalDate startDate,
			LocalDate endDate);

	List<ListenerProfile> findByLocationAndActiveTrue(String location);

	Page<ListenerProfile> findByUserNameContainingAndProfileStatusAndActiveTrue(String userName, String status,
			Pageable pageable);

	Page<ListenerProfile> findByUserIdInAndProfileStatusAndActiveTrue(List<Long> ids, String status, Pageable pageable);
	
	ListenerProfile findByuserNameAndProfileStatusAndActiveTrue(String name, String status);
	
	ListenerProfile findByUserNameIgnoringSpaces(String userName,String profileStatus);

	Page<ListenerProfile> findByEducationContainingIgnoreCaseAndActiveTrue(String education, Pageable pageable);

	Page<ListenerProfile> findByGenderIgnoreCaseAndActiveTrue(String gender, Pageable pageable);

	Page<ListenerProfile> findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(LocalDate startDate,
			LocalDate endDate, Pageable pageable);

	Page<ListenerProfile> findByLocationContainingIgnoreCaseAndActiveTrue(String location, Pageable pageable);

	ListenerProfile findFirstByAppActiveStatusTrueAndActiveTrueOrderByCreatedAtDesc();

	Optional<ListenerProfile> findRandomListenerProfile();
	
	Page<ListenerProfile> findByIsAdvertisementActiveTrueAndActiveTrue(Pageable pageable);
	
	List<ListenerProfile> findTop10ByIsAdvertisementActiveTrueAndActiveTrue();
	
	List<String> findUniqueLocations();

    List<ListenerProfile> findAllByActiveAndAppActiveStatusAndUserNotIn(Boolean true1, Boolean true2,
            List<Long> listenerIds);

	public List<ListenerProfile> findActiveProfilesByCreatedAtBetweenAndMinEarning(LocalDateTime startDate, LocalDateTime endDate, Double amount);
}
