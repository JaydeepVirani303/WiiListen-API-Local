package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.ListenerAvailability;
import com.wiilisten.entity.User;

@Repository
public interface ListenerAvailabilityRepository extends BaseRepository<ListenerAvailability, Long>{

	List<ListenerAvailability> findByUserAndActiveTrue(User user);

	List<ListenerAvailability> findByUserAndWeekDayAndActiveTrue(User user, String day);

}
