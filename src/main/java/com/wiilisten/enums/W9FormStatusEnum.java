package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum W9FormStatusEnum {

	EMPTY("EMPTY"),
	PENDING("PENDING"),
	APPROVED("APPROVED"),
	DECLINED("DECLINED");

	private String status;

	private W9FormStatusEnum(String status) {
		this.status = status;
	}

}
