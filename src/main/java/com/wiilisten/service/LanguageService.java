package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.Language;

public interface LanguageService extends BaseService<Language, Long>{

	Language findByIdAndActiveTrue(Long id);

	List<Language> findByActiveTrue();

}
