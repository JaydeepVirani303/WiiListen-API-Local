package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.OpenEndedQuestion;

public interface OpenEndedQuestionService extends BaseService<OpenEndedQuestion, Long>{

	List<OpenEndedQuestion> findByActiveTrue();
	
	List<OpenEndedQuestion> findByActiveTrueOrderByIdDesc();
	
	OpenEndedQuestion findByIdAndActiveTrue(Long id);

}
