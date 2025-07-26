package com.wiilisten.service.impl;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.OtpHistory;

import jakarta.annotation.PostConstruct;
import com.wiilisten.service.OtpHistoryService;

@Service
public class OtpHistoryServiceImpl extends BaseServiceImpl<OtpHistory, Long> implements OtpHistoryService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getOtpHistoryRepository();
	}

	@Override
	public OtpHistory findByOtpAndActiveTrue(String otp) {
		return getDaoFactory().getOtpHistoryRepository().findByOtpAndActiveTrue(otp);
	}

	@Override
	public OtpHistory findByEmailAndOtpAndActiveTrue(String email, String otp) {
		return getDaoFactory().getOtpHistoryRepository().findByEmailAndOtpAndActiveTrue(email, otp);
	}

	@Override
	public OtpHistory findByEmailAndActiveTrue(String email) {
		return getDaoFactory().getOtpHistoryRepository().findByEmailAndActiveTrue(email);
	}

	@Override
	public OtpHistory findByContactAndActiveTrue(String concatedContact) {
		return getDaoFactory().getOtpHistoryRepository().findByContactAndActiveTrue(concatedContact);
	}

	@Override
	public OtpHistory findByContactAndOtpAndActiveTrue(String concatedContact, String requestOtp) {
		return getDaoFactory().getOtpHistoryRepository().findByContactAndOtpAndActiveTrue(concatedContact, requestOtp);
	}
}
