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
public class ContactUsResponseDto {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("email")
	private String email;

	@JsonProperty("call_name")
	private String callName;

	@JsonProperty("profile_picture")
	private String profilePicture;

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("description")
	private String description;
	
	@JsonProperty("admin_response")
	private String adminResponse;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;

}
