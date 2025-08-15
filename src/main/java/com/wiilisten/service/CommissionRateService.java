package com.wiilisten.service;

import com.wiilisten.entity.CommissionRate;

public interface CommissionRateService extends BaseService<CommissionRate, Long>{

	CommissionRate findByActiveTrue();

    CommissionRate findFirstByOrderByIdAsc();
}
