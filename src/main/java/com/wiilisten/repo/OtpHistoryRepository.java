package com.wiilisten.repo;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.OtpHistory;

@Repository
public interface OtpHistoryRepository extends BaseRepository<OtpHistory, Long>{

	OtpHistory findByOtpAndActiveTrue(String otp);

	OtpHistory findByEmailAndOtpAndActiveTrue(String email, String otp);

	OtpHistory findByEmailAndActiveTrue(String email);

	OtpHistory findByContactAndActiveTrue(String concatedContact);

	OtpHistory findByContactAndOtpAndActiveTrue(String concatedContact, String requestOtp);

}
