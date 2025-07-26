package com.wiilisten.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.wiilisten.controller.BaseController;
import java.io.IOException;
import io.agora.media.RtcTokenBuilder2;
import io.agora.media.RtcTokenBuilder2.Role;
import io.agora.rtm.RtmTokenBuilder2;
@Service
public class AgoraService extends BaseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AgoraService.class);

	    @Value("${agora.appId}")
	    private String appId;

	    @Value("${agora.appCertificate}")
	    private String appCertificate;
	    static int tokenExpirationInSeconds = 3600;
	    static int privilegeExpirationInSeconds = 3600;
	  
	    public String generateRtcToken(String channelName, String uid) throws IOException {
	    	   System.out.printf("App Id: %s\n", appId);
	           System.out.printf("App Certificate: %s\n", appCertificate);
	           if (appId == null || appId.isEmpty() || appCertificate == null || appCertificate.isEmpty()) {
	       		LOGGER.info("Need to set environment variable AGORA_APP_ID and AGORA_APP_CERTIFICATE");
			  }

	           RtcTokenBuilder2 token = new RtcTokenBuilder2();
	           String result = token.buildTokenWithUserAccount(appId, appCertificate, channelName, uid, Role.ROLE_SUBSCRIBER,
	                   tokenExpirationInSeconds, privilegeExpirationInSeconds);
	           System.out.printf("Token with uid: %s\n", result);
			 	return result;
    
	    }
	    public String generateRtmToken(String uid) throws IOException {
	    	   System.out.printf("App Id: %s\n", appId);
	           System.out.printf("App Certificate: %s\n", appCertificate);
	           if (appId == null || appId.isEmpty() || appCertificate == null || appCertificate.isEmpty()) {
	       		LOGGER.info("Need to set environment variable AGORA_APP_ID and AGORA_APP_CERTIFICATE");
			  }

	           RtmTokenBuilder2 token = new RtmTokenBuilder2();
	           String result = token.buildToken(appId, appCertificate, uid, privilegeExpirationInSeconds);
	           System.out.printf("Token with uid: %s\n", result);
			 	return result;
 
	    }
	    
}

	   
