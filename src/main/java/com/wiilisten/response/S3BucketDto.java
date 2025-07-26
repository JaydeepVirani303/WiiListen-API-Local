package com.wiilisten.response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class S3BucketDto {
	@JsonProperty("bucket")
	private String bucket;
	
	@JsonProperty("access_key")
	private String accessKey;
	
	@JsonProperty("secret_key")
	private String secretKey;
}
