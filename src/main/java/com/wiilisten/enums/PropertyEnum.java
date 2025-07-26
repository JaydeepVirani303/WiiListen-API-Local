package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum PropertyEnum {

	DEFAUTL_EMAIL("default.email.id"),
	BUCKET("bucket"), 
	
	ACCESS_KEY_ID("access.key.id"), 
	SECRET_ACCESS_KEY("secret.access.key"), 
	REGION("region"),

	

	FOLDER("folder"),FCM_SERVER_KEY("fcm.server.key"),FCM_BASE_URL("fcm.url"),
	BUCKET_PATH("bucket.path");
	
	private String code;
	

	private PropertyEnum(final String code) {
		this.code = code;
	}

	
	
	
	
}
