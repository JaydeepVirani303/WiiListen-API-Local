package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.Administration;

@Repository
public interface AdministrationRepository extends BaseRepository<Administration, Long> {

	Administration findByEmailAndActiveTrue(String email);

	Administration findByContactAndActiveTrueAndCountryCode(String contact,String countryCode);

	Page<Administration> findByActiveTrue(Pageable pageable);
	
	Administration findByIdAndActiveTrue(Long id);
	
	Boolean existsByEmailAndActiveTrue(String email);
	
	List<Administration> findByRoleAndActiveTrueOrderByIdDesc(String role);

}
