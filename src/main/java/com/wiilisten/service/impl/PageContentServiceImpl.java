package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.PageContent;
import com.wiilisten.service.PageContentService;

import jakarta.annotation.PostConstruct;

@Service
public class PageContentServiceImpl extends BaseServiceImpl<PageContent, Long> implements PageContentService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getPageContentRepository();
	}

	@Override
	public PageContent findByTypeAndActiveTrue(String type) {
		return getDaoFactory().getPageContentRepository().findByTypeAndActiveTrue(type);
	}

	@Override
	public List<PageContent> findByActiveTrue() {
		return getDaoFactory().getPageContentRepository().findByActiveTrue();
	}

	@Override
	public PageContent findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getPageContentRepository().findByIdAndActiveTrue(id);
	}
}
