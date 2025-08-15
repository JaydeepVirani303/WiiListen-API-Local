package com.wiilisten.repo;

import org.springframework.stereotype.Repository;

import com.wiilisten.entity.CommissionRate;

@Repository
public interface CommissionRateRepository extends BaseRepository<CommissionRate, Long>{

	CommissionRate findByActiveTrue();

    CommissionRate findFirstByOrderByIdAsc();
}
