package com.wiilisten.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "administrative_notification",
		indexes = {
				@Index(name = "idx_active", columnList = "active")
		})
public class AdministrativeNotification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_administrative_notification",
		joinColumns = {
				@JoinColumn(name = "admin_notification_id", referencedColumnName = "id")
		},
		inverseJoinColumns = {
				@JoinColumn(name = "user_id", referencedColumnName = "id")
		}
	)
	private List<User> users;
	
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String content;
	
	private String tags; // SUBSCRIPTION_EXPIRE,CALL_REQUEST,ADVERTISEMENT,REVIEW,PREMIUM

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