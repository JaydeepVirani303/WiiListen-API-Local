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
public class BlockedUserResponseDto {
	
	@JsonProperty("id")
	private Long id;

	@JsonProperty("blocker_Id")
	private Long blockerId;

	@JsonProperty("blocker_name")
	private String blockerName;
	
	@JsonProperty("reason")
	private String reason;
	
	@JsonProperty("email")
	private String email;

	@JsonProperty("contact")
	private String contact;
	
	@JsonProperty("profile")
	private String profile;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;

}
