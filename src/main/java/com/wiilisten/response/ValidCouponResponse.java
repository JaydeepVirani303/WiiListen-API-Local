package com.wiilisten.response;

import com.wiilisten.entity.Coupons;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidCouponResponse {
    private String message;
    private Coupons coupons;
}