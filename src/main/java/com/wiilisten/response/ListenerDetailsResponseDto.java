package com.wiilisten.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListenerDetailsResponseDto {

	@JsonProperty("total_earning")
	private Double totalEarning;

	@JsonProperty("total_completed_minutes")
	private Long totalCompletedMinutes;

	@JsonProperty("total_attended_calls")
	private Long totalAttendedCalls;

}
