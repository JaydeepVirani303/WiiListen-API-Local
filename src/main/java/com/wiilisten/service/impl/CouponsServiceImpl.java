package com.wiilisten.service.impl;

import com.wiilisten.entity.Coupons;
import com.wiilisten.entity.UsedCoupon;
import com.wiilisten.repo.CouponsRepository;
import com.wiilisten.repo.UsedCouponRepository;
import com.wiilisten.request.ApplyCouponRequest;
import com.wiilisten.request.CouponsRequestDTO;
import com.wiilisten.request.ValidCouponRequest;
import com.wiilisten.response.CouponsResponseDTO;
import com.wiilisten.response.ValidCouponResponse;
import com.wiilisten.service.CouponsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponsServiceImpl implements CouponsService {

    private final CouponsRepository couponsRepository;

    private final UsedCouponRepository usedCouponRepository;

    @Override
    public CouponsResponseDTO createCoupon(CouponsRequestDTO requestDTO) {
        boolean exists = couponsRepository.existsByCouponCodeAndActive(requestDTO.getCouponCode(), true);
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
    public CouponsResponseDTO getCouponById(Long id) {
        Optional<Coupons> coupon = couponsRepository.findById(id);
        return coupon.map(this::mapToDTO).orElse(null);
    }

    @Override
    public Optional<CouponsResponseDTO> updateCoupon(Long id, CouponsRequestDTO requestDTO) {
        return couponsRepository.findById(id).map(existing -> {

            if (requestDTO.getCouponCode() != null)
                existing.setCouponCode(requestDTO.getCouponCode());

            if (requestDTO.getStartDate() != null)
                existing.setStartDate(requestDTO.getStartDate());

            if (requestDTO.getEndDate() != null)
                existing.setEndDate(requestDTO.getEndDate());

            if (requestDTO.getCouponType() != null)
                existing.setCouponType(requestDTO.getCouponType());

            if (requestDTO.getCouponAmount() != null)
                existing.setCouponAmount(requestDTO.getCouponAmount());

            if (requestDTO.getActive() != null)
                existing.setActive(requestDTO.getActive());

            existing.setUpdatedAt(LocalDateTime.now());
            return mapToDTO(couponsRepository.save(existing));
        });
    }


//    @Override
//    public boolean deleteCoupon(Long id) {
//        if (couponsRepository.existsById(id)) {
//            couponsRepository.deleteById(id);
//            return true;
//        }
//        return false;
//    }

    @Override
    public Map<Boolean, String> deleteCoupon(Long id) {
        Map<Boolean, String> map = new HashMap<>();
        // Check if coupon exists
        Optional<Coupons> optionalCoupon = couponsRepository.findById(id);
        if (optionalCoupon.isEmpty()) {
            map.put(false, "Coupon not found with id: " + id);
            return map;
        }

        // Check if coupon has been used
        boolean isUsed = usedCouponRepository.existsByCouponId(id);
        if (isUsed) {
            map.put(false, "This code is already used, so you can't delete it.");
            return map;
        }

        // Proceed with delete if not used
        couponsRepository.deleteById(id);
        map.put(true, "Coupon deleted successfully");
        return map;
    }

    @Override
    public boolean softDelete(Long id) {
        return couponsRepository.findById(id)
                .map(coupon -> {
                    coupon.setActive(false);
                    coupon.setUpdatedAt(LocalDateTime.now());
                    couponsRepository.save(coupon);
                    return true;
                })
                .orElse(false);
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
        Optional<Coupons> optionalCoupon = couponsRepository.findById(request.getCouponId());
        if (optionalCoupon.isEmpty()) {
            return "The coupon Id '" + request.getCouponId() + "' does not exist.";
        }

        Coupons coupon = optionalCoupon.get();

        if (!coupon.getActive()) {
            return "This coupon is currently inactive and cannot be applied.";
        }

        if (coupon.getEndDate().isBefore(LocalDateTime.now())) {
            return "This coupon has expired and can no longer be used.";
        }

        boolean alreadyUsed = usedCouponRepository.findByUserIdAndCouponId(request.getUserId(), coupon.getId()).isPresent();
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
    public Coupons checkValidCoupon(ValidCouponRequest request) {
        // Fetch active coupon directly
        Optional<Coupons> optionalCoupon = couponsRepository.findByCouponCodeAndActive(request.getCouponCode(), true);

        if (optionalCoupon.isEmpty()) {
            return null; // No active coupon found
        }

        Coupons coupon = optionalCoupon.get();
        LocalDateTime now = LocalDateTime.now();

        // ✅ Check valid date range (startDate <= now <= endDate)
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            return null; // Coupon not yet started or expired
        }

        // ✅ Check if user already used this coupon
        boolean alreadyUsed = usedCouponRepository
                .findByUserIdAndCouponId(request.getUserId(), coupon.getId())
                .isPresent();

        if (alreadyUsed) {
            return null; // Already used by this user
        }

        // ✅ Coupon is valid
        return coupon;
    }


}


//sing up -> url pass karo chho F.D
//url lo pwd + password
//
//zip -> ma decript +
//amin zip ma password
//
//admin ma decript and password