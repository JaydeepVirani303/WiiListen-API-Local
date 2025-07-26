package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.Category;
import com.wiilisten.service.CategoryService;

import jakarta.annotation.PostConstruct;

@Service
public class CategoryServiceImpl extends BaseServiceImpl<Category, Long> implements CategoryService {

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getCategoryRepository();
	}

	@Override
	public List<Category> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getCategoryRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public Category findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getCategoryRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public Page<Category> findAll(Pageable pageable) {
		return getDaoFactory().getCategoryRepository().findAll(pageable);
	}

	@Override
	public List<Category> findAllByOrderByIdDesc() {
		return getDaoFactory().getCategoryRepository().findAllByOrderByIdDesc();
	}

}
