package com.wiilisten.repo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;

@Repository
public interface ListenerProfileRepository extends BaseRepository<ListenerProfile, Long> {

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

	@Query("SELECT lp FROM ListenerProfile lp JOIN lp.languages l WHERE l.name = :languageName AND lp.active = true AND l.active = true AND lp.isEligibleForPremiumCallSearch = true ORDER BY lp.createdAt DESC")
	List<ListenerProfile> findByLanguageNameForPremiumListener(String languageName);

	List<ListenerProfile> findByActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByIdDesc();

	List<ListenerProfile> findByLocationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByIdDesc(
			String location);

	@Query("SELECT lp FROM ListenerProfile lp JOIN lp.languages l WHERE l.name = :languageName AND lp.active = true AND l.active = true ORDER BY lp.createdAt DESC")
	List<ListenerProfile> findByLanguageNameListener(String languageName);

	List<ListenerProfile> findByEducationAndActiveTrue(String education);

	List<ListenerProfile> findByGenderAndActiveTrue(String gender);

	List<ListenerProfile> findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(LocalDate startDate,
			LocalDate endDate);

	List<ListenerProfile> findByLocationAndActiveTrue(String location);

	Page<ListenerProfile> findByEducationContainingIgnoreCaseAndActiveTrue(String education, Pageable pageable);

	Page<ListenerProfile> findByGenderIgnoreCaseAndActiveTrue(String gender, Pageable pageable);

	Page<ListenerProfile> findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(LocalDate startDate,
			LocalDate endDate, Pageable pageable);

	Page<ListenerProfile> findByLocationContainingIgnoreCaseAndActiveTrue(String location, Pageable pageable);

	Page<ListenerProfile> findByUserNameContainingAndProfileStatusAndActiveTrue(String userName, String status,
			Pageable pageable);

	Page<ListenerProfile> findByUserIdInAndProfileStatusAndActiveTrue(List<Long> ids, String status, Pageable pageable);

	ListenerProfile findByuserNameAndProfileStatusAndActiveTrue(String name, String status);

	@Query("SELECT lp FROM ListenerProfile lp WHERE UPPER(REPLACE(lp.userName, ' ', '')) = UPPER(REPLACE(:userName, ' ', '')) AND lp.profileStatus = :profileStatus AND lp.active = true")
	ListenerProfile findByUserNameIgnoringSpaces(String userName,String profileStatus);

	ListenerProfile findFirstByAppActiveStatusTrueAndActiveTrueOrderByCreatedAtDesc();

	@Query(value = "SELECT * FROM listener_profile " + "WHERE profile_status = 'APPROVED' "
			+ "AND app_active_status = TRUE " + "AND active = TRUE " + "AND current_signup_step = 'STEP_7' "
			+ "ORDER BY RAND() " + "LIMIT 1", nativeQuery = true)
	Optional<ListenerProfile> findRandomListenerProfile();

	@Query("SELECT lp FROM ListenerProfile lp " +
			"WHERE lp.isAdvertisementActive = TRUE " +
			"AND lp.active = TRUE")
	Page<ListenerProfile> findByIsAdvertisementActiveTrueAndActiveTrue(Pageable pageable);

	@Query("SELECT lp FROM ListenerProfile lp " +
			"JOIN CallerProfile cp ON lp.user.id = cp.user.id " +
			"WHERE lp.isAdvertisementActive = TRUE " +
			"AND lp.active = TRUE " +
			"AND cp.searchSubscriptionStatus = 'ACTIVE'")
	Page<ListenerProfile> findActiveAdvertisementListenersWithActiveSubscription(Pageable pageable);
	
	List<ListenerProfile> findTop10ByIsAdvertisementActiveTrueAndActiveTrue();

	@Query("SELECT lp FROM ListenerProfile lp " +
			"JOIN CallerProfile cp ON lp.user.id = cp.user.id " +
			"WHERE lp.isAdvertisementActive = TRUE " +
			"AND lp.active = TRUE " +
			"AND cp.searchSubscriptionStatus = 'ACTIVE'")
	List<ListenerProfile> findTop10ActiveAdvertisementListenersWithActiveSubscription(Pageable pageable);


	@Query("SELECT DISTINCT lp.location FROM ListenerProfile lp WHERE lp.active = true")
	 List<String> findUniqueLocations();

    List<ListenerProfile> findByActiveAndAppActiveStatusAndUserIdNotIn(Boolean true1, Boolean true2,
            List<Long> listenerIds);

	@Query("SELECT lp FROM ListenerProfile lp " +
			"WHERE lp.createdAt >= :startDate " +
			"AND lp.createdAt <= :endDate " +
			"AND lp.totalEarning > :minEarning " +
			"ORDER BY lp.id")
	List<ListenerProfile> findProfilesByCreatedAtBetweenAndMinEarning(
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate,
			@Param("minEarning") Double minEarning
	);

	@Query("SELECT lp FROM ListenerProfile lp " +
			"WHERE lp.isAdvertisementActive = TRUE " +
			"AND lp.active = TRUE")
	List<ListenerProfile> findTop10ActiveAdvertisementListeners(Pageable pageable);

//	@Query("SELECT lp FROM ListenerProfile lp WHERE lp.totalEarning > 0 AND lp.active = true")
//	List<ListenerProfile> findAllWithEarnings();

    @Query("SELECT lp FROM ListenerProfile lp " +
            "WHERE (lp.totalEarning - COALESCE(lp.totalPaidEarning, 0)) > 0 " +
            "AND lp.active = true")
    List<ListenerProfile> findAllWithRemainingEarnings();
}
