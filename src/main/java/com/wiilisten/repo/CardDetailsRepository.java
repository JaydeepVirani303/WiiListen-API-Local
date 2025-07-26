package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.CardDetails;
import com.wiilisten.entity.User;

@Repository
public interface CardDetailsRepository extends BaseRepository<CardDetails, Long>{
	
	List<CardDetails> findByUserAndActiveTrue(User user);

}
