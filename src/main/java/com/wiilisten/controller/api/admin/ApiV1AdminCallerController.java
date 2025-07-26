package com.wiilisten.controller.api.admin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.CardDetails;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.response.CallerResponseDto;
import com.wiilisten.response.CardDetailsResponseDto;
import com.wiilisten.response.ReviewsAndRatingsResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.CALLER)
public class ApiV1AdminCallerController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminCallerController.class);

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getCallerList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
//			Page<CallerProfile> callerProfiles = getServiceRegistry().getCallerProfileService()
//					.findByActiveTrue(pageable);
			List<CallerProfile> callerProfiles = getServiceRegistry().getCallerProfileService()
					.findByActiveTrueOrderByIdDesc();
			List<CallerResponseDto> response = new ArrayList<>();
			callerProfiles.forEach(caller -> {
				CallerResponseDto callerResponseDto = new CallerResponseDto();
				User user = caller.getUser();
				BeanUtils.copyProperties(caller, callerResponseDto);
				callerResponseDto.setAverageCallDuration(user.getAverageCallDuration());
				callerResponseDto.setCallName(user.getCallName());
				callerResponseDto.setContactNumber(user.getContactNumber());
				callerResponseDto.setCountryCode(user.getCountryCode());
				callerResponseDto.setEmail(user.getEmail());
				callerResponseDto.setHasUnseenNotifications(user.getHasUnseenNotifications());
				callerResponseDto.setIsBlockedOrReported(user.getIsBlockedOrReported());
				callerResponseDto.setIsEmailVerified(user.getIsEmailVerified());
				callerResponseDto.setIsProfileSet(user.getIsProfileSet());
				callerResponseDto.setIsSuspended(user.getIsSuspended());
				callerResponseDto.setNotificationStatus(user.getNotificationStatus());
				callerResponseDto.setProfilePicture(user.getProfilePicture());
				response.add(callerResponseDto);
			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.FORWARD_SLASH)
	public ResponseEntity<Object> getSpecificCaller(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			CallerProfile caller = getServiceRegistry().getCallerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (caller == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CALLER_NOT_EXIST.getCode()));
			}
			CallerResponseDto response = new CallerResponseDto();
			User user = caller.getUser();
			BeanUtils.copyProperties(caller, response);
			response.setAverageCallDuration(user.getAverageCallDuration());
			response.setCallName(user.getCallName());
			response.setContactNumber(user.getContactNumber());
			response.setCountryCode(user.getCountryCode());
			response.setEmail(user.getEmail());
			response.setHasUnseenNotifications(user.getHasUnseenNotifications());
			response.setIsBlockedOrReported(user.getIsBlockedOrReported());
			response.setIsEmailVerified(user.getIsEmailVerified());
			response.setIsProfileSet(user.getIsProfileSet());
			response.setIsSuspended(user.getIsSuspended());
			response.setNotificationStatus(user.getNotificationStatus());
			response.setProfilePicture(user.getProfilePicture());

			List<ReviewsAndRatingsResponseDto> reviewsAndRatingsResponse = new ArrayList<>();

			List<UserRatingAndReview> reviewAndRatings = getServiceRegistry().getUserRatingAndReviewService()
					.findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(user);

			if (!reviewAndRatings.isEmpty()) {
				reviewAndRatings.forEach(reviewRating -> {
					ReviewsAndRatingsResponseDto dto = new ReviewsAndRatingsResponseDto();
					BeanUtils.copyProperties(reviewRating, dto);
					User user1 = reviewRating.getReviewerUser();
					dto.setReviewerId(user1.getId());
					dto.setReviewerName(user1.getCallName());
					dto.setContact(user1.getContactNumber());
					dto.setEmail(user1.getEmail());
					dto.setProfile(user1.getProfilePicture());
					reviewsAndRatingsResponse.add(dto);
				});
			}
			response.setReviewsAndRatings(reviewsAndRatingsResponse);

			List<CardDetailsResponseDto> cardDetailsResponseDtos = new ArrayList<>();
			List<CardDetails> cardDetails = getServiceRegistry().getCardDetailsService().findByUserAndActiveTrue(user);

			if (!cardDetails.isEmpty()) {
				cardDetails.forEach(cardDetail -> {
					CardDetailsResponseDto cardDetailsResponseDto = new CardDetailsResponseDto();
					BeanUtils.copyProperties(cardDetail, cardDetailsResponseDto);
					cardDetailsResponseDtos.add(cardDetailsResponseDto);
				});
			}
			response.setCardDetails(cardDetailsResponseDtos);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.BAN_STATUS + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateBanStatus(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			CallerProfile caller = getServiceRegistry().getCallerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (caller == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CALLER_NOT_EXIST.getCode()));
			}
			User user = caller.getUser();
			if (user.getIsSuspended()) {
				user.setIsSuspended(false);
				getServiceRegistry().getUserService().saveORupdate(user);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CALLER_UN_BANNED_SUCCESSFULLY.getCode()));
			}

			else if (!user.getIsSuspended()) {
				user.setIsSuspended(true);
				getServiceRegistry().getUserService().saveORupdate(user);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CALLER_BANNED_SUCCESSFULLY.getCode()));
			} else if (user.getIsSuspended() == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(
					getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.CALLER_NOT_EXIST.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> hardDeleteCategory(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			CallerProfile caller = getServiceRegistry().getCallerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (caller == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CALLER_NOT_EXIST.getCode()));
			}
			caller.setActive(false);
			User user = getServiceRegistry().getUserService().findOne(caller.getUser().getId());
			user.setActive(false);
			getServiceRegistry().getUserService().saveORupdate(user);
			getServiceRegistry().getCallerProfileService().saveORupdate(caller);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CALLER_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@Hidden
	@PostMapping(ApplicationURIConstants.BOOKEDCALLS + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getBookedCallsList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
					.findByCallRequestStatusAndCallStatusAndPaymentStatusAndActiveTrueOrderByCreatedAtDesc(
							ApplicationConstants.ACCEPTED, ApplicationConstants.COMPLETED, ApplicationConstants.PAID);
			if (bookedCalls.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_CALLS_FOUND.getCode()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
		return null;
	}

}
