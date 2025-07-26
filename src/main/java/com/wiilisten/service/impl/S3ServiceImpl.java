package com.wiilisten.service.impl;

import java.io.File;

import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.model.S3Object;
import com.wiilisten.service.S3Service;

@Service
public class S3ServiceImpl implements S3Service {

	@Override
	public void uploadFile(String key, File file) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public S3Object getFile(String key) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public void uploadFile(String key, File file) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	@Override
//	public S3Object getFile(String key) {
//		// TODO Auto-generated method stub
//		return null;
//	}

//	private final S3Client s3Client;
//	private final String bucketName;
//
//	public S3Service(@Value("${aws.region}") String region,
//	                     @Value("${aws.bucketName}") String bucketName) {
//	        this.bucketName = bucketName;
//	        this.s3Client = S3Client.builder()
//	                .region(Region.of(region))
//	                .credentialsProvider(DefaultCredentialsProvider.create())
//	                .build();
//	    }
//
//	@Override
//	public void uploadFile(String key, File file) {
//		PutObjectRequest request = PutObjectRequest.builder().bucket(bucketName).key(key)
//
//				.build();
//		s3Client.putObject(request, RequestBody.fromFile(file));
//	}
//
//	@Override
//	public S3Object getFile(String key) {
//		GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(key)
//
//				.build();
//		return s3Client.getObject(request);
//	}

}
