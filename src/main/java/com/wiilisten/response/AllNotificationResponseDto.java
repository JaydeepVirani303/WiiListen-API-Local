package com.wiilisten.response;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllNotificationResponseDto {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name; // For Call and Payment notifications

	@JsonProperty("title")
	private String title; // For Admin notifications

	@JsonProperty("content")
	private String content; // For Admin notifications
	
	@JsonProperty("ratings")
	private Character ratings; // For Admin notifications
	
	@JsonProperty("review")
	private String review; // For Admin notifications

	@JsonProperty("tags")
	private String tags; // For Admin notifications

	@JsonProperty("duration_in_minutes")
	private Long durationInMinutes; // For Call and Payment notifications

	@JsonProperty("type")
	private String type; // For Call and Payment notifications

	@JsonProperty("payable_amount")
	private Double payableAmount; // For Call and Payment notifications

	@JsonProperty("call_start_time")
	private LocalDateTime callStartTime; // For Payment notifications

	@JsonProperty("call_duration")
	private String callDuration; // For Payment notifications

	@JsonProperty("booking_date_time")
	private LocalDateTime bookingDateTime; // For Call notifications

	@JsonProperty("event")
	private String event; // For Call notifications

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;

	@JsonProperty("users")
	private List<UserResponseDto> users; // For Admin notifications
	
	@JsonProperty("notification_type")
	private String notificationType;
	
	 
}
