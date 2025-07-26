package com.wiilisten.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailabilityDTO {
	
	private String day;
	
	private String stime;
	
	private String etime;
	
	@JsonProperty("duty_timings")
	List<DutyTimeRequestDto> dutyTimings;
	
}
