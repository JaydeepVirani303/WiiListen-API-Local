package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wiilisten.utils.ApplicationConstants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubCategoryPaginationRequestDto {
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty(value = ApplicationConstants.PAGE_NUMBER)
	private Integer pageNumber;
	
	@JsonProperty(value = ApplicationConstants.SORT_BY)
	private String sortBy;
	
	@JsonProperty(value = ApplicationConstants.SORT_TYPE)
	private String sortType;
	
	@JsonProperty(value = ApplicationConstants.PAGE_SIZE)
	private Integer pageSize;

}
