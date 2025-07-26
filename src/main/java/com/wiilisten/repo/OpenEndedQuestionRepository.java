package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.OpenEndedQuestion;

@Repository
public interface OpenEndedQuestionRepository extends BaseRepository<OpenEndedQuestion, Long>{

	List<OpenEndedQuestion> findByActiveTrue();
	
	OpenEndedQuestion findByIdAndActiveTrue(Long id);
	
	List<OpenEndedQuestion> findByActiveTrueOrderByIdDesc();

}
