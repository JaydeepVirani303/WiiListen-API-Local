package com.wiilisten.response;

import java.util.List;

import com.amazonaws.services.dynamodbv2.xspec.B;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsResponseDto {
	
	private List<CallerProfileResponseDto> callerDetails;

	private List<BookedCallDetailsDto> bookedCallDetails;
	
	private Long count;

}
