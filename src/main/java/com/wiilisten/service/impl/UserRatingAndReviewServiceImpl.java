package com.wiilisten.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.service.UserRatingAndReviewService;

import jakarta.annotation.PostConstruct;

@Service
public class UserRatingAndReviewServiceImpl extends BaseServiceImpl<UserRatingAndReview, Long> implements UserRatingAndReviewService{

	@PostConstruct
	public void setBaseRepository() {
		super.baseRepository = getDaoFactory().getUserRatingAndReviewRepository();
	}

	@Override
	public List<UserRatingAndReview> findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(User user) {
		return getDaoFactory().getUserRatingAndReviewRepository().findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(user);
	}

	@Override
	public UserRatingAndReview findByIdAndActiveTrue(Long id) {
		return getDaoFactory().getUserRatingAndReviewRepository().findByIdAndActiveTrue(id);
	}

	@Override
	public Page<UserRatingAndReview> findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(User user, Pageable pageable) {
		return getDaoFactory().getUserRatingAndReviewRepository().findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(user, pageable);
	}

	@Override
	public List<UserRatingAndReview> findByReviewedUserAndActiveTrueAndIsTopCommentTrueOrderByCreatedAtDesc(User user) {
		return getDaoFactory().getUserRatingAndReviewRepository().findByReviewedUserAndActiveTrueAndIsTopCommentTrueOrderByCreatedAtDesc(user);
	}
}
