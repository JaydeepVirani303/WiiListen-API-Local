package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.ContactUs;
import com.wiilisten.service.ContactUsService;

import jakarta.annotation.PostConstruct;

@Service
public class ContactUsServiceImpl extends BaseServiceImpl<ContactUs, Long> implements ContactUsService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getContactUsRepository();
	}

	@Override
	public List<ContactUs> findByActiveTrueOrderByIdDesc() {
		return getDaoFactory().getContactUsRepository().findByActiveTrueOrderByIdDesc();
	}

	@Override
	public ContactUs findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getContactUsRepository().findByIdAndActiveTrue(id);
	}
}
