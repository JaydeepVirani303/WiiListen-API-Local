package com.wiilisten.request;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListnerProfileDTO {
	
	private String username;
	
	private String email;
	
	private String callName;
	
	private String quote;
	
	private String img;
	
	private String gender;
	
	private String education;
	
	private Date dob;
	
	private String language;
	
	private String location;
	
	private String videoProgress;
	
	private String w9Form;
	
	private String idproof;
	
	private Boolean premiumCall;
	
	private String maxDuration;
	
	private String price;
	
	private List<AvailabilityDTO> availability;
	
	private String accountName;
	
	private String abaNumber;
	
	private String accountNumber;
	
	private String accountType;
	
	private List<Long> languages;
	
	private String timeZone;
}
