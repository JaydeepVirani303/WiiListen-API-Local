package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.ListenerAnalytic;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.service.ListenerAnalyticService;

import jakarta.annotation.PostConstruct;

@Service
public class ListenerAnalyticServiceImpl extends BaseServiceImpl<ListenerAnalytic, Long> implements ListenerAnalyticService{
	
	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getListenerAnalyticRepo();
	}

	@Override
	public List<ListenerAnalytic> findByListenerAndActiveTrue(ListenerProfile listener) {
		return getDaoFactory().getListenerAnalyticRepo().findByListenerAndActiveTrue(listener);
	}

}
