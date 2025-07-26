package com.wiilisten.response;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallerResponseDto {

	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("call_name")
	private String callName;

	@JsonProperty("email")
	private String email;
	
	@JsonProperty("country_code")
	private String countryCode;
	
	@JsonProperty("contact_number")
	private String contactNumber;
	
	@JsonProperty("has_unseen_notifications")
	private Boolean hasUnseenNotifications;
	
	@JsonProperty("is_profile_set")
	private Boolean isProfileSet;
	
	@JsonProperty("profile_picture")
	private String profilePicture;
	
	@JsonProperty("is_email_verified")
	private Boolean isEmailVerified;

	@JsonProperty("is_suspended")
	private Boolean isSuspended; // ban/unban
	
	@JsonProperty("average_call_duration")
	private String averageCallDuration;

	@JsonProperty("is_blocked_or_reported")
	private Boolean isBlockedOrReported;

	@JsonProperty("notification_status")
	private Boolean notificationStatus; // ON/OFF

	@JsonProperty("points")
	private Long points;

	@JsonProperty("referral_code")
	private String referralCode;

	@JsonProperty("search_subscription_status")
	private String searchSubscriptionStatus;
	
	@JsonProperty("reviews_and_ratings")
	private List<ReviewsAndRatingsResponseDto> reviewsAndRatings;
	
	@JsonProperty("card_details")
	private List<CardDetailsResponseDto> cardDetails;
	
	@JsonProperty("active")
	private Boolean active;
	
	@JsonProperty("created_at")
	private Date createdAt;
	
	@JsonProperty("updated_at")
	private Date updatedAt;

}
