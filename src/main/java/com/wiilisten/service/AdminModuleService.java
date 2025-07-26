package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.AdminModule;

public interface AdminModuleService extends BaseService<AdminModule, Long>{
	
	AdminModule findByIdAndActiveTrue(Long id);
	
	List<AdminModule> findByActiveTrueOrderByIdDesc();

}
