package com.wiilisten.response;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CallStartResponseDto {

	private BookedCallDetailsDto bookedCallDto;
	private String token;
	private String channel_name;
	private Long bookingId;
	private Long listenerId;
	private String profilePicture;
	private String reciverName;
	private String notes;
}
