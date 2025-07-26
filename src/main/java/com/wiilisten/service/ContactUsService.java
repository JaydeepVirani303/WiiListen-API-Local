package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.ContactUs;

public interface ContactUsService extends BaseService<ContactUs, Long> {

	List<ContactUs> findByActiveTrueOrderByIdDesc();

	ContactUs findByIdAndActiveTrue(Long id);

}
