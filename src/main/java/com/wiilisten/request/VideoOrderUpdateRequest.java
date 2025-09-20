package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoOrderUpdateRequest {
    private Long id;

    @JsonProperty("order_number")
    private Integer orderNumber;
}