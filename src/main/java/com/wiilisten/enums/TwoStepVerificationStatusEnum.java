package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum TwoStepVerificationStatusEnum {

	ENABLED("ENABLED"),
	DISABLED("DISABLED");
	
	private String value;

	private TwoStepVerificationStatusEnum(String value) {
		this.value = value;
	}
}
