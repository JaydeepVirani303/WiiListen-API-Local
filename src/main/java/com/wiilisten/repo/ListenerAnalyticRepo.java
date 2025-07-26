package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.ListenerAnalytic;
import com.wiilisten.entity.ListenerProfile;

@Repository
public interface ListenerAnalyticRepo extends BaseRepository<ListenerAnalytic, Long>{
	
	List<ListenerAnalytic> findByListenerAndActiveTrue(ListenerProfile listener);

}
