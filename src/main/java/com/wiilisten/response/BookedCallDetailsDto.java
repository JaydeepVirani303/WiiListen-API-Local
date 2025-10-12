package com.wiilisten.response;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookedCallDetailsDto {

	@JsonProperty("booking_id")
	private Long bookingId;
	
	@JsonProperty("listener_id")
	private Long listenerId;
	
	@JsonProperty("listener_user_id")
	private Long listenerUserId;
	
	@JsonProperty("caller_id")
	private Long callerId;
	
	@JsonProperty("caller_user_id")
	private Long callerUserId;
	
	@JsonProperty("booking_date_time")
	private LocalDateTime bookingDateTime;
	
	@JsonProperty("duration_in_minutes")
	private Long durationInMinutes;
	
	@JsonProperty("price")
	private Double price;
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("rate_per_minute")
	private Double ratePerMinute;
	
	@JsonProperty("subtotal")
	private Double subTotal;
	
	@JsonProperty("call_request_status")
	private String callRequestStatus; // PENDING/ACCEPTED/REJECTED/RESCHEDULED
	
	@JsonProperty("call_status")
	private String callStatus; // 

	@JsonProperty("username")
	private String userName;
	
	@JsonProperty("notes")
	private String notes;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("call_name")
	private String callName;
	
	@JsonProperty("profile_picture")
	private String img;
	
	@JsonProperty("call_max_duration")
	private String maxDuration;
	
	@JsonProperty("current_ratings")
	private Double rating;
	
	@JsonProperty("total_reviews")
	private Long totalReviews;
	
	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	@JsonProperty("created_at")
	private Date createdAt;
	
	@JsonProperty("rejection_reason")
	private String rejectionReason;
	
	@JsonProperty("cancellation_reason")
	private String cancelationReason;
	
	@JsonProperty("tax_value")
	private Double taxValue;
	
	@JsonProperty("discount_value")
	private Double discountValue;
	
	@JsonProperty("payment_intent")
	private String paymentIntent;
	
	@JsonProperty("callType")
	private String callType; 
	
	@JsonProperty("requested_time_zone")
	private String requestedTimeZone; 

	@JsonProperty("sponsored")
	private Boolean sponsored;

	@JsonProperty("coupon_id")
	private Long couponId;
}
