package com.wiilisten.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

@Entity
@Data
public class AdminModulePermission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "administration_id")
	private Administration administration;

	@ManyToOne
	@JoinColumn(name = "admin_module_id")
	private AdminModule adminModule;

	private Boolean canAdd;
	
	private Boolean canUpdate;
	
	private Boolean canDelete;
	
	private Boolean canView;

	private Boolean active;

	@CreationTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date createdAt;

	@UpdateTimestamp
	@Temporal(value = TemporalType.TIMESTAMP)
	private Date updatedAt;
	
	@PrePersist
	public void prePersist() {
		if (canAdd == null) {
			canAdd = Boolean.FALSE;
		}
		if (canUpdate == null) {
			canUpdate = Boolean.FALSE;
		}
		if (canDelete == null) {
			canDelete = Boolean.FALSE;
		}
		if (canView == null) {
			canView = Boolean.FALSE;
		}
	}


}
