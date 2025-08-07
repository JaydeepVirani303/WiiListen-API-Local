package com.wiilisten.entity;

import java.time.LocalTime;
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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "listener_availability",
		indexes = {
				@Index(name = "idx_userid_active", columnList = "user_id, active")
		})
public class ListenerAvailability {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	private String weekDay;
	
	private LocalTime startTime;
	
	private LocalTime endTime;
	
	private Boolean active;
	
	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;

	private String timeZone;
}
