package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.PageContent;

public interface PageContentService extends BaseService<PageContent, Long>{

	PageContent findByTypeAndActiveTrue(String type);
	
	List<PageContent> findByActiveTrue();
	
	PageContent findByIdAndActiveTrue(Long id);

}
