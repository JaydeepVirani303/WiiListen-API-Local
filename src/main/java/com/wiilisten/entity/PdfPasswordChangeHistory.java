package com.wiilisten.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "pdf_password_change_history")
public class PdfPasswordChangeHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "old_password", length = 500)
    private String oldPassword; // AES encrypted

    @Column(name = "new_password", nullable = false, length = 500)
    private String newPassword; // AES encrypted

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "changed_at", updatable = false)
    private Date changedAt;

    @Column(name = "status", length = 50)
    private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}
