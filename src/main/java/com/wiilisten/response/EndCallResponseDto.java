package com.wiilisten.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EndCallResponseDto {
	
	private Long id;
	private Double subTotal;	
	private Double taxValue;
	private String appliedDiscountCode;
	private Double discountValue;
	private Double payableAmount;
	private Long durationInMinutes;
	private Double averageRatings;
	private Integer totalReview;
	private Double ratePerMinute;
	private List<ReviewsAndRatingsResponseDto> reviewsAndRatings;
	
}
