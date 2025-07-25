package com.wiilisten.request;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class TimeSlotDto {

	private LocalTime startTime;
	private LocalTime endTime;

	public TimeSlotDto(LocalTime startTime, LocalTime endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public String toString() {
		return startTime + " - " + endTime;
	}

	public boolean overlaps(TimeSlotDto other) {
		return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
	}

	public TimeSlotDto() {
		super();
	}

}