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
    private Double paymentToListener;
    private Double wiilistenCommission;
    private Double callerDiscount;
    private String listenerLocation;
    private String disbursementProcessedDate; // missing in model
    private String callerLocation; // missing in model unless User has it
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
                .paymentToListener(bookedCall.getPayableAmount())
                .wiilistenCommission(bookedCall.getAdminCommissionRate())
                .callerDiscount(bookedCall.getDiscountValue())
                .listenerLocation(bookedCall.getListener() != null ? bookedCall.getListener().getLocation() : null)
                .disbursementProcessedDate(null)
                .callerLocation(null)
                .paymentDate(bookedCall.getAcceptedDateTime() != null
                        ? bookedCall.getAcceptedDateTime().format(formatter)
                        : null)
                .callType(bookedCall.getType())
                .durationInMinutes(bookedCall.getDurationInMinutes())
                .build();
    }
}
