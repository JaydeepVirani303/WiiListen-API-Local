package com.wiilisten.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardDetailsResponseDto {
	
	@JsonProperty("id")
	private Long id;

	@JsonProperty("type")
	private String type;

	@JsonProperty("number")
	private String number;
	
	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;

}
