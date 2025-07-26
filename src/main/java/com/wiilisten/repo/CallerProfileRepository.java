package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.User;

@Repository
public interface CallerProfileRepository extends BaseRepository<CallerProfile, Long>{

	CallerProfile findByUserAndActiveTrue(User user);

	CallerProfile findByUser(User user);

	CallerProfile findByIdAndActiveTrue(Long id);
	
	List<CallerProfile> findAllByOrderByIdDesc();
	
	List<CallerProfile> findByActiveTrueOrderByIdDesc();
	
	CallerProfile findByUserAndSearchSubscriptionStatusAndActiveTrue(User user,String status);

}
