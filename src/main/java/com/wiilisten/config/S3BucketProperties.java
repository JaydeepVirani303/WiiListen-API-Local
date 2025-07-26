package com.wiilisten.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Getter
@Component
@PropertySource("classpath:application.properties")
public class S3BucketProperties {
	@Value("${s3.bucket}")
	private String bucket;
	
	@Value("${s3.accessKey}")
	private String accessKey;
	
	@Value("${s3.secretKey}")
	private String secretKey;
}
