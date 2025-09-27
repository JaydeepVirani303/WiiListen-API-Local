package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.TrainingMaterial;

@Repository
public interface TrainingMaterialRepository extends BaseRepository<TrainingMaterial, Long>{

	@Query("SELECT tm FROM TrainingMaterial tm " +
			"WHERE tm.active = true " +
			"AND (:contentType IS NULL OR tm.contentType = :contentType) " +
			"AND (:subCategory IS NULL OR tm.subCategory = :subCategory)")
	List<TrainingMaterial> findByContentTypeAndSubCategoryAndActiveTrue(@Param("contentType") String contentType,
																		@Param("subCategory") String subCategory);

	@Query("SELECT COUNT(tm) FROM TrainingMaterial tm " +
			"WHERE tm.active = true " +
			"AND (:contentType IS NULL OR tm.contentType = :contentType) " +
			"AND (:subCategory IS NULL OR tm.subCategory = :subCategory)")
	Long countByContentTypeAndSubCategoryAndActiveTrue(@Param("contentType") String contentType,
													   @Param("subCategory") String subCategory);


	Long countByContentTypeAndActiveTrue(String contentType);
	
	TrainingMaterial findByIdAndActiveTrue(Long id);
	
	List<TrainingMaterial> findByActiveTrueOrderByIdDesc();

	List<TrainingMaterial> findByActiveTrueOrderByOrderNumberAsc();

	List<TrainingMaterial> findAllByActiveTrueOrderByOrderNumberAsc();

	Long countByActiveTrue();
}
