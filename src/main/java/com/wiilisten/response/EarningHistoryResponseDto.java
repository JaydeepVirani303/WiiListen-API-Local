package com.wiilisten.response;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarningHistoryResponseDto {
	
	@JsonProperty("start_date")
	private String startDate;
	
	@JsonProperty("end_date")
	private String endDate;
	
	@JsonProperty("total_income")
    private Double totalIncome;
	
	@JsonProperty("average_income")
	private Double averageIncome;
	
	@JsonProperty("earnings")
	private List<EarningResponseDto> earnings=new ArrayList<>();

}
