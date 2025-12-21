package com.wiilisten.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarningResponseDto {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("payment_status")
	private String paymentStatus; // DEPOSITED/PROCESSED

	@JsonProperty("amount")
	private Double amount;

	@JsonProperty("reason")
	private String reason; // CALL/ADVERTISEMENT/REFUND

	@JsonProperty("metadata")
	private String metadata;

	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("updated_at")
	private String updatedAt;

}
