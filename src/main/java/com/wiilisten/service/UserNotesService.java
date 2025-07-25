package com.wiilisten.service;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.UserNotes;

public interface UserNotesService extends BaseService<UserNotes, Long>{
	
	UserNotes findByCallerProfileAndListenerProfileAndActiveTrue(CallerProfile callerProfile,ListenerProfile listenerProfile);

}
