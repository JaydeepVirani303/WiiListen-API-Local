package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum OtpTypeEnum {

	EMAIL("EMAIL"),
	CONTACT("CONTACT");
	
	private String type;

	private OtpTypeEnum(String type) {
		this.type = type;
	}
}
