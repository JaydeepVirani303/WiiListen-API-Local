package com.wiilisten.response;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationCallDto {

	@JsonProperty("booking_id")
	private Long bookingId;
	
	@JsonProperty("booking_date_time")
	private LocalDateTime bookingDateTime;
	
	@JsonProperty("duration_in_minutes")
	private Long durationInMinutes;
	
	@JsonProperty("price")
	private Double price;	
	
	@JsonProperty("subtotal")
	private Double subTotal;
	
	@JsonProperty("call_name")
	private String callName;
	
	@JsonProperty("event")
	private String event;
	
	@JsonProperty("content")
	private String content;
	
	
	@JsonProperty("sender_id")
	private Long senderId;
	
	@JsonProperty("recipient_id")
	private Long recipientId;
	
	
}
