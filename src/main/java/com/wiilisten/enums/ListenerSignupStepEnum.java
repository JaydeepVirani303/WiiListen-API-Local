package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum ListenerSignupStepEnum {

	STEP_1("STEP_1"),
	STEP_2("STEP_2"),
	STEP_3("STEP_3"),
	STEP_4("STEP_4"),
	STEP_5("STEP_5"),
	STEP_6("STEP_6"),
	STEP_7("STEP_7");
	
	private String value;

	private ListenerSignupStepEnum(String value) {
		this.value = value;
	}
	
	
}
