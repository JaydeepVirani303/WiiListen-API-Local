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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "user_rating_and_review",
		indexes = {
				@Index(name = "idx_active", columnList = "active"),
				@Index(name = "idx_reviewedUserId_active", columnList = "reviewed_user_id,active"),
		})
public class UserRatingAndReview {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "reviewed_user_id")
	private User reviewedUser;
	
	@ManyToOne
	@JoinColumn(name = "reviewer_user_id")
	private User reviewerUser;
	
	private Integer rating;
	
	@Column(columnDefinition = "TEXT")
	private String review;
	
	private Boolean isTopComment;
	
	private Boolean active;
	
	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;

}
