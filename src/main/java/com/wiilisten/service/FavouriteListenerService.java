package com.wiilisten.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.FavouriteListener;

public interface FavouriteListenerService extends BaseService<FavouriteListener, Long>{

	Page<FavouriteListener> findByCallerIdAndActiveTrue(Long id, Pageable pageable);

	FavouriteListener findByCallerIdAndListenerId(Long callerId, Long listenerId);
	
	Boolean existsByCallerIdAndListenerIdAndActiveTrue(Long callerId, Long listenerId);

	List<FavouriteListener> findTop10ByCallerIdAndActiveTrue(Long id);

}
