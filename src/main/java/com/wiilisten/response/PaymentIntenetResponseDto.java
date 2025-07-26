package com.wiilisten.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.stripe.model.PaymentIntent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PaymentIntenetResponseDto {
 
	private String payamentIntentId;
	private String payamentIntenetClientSecretKey;
	private String customerId;
	private String ephemeralKey;
	private String stripeSecretKey;
	private String publishableKey;
	private Boolean paymentSuccess;
	private Long listenerId;
	private Long bookingId;
	private Double finalAmount;
	private String callMaxDuration;
	@JsonIgnore
	private PaymentIntent paymentIntent;
	

}
