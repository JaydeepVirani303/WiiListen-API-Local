package com.wiilisten.entity;

import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import lombok.Data;

@Data
@Entity
@Table(name = "language",
		indexes = {
				@Index(columnList = "active"),
				@Index(columnList = "id,active")
		})
public class Language {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String name;
	
	private String code;
	
	private Boolean active;
	
	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;
}
