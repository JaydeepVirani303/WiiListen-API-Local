package com.wiilisten.controller.api.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.AdministrativeNotification;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.Subscription;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.response.CallerProfileResponseDto;
import com.wiilisten.response.UserReviewDetailsResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.LISTENER
		+ ApplicationURIConstants.HOME)
public class ApiV1ListenerHomeController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1ListenerHomeController.class);

	@GetMapping(ApplicationURIConstants.APP_STATUS + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateApplicationActiveStatus() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);

			listener.setAppActiveStatus(listener.getAppActiveStatus() ? false : true);

			getServiceRegistry().getListenerProfileService().saveORupdate(listener);

			AdministrativeNotification administrativeNotification = new AdministrativeNotification();
			Map<String, String> payload = new HashMap<>();

			administrativeNotification.setTitle(ApplicationConstants.ACTIVE_STATUS);
			if(listener.getAppActiveStatus()) {
				administrativeNotification.setContent(ApplicationConstants.ACTIVE_STATUS_ON);
			}else {
				administrativeNotification.setContent(ApplicationConstants.ACTIVE_STATUS_OFF);
			}
			
			administrativeNotification.setUsers(Collections.singletonList(user));
			administrativeNotification.setTags(ApplicationConstants.APP_STATUS);
			administrativeNotification.setActive(true);
			if(user.getNotificationStatus()) {
				getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);
			}
			

			payload.put(ApplicationConstants.TITLE, ApplicationConstants.ACTIVE_STATUS);
			if(listener.getAppActiveStatus()) {
				payload.put(ApplicationConstants.BODY, ApplicationConstants.ACTIVE_STATUS_ON);
			}else {
				payload.put(ApplicationConstants.BODY, ApplicationConstants.ACTIVE_STATUS_OFF);
			}
			
			payload.put(ApplicationConstants.TAG, ApplicationConstants.APP_STATUS);
			String receiverDeviceToken = user.getDeviceToken();
			if (receiverDeviceToken != null && user.getNotificationStatus() && user.getIsLoggedIn()) {
				// Send push notification using FCM
				getServiceRegistry().getFcmService().sendPushNotification(receiverDeviceToken, payload);
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(
					getCommonServices().generateSuccessResponseWithMessageKey(SuccessMsgEnum.STATUS_CHANGED.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.CALLER)
	public ResponseEntity<Object> getSpecificCallerBy(@RequestParam(required = false) Long id) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			CallerProfileResponseDto responseData = new CallerProfileResponseDto();

			if (!ApplicationUtils.isEmpty(id)) {
				CallerProfile caller = getServiceRegistry().getCallerProfileService().findByIdAndActiveTrue(id);
				if (caller == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
				}

				responseData = convertEntityToDto(caller);

			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseData));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.ADVERTISEMENT + ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addAdvertisement(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Subscription subscription = getServiceRegistry().getSubscriptionService()
					.findByIdAndIsDeletedFalseAndActiveTrue(idRequestDto.getId());
			if (subscription == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
		return null;
	}

	private CallerProfileResponseDto convertEntityToDto(CallerProfile caller) {
		// TODO Auto-generated method stub
		CallerProfileResponseDto resDto = new CallerProfileResponseDto();
		resDto.setCallerId(caller.getId());
		resDto.setCallName(caller.getUser().getCallName());
		resDto.setCurrentRating(caller.getUser().getCurrentRating());
		resDto.setProfilePicture(caller.getUser().getProfilePicture());
		resDto.setTotalReviews(caller.getUser().getTotalReviews());
		List<UserRatingAndReview> callerReviews = getServiceRegistry().getUserRatingAndReviewService()
				.findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(caller.getUser());
		if (!ApplicationUtils.isEmpty(callerReviews)) {
			List<UserReviewDetailsResponseDto> reviews = new ArrayList<>();

			callerReviews.forEach(review -> {

				UserReviewDetailsResponseDto tempReview = new UserReviewDetailsResponseDto();
				tempReview.setCallName(review.getReviewerUser().getCallName());
				tempReview.setProfilePicture(review.getReviewerUser().getProfilePicture());
				tempReview.setRating(review.getRating());
				tempReview.setReview(review.getReview());

				reviews.add(tempReview);
			});

			resDto.setReviews(reviews);
		}

		return resDto;
	}
}
