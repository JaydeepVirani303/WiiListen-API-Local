package com.wiilisten.entity;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wiilisten.utils.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "booked_calls", indexes = { @Index(name = "idx_active", columnList = "active"),
		@Index(name = "idx_callerid_active", columnList = "caller_id,active"),
		@Index(name = "idx_callerid_type_active", columnList = "caller_id,type,active"),
		@Index(name = "idx_listenerid_active", columnList = "listener_id,active"),
		@Index(name = "idx_listenerid_type_active", columnList = "listener_id,type,active"), })
public class BookedCalls {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "caller_id")
	private CallerProfile caller;

	@ManyToOne
	@JoinColumn(name = "listener_id")
	private ListenerProfile listener;

	@ManyToOne
	@JoinColumn(name = "card_id")
	private CardDetails cardDetails;

	private String type; // SCHEDULE/ON_DEMAND

	private LocalDateTime bookingDateTime;

	private Long durationInMinutes;
	// private Double durationInMinutes;

	private String callRequestStatus; // PENDING/ACCEPTED/REJECTED/RESCHEDULED/COMPLETED

	private String callStatus; // SCHEDULED/ON_GOING/COMPLETED/CANCELED

	private String rejectionReason;

	private String cancelationReason;

	@Column(columnDefinition = "TEXT")
	private String notes;

	private LocalDateTime callerJoinedAt;

	private LocalDateTime callerLeavedAt;

	private LocalDateTime listenerJoinedAt;

	private LocalDateTime listenerLeavedAt;

	private Double price;

	private Double subTotal;

	private Double taxValue;

	private String appliedDiscountCode;

	private String paymentIntent;

	private Double discountValue;

	private Double payableAmount;

	private String paymentStatus; // BLOCKED/PAID/UNPAID

	private Double adminCommissionRate;

	private LocalDateTime requestedDateTime;

	private LocalDateTime acceptedDateTime;

	private String requestedTimeZone;
	
	private Boolean isNotificationSent30Min;

	private Boolean isNotificationSent1Hour;

	private Boolean isNotificationSent1Day;

	private Boolean active;

	private Boolean sponsored;

	@Column(columnDefinition = ApplicationConstants.TEXT)
	private String paymentlog;

	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;

	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	@PrePersist
	public void prePersist() {
		isNotificationSent30Min=Boolean.FALSE;
		isNotificationSent1Hour=Boolean.FALSE;
		isNotificationSent1Day=Boolean.FALSE;
		durationInMinutes = 0L;

	}
}
