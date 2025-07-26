package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.ContactUs;

@Repository
public interface ContactUsRepository extends BaseRepository<ContactUs, Long>{
	
	List<ContactUs> findByActiveTrueOrderByIdDesc();
	
	ContactUs findByIdAndActiveTrue(Long id);

}
