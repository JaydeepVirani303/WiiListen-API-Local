package com.wiilisten.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.Category;

public interface CategoryService extends BaseService<Category, Long>{
	
	List<Category> findByActiveTrueOrderByIdDesc();
	Category findByIdAndActiveTrue(Long id);
	Page<Category> findAll(Pageable pageable);
	List<Category> findAllByOrderByIdDesc();

}
