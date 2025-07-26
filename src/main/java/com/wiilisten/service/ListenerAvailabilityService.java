package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.ListenerAvailability;
import com.wiilisten.entity.User;

public interface ListenerAvailabilityService extends BaseService<ListenerAvailability, Long>{

	List<ListenerAvailability> findByUserAndActiveTrue(User user);

	List<ListenerAvailability> findByUserAndWeekDayAndActiveTrue(User user, String day);

}
