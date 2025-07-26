package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.Faq;

public interface FaqService extends BaseService<Faq, Long> {

	List<Faq> findByActiveTrueOrderByIdDesc();

	Faq findByIdAndActiveTrue(Long id);

}
