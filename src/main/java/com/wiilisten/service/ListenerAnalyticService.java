package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.ListenerAnalytic;
import com.wiilisten.entity.ListenerProfile;

public interface ListenerAnalyticService extends BaseService<ListenerAnalytic, Long>{
	
	List<ListenerAnalytic> findByListenerAndActiveTrue(ListenerProfile listener);

}
