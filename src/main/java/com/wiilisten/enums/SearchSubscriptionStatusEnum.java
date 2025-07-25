package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum SearchSubscriptionStatusEnum {

	ACTIVE("ACTIVE"),
	DEACTIVE("DEACTIVE"),
	EXPIRED("EXPIRED");
	
	private String status;

	private SearchSubscriptionStatusEnum(String status) {
		this.status = status;
	}
}
