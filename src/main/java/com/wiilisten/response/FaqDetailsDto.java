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
public class FaqDetailsDto {

	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("question")
	private String question;
	
	@JsonProperty("answer")
	private String answer;
	
	@JsonProperty("active")
	private Boolean active;
	
	@JsonProperty("created_at")
	private Date createdAt;
	
	@JsonProperty("updated_at")
	private Date updatedAt;
}
