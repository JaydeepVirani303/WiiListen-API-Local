package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminProfileRequestDto {
	
	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;
	
	@JsonProperty("email")
	private String email;

	@JsonProperty("country_code")
	private String countryCode;
	
	@JsonProperty("contact")
	private String contact;
	
	@JsonProperty("country_short_name")
	private String countryShortName;
	
	@JsonProperty("role")
	private String role; 
	
	@JsonProperty("profile_picture")
	private String profilePicture;

}
