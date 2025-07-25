package com.wiilisten.repo;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.UserNotes;

@Repository
public interface UserNotesRepository extends BaseRepository<UserNotes, Long>{
	
	UserNotes findByCallerProfileAndListenerProfileAndActiveTrue(CallerProfile callerProfile,ListenerProfile listenerProfile);

}
