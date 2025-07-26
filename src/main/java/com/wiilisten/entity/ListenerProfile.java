package com.wiilisten.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wiilisten.enums.ListenerSignupStepEnum;
import com.wiilisten.enums.ProfileStatusEnum;
import com.wiilisten.enums.TrainingVideoProgressStatusEnum;
import com.wiilisten.utils.ApplicationConstants;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "listener_profile",
		indexes = {
				@Index(name="idx_active", columnList = "active"),
				@Index(name="idx_userid", columnList = "user_id"),
				@Index(name="idx_username_active", columnList = "userName,active")
		})
public class ListenerProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_languages", 
			joinColumns = {
					@JoinColumn(name = "user_id", referencedColumnName = "id")
			},
			inverseJoinColumns = {
					@JoinColumn(name = "language_id", referencedColumnName = "id")
			})
	private List<Language> languages;
	
	private String userName;
	
	@Column(columnDefinition = "TEXT")
	private String notableQuote;
	
	private String trainingVideoProgress; // PENDING/COMPLETED
	
	private Long currentTrainingVideo;
	
	private Boolean isEligibleForPremiumCallSearch;
	
	private String gender;
	
	private String education;
	
	private LocalDate dateOfBirth;
	
	private String location;
	
	private String callMaxDuration;
	
	private Double ratePerMinute;
	
	private String idProof;
	
	private String profileStatus; // APPROVED/REJECTED/PENDING
	
	private Boolean appActiveStatus; // ON/OFF -- FOR ON-DEMAND CALLS
	
	private Double totalEarning;
	
	private Long totalCompletedMinutes;
	
	private Long totalAttendedCalls;

	private Double totalPaidEarning;

	@Column(columnDefinition = ApplicationConstants.TEXT)
	private String paymentlog;
	
	private Long points;
	
	private String currentSignupStep;
	
	private Boolean isAdvertisementActive;
	
	private String w9Form;
	
	private Long analytics;
	
	private Boolean active;
	
	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;

//	TODO change to PENDING when goes to LIVE
	@PrePersist
	private void postConstruct() {
		
		isAdvertisementActive=Boolean.FALSE;
		active = Boolean.TRUE;
		appActiveStatus = Boolean.FALSE;
		isEligibleForPremiumCallSearch = Boolean.FALSE;
		currentSignupStep = ListenerSignupStepEnum.STEP_1.getValue();
		trainingVideoProgress = TrainingVideoProgressStatusEnum.PENDING.getStatus();
		currentTrainingVideo = 0L;
		points = 0L;
		profileStatus = ProfileStatusEnum.PENDING.getStatus();
		totalAttendedCalls = 0L;
		totalCompletedMinutes = 0L;
		totalEarning = 0.0;
		
	}
}
