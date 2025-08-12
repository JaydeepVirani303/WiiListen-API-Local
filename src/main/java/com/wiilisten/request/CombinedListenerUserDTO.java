package com.wiilisten.request;

import com.wiilisten.entity.Language;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class CombinedListenerUserDTO {

    // ListenerProfile fields (non-sensitive)
    private Long listenerId;
    private List<String> languages; // Language names only
    private String userName;
    private String notableQuote;
    private String trainingVideoProgress;
    private String gender;
    private String education;
    private String dateOfBirth; // <-- now String, not LocalDate
    private String location;
    private Double ratePerMinute;
    private String profileStatus;
    private Double totalEarning;
    private Long totalCompletedMinutes;
    private Long totalAttendedCalls;
    private Long points;
    private Boolean active;
    private Date createdAt;
    private Double totalCommission;

    // User fields (non-sensitive)
    private String callName;
    private String profilePicture;
    private Double currentRating;
    private Long totalReviews;
    private String timeZone;

    public static CombinedListenerUserDTO toDTO(ListenerProfile listenerProfile) {
        User user = listenerProfile.getUser();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return CombinedListenerUserDTO.builder()
                .listenerId(listenerProfile.getId())
                .languages(listenerProfile.getLanguages() != null
                        ? listenerProfile.getLanguages().stream()
                        .map(Language::getName)
                        .collect(Collectors.toList())
                        : null)
                .userName(listenerProfile.getUserName())
                .notableQuote(listenerProfile.getNotableQuote())
                .trainingVideoProgress(listenerProfile.getTrainingVideoProgress())
                .gender(listenerProfile.getGender())
                .education(listenerProfile.getEducation())
                .dateOfBirth(listenerProfile.getDateOfBirth() != null
                        ? listenerProfile.getDateOfBirth().format(formatter)
                        : null)
                .location(listenerProfile.getLocation())
                .ratePerMinute(listenerProfile.getRatePerMinute())
                .profileStatus(listenerProfile.getProfileStatus())
                .totalEarning(listenerProfile.getTotalEarning())
                .totalCompletedMinutes(listenerProfile.getTotalCompletedMinutes())
                .totalAttendedCalls(listenerProfile.getTotalAttendedCalls())
                .points(listenerProfile.getPoints())
                .active(listenerProfile.getActive())
                .createdAt(listenerProfile.getCreatedAt())
                .totalCommission(listenerProfile.getTotalCommission())

                .callName(user != null ? user.getCallName() : null)
                .profilePicture(user != null ? user.getProfilePicture() : null)
                .currentRating(user != null ? user.getCurrentRating() : null)
                .totalReviews(user != null ? user.getTotalReviews() : null)
                .timeZone(user != null ? user.getTimeZone() : null)
                .build();
    }
}
