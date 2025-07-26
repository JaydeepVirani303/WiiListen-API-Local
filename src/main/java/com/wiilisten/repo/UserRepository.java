package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.User;

@Repository
public interface UserRepository extends BaseRepository<User, Long>{

	User findByEmailAndActiveTrue(String email);

	User findByReferralCodeAndActiveTrue(String referralCode);

	User findByCountryCodeAndContactNumberAndActiveTrue(String countryCode, String contact);
	
	User findByEmailAndIsSuspendedTrueAndActiveTrue(String email);
	
	Boolean existsByEmailAndActiveTrue(String email);
	
	List<User> findByActiveTrueAndIsSuspendedFalseOrderByIdDesc();
	
	List<User> findByRoleAndActiveTrueAndIsSuspendedFalseOrderByIdDesc(String role);
	
	User findByIdAndActiveTrueAndIsSuspendedFalse(Long id);
	
	Page<User> findByCallNameContainingIgnoreCaseAndActiveTrueAndRole(String name,Pageable pageable,String role);
	
	Page<User> findByCallNameIgnoreCaseAndActiveTrueAndRole(String name,Pageable pageable,String role);

}
