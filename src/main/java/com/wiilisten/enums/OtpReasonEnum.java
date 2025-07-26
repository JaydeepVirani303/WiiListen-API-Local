package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum OtpReasonEnum {

	EMAIL_OTP_FOR_SIGNUP("EMAIL_OTP_FOR_SIGNUP"),
	FORGOT_PASSWORD_OTP("FORGOT_PASSWORD_OTP"),
	ENABLE_TWO_STEP_VERIFICATION("ENABLE_TWO_STEP_VERIFICATION")
	;
	
	private String value;

	private OtpReasonEnum(String value) {
		this.value = value;
	}
	
}
