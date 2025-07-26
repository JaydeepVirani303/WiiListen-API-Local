package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.Language;

@Repository
public interface LanguageRepository extends BaseRepository<Language, Long>{

	Language findByIdAndActiveTrue(Long id);

	List<Language> findByActiveTrue();

}
