package com.wiilisten.service.impl;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.CommissionRate;
import com.wiilisten.service.CommissionRateService;

import jakarta.annotation.PostConstruct;

@Service
public class CommissionRateServiceImpl extends BaseServiceImpl<CommissionRate, Long> implements CommissionRateService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getCommissionRateRepository();
	}

	@Override
	public CommissionRate findByActiveTrue() {
		return getDaoFactory().getCommissionRateRepository().findByActiveTrue();
	}
}
