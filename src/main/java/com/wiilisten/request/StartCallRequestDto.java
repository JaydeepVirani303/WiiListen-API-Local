package com.wiilisten.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartCallRequestDto {
	
	private Long callerId;
	
	private Long listenerId;
	
	private String callType;
	
	private Long bookedCallId;
	
	private String callMaxDuration;

}
