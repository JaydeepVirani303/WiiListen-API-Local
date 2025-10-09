package com.wiilisten.response;

import com.wiilisten.enums.CouponType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponsResponseDTO {

    private Long id;

    private String couponCode;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private CouponType couponType;

    private Double couponAmount;

    private Integer usedCount;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
