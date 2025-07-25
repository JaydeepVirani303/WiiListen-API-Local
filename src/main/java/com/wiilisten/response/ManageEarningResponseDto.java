package com.wiilisten.response;

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
public class ManageEarningResponseDto {
	
	@JsonProperty("total_earning")
	private Double totalEarning;
	
	@JsonProperty("listener_response")
	private List<ListenerResponseDto> listenerResponse;

}
