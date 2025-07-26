package com.wiilisten.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRatingAndReviewDetailsDto {

	private Long id;
	
	@JsonProperty("call_name")
	private String callName;
	
	@JsonProperty("profile_picture")
	private String profilePicture;
	
	private String review;
	
	private Boolean isTopComment;
	
	private int rating;
	
	@JsonProperty("review_datetime")
	private String reviewDateTime;
	
}
