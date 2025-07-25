package com.wiilisten.controller.api.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.REVIEWS_AND_RATINGS)
public class ApiV1AdminReviewRatingController extends BaseController{

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminReviewRatingController.class);
	
	@PostMapping(ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> getReviewsAndRatingsList(@RequestBody IdRequestDto idRequestDto){
		
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		
		try {
			UserRatingAndReview ratingAndReview = getServiceRegistry().getUserRatingAndReviewService().findByIdAndActiveTrue(idRequestDto.getId());
			if (ratingAndReview == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.REVIEWS_AND_RATINGS_NOT_EXIST.getCode()));
			}
			ratingAndReview.setActive(false);
			getServiceRegistry().getUserRatingAndReviewService().saveORupdate(ratingAndReview);
			
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.REVIEWS_AND_RATINGS_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
		
	}

}
