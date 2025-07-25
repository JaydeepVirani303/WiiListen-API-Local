package com.wiilisten.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FavoriteListenerDetailsDto {

//	private Long id;
	
	@JsonProperty("listener_id")
	private Long listenerId;
	
	@JsonProperty("listener_user_id")
	private Long listenerUserId;
	
	@JsonProperty("profile_picture")
	private String profilePicture;
	
	@JsonProperty("app_active_status")
	private Boolean appActiveStatus;
	
	@JsonProperty("current_ratings")
	private Double currentRatings;
	
	@JsonProperty("call_name")
	private String callName;
	
	@JsonProperty("user_name")
	private String userName;
	
	@JsonProperty("rate_per_minute_for_schedule")
	private Double ratePerMinuteForSchedule;
	
	@JsonProperty("rate_per_minute_for_ondemand")
	private Double ratePerMinuteForOnDemand;
	
	@JsonProperty("max_duration")
	private String maxDuration ;
}
