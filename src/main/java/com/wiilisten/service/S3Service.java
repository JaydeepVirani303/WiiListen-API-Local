package com.wiilisten.service;

import java.io.File;

import com.amazonaws.services.s3.model.S3Object;


public interface S3Service {
	
	void uploadFile(String key, File file);
    S3Object getFile(String key);

}
