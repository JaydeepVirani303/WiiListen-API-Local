package com.wiilisten.request;

import com.wiilisten.enums.CouponType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponsRequestDTO {

    private Long id; // optional for create, required for update

    private String couponCode;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private CouponType couponType; // PERCENTAGE or FLAT

    private Double couponAmount;

    private Boolean active; // optional, default = true

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt = LocalDateTime.now();
}

