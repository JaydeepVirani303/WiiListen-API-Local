package com.wiilisten.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "used_coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsedCoupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // ID of the user who used the coupon

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupons coupon;

    private LocalDateTime usedAt;
}

