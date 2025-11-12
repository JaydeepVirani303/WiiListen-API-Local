package com.wiilisten.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "listener_pay_scheduler_config")
public class ListenerPaySchedulerConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // MANUAL / AUTOMATIC
    private String frequency; // WEEKLY / BIWEEKLY / NULL

    @Column(name = "is_active")
    private boolean active;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
