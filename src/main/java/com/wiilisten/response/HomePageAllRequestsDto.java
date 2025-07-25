package com.wiilisten.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomePageAllRequestsDto {
	
	@JsonProperty("pending_request_list")
	private List<BookedCallDetailsDto> pendingRequestList;
	
	@JsonProperty("upcoming_call_list")
	private List<BookedCallDetailsDto> upcomigCallList;
	
	@JsonProperty("favourite_listener_list")
	private List<FavoriteListenerDetailsDto> favouriteListenerList;
	
	@JsonProperty("sponser_listener_list")
	private List<FavoriteListenerDetailsDto> sponserListenerList;
}
