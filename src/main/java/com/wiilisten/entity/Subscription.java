package com.wiilisten.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Data
public class Subscription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String type; // MONTHLY/YEARLY/WEEKLY

	private Double price;
	
	private String category; //SUBSCRIPTION/ADVERTISEMENT

	private String deviceOs;
	
	private String planId;

	private Boolean isDeleted;

	private Boolean active;

	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;

	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;

	private Integer durationInDays;

}
