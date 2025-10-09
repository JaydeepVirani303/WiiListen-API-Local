package com.wiilisten.service;

import com.wiilisten.request.ApplyCouponRequest;
import com.wiilisten.request.CouponsRequestDTO;
import com.wiilisten.response.CouponsResponseDTO;

import java.util.List;
import java.util.Optional;

public interface CouponsService {

    CouponsResponseDTO createCoupon(CouponsRequestDTO requestDTO);

    List<CouponsResponseDTO> getAllCoupons();

    CouponsResponseDTO getCouponByCode(String code);

    Optional<CouponsResponseDTO> updateCoupon(Long id, CouponsRequestDTO requestDTO);

    boolean deleteCoupon(Long id);

    String applyCoupon(ApplyCouponRequest request);
}
