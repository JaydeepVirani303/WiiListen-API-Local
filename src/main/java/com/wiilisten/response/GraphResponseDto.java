package com.wiilisten.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphResponseDto {
	
	private Double averageIncome;
	
	private List<Object> allValues;
	
}
