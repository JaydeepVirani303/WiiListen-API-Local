package com.wiilisten.controller.api.admin;

import com.wiilisten.controller.BaseController;
import com.wiilisten.request.ApplyCouponRequest;
import com.wiilisten.request.CouponsRequestDTO;
import com.wiilisten.request.ValidCouponRequest;
import com.wiilisten.response.CouponsResponseDTO;
import com.wiilisten.response.ValidCouponResponse;
import com.wiilisten.service.CouponsService;
import com.wiilisten.utils.ApplicationURIConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN + ApplicationURIConstants.COUPON)
@RequiredArgsConstructor
public class ApiV1AdminCouponsController extends BaseController {

    private final CouponsService couponsService;

    // --- Create Coupon ---
    @PostMapping(ApplicationURIConstants.ADD)
    public ResponseEntity<?> createCoupon(@RequestBody CouponsRequestDTO requestDTO) {
        CouponsResponseDTO createdCoupon = couponsService.createCoupon(requestDTO);
        if (createdCoupon == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", "Coupon code already exists",
                            "couponCode", requestDTO.getCouponCode()
                    ));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }

    // --- Get All Coupons ---
    @GetMapping
    public ResponseEntity<List<CouponsResponseDTO>> getAllCoupons() {
        return ResponseEntity.ok(couponsService.getAllCoupons());
    }

    // --- Get Coupon by Code ---
//    @GetMapping("/{code}")
//    public ResponseEntity<?> getCouponByCode(@PathVariable String code) {
//        CouponsResponseDTO coupon = couponsService.getCouponByCode(code);
//        if (coupon == null) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                    .body(Map.of(
//                            "message", "Coupon not found",
//                            "couponCode", code
//                    ));
//        }
//        return ResponseEntity.ok(coupon);
//    }

    // --- Get Coupon by Id ---
    @GetMapping("/{id}")
    public ResponseEntity<?> getCouponById(@PathVariable Long id) {
        CouponsResponseDTO coupon = couponsService.getCouponById(id);
        if (coupon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message", "Coupon not found"
                    ));
        }
        return ResponseEntity.ok(coupon);
    }

    // --- Update Coupon ---
    @PutMapping(ApplicationURIConstants.UPDATE + "/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody CouponsRequestDTO requestDTO) {
        return couponsService.updateCoupon(id, requestDTO)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Coupon not found with id: " + id)));
    }


    // --- Delete Coupon ---
    @DeleteMapping(ApplicationURIConstants.DELETE + "/{id}")
    public ResponseEntity<Object> deleteCoupon(@PathVariable Long id) {
        Map<Boolean, String> result = couponsService.deleteCoupon(id);
        boolean isSuccess = result.containsKey(true);
        String message = isSuccess ? result.get(true) : result.get(false);
        if (isSuccess) {
            return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(message));
        } else {
            return ResponseEntity.ok(getCommonServices().generateGenericFailResponse(message));
        }
    }

    // --- Delete Coupon ---
    @DeleteMapping(ApplicationURIConstants.STATUS + "/{id}")
    public ResponseEntity<Map<String, String>> softDelete(@PathVariable Long id) {
        boolean deleted = couponsService.softDelete(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Coupon De-activated successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Coupon not found with id: " + id));
        }
    }

    // --- Apply Coupon ---
    @PostMapping(ApplicationURIConstants.APPLY)
    public ResponseEntity<String> applyCoupon(@RequestBody ApplyCouponRequest request) {
        String message = couponsService.applyCoupon(request);
        return ResponseEntity.ok(message);
    }
}
