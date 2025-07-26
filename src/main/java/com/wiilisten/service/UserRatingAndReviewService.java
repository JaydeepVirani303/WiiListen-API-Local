package com.wiilisten.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;

public interface UserRatingAndReviewService extends BaseService<UserRatingAndReview, Long>{

	List<UserRatingAndReview> findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(User user);

	UserRatingAndReview findByIdAndActiveTrue(Long id);
	
	Page<UserRatingAndReview> findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(User user,Pageable pageable);
	
	List<UserRatingAndReview> findByReviewedUserAndActiveTrueAndIsTopCommentTrueOrderByCreatedAtDesc(User user);

}
