package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.CardDetails;
import com.wiilisten.entity.User;
import com.wiilisten.service.CardDetailsService;

import jakarta.annotation.PostConstruct;

@Service
public class CardDetailsServiceImpl extends BaseServiceImpl<CardDetails, Long> implements CardDetailsService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getCardDetailsRepository();
	}

	@Override
	public List<CardDetails> findByUserAndActiveTrue(User user) {
		return getDaoFactory().getCardDetailsRepository().findByUserAndActiveTrue(user);
	}
}
