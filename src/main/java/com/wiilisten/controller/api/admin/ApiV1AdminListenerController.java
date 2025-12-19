package com.wiilisten.controller.api.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import com.stripe.param.AccountLinkCreateParams;
import com.stripe.param.PayoutCreateParams;
import com.stripe.param.TransferCreateParams;
import com.wiilisten.entity.*;
import com.wiilisten.enums.KycStatus;
import com.wiilisten.repo.ListenerProfileRepository;
import com.wiilisten.repo.PaymentStatusHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amazonaws.services.dynamodbv2.xspec.M;
import com.wiilisten.controller.BaseController;
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

	@Value("${stripe.SecretKey}")
	private String StripeKey;

	@Autowired
	PaymentStatusHistoryRepository paymentStatusHistoryRepository;

	@Autowired
	ListenerProfileRepository listenerProfileRepository;


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
//			ListenerBankDetails bankDetails = getServiceRegistry().getListenerBankDetailsService()
//					.findByUserAndActiveTrue(listener.getUser());
			ListenerResponseDto response = new ListenerResponseDto();
			BeanUtils.copyProperties(listener, response);
			BeanUtils.copyProperties(listener.getUser(), response);
//			if (bankDetails != null) {
//				BeanUtils.copyProperties(bankDetails, response);
//			}

			response.setUserId(listener.getUser().getId());
			response.setListnerId(listener.getId());
			response.setW9sUrl(listener.getW9Form());
//			if (bankDetails != null) {
//				response.setBankDetailsId(bankDetails.getId());
//			}

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
	public ResponseEntity<Object> payEarningsManually(@RequestBody IdRequestDto idRequestDto) {
		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Stripe.apiKey = StripeKey;
			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());

			if (listener == null) {
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_EXIST.getCode()));
			}

			User user = listener.getUser();
			if (user.getStripeAccountId() == null) {
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.STRIPE_ACCOUNT_NOT_FOUND.getCode()));
			}

			if (user.getExternalAccountId() == null) {
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.BANK_DETAILS_NOT_EXIST.getCode()));
			}

			// --- Step 1: Check Platform Balance ---
			Balance balance = Balance.retrieve();
			long available = balance.getAvailable().stream()
					.filter(b -> "usd".equalsIgnoreCase(b.getCurrency()))
					.mapToLong(Balance.Available::getAmount)
					.sum();

			if (available <= 0) {
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.INSUFFICIENT_BALANCE.getCode()));
			}

			// --- Step 2: Calculate Amount ---
			Double totalEarnings = listener.getTotalEarning() != null ? listener.getTotalEarning() : 0.0;
			Double totalPaidEarnings = listener.getTotalPaidEarning() != null ? listener.getTotalPaidEarning() : 0.0;

			Double remainingEarnings = 0.0;
			if (totalEarnings > totalPaidEarnings) {
				remainingEarnings = totalEarnings - totalPaidEarnings;
			}

			long amountInCents = Math.round(remainingEarnings * 100);

			if (available < amountInCents) {
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.INSUFFICIENT_BALANCE.getCode()));
			}

			// --- Step 3: Transfer (Platform → Connected Account) ---
			TransferCreateParams transferParams = TransferCreateParams.builder()
					.setAmount(amountInCents)
					.setCurrency("usd")
					.setDestination(user.getStripeAccountId())
					.setDescription("Manual payout for listener earnings ID: " + listener.getId())
					.build();

			Transfer transfer = Transfer.create(transferParams);
			LOGGER.info("Transfer successful, ID: {}", transfer.getId());

			// --- Step 4: Payout (Connected Account → Bank) ---
			RequestOptions connectedAccountOpts = RequestOptions.builder()
					.setStripeAccount(user.getStripeAccountId())
					.build();

			PayoutCreateParams payoutParams = PayoutCreateParams.builder()
					.setAmount(amountInCents)
					.setCurrency("usd")
					.setDescription("Payout to bank for listener ID: " + listener.getId())
					.build();

			Payout payout = Payout.create(payoutParams, connectedAccountOpts);
			LOGGER.info("Payout successful, ID: {}", payout.getId());

			// --- Step 5: Update DB ---
			if (listener.getTotalPaidEarning() == null) {
				listener.setTotalPaidEarning(remainingEarnings);
			} else {
				listener.setTotalPaidEarning(listener.getTotalPaidEarning() + remainingEarnings);
			}
			listener.setPaymentlog("Transfer: " + transfer.getId() + ", Payout: " + payout.getId());
			getServiceRegistry().getListenerProfileService().saveORupdate(listener);

			// --- Step 6: Return Response ---
			Map<String, Object> response = Map.of(
					"listenerId", listener.getId(),
					"amountPaid", remainingEarnings,
					"currency", "USD"
			);
			listenerProfileRepository.save(listener);
			savePaymentHistory(new PaymentStatusHistory(payout.getId(), transfer.getId(), listener.getId(), (double) amountInCents / 100, "USD", listener.getUserName(), user.getEmail(), user.getContactNumber()));
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

		} catch (StripeException e) {
			LOGGER.error("Stripe error during payout: {}", e.getMessage(), e);
			return ResponseEntity.ok(getCommonServices()
					.generateBadResponseWithMessageKey(ErrorDataEnum.STRIPE_API_ERROR.getCode()));
		} catch (Exception e) {
			LOGGER.error("Unexpected error: {}", e.getMessage(), e);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	public PaymentStatusHistory savePaymentHistory(PaymentStatusHistory payment) {
		return paymentStatusHistoryRepository.save(payment);
	}
}
