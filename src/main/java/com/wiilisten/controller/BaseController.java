package com.wiilisten.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.entity.Administration;
import com.wiilisten.entity.User;
import com.wiilisten.utils.CommonServices;
import com.wiilisten.utils.ServiceRegistry;
import com.wiilisten.utils.TokenUtil;

import lombok.Getter;

@RestController
@Getter
public class BaseController {
	
	@Autowired
	private ServiceRegistry serviceRegistry;

	@Autowired
	private CommonServices commonServices;

	@Autowired
	private TokenUtil tokenUtil;

//	public User getLoggedInUser() {
//		final UserDetail userDetail = (UserDetail) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		
//		return userDetail.getUser();
//	    }

	public User getLoggedInUser() {
		return getServiceRegistry().getUserService().findByEmailAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName());
	}
	
	public Administration getLoggedInSubAdmin() {
		return getServiceRegistry().getAdministrationService().findByEmailAndActiveTrue(SecurityContextHolder.getContext().getAuthentication().getName());
	}
}
