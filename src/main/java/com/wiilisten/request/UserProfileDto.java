package com.wiilisten.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.wiilisten.response.PlanPurchaseDetailResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileDto {

	private Long id;
	@JsonProperty("profile_id")
	private Long profileId;
	private String email;
	
	private String role;
	
	private String token;
	
	@JsonProperty("call_name")
	private String callName;
	
	@JsonProperty("profile_picture")
	private String profilePicture;
	
	@JsonProperty("total_reviews")
	private Long totalReviews;
	
	@JsonProperty("current_rating")
	private Double currentRating;
	
	@JsonProperty("two_step_verification_status")
	private String twoStepVerificationStatus; // ENABLE/DISABLE
	
	@JsonProperty("has_unseen_notifications")
	private Boolean hasUnseenNotifications;
	
	/*
	 * true: 
	 * 		CALLER -if email verified at signup stage
	 * 		LISTENER -if email verified and completes all 6 signup steps properly
	 * false: if any verification or details pending in above cases
	 */
	@JsonProperty("is_profile_set")
	private Boolean isProfileSet;
	
	/*
	 * At signup step email verification	 * 
	 */
	@JsonProperty("is_email_verified")
	private Boolean isEmailVerified;
	
	@JsonProperty("is_suspended")
	private Boolean isSuspended; // ban/unban
	
	@JsonProperty("notification_status")
	private Boolean notificationStatus; // ON/OFF
	
	@JsonProperty("is_logged_in")
	private Boolean isLoggedIn;
	
	@JsonProperty("username")
	private String userName;
	
	@JsonProperty("training_video_progress")
	private String trainingVideoProgress; // PENDING/COMPLETED
	
	@JsonProperty("current_training_video")
	private Long currentTrainingVideo;
	
	@JsonProperty("is_eligible_for_premium_call_search")
	private Boolean isEligibleForPremiumCallSearch;
	
	@JsonProperty("search_subscription_status")
	private String searchSubscriptionStatus; // ACTIVE/DEACTIVE/EXPIRED
	
	private String location;
	
	private List<Long> languages;
	
	@JsonProperty("notable_quote")
	private String notableQuote;
	
	@JsonProperty("is_advertisement_active")
	private Boolean isAdvertisementActive;
	
	private String gender;
	
	private String education;
	
	@JsonProperty("dob")
	private LocalDate dateOfBirth;
	
	@JsonProperty("call_max_duration")
	private String callMaxDuration;
	
	@JsonProperty("rate_per_minute")
	private Double ratePerMinute;
	
	@JsonProperty("id_proof")
	private String idProof;
	
	@JsonProperty("w9_form")
	private String w9Form;
	
	@JsonProperty("profile_status")
	private String profileStatus; // APPROVED/REJECTED/PENDING
	
	@JsonProperty("app_active_status")
	private Boolean appActiveStatus; // ON/OFF -- FOR ON-DEMAND CALLS
	
	@JsonProperty("current_signup_step")
	private String currentSignupStep;
	
	private List<AvailabilityDTO> availability;
//	private Map<String, List<DutyTimeRequestDto>> availability;
	
	@JsonProperty("full_name")
	private String fullName;
	
	@JsonProperty("routing_or_aba_number")
	private String routingOrAbaNumber;
	
	@JsonProperty("account_number")
	private String accountNumber;
	
	@JsonProperty("account_type")
	private String accountType;
	
	@JsonProperty("referral_code")
	private String referralCode;
	
	@JsonProperty("country_code")
	private String countryCode;
	
	@JsonProperty("contact_number")
	private String contactNumber;
	
	@JsonProperty("is_account_swittched")
	private Boolean isAccountSwittched;

	private String timeZone;

	private List<PlanPurchaseDetailResponseDto> purchasedPlansDetail;
	
}
