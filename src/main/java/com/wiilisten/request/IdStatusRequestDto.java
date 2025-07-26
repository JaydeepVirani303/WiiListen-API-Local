package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdStatusRequestDto {
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("type")
	private String type;

}
