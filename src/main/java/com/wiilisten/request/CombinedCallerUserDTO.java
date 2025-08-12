package com.wiilisten.request;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CombinedCallerUserDTO {

    // CallerProfile fields
    private Long callerId;
    private Long points;
    private String referralCode;
    private String searchSubscriptionStatus;
    private Boolean active;

    // User fields
    private Long userId;
    private String email;
    private String role;
    private String callName;
    private String profilePicture;
    private String countryCode;
    private String contactNumber;
    private Long totalReviews;
    private Double currentRating;
    private Boolean isProfileSet;
    private Boolean isEmailVerified;
    private Boolean isSuspended;
    private String timeZone;

    public static CombinedCallerUserDTO toDTO(CallerProfile callerProfile) {
        User user = callerProfile.getUser();

        return CombinedCallerUserDTO.builder()
                .callerId(callerProfile.getId())
                .points(callerProfile.getPoints())
                .referralCode(callerProfile.getReferralCode())
                .searchSubscriptionStatus(callerProfile.getSearchSubscriptionStatus())
                .active(callerProfile.getActive())

                // Null-safe user mapping
                .userId(user != null ? user.getId() : null)
                .email(user != null ? user.getEmail() : null)
                .role(user != null ? user.getRole() : null)
                .callName(user != null ? user.getCallName() : null)
                .profilePicture(user != null ? user.getProfilePicture() : null)
                .countryCode(user != null ? user.getCountryCode() : null)
                .contactNumber(user != null ? user.getContactNumber() : null)
                .totalReviews(user != null ? user.getTotalReviews() : null)
                .currentRating(user != null ? user.getCurrentRating() : null)
                .isProfileSet(user != null ? user.getIsProfileSet() : null)
                .isEmailVerified(user != null ? user.getIsEmailVerified() : null)
                .isSuspended(user != null ? user.getIsSuspended() : null)
                .timeZone(user != null ? user.getTimeZone() : null)
                .build();
    }
}
