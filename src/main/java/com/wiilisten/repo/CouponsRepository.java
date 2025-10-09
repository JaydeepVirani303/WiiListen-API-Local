package com.wiilisten.repo;

import com.wiilisten.entity.Coupons;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponsRepository extends JpaRepository<Coupons, Long> {
    Optional<Coupons> findByCouponCode(String couponCode);
}
