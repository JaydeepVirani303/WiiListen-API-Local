package com.wiilisten.response;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MyRatingsDetailsDto {

	private Double currentRating;
	
	private Long totalReviews;
	
	private Map<Integer, Long> startWiseRating;
	
	private List<UserRatingAndReviewDetailsDto> reviews;
	
}
