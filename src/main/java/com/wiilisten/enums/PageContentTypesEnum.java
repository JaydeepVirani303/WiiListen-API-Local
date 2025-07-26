package com.wiilisten.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PageContentTypesEnum {

	ABOUT_US("ABOUT_US"),
	PRIVACY_POLICY("PRIVACY_POLICY"),
	TERMS_AND_CONDITION("TERMS_AND_CONDITION");
	
	private String type;
	
}
