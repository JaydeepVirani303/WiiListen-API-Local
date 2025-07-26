package com.wiilisten.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TotalReviewAndRatingResponseDto {
	
	private Double averageRatings;
	private Integer totalReview;
	private List<ReviewsAndRatingsResponseDto> reviewsAndRatings;

}
