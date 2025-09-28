package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.wiilisten.utils.ApplicationConstants;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationAndSortingDetails {

	@JsonProperty(ApplicationConstants.PAGE_NUMBER)
	private Integer pageNumber;

	@JsonProperty(ApplicationConstants.SORT_BY)
	private String sortBy;

	@JsonProperty(ApplicationConstants.SORT_TYPE)
	private String sortType;

	@JsonProperty(ApplicationConstants.PAGE_SIZE)
	private Integer pageSize;

	@JsonProperty(ApplicationConstants.REQUESTED_TIMEZONE)
	private String requestedTimeZone;
}
