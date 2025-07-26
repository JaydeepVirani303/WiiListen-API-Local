package com.wiilisten.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;

@Repository
public interface UserRatingAndReviewRepository extends BaseRepository<UserRatingAndReview, Long>{

	List<UserRatingAndReview> findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(User user);

	UserRatingAndReview findByIdAndActiveTrue(Long id);
	
	Page<UserRatingAndReview> findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(User user,Pageable pageable);
	
	List<UserRatingAndReview> findByReviewedUserAndActiveTrueAndIsTopCommentTrueOrderByCreatedAtDesc(User user);
	
}
