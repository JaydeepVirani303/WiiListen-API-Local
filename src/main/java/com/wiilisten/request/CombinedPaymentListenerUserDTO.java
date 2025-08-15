package com.wiilisten.request;

import com.wiilisten.entity.BookedCalls;
import lombok.Builder;
import lombok.Data;

import java.time.format.DateTimeFormatter;

@Data
@Builder
public class CombinedPaymentListenerUserDTO {

    private String listenerUserName;
    private String callerUserName;
    private Double paymentToListener; //payment made to listener
    private Double wiilistenCommission; //commission for Wiilisten
    private Double callerDiscount; // caller discount
    private String listenerLocation; // location of payment recipient
    private String disbursementProcessedDate; // booking date time
    private String callerLocation; // location of caller
    private String paymentDate; // missing in model unless you reuse existing date (currently call acceptDateTime consider as a paymentDate)
    private String callType; // ON_DEMAND / SCHEDULE
    private Long durationInMinutes;

    public static CombinedPaymentListenerUserDTO toDTO(BookedCalls bookedCall) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return CombinedPaymentListenerUserDTO.builder()
                .listenerUserName(bookedCall.getListener() != null ? bookedCall.getListener().getUserName() : null)
                .callerUserName(
                        bookedCall.getCaller() != null && bookedCall.getCaller().getUser() != null
                                ? bookedCall.getCaller().getUser().getCallName()
                                : null
                )
                .paymentToListener(bookedCall.getListener() != null && bookedCall.getListener().getTotalEarning() != null && bookedCall.getListener().getTotalCommission() != null
                        ? bookedCall.getListener().getTotalEarning() - bookedCall.getListener().getTotalCommission()
                        : 0)
                .wiilistenCommission(bookedCall.getAdminCommissionRate())
                .callerDiscount(null)
                .listenerLocation(bookedCall.getListener() != null && bookedCall.getListener().getUser() != null
                        ? bookedCall.getListener().getUser().getTimeZone()
                        : null)
                .disbursementProcessedDate(bookedCall.getBookingDateTime() != null
                        ? bookedCall.getBookingDateTime().format(formatter)
                        : null)
                .callerLocation(bookedCall.getCaller() != null && bookedCall.getCaller().getUser() != null
                        ? bookedCall.getCaller().getUser().getTimeZone()
                        : null)
                .paymentDate(bookedCall.getCallerJoinedAt() != null
                        ? bookedCall.getCallerJoinedAt().format(formatter)
                        : null)
                .callType(bookedCall.getType())
                .durationInMinutes(bookedCall.getDurationInMinutes())
                .build();
    }
}
