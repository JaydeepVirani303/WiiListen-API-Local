package com.wiilisten.repo;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.EarningHistory;
import com.wiilisten.entity.User;

@Repository
public interface EarningHistoryRepository extends BaseRepository<EarningHistory, Long> {

	// List<EarningHistory> findBy
	@Query(value = "SELECT eh.* " + "FROM earning_history eh "
			+ "INNER JOIN listener_profile lp ON eh.user_id = lp.user_id " + "WHERE lp.education = :education "
			+ "ORDER BY eh.created_at DESC", nativeQuery = true)
	List<EarningHistory> findByListenerProfileEducation(String education);

	@Query(value = "SELECT eh.* " + "FROM earning_history eh "
			+ "INNER JOIN listener_profile lp ON eh.user_id = lp.user_id " + "WHERE lp.location = :location "
			+ "ORDER BY eh.created_at DESC", nativeQuery = true)
	List<EarningHistory> findByListenerProfileLocation(String location);

	@Query(value = "SELECT eh.* " + "FROM earning_history eh "
			+ "INNER JOIN listener_profile lp ON eh.user_id = lp.user_id " + "WHERE lp.gender = :gender "
			+ "ORDER BY eh.created_at DESC", nativeQuery = true)
	List<EarningHistory> findByListenerProfileGender(String gender);

	@Query(value = "SELECT eh.* " + "FROM earning_history eh "
			+ "INNER JOIN listener_profile lp ON eh.user_id = lp.user_id "
			+ "INNER JOIN user_languages ul ON lp.user_id = ul.user_id "
			+ "WHERE ul.language_id IN (SELECT id FROM language WHERE name = :languageName)", nativeQuery = true)
	List<EarningHistory> findByListenerProfileLanguage(String languageName);

	List<EarningHistory> findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(Date startDate, Date endDate,
			User user);

	List<EarningHistory> findByActiveTrueAndUserOrderByCreatedAtDesc(User user);

	@Query("SELECT e FROM EarningHistory e " + "JOIN e.user u " + "JOIN ListenerProfile l ON l.user.id = u.id "
			+ "WHERE l.dateOfBirth BETWEEN :startDate AND :endDate")
	List<EarningHistory> findEarningHistoryByListenerDateOfBirthBetween(LocalDate startDate, LocalDate endDate);

	Integer countDistinctUserBy();
	
    List<EarningHistory> findByUserIdInAndCreatedAtBetweenAndActiveTrue(List<Long> userIds, Date startDate, Date endDate);
    
    List<EarningHistory> findByUserIdInAndActiveTrue(List<Long> userIds);

}
