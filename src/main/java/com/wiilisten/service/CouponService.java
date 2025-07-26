package com.wiilisten.service;

import com.wiilisten.entity.Coupon;

public interface CouponService extends BaseService<Coupon, Long>{
	
	Coupon findByCodeAndActiveTrue(String code);

}
