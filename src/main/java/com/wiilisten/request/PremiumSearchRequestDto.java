package com.wiilisten.request;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiilisten.utils.ApplicationConstants;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PremiumSearchRequestDto {

	@JsonProperty(value = ApplicationConstants.PAGE_NUMBER)
	private Integer pageNumber;

	@JsonProperty(value = ApplicationConstants.SORT_BY)
	private String sortBy;

	@JsonProperty(value = ApplicationConstants.SORT_TYPE)
	private String sortType;

	@JsonProperty(value = ApplicationConstants.PAGE_SIZE)
	private Integer pageSize;
	
	@JsonProperty("name")
	private String name;

	@JsonProperty("location")
	private String location;

	@JsonProperty("education")
	private String education;

	@JsonProperty("gender")
	private String gender;
	
	@JsonProperty("start_date")
	private LocalDate startDate;
	
	@JsonProperty("end_date")
	private LocalDate endDate;

	@JsonProperty("ids")
	private List<Long> ids;

}
