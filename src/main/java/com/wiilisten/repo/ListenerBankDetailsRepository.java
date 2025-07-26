package com.wiilisten.repo;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.ListenerBankDetails;
import com.wiilisten.entity.User;

@Repository
public interface ListenerBankDetailsRepository extends BaseRepository<ListenerBankDetails, Long>{

	ListenerBankDetails findByUserAndActiveTrue(User user);

}
