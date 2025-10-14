package com.wiilisten.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewsAndRatingsResponseDto {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("reviewer_Id")
	private Long reviewerId;

	@JsonProperty("reviewer_name")
	private String reviewerName;
	
	@JsonProperty("email")
	private String email;

	@JsonProperty("contact")
	private String contact;
	
	@JsonProperty("profile")
	private String profile;
	
	@JsonProperty("rating")
	private Integer rating;

	@JsonProperty("review")
	private String review;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;

}
