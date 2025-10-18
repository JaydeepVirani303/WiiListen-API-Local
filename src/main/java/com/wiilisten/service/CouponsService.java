package com.wiilisten.service;

import com.wiilisten.entity.Coupons;
import com.wiilisten.request.ApplyCouponRequest;
import com.wiilisten.request.CouponsRequestDTO;
import com.wiilisten.request.ValidCouponRequest;
import com.wiilisten.response.CouponsResponseDTO;
import com.wiilisten.response.ValidCouponResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CouponsService {

    CouponsResponseDTO createCoupon(CouponsRequestDTO requestDTO);

    List<CouponsResponseDTO> getAllCoupons();

    CouponsResponseDTO getCouponByCode(String code);

    Optional<CouponsResponseDTO> updateCoupon(Long id, CouponsRequestDTO requestDTO);

    Map<Boolean, String> deleteCoupon(Long id);

    boolean softDelete(Long id);

    String applyCoupon(ApplyCouponRequest request);

    boolean isUsedCoupons(Long userId, Long couponId);

    Coupons checkValidCoupon(ValidCouponRequest request);

    CouponsResponseDTO getCouponById(Long id);
}
