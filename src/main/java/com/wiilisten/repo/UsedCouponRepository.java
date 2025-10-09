package com.wiilisten.repo;

import com.wiilisten.entity.Coupons;
import com.wiilisten.entity.UsedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsedCouponRepository extends JpaRepository<UsedCoupon, Long> {
    Optional<UsedCoupon> findByUserIdAndCoupon(Long userId, Coupons coupon);
}