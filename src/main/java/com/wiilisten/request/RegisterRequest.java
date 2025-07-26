package com.wiilisten.request;

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
public class RegisterRequest {

	@JsonProperty("call_name")
    private String callName;
	
    private String username;
	
	@JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
    
    @JsonProperty("role")
    private String role;
    
    @JsonProperty("referral_code")
    private String referralCode;
    
    @JsonProperty("device_os")
    private String deviceOs;

    @JsonProperty("device_uuid")
    private String deviceUUID;

    @JsonProperty("version")
    private String deviceVersion;

    @JsonProperty("device_token")
    private String deviceToken;

    @JsonProperty("voip_token")
    private String voipToken;
    
}
