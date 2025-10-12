package com.wiilisten.response;

import java.time.LocalDate;
import java.util.Date;
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
public class ListenerResponseDto {
	
	@JsonProperty("listener_id")
	private Long listnerId;
	
	@JsonProperty("user_id")
	private Long userId;
	
	@JsonProperty("bank_details_id")
	private Long bankDetailsId;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("call_name")
	private String callName;

	@JsonProperty("profile_picture")
	private String profilePicture;
	
	@JsonProperty("country_code")
	private String countryCode;

	@JsonProperty("contact_number")
	private String contactNumber;

	@JsonProperty("total_reviews")
	private Long totalReviews;

	@JsonProperty("current_rating")
	private Double currentRating;

	@JsonProperty("two_step_verification_status")
	private String twoStepVerificationStatus; // ENABLE/DISABLE

	@JsonProperty("has_unseen_notifications")
	private Boolean hasUnseenNotifications;

	@JsonProperty("is_profile_set")
	private Boolean isProfileSet;

	@JsonProperty("is_email_verified")
	private Boolean isEmailVerified;

	@JsonProperty("is_suspended")
	private Boolean isSuspended; // ban/unban

	@JsonProperty("referral_code")
	private String referralCode;

	@JsonProperty("average_call_duration")
	private String averageCallDuration;

	@JsonProperty("is_blocked_or_reported")
	private Boolean isBlockedOrReported;

	@JsonProperty("notification_status")
	private Boolean notificationStatus;

	@JsonProperty("notable_quote")
	private String notableQuote;

	@JsonProperty("training_video_progress")
	private String trainingVideoProgress; // PENDING/COMPLETED

	@JsonProperty("current_training_video")
	private Long currentTrainingVideo;

	@JsonProperty("is_eligible_for_premium_call_search")
	private Boolean isEligibleForPremiumCallSearch;

	@JsonProperty("gender")
	private String gender;

	@JsonProperty("education")
	private String education;

	@JsonProperty("date_of_birth")
	private LocalDate dateOfBirth;

	@JsonProperty("location")
	private String location;

	@JsonProperty("call_max_duration")
	private String callMaxDuration;

	@JsonProperty("rate_per_minute")
	private Double ratePerMinute;

	@JsonProperty("id_proof")
	private String idProof;

	@JsonProperty("profile_status")
	private String profileStatus; // APPROVED/REJECTED/PENDING

	@JsonProperty("app_active_status")
	private Boolean appActiveStatus; // ON/OFF -- FOR ON-DEMAND CALLS

	@JsonProperty("total_earning")
	private Double totalEarning;

	@JsonProperty("total_completed_minutes")
	private Long totalCompletedMinutes;

	@JsonProperty("total_attended_calls")
	private Long totalAttendedCalls;

	@JsonProperty("points")
	private Long points;

	@JsonProperty("current_signup_step")
	private String currentSignupStep;

	@JsonProperty("user_name")
	private String userName;

	@JsonProperty("routing_or_aba_number")
	private String routingOrAbaNumber;

	@JsonProperty("account_number")
	private String accountNumber;

	@JsonProperty("account_type")
	private String accountType;

	@JsonProperty("listener_availabilities")
	private List<ListenerAvailabilityResponseDto> listenerAvailabilities;
	
	@JsonProperty("reviews_and_ratings")
	private List<ReviewsAndRatingsResponseDto> reviewsAndRatings;
	
	@JsonProperty("reports")
	private List<BlockedUserResponseDto> reports;
	
	@JsonProperty("active")
	private Boolean active;
	
	@JsonProperty("created_at")
	private Date createdAt;
	
	@JsonProperty("updated_at")
	private Date updatedAt;

	@JsonProperty("w9s_url")
	private String w9sUrl;

}
