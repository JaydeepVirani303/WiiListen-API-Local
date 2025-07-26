package com.wiilisten.entity;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wiilisten.enums.ListenerSignupStepEnum;
import com.wiilisten.enums.ProfileStatusEnum;
import com.wiilisten.enums.TrainingVideoProgressStatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
public class ListenerAnalytic {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "listener_id")
	private ListenerProfile listener;

	@ManyToOne
	@JoinColumn(name = "caller_id")
	private CallerProfile caller;
	
	private LocalDateTime visitingTime;

	private Boolean active;

	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;

	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	@PrePersist
	private void postConstruct() {
			
		active = Boolean.TRUE;
		
	}

}
