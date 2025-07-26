package com.wiilisten.service;

import com.wiilisten.entity.NewsLetter;

public interface NewsLetterService extends BaseService<NewsLetter, Long>{
	
	NewsLetter findByEmailAndActiveTrue(String email);

}
