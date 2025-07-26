package com.wiilisten.repo;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.Coupon;

@Repository
public interface CouponRepository extends BaseRepository<Coupon, Long>{
	
	Coupon findByCodeAndActiveTrue(String code);

}
