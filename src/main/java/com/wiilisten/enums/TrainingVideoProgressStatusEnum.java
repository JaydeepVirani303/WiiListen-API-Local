package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum TrainingVideoProgressStatusEnum {

	PENDING("PENDING"),
	COMPLETED("COMPLETED");
	
	private String status;

	private TrainingVideoProgressStatusEnum(String status) {
		this.status = status;
	}
}
