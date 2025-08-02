package com.wiilisten.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiilisten.request.AdministrationAuthorityRequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminDetailsResponseDto {
	
	private String token;
	
	private Long id;

	private String name;
	
	private String email;
	
	private String password;

	private String contact;
	
	private String role; 
	
	@JsonProperty("authorities")
	private List<AdministrationAuthorityRequestDto> authorities;
	
	private String profilePicture;

	private Boolean active;	
	
	private Date createdAt;
	
	private Date updatedAt;

	private Boolean twoFactorEnabled;
}
