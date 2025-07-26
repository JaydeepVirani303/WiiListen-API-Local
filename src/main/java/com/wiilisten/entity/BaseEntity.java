package com.wiilisten.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

public class BaseEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created")
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated")
	private Date updated;
	
	@PrePersist
    protected void onCreate() {
        created = new Date(); 
        updated = new Date(); 
    }

    @PreUpdate
    protected void onUpdate() {
        updated = new Date(); 
    }

}
