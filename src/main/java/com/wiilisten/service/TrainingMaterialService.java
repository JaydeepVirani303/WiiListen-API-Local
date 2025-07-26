package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.TrainingMaterial;

public interface TrainingMaterialService extends BaseService<TrainingMaterial, Long>{

	List<TrainingMaterial> findByContentTypeAndActiveTrue(String contentType);

	Long countByContentTypeAndActiveTrue(String contentType);
	
	TrainingMaterial findByIdAndActiveTrue(Long id);
	
	List<TrainingMaterial> findByActiveTrueOrderByIdDesc();

}
