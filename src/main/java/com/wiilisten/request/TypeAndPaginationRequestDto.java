package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiilisten.utils.ApplicationConstants;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeAndPaginationRequestDto {
	
	@JsonProperty("type")
	private String type;
	
	@JsonProperty(value = ApplicationConstants.PAGE_NUMBER)
	private Integer pageNumber;
	
	@JsonProperty(value = ApplicationConstants.SORT_BY)
	private String sortBy;
	
	@JsonProperty(value = ApplicationConstants.SORT_TYPE)
	private String sortType;
	
	@JsonProperty(value = ApplicationConstants.PAGE_SIZE)
	private Integer pageSize;

}
