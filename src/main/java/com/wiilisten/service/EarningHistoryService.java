package com.wiilisten.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.wiilisten.entity.EarningHistory;
import com.wiilisten.entity.User;

public interface EarningHistoryService extends BaseService<EarningHistory, Long>{
	
	List<EarningHistory> findByListenerProfileEducation(String education);
	
	List<EarningHistory> findByListenerProfileLocation(String location);
	
	List<EarningHistory> findByListenerProfileGender(String gender);
	
	List<EarningHistory> findByListenerProfileLanguage(String languageName);
	
	List<EarningHistory> findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(Date startDate,Date endDate,User user);
	
	List<EarningHistory> findByActiveTrueAndUserOrderByCreatedAtDesc(User user);
	
	List<EarningHistory> findEarningHistoryByListenerDateOfBirthBetween(LocalDate startDate, LocalDate endDate);
	
	Integer countDistinctUserBy();
	
    List<EarningHistory> findByUserIdInAndCreatedAtBetweenAndActiveTrue(List<Long> userIds, Date startDate, Date endDate);
    
    List<EarningHistory> findByUserIdInAndActiveTrue(List<Long> userIds);


}
