package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.TrainingMaterial;

public interface TrainingMaterialService extends BaseService<TrainingMaterial, Long>{

	List<TrainingMaterial> findByContentTypeAndSubCategoryAndActiveTrue(String contentType, String subCategory);

	Long countByContentTypeAndSubCategoryAndActiveTrue(String contentType, String subCategory);


	Long countByContentTypeAndActiveTrue(String contentType);
	
	TrainingMaterial findByIdAndActiveTrue(Long id);
	
	List<TrainingMaterial> findByActiveTrueOrderByIdDesc();

	List<TrainingMaterial> findByActiveTrueOrderByOrderNumberAsc();

}
