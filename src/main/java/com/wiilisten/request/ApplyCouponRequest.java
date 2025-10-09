package com.wiilisten.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplyCouponRequest {
    private Long userId;
    private String couponCode;
}

