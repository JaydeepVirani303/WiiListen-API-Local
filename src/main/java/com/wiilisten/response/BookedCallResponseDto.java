package com.wiilisten.response;

import java.time.LocalDateTime;
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
public class BookedCallResponseDto {
	
	@JsonProperty("id")
	private Long id;

	@JsonProperty("caller_id")
	private Long callerId;

	@JsonProperty("listener_id")
	private Long listenerId;
	
	@JsonProperty("caller_name")
	private String callerName;

	@JsonProperty("listener_name")
	private String listenerName;

	@JsonProperty("booking_date_time")
	private LocalDateTime bookingDateTime;

	@JsonProperty("duration_in_minutes")
	private Long durationInMinutes;

	@JsonProperty("caller_joined_at")
	private LocalDateTime callerJoinedAt;

	@JsonProperty("caller_leaved_at")
	private LocalDateTime callerLeavedAt;

	@JsonProperty("listener_joined_at")
	private LocalDateTime listenerJoinedAt;

	@JsonProperty("listener_leaved_at")
	private LocalDateTime listenerLeavedAt;

	@JsonProperty("price")
	private Double price;

	@JsonProperty("sub_total")
	private Double subTotal;

	@JsonProperty("tax_value")
	private Double taxValue;
	
	@JsonProperty("payable_amount")
	private Double payableAmount;
	
	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;

}
