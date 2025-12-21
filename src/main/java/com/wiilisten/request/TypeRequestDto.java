package com.wiilisten.request;

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
public class TypeRequestDto {
	
	@JsonProperty("type")
	private String type;

	@JsonProperty("sub_category")
	private String subCategory;

	@JsonProperty(ApplicationConstants.REQUESTED_TIMEZONE)
	private String requestedTimeZone;
}
