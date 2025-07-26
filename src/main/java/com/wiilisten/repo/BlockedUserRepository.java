package com.wiilisten.repo;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.BlockedUser;
import com.wiilisten.entity.User;

@Repository
public interface BlockedUserRepository extends BaseRepository<BlockedUser, Long>{

	BlockedUser findByBlockerUserAndBlockedUserAndActiveTrue(User blockerUser, User blockedUser);
	
	List<BlockedUser> findByBlockedUserAndActiveTrueAndType(User user,String type);
	
	List<BlockedUser> findByBlockerUserAndActiveTrueAndType(User user,String type);
	
	BlockedUser findByBlockerUserAndBlockedUserAndActiveTrueAndType(User blockerUser, User blockedUser,String type);
	

}
