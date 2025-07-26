package com.wiilisten.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CallerProfileResponseDto {
	@JsonProperty("caller_id")
	private Long callerId;
	
	@JsonProperty("user_id")
	private Long userId;
	
	@JsonProperty("call_name")
	private String callName;
	
	@JsonProperty("profile_picture")
	private String profilePicture;
	
	@JsonProperty("current_rating")
	private Double currentRating;
	
	@JsonProperty("total_reviews")
	private Long totalReviews;
	
	@JsonProperty("app_active_status")
	private Boolean appActiveStatus;
	
	@JsonProperty("reviews")
	private List<UserReviewDetailsResponseDto> reviews;

}
