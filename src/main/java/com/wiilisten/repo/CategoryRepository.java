package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.Category;

@Repository
public interface CategoryRepository extends BaseRepository<Category, Long> {

	List<Category> findByActiveTrueOrderByIdDesc();

	Category findByIdAndActiveTrue(Long id);

	//Page<Category> findAll(Pageable pageable);
	
	List<Category> findAllByOrderByIdDesc();

}
