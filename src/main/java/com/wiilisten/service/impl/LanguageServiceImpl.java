package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.Language;
import com.wiilisten.service.LanguageService;

import jakarta.annotation.PostConstruct;

@Service
public class LanguageServiceImpl extends BaseServiceImpl<Language, Long> implements LanguageService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getLanguageRepository();
	}

	@Override
	public Language findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getLanguageRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public List<Language> findByActiveTrue() {
		return getDaoFactory().getLanguageRepository().findByActiveTrue();
	}
}
