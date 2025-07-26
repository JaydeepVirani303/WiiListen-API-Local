package com.wiilisten.entity;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "otp_history",
		indexes = {
				@Index(name = "idx_type_email_active", columnList = "type,email,active"),
				@Index(name = "idx_type_contact_active", columnList = "type,contact,active"),
		})
public class OtpHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private String type;
	
	private String email;
	
	private String contact;
	
	private String otp;
	
	private Boolean isExpired;
	
	private Boolean isUtilized;
	
	private LocalDateTime expiryDateTime;
	
	private String reason;

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
		isExpired = Boolean.FALSE;
		isUtilized = Boolean.FALSE;
		
	}
}
