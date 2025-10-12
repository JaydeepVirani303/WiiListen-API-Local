package com.wiilisten.controller.api.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.stripe.model.Payout;
import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.AdministrativeNotification;
import com.wiilisten.entity.BlockedUser;
import com.wiilisten.entity.ListenerAvailability;
import com.wiilisten.entity.ListenerBankDetails;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.IdStatusRequestDto;
import com.wiilisten.response.BlockedUserResponseDto;
import com.wiilisten.response.ListenerAvailabilityResponseDto;
import com.wiilisten.response.ListenerResponseDto;
import com.wiilisten.response.ReviewsAndRatingsResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.LISTENER)
public class ApiV1AdminListenerController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminListenerController.class);

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getListenerList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			// Page<ListenerProfile> listenerProfiles =
			// getServiceRegistry().getListenerProfileService()
			// .findByActiveTrue(pageable);
			List<ListenerProfile> listenerProfiles = getServiceRegistry().getListenerProfileService()
					.findByActiveTrueOrderByIdDesc();
			List<ListenerResponseDto> response = new ArrayList<>();
			listenerProfiles.forEach(listener -> {
				ListenerResponseDto listenerProfileDTO = new ListenerResponseDto();
				BeanUtils.copyProperties(listener, listenerProfileDTO);
				BeanUtils.copyProperties(listener.getUser(), listenerProfileDTO);
				listenerProfileDTO.setUserId(listener.getUser().getId());
				listenerProfileDTO.setListnerId(listener.getId());
				response.add(listenerProfileDTO);
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
	public ResponseEntity<Object> getSpecificListnerDetails(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (listener == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_EXIST.getCode()));
			}
			ListenerBankDetails bankDetails = getServiceRegistry().getListenerBankDetailsService()
					.findByUserAndActiveTrue(listener.getUser());
			ListenerResponseDto response = new ListenerResponseDto();
			BeanUtils.copyProperties(listener, response);
			BeanUtils.copyProperties(listener.getUser(), response);
			if (bankDetails != null) {
				BeanUtils.copyProperties(bankDetails, response);
			}

			response.setUserId(listener.getUser().getId());
			response.setListnerId(listener.getId());
			response.setW9sUrl(listener.getW9Form());
			if (bankDetails != null) {
				response.setBankDetailsId(bankDetails.getId());
			}

			List<ListenerAvailability> listenerAvailabilities = getServiceRegistry().getListenerAvailabilityService()
					.findByUserAndActiveTrue(listener.getUser());
			if (!listenerAvailabilities.isEmpty()) {
				List<ListenerAvailabilityResponseDto> listenerAvailabiliy = new ArrayList<>();
				listenerAvailabilities.forEach(user -> {
					ListenerAvailabilityResponseDto listenerResponseDto = new ListenerAvailabilityResponseDto();
					BeanUtils.copyProperties(user, listenerResponseDto);
					listenerAvailabiliy.add(listenerResponseDto);
				});
				response.setListenerAvailabilities(listenerAvailabiliy);
			}

			List<ReviewsAndRatingsResponseDto> reviewsAndRatingsResponse = new ArrayList<>();

			List<UserRatingAndReview> reviewAndRatings = getServiceRegistry().getUserRatingAndReviewService()
					.findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(listener.getUser());

			if (!reviewAndRatings.isEmpty()) {
				reviewAndRatings.forEach(reviewRating -> {
					ReviewsAndRatingsResponseDto dto = new ReviewsAndRatingsResponseDto();
					BeanUtils.copyProperties(reviewRating, dto);
					User user = reviewRating.getReviewerUser();
					dto.setReviewerId(user.getId());
					dto.setReviewerName(user.getCallName());
					dto.setContact(user.getContactNumber());
					dto.setEmail(user.getEmail());
					dto.setProfile(user.getProfilePicture());
					reviewsAndRatingsResponse.add(dto);
				});
			}
			response.setReviewsAndRatings(reviewsAndRatingsResponse);

			List<BlockedUserResponseDto> blockedUserResponseDtos = new ArrayList<>();
			List<BlockedUser> blockedUsers = getServiceRegistry().getBlockedUserService()
					.findByBlockedUserAndActiveTrueAndType(listener.getUser(), "REPORTED");

			if (!blockedUsers.isEmpty()) {
				blockedUsers.forEach(blockedUser -> {
					BlockedUserResponseDto dto = new BlockedUserResponseDto();
					User user = blockedUser.getBlockerUser();
					BeanUtils.copyProperties(blockedUser, dto);
					dto.setBlockerId(user.getId());
					dto.setBlockerName(user.getCallName());
					dto.setContact(user.getContactNumber());
					dto.setEmail(user.getEmail());
					dto.setProfile(user.getProfilePicture());
					dto.setReason(blockedUser.getReason());
					blockedUserResponseDtos.add(dto);
					;
				});
			}
			response.setReports(blockedUserResponseDtos);

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
			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (listener == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CALLER_NOT_EXIST.getCode()));
			}
			User user = listener.getUser();
			if (user.getIsSuspended()) {
				user.setIsSuspended(false);
				getServiceRegistry().getUserService().saveORupdate(user);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
						SuccessMsgEnum.LISTENER_UN_BANNED_SUCCESSFULLY.getCode()));
			}

			else if (!user.getIsSuspended()) {
				user.setIsSuspended(true);
				getServiceRegistry().getUserService().saveORupdate(user);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.LISTENER_BANNED_SUCCESSFULLY.getCode()));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(
					getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_EXIST.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.REGISTER_STATUS + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateRegisterStatus(@RequestBody IdStatusRequestDto idStatusRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(idStatusRequestDto.getId());
			if (listener == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CALLER_NOT_EXIST.getCode()));
			}
			listener.setProfileStatus(idStatusRequestDto.getStatus());
			getServiceRegistry().getListenerProfileService().saveORupdate(listener);

			AdministrativeNotification administrativeNotification = new AdministrativeNotification();
			Map<String, String> payload = new HashMap<>();

			if (listener.getUser().getNotificationStatus() && listener.getUser().getIsLoggedIn()) {
				administrativeNotification.setTitle(ApplicationConstants.PROFILE_UPDATED);
				administrativeNotification.setContent(ApplicationConstants.REGISTRATION_ACCEPTED);
				administrativeNotification.setUsers(Collections.singletonList(listener.getUser()));
				administrativeNotification.setTags(ApplicationConstants.PROFILE_UPDATED);
				administrativeNotification.setActive(true);
				getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

				payload.put(ApplicationConstants.TITLE, ApplicationConstants.PROFILE_UPDATED);
				payload.put(ApplicationConstants.BODY, ApplicationConstants.REGISTRATION_ACCEPTED);
				payload.put(ApplicationConstants.TAG, ApplicationConstants.PROFILE_UPDATED);
				String receiverDeviceToken = listener.getUser().getDeviceToken();
				if (receiverDeviceToken != null) {
					// Send push notification using FCM
					getServiceRegistry().getFcmService().sendPushNotification(receiverDeviceToken, payload);
				}
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.LISTENER_PROFILE_STATUS_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.REGISTER_STATUS + ApplicationURIConstants.PENDING
			+ ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getRegisterStatusList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			// Page<ListenerProfile> pendingStatusList =
			// getServiceRegistry().getListenerProfileService()
			// .findByProfileStatusAndActiveTrue(ApplicationConstants.PENDING, pageable);
			List<ListenerProfile> pendingStatusList = getServiceRegistry().getListenerProfileService()
					.findByProfileStatusAndActiveTrueOrderByIdDesc(ApplicationConstants.PENDING);
			List<ListenerResponseDto> response = new ArrayList<>();
			pendingStatusList.forEach(listener -> {
				ListenerResponseDto listenerProfileDTO = new ListenerResponseDto();
				BeanUtils.copyProperties(listener, listenerProfileDTO);
				BeanUtils.copyProperties(listener.getUser(), listenerProfileDTO);
				listenerProfileDTO.setUserId(listener.getUser().getId());
				listenerProfileDTO.setListnerId(listener.getId());
				response.add(listenerProfileDTO);
			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
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

			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (listenerProfile == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_EXIST.getCode()));
			}
			listenerProfile.setActive(false);
			User user = getServiceRegistry().getUserService().findOne(listenerProfile.getUser().getId());
			user.setActive(false);
			getServiceRegistry().getUserService().saveORupdate(user);
			getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.LISTENER_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.PAYMENT)
	public ResponseEntity<Object> getPaymentDetails(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (listener == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_EXIST.getCode()));
			}
			ListenerBankDetails bankDetails = getServiceRegistry().getListenerBankDetailsService()
					.findByUserAndActiveTrue(listener.getUser());
			ListenerResponseDto response = new ListenerResponseDto();

			if (bankDetails == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.BANK_DETAILS_NOT_EXIST.getCode()));
			}

			Double total = listener.getTotalEarning();
			Double withdrawn = listener.getTotalPaidEarning();

			Double totalEarnings = total - withdrawn;
			response.setTotalEarning(totalEarnings);

			Map<String, String> account = new HashMap<>();
			account.put("account_number", bankDetails.getAccountNumber());
			account.put("routing_number", bankDetails.getRoutingOrAbaNumber());
			account.put("account_holder_name", listener.getUserName());
			account.put("account_holder_type", bankDetails.getAccountType());
			account.put("currency", "usd");

			Map<String, Object> accountdetail = getServiceRegistry().getPaymentService()
					.createConnectedAccount(account);

			System.err.println("accountdetail:>>>>>>>>>>> " + accountdetail);
			if (accountdetail.get("failure_code") != null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.PAYMENT_FAILED.getCode()));
			} else {

				Long earnings = (Long) totalEarnings.longValue();

				Map<String, Object> responseMap = new HashMap<>();
				responseMap.put("account", accountdetail);
				responseMap.put("totalEarnings", earnings);
				

				Map<String, Object> transfer = getServiceRegistry().getPaymentService().createTransfer(responseMap);
				
				System.err.println("transfer:>>>>>>>>>>>> " + transfer);

				if (transfer.get("failure_code") != null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.PAYMENT_FAILED.getCode()));
				} else {

					Map<String, Object> transferResponse = new HashMap<>();
					transferResponse.put("transfer", transfer);
					transferResponse.put("totalEarnings", totalEarnings);

					Map<String, Object> payout = getServiceRegistry().getPaymentService()
							.createPayout(transferResponse);
					
					System.err.println("payout:>>>>>>>>>>>>> " + payout);

					if (payout.get("failure_code") != null) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices()
								.generateBadResponseWithMessageKey(ErrorDataEnum.PAYMENT_FAILED.getCode()));
					} else {

						listener.setTotalPaidEarning(0D);
						listener.setPaymentlog(payout.toString());
						getServiceRegistry().getListenerProfileService().saveORupdate(listener);

						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

}
