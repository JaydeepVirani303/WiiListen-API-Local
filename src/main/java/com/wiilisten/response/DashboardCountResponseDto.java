package com.wiilisten.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardCountResponseDto {
	
	@JsonProperty("schedule")
	private Integer schedule;

	@JsonProperty("on_demand")
	private Integer onDemand;

	@JsonProperty("both")
	private Integer both;

}
