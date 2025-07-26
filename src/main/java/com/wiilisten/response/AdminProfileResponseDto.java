package com.wiilisten.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminProfileResponseDto {
	
	@JsonProperty("id")
	private Long id;

	@JsonProperty("admin_name")
	private String adminName;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("password")
	private String password;

	@JsonProperty("contact")
	private String contact;
	
	@JsonProperty("country_code")
	private String countryCode;
	
	@JsonProperty("country_short_name")
	private String countryShortName;
	
	@JsonProperty("role")
	private String role; 
	
	@JsonProperty("profile_picture")
	private String profilePicture;
	
	@JsonProperty("isLoggedIn")
	private Boolean isLoggedIn;

	@JsonProperty("active")
	private Boolean active;
	
	@JsonProperty("created_at")
	private Date createdAt;
	
	@JsonProperty("updated_at")
	private Date updatedAt;


}
