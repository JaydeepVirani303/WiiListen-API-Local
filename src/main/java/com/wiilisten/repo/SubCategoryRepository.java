package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.SubCategory;

@Repository
public interface SubCategoryRepository extends BaseRepository<SubCategory, Long> {

	List<SubCategory> findByActiveTrueOrderByIdDesc();

	SubCategory findByIdAndActiveTrue(Long id);
	
	List<SubCategory> findAllByOrderByIdDesc();
	
	List<SubCategory> findByCategoryId(Long id);

}
