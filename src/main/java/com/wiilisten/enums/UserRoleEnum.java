package com.wiilisten.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {

	CALLER("CALLER"),
	LISTENER("LISTENER"),
	ADMIN("ADMIN"),
	SUBADMIN("SUBADMIN");
	
	private String role;

	private UserRoleEnum(String role) {
		this.role = role;
	}
	
}
