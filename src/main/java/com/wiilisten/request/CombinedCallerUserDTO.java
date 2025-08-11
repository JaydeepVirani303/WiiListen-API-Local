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
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .callName(user.getCallName())
                .profilePicture(user.getProfilePicture())
                .countryCode(user.getCountryCode())
                .contactNumber(user.getContactNumber())
                .totalReviews(user.getTotalReviews())
                .currentRating(user.getCurrentRating())
                .isProfileSet(user.getIsProfileSet())
                .isEmailVerified(user.getIsEmailVerified())
                .isSuspended(user.getIsSuspended())
                .timeZone(user.getTimeZone())
                .build();
    }
}
