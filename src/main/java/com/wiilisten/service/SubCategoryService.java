package com.wiilisten.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.SubCategory;

public interface SubCategoryService extends BaseService<SubCategory, Long> {

	List<SubCategory> findByActiveTrueOrderByIdDesc();

	SubCategory findByIdAndActiveTrue(Long id);

	List<SubCategory> findAllByOrderByIdDesc();

	List<SubCategory> findByCategoryId(Long id);

}
