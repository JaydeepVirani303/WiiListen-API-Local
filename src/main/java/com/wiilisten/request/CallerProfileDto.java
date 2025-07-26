package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallerProfileDto {

	private String email;
	
	private String token;
	
//	@JsonProperty("contact_number")
//	private String contactNumber;
//	
//	@JsonProperty("country_code")
//	private String countryCode;
	
	@JsonProperty("call_name")
	private String callName;
	
	@JsonProperty("profile_picture")
	private String profilePicture;
	
}
