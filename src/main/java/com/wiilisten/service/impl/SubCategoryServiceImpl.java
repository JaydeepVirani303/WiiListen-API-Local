package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.SubCategory;
import com.wiilisten.service.SubCategoryService;

import jakarta.annotation.PostConstruct;

@Service
public class SubCategoryServiceImpl extends BaseServiceImpl<SubCategory, Long> implements SubCategoryService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getSubCategoryRepository();
	}

	@Override
	public List<SubCategory> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getSubCategoryRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public SubCategory findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getSubCategoryRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public List<SubCategory> findAllByOrderByIdDesc() {
		return getDaoFactory().getSubCategoryRepository().findAllByOrderByIdDesc();
	}

	@Override
	public List<SubCategory> findByCategoryId(Long id) {
		return getDaoFactory().getSubCategoryRepository().findByCategoryId(id);
	}

	
}
