package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.TrainingMaterial;
import com.wiilisten.service.TrainingMaterialService;

import jakarta.annotation.PostConstruct;

@Service
public class TrainingMaterialServiceImpl extends BaseServiceImpl<TrainingMaterial, Long> implements TrainingMaterialService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getTrainingMaterialRepository();
	}

	@Override
	public List<TrainingMaterial> findByContentTypeAndSubCategoryAndActiveTrue(String contentType, String subCategory) {
		return getDaoFactory().getTrainingMaterialRepository().findByContentTypeAndSubCategoryAndActiveTrue(contentType, subCategory);
	}

	@Override
	public Long countByContentTypeAndSubCategoryAndActiveTrue(String contentType, String subCategory) {
		return getDaoFactory().getTrainingMaterialRepository().countByContentTypeAndSubCategoryAndActiveTrue(contentType, subCategory);
	}

	@Override
	public Long countByContentTypeAndActiveTrue(String string) {
		return getDaoFactory().getTrainingMaterialRepository().countByContentTypeAndActiveTrue(string);
	}

	@Override
	public TrainingMaterial findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getTrainingMaterialRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public List<TrainingMaterial> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getTrainingMaterialRepository().findByActiveTrueOrderByIdDesc();
	}
}
