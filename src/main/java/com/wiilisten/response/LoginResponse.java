package com.wiilisten.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponse {

    private Long id;
    
    private String name;
    
    private String email;
    
    private String token;
        
    private Boolean online;
    
    private String role;
    
    private String profileImg;
    
    private Boolean otpVerify;
    
    private String step;

}
