package com.wiilisten.controller.api.caller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.response.MyRatingsDetailsDto;
import com.wiilisten.response.UserRatingAndReviewDetailsDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.MY_RATINGS)
public class ApiV1CallerRatingsController extends BaseController{

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1CallerRatingsController.class);
	
	@GetMapping(value = ApplicationURIConstants.FOWARD_SLASH)
	public ResponseEntity<Object> getMyRatings(){
		
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {
			
			User user = getLoggedInUser();
			
			MyRatingsDetailsDto response = new MyRatingsDetailsDto();
			response.setCurrentRating( new BigDecimal(user.getCurrentRating()).setScale(1, RoundingMode.HALF_UP).doubleValue() );
			response.setTotalReviews(user.getTotalReviews());
			
			Map<Integer, Long> startWiseRating = new HashMap<>();
			startWiseRating.put(1, 0L);
			startWiseRating.put(2, 0L);
			startWiseRating.put(3, 0L);
			startWiseRating.put(4, 0L);
			startWiseRating.put(5, 0L);
			List<UserRatingAndReviewDetailsDto> responseReviews = new ArrayList<>();
			List<UserRatingAndReview> reviews = getServiceRegistry().getUserRatingAndReviewService().findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(user);
			if(!ApplicationUtils.isEmpty(reviews)) {
				reviews.forEach(review -> {
					UserRatingAndReviewDetailsDto tempReview = new UserRatingAndReviewDetailsDto();
					tempReview.setId(review.getId());
					tempReview.setIsTopComment(review.getIsTopComment());
					tempReview.setCallName(review.getReviewerUser().getCallName());
					tempReview.setProfilePicture(review.getReviewerUser().getProfilePicture());
					tempReview.setRating(review.getRating());
					tempReview.setReview(review.getReview());
					
					if(review.getCreatedAt() != null) {
						LocalDateTime reviewedOn = LocalDateTime.ofInstant(review.getCreatedAt().toInstant(), ZoneOffset.UTC);
						tempReview.setReviewDateTime(
								reviewedOn.format(DateTimeFormatter.ofPattern("YYYY/MM/dd HH:mm:ss"))
								);
					}
					responseReviews.add(tempReview);
					
					startWiseRating.put(review.getRating(), (long) startWiseRating.get(review.getRating()) + 1 );
					
				});
				response.setStartWiseRating(startWiseRating);
				response.setReviews(responseReviews);
			}
			
		
			
		
			
			
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
}
