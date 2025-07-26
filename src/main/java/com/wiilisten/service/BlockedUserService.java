package com.wiilisten.service;

import java.util.List;

import com.wiilisten.entity.BlockedUser;
import com.wiilisten.entity.User;

public interface BlockedUserService extends BaseService<BlockedUser, Long>{

	BlockedUser findByBlockerUserAndBlockedUserAndActiveTrue(User blockerUser, User blockedUser);
	
	List<BlockedUser> findByBlockedUserAndActiveTrueAndType(User user,String type);
	
	List<BlockedUser> findByBlockerUserAndActiveTrueAndType(User user,String type);
	
	BlockedUser findByBlockerUserAndBlockedUserAndActiveTrueAndType(User blockerUser, User blockedUser,String type);

}
