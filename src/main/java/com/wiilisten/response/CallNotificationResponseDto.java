package com.wiilisten.response;

import java.time.LocalDateTime;
import java.util.Date;

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
public class CallNotificationResponseDto {
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("duration_in_minutes")
	private Long durationInMinutes;
	
	@JsonProperty("event")
	private String event; // CALL_SCHEDULED, CALL_REMINDER
	
	@JsonProperty("type")
	private String type; // SCHEDULE/ON_DEMAND
	
	@JsonProperty("notification_type")
	private String notificationType;
	
	@JsonProperty("payable_amount")
	private Double payableAmount;
	
	@JsonProperty("booking_date_time")
	private LocalDateTime bookingDateTime;
	
	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;
	
	

}
