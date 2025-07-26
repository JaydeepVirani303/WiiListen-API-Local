package com.wiilisten.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthorityUpdateDto {
	
	@JsonProperty("admin_id")
	private Long adminId;
	
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

	@JsonProperty("profile_picture")
	private String profilePicture;

	@JsonProperty("authorities")
	private List<AdministrationAuthorityRequestDto> authorities;

}
