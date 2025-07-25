package com.wiilisten.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class EndCallRequestDto {
	private Long bookingId;
	private String callStatus;
	private String reason;
	private String duration;
	private String callType;
	private String notes;
}
