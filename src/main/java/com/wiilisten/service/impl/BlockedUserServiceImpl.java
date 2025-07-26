package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wiilisten.entity.BlockedUser;
import com.wiilisten.entity.User;
import com.wiilisten.service.BlockedUserService;

import jakarta.annotation.PostConstruct;

@Service
public class BlockedUserServiceImpl extends BaseServiceImpl<BlockedUser, Long> implements BlockedUserService{

	@PostConstruct
    public void setBaseRepository(){
        super.baseRepository = getDaoFactory().getBlockedUserRepository();
    }

	@Override
	public BlockedUser findByBlockerUserAndBlockedUserAndActiveTrue(User blockerUser, User blockedUser) {
		return getDaoFactory().getBlockedUserRepository().findByBlockerUserAndBlockedUserAndActiveTrue(blockerUser, blockedUser);
	}

	@Override
	public List<BlockedUser> findByBlockedUserAndActiveTrueAndType(User user, String type) {
		return getDaoFactory().getBlockedUserRepository().findByBlockedUserAndActiveTrueAndType(user, type);
	}

	@Override
	public List<BlockedUser> findByBlockerUserAndActiveTrueAndType(User user,String type) {
		return getDaoFactory().getBlockedUserRepository().findByBlockerUserAndActiveTrueAndType(user,type);
	}

	@Override
	public BlockedUser findByBlockerUserAndBlockedUserAndActiveTrueAndType(User blockerUser, User blockedUser,
			String type) {
		return getDaoFactory().getBlockedUserRepository().findByBlockerUserAndBlockedUserAndActiveTrueAndType(blockerUser, blockedUser, type);
	}

	
}
