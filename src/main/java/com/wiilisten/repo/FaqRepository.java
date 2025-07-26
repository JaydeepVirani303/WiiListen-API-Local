package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.Faq;

@Repository
public interface FaqRepository extends BaseRepository<Faq, Long>{

	List<Faq> findByActiveTrueOrderByIdDesc();
	
	Faq findByIdAndActiveTrue(Long id);

}
