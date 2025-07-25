package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.PageContent;

@Repository
public interface PageContentRepository extends BaseRepository<PageContent, Long>{

	PageContent findByTypeAndActiveTrue(String type);
	
	List<PageContent> findByActiveTrue();
	
	PageContent findByIdAndActiveTrue(Long id);

}
