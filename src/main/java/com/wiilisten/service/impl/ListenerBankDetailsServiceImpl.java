package com.wiilisten.service.impl;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.ListenerBankDetails;
import com.wiilisten.entity.User;
import com.wiilisten.service.ListenerBankDetailsService;

import jakarta.annotation.PostConstruct;

@Service
public class ListenerBankDetailsServiceImpl extends BaseServiceImpl<ListenerBankDetails, Long> implements ListenerBankDetailsService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getListenerBankDetailsRepository();
	}

	@Override
	public ListenerBankDetails findByUserAndActiveTrue(User user) {
		return getDaoFactory().getListenerBankDetailsRepository().findByUserAndActiveTrue(user);
	}
}
