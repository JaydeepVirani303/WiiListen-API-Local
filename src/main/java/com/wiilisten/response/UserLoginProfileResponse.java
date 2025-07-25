package com.wiilisten.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wiilisten.request.AvailabilityDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginProfileResponse {
	
private String callName;
	
	private String quote;
	
	private String img;
	
	private String gender;
	
	private String education;
	
	private String dob;
	
	private String language;
	
	private String location;
	
	private String videoProgress;
	
	private Boolean premiumCall;
	
	private String role;
	
	private String email;
	
	private String name;
	
	private Long id;
	
	private String w9form;
	
	private String idproof;
	
	private Boolean isOnline;
	
	private String step;
	
	private String maxDuration;
	
	private String price;
	
	private List<AvailabilityDTO> availability;
	
	private String accountName;
	
	private String abaNumber;
	
	private String accountNumber;
	
	private String accountType;
	
	private String token;
	
	private Boolean otpVerify;
	
	private Boolean stepCompleted;

}
