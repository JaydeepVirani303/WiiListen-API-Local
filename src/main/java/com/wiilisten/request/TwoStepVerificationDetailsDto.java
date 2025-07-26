package com.wiilisten.request;

import lombok.NoArgsConstructor;

import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
@Setter
@NoArgsConstructor
public class TwoStepVerificationDetailsDto {

	@JsonProperty("country_code")
	private String countryCode;
	
	private String contact;
	
	private String otp;
	
}
