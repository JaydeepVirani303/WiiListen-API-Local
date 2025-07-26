package com.wiilisten.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wiilisten.enums.TwoStepVerificationStatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "user", indexes = {
		@Index(name="idx_id", columnList = "id"),
		@Index(name="idx_active", columnList = "active"),
		@Index(name="idx_email_active", columnList = "email,active")
})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String email;
	
	private String password;
	
	private String role; // LISTENER, CALLER 
	
	private String callName;
	
	private String profilePicture;
	
	private String countryCode;
	
	private String contactNumber;
	
	private Long totalReviews;
	
	private Double currentRating;
	
	private String twoStepVerificationStatus; // ENABLE/DISABLE
	
	private Boolean hasUnseenNotifications;
	
	private Boolean isProfileSet;
	
	private Boolean isEmailVerified;
	
	private Boolean isSuspended; // ban/unban
	
	private String referralCode;
	
	private String averageCallDuration;
	
	private Boolean isBlockedOrReported;
	
	private Boolean notificationStatus; // ON/OFF
	
	private String timeZone;
	
	private String deviceUUID;
	
	private String deviceVersion;
	
	private String deviceToken;
	
	private String deviceOs;
	
	private String voipToken;
	
	private Boolean isLoggedIn;
	
	private Boolean active;
	
	private String stripeCustomerId;
	
	private String paymentIntent;
	
	private String jwtToken;
	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;

	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	@PrePersist
	private void postConstruct() {
	
		active = Boolean.TRUE;
		isBlockedOrReported = Boolean.FALSE;
		isLoggedIn = Boolean.FALSE;
		isSuspended = Boolean.FALSE;
		notificationStatus = Boolean.FALSE;
		twoStepVerificationStatus = TwoStepVerificationStatusEnum.DISABLED.getValue();
		isProfileSet = Boolean.FALSE;
		isEmailVerified = Boolean.FALSE;
		currentRating = 0.0;
		totalReviews = 0L;
		
	}
}
