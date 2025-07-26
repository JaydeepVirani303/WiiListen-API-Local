package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum ProfileStatusEnum {

	APPROVED("APPROVED"),
	REJECTED("REJECTED"),
	PENDING("PENDING");
	
	private String status;

	private ProfileStatusEnum(String status) {
		this.status = status;
	}
	
	
}
