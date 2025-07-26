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
public class ListenerProfileResponseDto {

	@JsonProperty("listener_id")
	private Long listenerId;
	
	@JsonProperty("call_name")
	private String callName;
	
	@JsonProperty("notable_quote")
	private String notableQuote;
	
	@JsonProperty("profile_picture")
	private String profilePicture;
	
	@JsonProperty("is_favorite")
	private Boolean isFavorite;
	
	@JsonProperty("call_max_duration")
	private String callMaxDuration;
	
	@JsonProperty("rate_per_minute_for_ondemand")
	private Double ratePerMinuteForOnDemand;
	
	@JsonProperty("current_rating")
	private Double currentRating;
	
	@JsonProperty("total_reviews")
	private Long totalReviews;
	
	@JsonProperty("app_active_status")
	private Boolean appActiveStatus;
	
	private List<UserReviewDetailsResponseDto> reviews;
}
