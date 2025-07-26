package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlockedUserRequestDto {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("user_id")
	private Long userId;

	@JsonProperty("type")
	private String type; // BLOCKED/REPORTED

	@JsonProperty("reason")
	private String reason;

}
