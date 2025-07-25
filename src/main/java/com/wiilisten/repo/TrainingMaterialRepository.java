package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.TrainingMaterial;

@Repository
public interface TrainingMaterialRepository extends BaseRepository<TrainingMaterial, Long>{

	List<TrainingMaterial> findByContentTypeAndActiveTrue(String contentType);

	Long countByContentTypeAndActiveTrue(String contentType);
	
	TrainingMaterial findByIdAndActiveTrue(Long id);
	
	List<TrainingMaterial> findByActiveTrueOrderByIdDesc();

}
