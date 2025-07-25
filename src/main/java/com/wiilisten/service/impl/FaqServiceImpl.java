package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.Faq;
import com.wiilisten.service.FaqService;

import jakarta.annotation.PostConstruct;

@Service
public class FaqServiceImpl extends BaseServiceImpl<Faq, Long> implements FaqService{

	@PostConstruct
	public void serBaseRepository() {
		super.baseRepository = getDaoFactory().getFaqRepository();
	}

	@Override
	public List<Faq> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getFaqRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public Faq findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getFaqRepository().findByIdAndActiveTrue(id);
	}
}
