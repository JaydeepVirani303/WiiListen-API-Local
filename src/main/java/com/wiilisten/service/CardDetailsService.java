package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.CardDetails;
import com.wiilisten.entity.User;

public interface CardDetailsService extends BaseService<CardDetails, Long>{
	
	List<CardDetails> findByUserAndActiveTrue(User user);

}
