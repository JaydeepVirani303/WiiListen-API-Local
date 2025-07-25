package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.FavouriteListener;

@Repository
public interface FavouriteListenerRepository extends BaseRepository<FavouriteListener, Long>{

	Page<FavouriteListener> findByCallerIdAndActiveTrue(Long caller, Pageable pageable);

	FavouriteListener findByCallerIdAndListenerId(Long callerId, Long listenerId);

	Boolean existsByCallerIdAndListenerIdAndActiveTrue(Long callerId, Long listenerId);

	List<FavouriteListener> findTop10ByCallerIdAndActiveTrue(Long id);
	

}
