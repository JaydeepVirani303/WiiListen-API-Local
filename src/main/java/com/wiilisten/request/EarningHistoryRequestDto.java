package com.wiilisten.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarningHistoryRequestDto {
	
	@JsonProperty("location")
	private String location;
	
	@JsonProperty("age")
	private Integer age;
	
	@JsonProperty("education")
	private String education;
	
	@JsonProperty("gender")
	private String gender;
	
	@JsonProperty("language")
	private String language;
	
	@JsonProperty("start_date")
	private Date startDate;
	
	@JsonProperty("end_date")
	private Date endDate;

}
