package com.wiilisten.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "administration",
		indexes = {
				@Index(name = "idx_active", columnList = "active"),
				@Index(name = "idx_email_active", columnList = "email,active")
		})
public class Administration {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	
	private String email;
	
	private String password;
	
	private String countryCode;

	private String contact;
	
	private String countryShortName;
	
	private String role; // ADMIN/SUBADMIN
	
	@Column(columnDefinition = "TEXT")
	private String authorities;
	
	private String profilePicture;
	
	private Boolean isLoggedIn;

	private Boolean active;
	
	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;
}
