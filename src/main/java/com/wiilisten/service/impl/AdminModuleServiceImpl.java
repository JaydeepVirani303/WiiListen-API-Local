package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.AdminModule;
import com.wiilisten.service.AdminModuleService;

import jakarta.annotation.PostConstruct;

@Service
public class AdminModuleServiceImpl extends BaseServiceImpl<AdminModule, Long> implements AdminModuleService{
	
	@PostConstruct
    public void setBaseRepository(){
        super.baseRepository = getDaoFactory().getAdminModuleRepository();
    }

	@Override
	public AdminModule findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getAdminModuleRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public List<AdminModule> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getAdminModuleRepository().findByActiveTrueOrderByIdDesc();
	}

}
