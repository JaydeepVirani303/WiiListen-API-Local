package com.wiilisten.repo;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.NewsLetter;

@Repository
public interface NewsLetterRepository extends BaseRepository<NewsLetter, Long>{
	
	NewsLetter findByEmailAndActiveTrue(String email);

}
