package com.wiilisten.service.impl;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.NewsLetter;
import com.wiilisten.service.NewsLetterService;

import jakarta.annotation.PostConstruct;

@Service
public class NewsLetterServiceImpl extends BaseServiceImpl<NewsLetter, Long> implements NewsLetterService{
	
	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getNewsLetterRepository();
	}

	@Override
	public NewsLetter findByEmailAndActiveTrue(String email) {
		return getDaoFactory().getNewsLetterRepository().findByEmailAndActiveTrue(email);
	}

}
