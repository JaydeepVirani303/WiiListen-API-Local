package com.wiilisten.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class JoinCallDto {
	
	private String channelId;
	
	private Long bookingId;
	
	private Boolean isSendNotification;
	
	private Long listenerId;
	
	private String callType;
	
}
