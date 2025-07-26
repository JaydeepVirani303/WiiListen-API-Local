package com.wiilisten.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiilisten.utils.ApplicationConstants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminNotificationResponseDto {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("content")
	private String content;
	
	@JsonProperty("review")
	private String review;
	
	@JsonProperty("ratings")
	private Character ratings;
	
	@JsonProperty("tags")
	private String tags;
	
	@JsonProperty("notification_type")
	private String notificationType;
	
	@JsonProperty("users")
	private List<UserResponseDto> users;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;
	


}
