package com.wiilisten.service;

import com.wiilisten.entity.ListenerBankDetails;
import com.wiilisten.entity.User;

public interface ListenerBankDetailsService extends BaseService<ListenerBankDetails, Long>{

	ListenerBankDetails findByUserAndActiveTrue(User user);

}
