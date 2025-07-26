package com.wiilisten.request;

import java.time.LocalDate;
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
public class BookedCallDto {

	
	@JsonProperty("listener_id")
	private Long listenerId;
	
	@JsonProperty("booking_time")
	private LocalDate bookingDateTime;
	
	@JsonProperty("duration_in_minutes")
	private Integer durationInMinutes;
	
	@JsonProperty("time_zone")
	private String timeZone;
	

	
	
}
