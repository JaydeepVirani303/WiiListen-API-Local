package com.wiilisten.response;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiilisten.utils.ApplicationConstants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PaymentNotificationResponseDto {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("duration_in_minutes")
	private Long durationInMinutes;

	@JsonProperty("notification_type")
	private String notificationType;

	@JsonProperty("type")
	private String type; // SCHEDULE/ON_DEMAND

	@JsonProperty("payable_amount")
	private Double payableAmount;

	@JsonProperty("call_start_time")
	private LocalDateTime callStartTime;

	@JsonProperty("call_duration")
	private String callDuration;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;


}
