package com.wiilisten.service.impl;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.UserNotes;
import com.wiilisten.service.UserNotesService;

import jakarta.annotation.PostConstruct;

@Service
public class UserNotesServiceImpl extends BaseServiceImpl<UserNotes, Long> implements UserNotesService{
	
	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getUserNotesRepository();
	}

	@Override
	public UserNotes findByCallerProfileAndListenerProfileAndActiveTrue(CallerProfile callerProfile,
			ListenerProfile listenerProfile) {
		return getDaoFactory().getUserNotesRepository().findByCallerProfileAndListenerProfileAndActiveTrue(callerProfile, listenerProfile);
	}

}
