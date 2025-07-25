package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.AdminModule;

@Repository
public interface AdminModuleRepository extends BaseRepository<AdminModule, Long>{
	
	AdminModule findByIdAndActiveTrue(Long id);
	
	List<AdminModule> findByActiveTrueOrderByIdDesc();

}
