package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.FavouriteListener;
import com.wiilisten.service.FavouriteListenerService;

import jakarta.annotation.PostConstruct;

@Service
public class FavouriteListenerServiceImpl extends BaseServiceImpl<FavouriteListener, Long> implements FavouriteListenerService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getFavouriteListenerRepository();
	}

	@Override
	public Page<FavouriteListener> findByCallerIdAndActiveTrue(Long id, Pageable pageable) {
		return getDaoFactory().getFavouriteListenerRepository().findByCallerIdAndActiveTrue(id, pageable);
	}

	@Override
	public FavouriteListener findByCallerIdAndListenerId(Long callerId, Long listenerId) {
		return getDaoFactory().getFavouriteListenerRepository().findByCallerIdAndListenerId(callerId, listenerId);
	}

	@Override
	public Boolean existsByCallerIdAndListenerIdAndActiveTrue(Long callerId, Long listenerId) {
		return getDaoFactory().getFavouriteListenerRepository().existsByCallerIdAndListenerIdAndActiveTrue(callerId, listenerId);
	}

	@Override
	public List<FavouriteListener> findTop10ByCallerIdAndActiveTrue(Long id) {
		// TODO Auto-generated method stub
		return getDaoFactory().getFavouriteListenerRepository().findTop10ByCallerIdAndActiveTrue(id);
	}
}
