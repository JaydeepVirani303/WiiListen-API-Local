package com.wiilisten.service.impl;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.Coupon;
import com.wiilisten.service.CouponService;

import jakarta.annotation.PostConstruct;

@Service
public class CouponServiceImpl extends BaseServiceImpl<Coupon, Long> implements CouponService{
	

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getCouponRepository();
	}

	@Override
	public Coupon findByCodeAndActiveTrue(String code) {
		return getDaoFactory().getCouponRepository().findByCodeAndActiveTrue(code);
	}

}
