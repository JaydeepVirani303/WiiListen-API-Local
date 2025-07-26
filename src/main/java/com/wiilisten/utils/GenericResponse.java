package com.wiilisten.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
@NoArgsConstructor
public class GenericResponse {

	private String code;
	private String message;
	private Object data;

	public GenericResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}

	public GenericResponse(String code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

}
