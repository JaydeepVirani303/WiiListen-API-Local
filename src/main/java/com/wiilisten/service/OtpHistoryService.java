package com.wiilisten.service;

import com.wiilisten.entity.OtpHistory;

public interface OtpHistoryService extends BaseService<OtpHistory, Long>{

	OtpHistory findByOtpAndActiveTrue(String otp);

	OtpHistory findByEmailAndOtpAndActiveTrue(String email, String otp);

	OtpHistory findByEmailAndActiveTrue(String email);

	OtpHistory findByContactAndActiveTrue(String concatedContact);

	OtpHistory findByContactAndOtpAndActiveTrue(String concatedContact, String requestOtp);

}
