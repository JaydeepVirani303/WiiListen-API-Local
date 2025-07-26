package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactUsReplyRequestDto {
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("admin_response")
	private String adminResponse;

}
