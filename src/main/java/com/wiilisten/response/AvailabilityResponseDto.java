package com.wiilisten.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wiilisten.request.TimeSlotDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AvailabilityResponseDto {
	
	private LocalDate date;
	private List<TimeSlotDto> availbleTime;
	private ZonedDateTime startTime;
	private ZonedDateTime endTime;

}
