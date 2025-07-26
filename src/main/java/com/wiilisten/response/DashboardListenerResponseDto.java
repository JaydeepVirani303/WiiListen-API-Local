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
public class DashboardListenerResponseDto {
	
	@JsonProperty("earning_count")
	private Double earningCount;
	
	@JsonProperty("listener_count")
	private Integer listenerCount;
	
	@JsonProperty("caller_count")
	private Integer callerCount;
	
	@JsonProperty("premium_listener_count")
	private Integer premiumListenerCount;
}
