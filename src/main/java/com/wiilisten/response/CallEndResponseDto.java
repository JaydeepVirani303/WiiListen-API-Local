package com.wiilisten.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallEndResponseDto {

	private Long id;

	private String category;

	private String subCategory;

	private String duration;

	private Double subTotal;

	private Double taxValue;

	private String appliedDiscountCode;

	private Double discountValue;

	private Double payableAmount;
	
	private List<ReviewsAndRatingsResponseDto> reviewsAndRatings;

}
