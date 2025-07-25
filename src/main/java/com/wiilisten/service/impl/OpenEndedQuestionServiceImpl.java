package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.OpenEndedQuestion;
import com.wiilisten.service.OpenEndedQuestionService;

import jakarta.annotation.PostConstruct;

@Service
public class OpenEndedQuestionServiceImpl extends BaseServiceImpl<OpenEndedQuestion, Long> implements OpenEndedQuestionService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getOpenEndedQuestionRepository();
	}

	@Override
	public OpenEndedQuestion findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getOpenEndedQuestionRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public List<OpenEndedQuestion> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getOpenEndedQuestionRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public List<OpenEndedQuestion> findByActiveTrue() {
		return getDaoFactory().getOpenEndedQuestionRepository().findByActiveTrue();
	}


}
