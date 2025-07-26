package com.wiilisten.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.wiilisten.enums.SearchSubscriptionStatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "caller_profile", 
		indexes = {
				@Index(name="idx_id", columnList = "id"),
				@Index(name="idx_active", columnList = "active")
		})
public class CallerProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private Long points;
	
	private String referralCode;
	
	private String searchSubscriptionStatus; // ACTIVE/DEACTIVE/EXPIRED
	
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
		points = 0L;
		searchSubscriptionStatus = SearchSubscriptionStatusEnum.DEACTIVE.getStatus();
		
	}
}
