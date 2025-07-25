package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.User;

public interface CallerProfileService extends BaseService<CallerProfile, Long> {

	CallerProfile findByUserAndActiveTrue(User user);

	CallerProfile findByUser(User user);

	CallerProfile findByIdAndActiveTrue(Long id);

	List<CallerProfile> findAllByOrderByIdDesc();

	List<CallerProfile> findByActiveTrueOrderByIdDesc();

	CallerProfile findByUserAndSearchSubscriptionStatusAndActiveTrue(User user, String status);

}
