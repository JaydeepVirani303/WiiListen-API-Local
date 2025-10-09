package com.wiilisten.service.impl;

import com.wiilisten.entity.Coupons;
import com.wiilisten.entity.UsedCoupon;
import com.wiilisten.repo.CouponsRepository;
import com.wiilisten.repo.UsedCouponRepository;
import com.wiilisten.request.ApplyCouponRequest;
import com.wiilisten.request.CouponsRequestDTO;
import com.wiilisten.response.CouponsResponseDTO;
import com.wiilisten.service.CouponsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponsServiceImpl implements CouponsService {

    private final CouponsRepository couponsRepository;

    private final UsedCouponRepository usedCouponRepository;

    @Override
    public CouponsResponseDTO createCoupon(CouponsRequestDTO requestDTO) {
        boolean exists = couponsRepository.findByCouponCode(requestDTO.getCouponCode()).isPresent();
        if (exists) return null;

        Coupons coupon = mapToEntity(requestDTO);
        coupon.setUsedCount(0);
        coupon.setActive(requestDTO.getActive() != null ? requestDTO.getActive() : true);
        coupon.setCreatedAt(requestDTO.getCreatedAt());
        coupon.setUpdatedAt(requestDTO.getUpdatedAt());
        return mapToDTO(couponsRepository.save(coupon));
    }

    @Override
    public List<CouponsResponseDTO> getAllCoupons() {
        return couponsRepository.findAll().stream().map(this::mapToDTO).toList();
    }

    @Override
    public CouponsResponseDTO getCouponByCode(String code) {
        Optional<Coupons> coupon = couponsRepository.findByCouponCode(code);
        return coupon.map(this::mapToDTO).orElse(null);
    }

    @Override
    public Optional<CouponsResponseDTO> updateCoupon(Long id, CouponsRequestDTO requestDTO) {
        return couponsRepository.findById(id).map(existing -> {
            existing.setCouponCode(requestDTO.getCouponCode());
            existing.setStartDate(requestDTO.getStartDate());
            existing.setEndDate(requestDTO.getEndDate());
            existing.setCouponType(requestDTO.getCouponType());
            existing.setCouponAmount(requestDTO.getCouponAmount());
            existing.setActive(requestDTO.getActive());
            existing.setUpdatedAt(LocalDateTime.now());
            return mapToDTO(couponsRepository.save(existing));
        });
    }

    @Override
    public boolean deleteCoupon(Long id) {
        if (couponsRepository.existsById(id)) {
            couponsRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- Mapping Methods ---
    private CouponsResponseDTO mapToDTO(Coupons coupon) {
        return CouponsResponseDTO.builder()
                .id(coupon.getId())
                .couponCode(coupon.getCouponCode())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .couponType(coupon.getCouponType())
                .couponAmount(coupon.getCouponAmount())
                .usedCount(coupon.getUsedCount())
                .active(coupon.getActive())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }

    private Coupons mapToEntity(CouponsRequestDTO dto) {
        return Coupons.builder()
                .couponCode(dto.getCouponCode())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .couponType(dto.getCouponType())
                .couponAmount(dto.getCouponAmount())
                .active(dto.getActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    @Override
    public String applyCoupon(ApplyCouponRequest request) {
        Optional<Coupons> optionalCoupon = couponsRepository.findByCouponCode(request.getCouponCode());
        if (optionalCoupon.isEmpty()) {
            return "The coupon code '" + request.getCouponCode() + "' does not exist.";
        }

        Coupons coupon = optionalCoupon.get();

        if (!coupon.getActive()) {
            return "This coupon is currently inactive and cannot be applied.";
        }

        if (coupon.getEndDate().isBefore(LocalDateTime.now())) {
            return "This coupon has expired and can no longer be used.";
        }

        boolean alreadyUsed = usedCouponRepository.findByUserIdAndCoupon(request.getUserId(), coupon).isPresent();
        if (alreadyUsed) {
            return "You have already used this coupon. Each coupon can be used only once per user.";
        }

        UsedCoupon usedCoupon = UsedCoupon.builder()
                .userId(request.getUserId())
                .coupon(coupon)
                .usedAt(LocalDateTime.now())
                .build();
        usedCouponRepository.save(usedCoupon);

        coupon.setUsedCount(coupon.getUsedCount() + 1);
        couponsRepository.save(coupon);

        return "Coupon applied successfully! Enjoy your discount.";
    }

    @Override
    public boolean checkValidCoupon(ApplyCouponRequest request) {
        Optional<Coupons> optionalCoupon = couponsRepository.findByCouponCode(request.getCouponCode());
        if (optionalCoupon.isEmpty()) {
            return false;
        }
        Coupons coupon = optionalCoupon.get();
        if (!coupon.getActive()) {
            return false;
        }
        if (coupon.getEndDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        boolean alreadyUsed = usedCouponRepository.findByUserIdAndCoupon(request.getUserId(), coupon).isPresent();
        if (alreadyUsed) {
            return false;
        }
        return true;
    }
}
