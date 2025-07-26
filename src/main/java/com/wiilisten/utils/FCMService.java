package com.wiilisten.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.wiilisten.controller.api.ApiV1HomeController;
import com.wiilisten.service.FirebaseAuthService;

@Service
public class FCMService {

	@Value("${fcm.server-key}")
	private String fcmServerKey; // Your FCM server key obtained from Firebase console

	@Value("${apns.teamId}")
	private String apnsTeamId;

	@Value("${apns.keyId}")
	private String apnsKeyId;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private FirebaseAuthService firebaseAuthService;

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1HomeController.class);

	private static final String FCM_ENDPOINT = "https://fcm.googleapis.com/v1/projects/wiilisten-ed0fd/messages:send";

	// public void sendPushNotification(String deviceToken, Map<String, String>
	// payload) throws IOException {
	// // Get the OAuth2 access token
	// String accessToken = firebaseAuthService.getAccessToken();

	// // deviceToken="BE92B2B0-1978-443C-9034-28045051A2F4"; //ios
	// // deviceToken="FA1F41F8-35F1-4383-A5BA-78A96675B808"; //ios
	// // deviceToken = "6c860282a89dfdf4"; // Android

	// // Create FCM v1 payload
	// Map<String, Object> fcmPayload = Map.of("message", Map.of("token",
	// deviceToken,
	// "data", payload, "notification", Map.of("title", payload.get("title"),
	// "body", payload.get("body"))));

	// System.err.println("ANDROID PUSH PAYLOAD ------->>>>>>> " + payload);

	// // Set HTTP headers with the OAuth2 token
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON);
	// headers.setBearerAuth(accessToken); // Use OAuth2 token here

	// System.err.println("DIVECE TOKEN >>>>> " + deviceToken);

	// HttpEntity<Map<String, Object>> request = new HttpEntity<>(fcmPayload,
	// headers);

	// System.err.println("FCM PAYLOAD>>>>> " + request);

	// // Send POST request to FCM endpoint
	// RestTemplate restTemplate = new RestTemplate();
	// String response = restTemplate.exchange(FCM_ENDPOINT, HttpMethod.POST,
	// request, String.class).getBody();

	// System.err.println("RESPONSE OF FCM>>>>>>>>>>> " + response);

	// LOGGER.info("Successfully sent message: {}", response);
	// }

	public void sendPushNotification(String deviceToken, Map<String, String> payload) throws IOException {
		// Get the OAuth2 access token
		String accessToken = firebaseAuthService.getAccessToken();

		// Customize FCM v1 payload to include notification for iOS and data for Android
		Map<String, Object> fcmPayload = Map.of(
				"message", Map.of(
						"token", deviceToken,
						"data", payload, // Data payload for Android
//						"notification", Map.of( // Notification payload for iOS
//								"title", payload.get("title"),
//								"body", payload.get("body")),
						"android", Map.of( // Android-specific configuration
								"priority", "high",
//								"notification", Map.of(
//										"title", payload.get("title"),
//										"body", payload.get("body")),
								"data", Map.of(
										"title", payload.get("title"),
										"body", payload.get("body")))));

		System.err.println("CUSTOMIZED PUSH PAYLOAD ------->>>>>>> " + fcmPayload);

		// Set HTTP headers with the OAuth2 token
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken); // Use OAuth2 token here

		System.err.println("DEVICE TOKEN >>>>> " + deviceToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(fcmPayload, headers);

		System.err.println("FCM PAYLOAD>>>>>  " + request);

		// Send POST request to FCM endpoint
		RestTemplate restTemplate = new RestTemplate();
		try {
			String response = restTemplate.exchange(FCM_ENDPOINT, HttpMethod.POST, request, String.class).getBody();
			System.err.println("RESPONSE OF FCM>>>>>>>>>>> " + response);
			LOGGER.info("Successfully sent message: {}", response);
		} catch (HttpClientErrorException e) {
			if (e.getStatusCode().value() == 404 && e.getResponseBodyAsString().contains("UNREGISTERED")) {
				LOGGER.error("Device token is unregistered or invalid: {}", deviceToken);
			} else {
				LOGGER.error("Error sending push notification: {}", e.getMessage());
			}
		}
	}

	public void sendPushNotification1(String deviceToken, Map<String, String> payload) throws IOException {
		String accessToken = firebaseAuthService.getAccessToken();

		Map<String, Object> fcmPayload = Map.of(
				"message", Map.of(
						"token", deviceToken,
						"notification", Map.of(
								"title", payload.get("title"),
								"body", payload.get("body")),
						"apns", Map.of(
								"payload", Map.of(
										"aps", Map.of(
												"alert", Map.of(
														"title", payload.get("title"),
														"body", payload.get("body")),
												"sound", "default")))));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(accessToken);

		HttpEntity<Map<String, Object>> request = new HttpEntity<>(fcmPayload, headers);
		RestTemplate restTemplate = new RestTemplate();

		try {
			String response = restTemplate.exchange(FCM_ENDPOINT, HttpMethod.POST, request, String.class).getBody();
			System.out.println("Successfully sent message: " + response);
		} catch (Exception e) {
			e.printStackTrace(); // Log the error
		}
	}

	// public void sendPushNotification(String deviceToken, Map<String, String>
	// payload) {
	// String fcmEndpoint = ApplicationURIConstants.PUSH_NOTIFICATION_SEND;
	//
	// // Construct FCM request payload
	// Map<String, Object> fcmPayload = Map.of(
	// "to", deviceToken,
	// "priority", "high",
	// "data",payload,
	// "notification", payload
	// );
	// // Set up request headers with FCM server key
	// System.err.println("fcmPayload is {}"+fcmPayload);
	// LOGGER.info("fcmPayload is {}"+fcmPayload);
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON);
	// headers.set("Authorization", "key=" + fcmServerKey);
	// HttpEntity<Map<String, Object>> request = new HttpEntity<>(fcmPayload,
	// headers);
	//
	// // Send POST request to FCM endpoint
	//
	// RestTemplate restTemplate = new RestTemplate();
	// restTemplate.postForObject(fcmEndpoint, request, String.class);
	// }
	// public void sendPushNotificationForStartCall(String deviceToken, Map<String,
	// String> payload,Map<String, String> notificationPayload) {
	// String fcmEndpoint = ApplicationURIConstants.PUSH_NOTIFICATION_SEND;
	//
	// LOGGER.info("inside method");
	// // Construct FCM request payload
	// Map<String, Object> fcmPayload = Map.of(
	// "to", deviceToken,
	// "notification",notificationPayload,
	// "data",payload
	// );
	// System.err.println("fcmPayload :"+fcmPayload);
	// // Set up request headers with FCM server key
	// HttpHeaders headers = new HttpHeaders();
	// headers.setContentType(MediaType.APPLICATION_JSON);
	// headers.set("Authorization", "key=" + fcmServerKey);
	//
	// System.err.println("header :>>>>>> " + headers);
	// HttpEntity<Map<String, Object>> request = new HttpEntity<>(fcmPayload,
	// headers);
	//
	// // Send POST request to FCM endpoint
	//
	// System.err.println("fcmPayload :"+fcmEndpoint);
	// RestTemplate restTemplate = new RestTemplate();
	// restTemplate.postForObject(fcmEndpoint, request, String.class);
	// }

	public void sendPushNotificationForStartCall(String deviceToken, Map<String, String> payload) {
		String fcmEndpoint = ApplicationURIConstants.PUSH_NOTIFICATION_SEND;

		LOGGER.info("inside method");
		// Construct FCM request payload
		Map<String, Object> fcmPayload = Map.of("to", deviceToken, "priority", "high", "data", payload);
		System.err.println("fcmPayload :" + fcmPayload);
		LOGGER.info("fcmPayload is {}" + fcmPayload);
		// Set up request headers with FCM server key
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "key=" + fcmServerKey);

		System.err.println("header :>>>>>> " + headers);
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(fcmPayload, headers);

		// Send POST request to FCM endpoint

		System.err.println("fcmPayload :" + fcmEndpoint);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForObject(fcmEndpoint, request, String.class);
	}

	public void sendPushNotificationForStartCallIOS(String deviceToken, Map<String, String> payload,
			Map<String, String> notificationPayload) throws Exception {

		File file = ResourceUtils.getFile("classpath:AuthKey_72XZ2S86N8.p8");

		ApnsSigningKey signingKey = ApnsSigningKey.loadFromPkcs8File(file, apnsTeamId, apnsKeyId);
		ApnsClient apnsClient = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST) // Use
																												// .PRODUCTION_APNS_HOST
																												// for
																												// production
				.setSigningKey(signingKey).build();
		Map<String, Object> fcmPayload = Map.of("to", deviceToken, "notification", notificationPayload, "data",
				payload);
		final SimpleApnsPayloadBuilder payloadBuilder = new SimpleApnsPayloadBuilder();
		payloadBuilder.setAttributes(fcmPayload);
		LOGGER.info("fcmPayload is{}" + fcmPayload);
		System.err.println("fcmPayload is{}" + fcmPayload);
		System.out.println("Build payloadBuilder");
		final String payload1 = payloadBuilder.build();
		final String token = SanitizeTokenString(deviceToken);
		System.out.println("-----------" + token);
		SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, "com.wiilisten.voip",
				payload1);

		PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture = apnsClient
				.sendNotification(pushNotification);
		System.out.println("Notification sent.");
		PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse;
		try {
			pushNotificationResponse = sendNotificationFuture.get();
			if (pushNotificationResponse.isAccepted()) {
				System.out.println("Notification accepted by APNs gateway.");
			} else {
				System.out.println(
						"Notification rejected by the APNs gateway: " + pushNotificationResponse.getRejectionReason());

				pushNotificationResponse.getTokenInvalidationTimestamp()
						.ifPresent(timestamp -> System.out.println("…and the token is invalid as of " + timestamp));
			}

			apnsClient.close().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String SanitizeTokenString(final String tokenString) {
		return tokenString.replaceAll("[^a-fA-F0-9]", "");
	}

}
