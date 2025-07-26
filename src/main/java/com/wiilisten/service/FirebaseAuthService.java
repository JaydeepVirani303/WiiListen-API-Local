package com.wiilisten.service;

import java.io.IOException;
import java.util.Collections;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class FirebaseAuthService {

	private static final String FIREBASE_SCOPE = "https://www.googleapis.com/auth/firebase.messaging";

	public String getAccessToken() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
				new ClassPathResource("wiilisten-ed0fd-firebase-adminsdk-gc7vi-f9718ab131.json").getInputStream())
				.createScoped(Collections.singleton(FIREBASE_SCOPE));
		googleCredentials.refreshIfExpired();
		AccessToken token = googleCredentials.getAccessToken();
		return token.getTokenValue();
	}

}
