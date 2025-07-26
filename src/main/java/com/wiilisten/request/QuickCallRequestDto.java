package com.wiilisten.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuickCallRequestDto {
	
	@JsonProperty("listenerUserIds")
    private List<Long> listenerUserIds;

    @JsonProperty("bookingId")
    private String bookingId;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("callStatus")
    private String callStatus;

}
