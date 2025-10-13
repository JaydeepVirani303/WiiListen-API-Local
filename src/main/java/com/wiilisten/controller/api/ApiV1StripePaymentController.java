package com.wiilisten.controller.api;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.wiilisten.entity.*;
import com.wiilisten.enums.CouponType;
import com.wiilisten.repo.CouponsRepository;
import com.wiilisten.request.ApplyCouponRequest;
import com.wiilisten.service.CouponsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.wiilisten.controller.BaseController;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.IdStatusRequestDto;
import com.wiilisten.response.BookedCallDetailsDto;
import com.wiilisten.response.PaymentIntenetResponseDto;
import com.wiilisten.service.PaymentService;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.FCMService;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.COMMON)
public class ApiV1StripePaymentController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1HomeController.class);

	@Value("${stripe.SecretKey}")
	private String StripeKey;
	@Value("${stripe.PublishableKey}")
	private String PublishableKey;

	@Autowired
	private FCMService fcmService;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private CouponsRepository couponsRepository;

	@Autowired
	private CouponsService couponsService;

	@PostMapping(ApplicationURIConstants.PAYMENT_INTENT)
	public ResponseEntity<Object> createPaymentIntent(@RequestBody BookedCallDetailsDto bookedCallDetailsDto)
			throws Exception {
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		User user = null;
		Double finalamount = 0D;
		ListenerProfile listener = null;

		user = getLoggedInUser();

		if (!bookedCallDetailsDto.getCallType().equalsIgnoreCase(ApplicationConstants.QUICK_CALL)) {
			listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(bookedCallDetailsDto.getListenerId());
		} else {
			Optional<ListenerProfile> findRandomListenerProfile = getServiceRegistry().getListenerProfileService()
					.findRandomListenerProfile();
			if (!findRandomListenerProfile.isPresent()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.NO_ACTIVE_LISTENER_FOUND.getCode()));
			}
			listener = findRandomListenerProfile.get();
		}
		if (listener == null) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(
					getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
		}
		double subTotal;
		if (bookedCallDetailsDto.getCallType().equalsIgnoreCase(ApplicationConstants.QUICK_CALL)
				|| (bookedCallDetailsDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND))) {
//			subTotal = 1.2D * Double.parseDouble(listener.getCallMaxDuration());
			subTotal = 1.2D * Double.parseDouble(listener.getCallMaxDuration()) * listener.getRatePerMinute();
		} else {
			subTotal = listener.getRatePerMinute() * bookedCallDetailsDto.getDurationInMinutes();
		}
		finalamount = subTotal;
		if (bookedCallDetailsDto.getTaxValue() != null) {
			finalamount = finalamount + bookedCallDetailsDto.getTaxValue();
		}
		if (bookedCallDetailsDto.getDiscountValue() != null) {
			finalamount = finalamount - bookedCallDetailsDto.getDiscountValue();
		}

		finalamount = Math.round(finalamount * 100.0) / 100.0;

		PaymentIntenetResponseDto paymentIntenetResponseDto = new PaymentIntenetResponseDto();
		if (user.getStripeCustomerId() == null) {
			// create customerid if not exist
			Customer customer = getServiceRegistry().getPaymentService().createStripeCustomer(user);
			if (customer != null) {
				user.setStripeCustomerId(customer.getId());
				user = getServiceRegistry().getUserService().saveORupdate(user);
			}
		}

		// create ephemeralKey
		String ephemeralKey = getServiceRegistry().getPaymentService().getEphemeralKey(user.getStripeCustomerId());
		PaymentIntent intent = null;

		Long couponId = bookedCallDetailsDto.getCouponId();
		boolean exists = false;
		if (couponId != null) {
			exists = couponsRepository.findById(couponId).isPresent();
		}
		long amount = finalamount.longValue();

		if (exists) {
			Coupons optionalCoupon = couponsRepository.findById(couponId).get();
			CouponType couponType = optionalCoupon.getCouponType();
			double couponDiscount = 0;

			if (couponType.equals(CouponType.FLAT)) {
				couponDiscount = optionalCoupon.getCouponAmount();
//				amount = (long) (amount - couponDiscount);
				amount = (long) (amount - (couponDiscount * 100));
			} else if (couponType.equals(CouponType.PERCENTAGE)) {
				couponDiscount = optionalCoupon.getCouponAmount();
				double discount = (amount * couponDiscount) / 100;
				amount = (long) (amount - discount);
			}
			intent = getServiceRegistry().getPaymentService().createPaymentIntenet(amount,
					user.getStripeCustomerId());
			//here marked as use coupon
			ApplyCouponRequest request = new ApplyCouponRequest(user.getId(), bookedCallDetailsDto.getCouponId());
			couponsService.applyCoupon(request);
		} else {
			intent = getServiceRegistry().getPaymentService().createPaymentIntenet(amount,
					user.getStripeCustomerId());
		}



		// paymentIntent create and hold

		if (intent != null) {
			user.setPaymentIntent(intent.getId());
			user = getServiceRegistry().getUserService().saveORupdate(user);
		}
		if (!bookedCallDetailsDto.getCallType().equalsIgnoreCase(ApplicationConstants.SCHEDULED)) {
			CallerProfile callerProfile = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);

			BookedCalls bookedCall = new BookedCalls();
			bookedCall.setCouponId(bookedCallDetailsDto.getCouponId());
			bookedCall.setCaller(callerProfile);
			bookedCall.setListener(listener);
			bookedCall.setActive(true);
			bookedCall.setCallerJoinedAt(LocalDateTime.now());
			bookedCall.setSponsored(bookedCallDetailsDto.getSponsored());
			bookedCall.setBookingDateTime(LocalDateTime.now().withSecond(0).withNano(0));
			if (bookedCallDetailsDto.getDurationInMinutes() != null) {
				bookedCall.setDurationInMinutes(bookedCallDetailsDto.getDurationInMinutes());
			} else {
				bookedCall.setDurationInMinutes(0L);
			}
			bookedCall.setCallRequestStatus(ApplicationConstants.PENDING);
			bookedCall.setCallStatus(ApplicationConstants.SCHEDULED);
			bookedCall.setType(bookedCallDetailsDto.getCallType());
			bookedCall.setPaymentlog((intent.toJson()).toString());
			bookedCall.setPaymentStatus(ApplicationConstants.HOLD);
			bookedCall.setPaymentIntent(intent.getId());

			if (bookedCallDetailsDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND)) {
				bookedCall.setActive(false);
			}

			getServiceRegistry().getBookedCallsService().saveORupdate(bookedCall);
			paymentIntenetResponseDto.setBookingId(bookedCall.getId());
		}

		paymentIntenetResponseDto.setCustomerId(user.getStripeCustomerId());
		paymentIntenetResponseDto.setEphemeralKey(ephemeralKey);
		paymentIntenetResponseDto.setPayamentIntentId(intent.getId());
		paymentIntenetResponseDto.setPayamentIntenetClientSecretKey(intent.getClientSecret());
		paymentIntenetResponseDto.setPublishableKey(PublishableKey);
		paymentIntenetResponseDto.setStripeSecretKey(StripeKey);
		paymentIntenetResponseDto.setPaymentIntent(intent);
		paymentIntenetResponseDto.setPaymentSuccess(false);
		paymentIntenetResponseDto.setListenerId(listener.getId());
		paymentIntenetResponseDto.setCallMaxDuration(listener.getCallMaxDuration());
		paymentIntenetResponseDto.setFinalAmount((double) amount);

		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(paymentIntenetResponseDto));

	}

	@PostMapping(ApplicationURIConstants.PAYMENT_INTENT + ApplicationURIConstants.SUBSCRIPTION)
	public ResponseEntity<Object> createPaymentForSubscription(@RequestBody IdStatusRequestDto idRequestDto)
			throws Exception {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		Subscription subscription = getServiceRegistry().getSubscriptionService()
				.findByIdAndIsDeletedFalseAndActiveTrue(idRequestDto.getId());
		if (subscription == null) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
		}

		// PaymentIntent intent = getServiceRegistry().getPaymentService()
		// 		.createSubscriptionPaymentIntenet(subscription.getPrice().longValue() * 100);

		// if (intent == null) {
		// 	LOGGER.info(ApplicationConstants.EXIT_LABEL);
		// 	return ResponseEntity.ok(getCommonServices()
		// 			.generateBadResponseWithMessageKey(ErrorDataEnum.PAYMENT_UNSUCCESSFULL.getCode()));
		// }

		User user = getLoggedInUser();

		// Check if user already has an active subscription of same type that is not expired
		UserSubscription existingSubscription = getServiceRegistry().getUserSubscriptionService()
				.findTopByUserAndTypeAndActiveTrueOrderByIdDesc(user, idRequestDto.getType());

		if (existingSubscription != null && existingSubscription.getExpiryDate() != null &&
				existingSubscription.getExpiryDate().isAfter(LocalDateTime.now())) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PLAN_ALREADY_SUBSCRIBED.getCode()));
		}

		if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
			CallerProfile callerProfile = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			if (callerProfile.getSearchSubscriptionStatus().equalsIgnoreCase(ApplicationConstants.ACTIVE)) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PLAN_ALREADY_SUBSCRIBED.getCode()));
			}
			callerProfile.setSearchSubscriptionStatus(ApplicationConstants.ACTIVE);
			getServiceRegistry().getCallerProfileService().saveORupdate(callerProfile);
		}
		if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByUserAndActiveTrue(user);
			if (listenerProfile.getIsEligibleForPremiumCallSearch()
					&& idRequestDto.getType().equals(ApplicationConstants.SUBSCRIPTION)) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PLAN_ALREADY_SUBSCRIBED.getCode()));
			}
			if (listenerProfile.getIsAdvertisementActive()
					&& idRequestDto.getType().equals(ApplicationConstants.ADVERTISEMENT)) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PLAN_ALREADY_SUBSCRIBED.getCode()));
			}
			if (idRequestDto.getType().equals(ApplicationConstants.SUBSCRIPTION)) {
				listenerProfile.setIsEligibleForPremiumCallSearch(true);
			} else if (idRequestDto.getType().equals(ApplicationConstants.ADVERTISEMENT)) {
				listenerProfile.setIsAdvertisementActive(true);
			}

			getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);
		}
		UserSubscription userSubscription = new UserSubscription();
		userSubscription.setUser(user);
		userSubscription.setSubscription(subscription);
		userSubscription.setType(idRequestDto.getType());
		userSubscription.setActive(true);
		int duration = subscription.getDurationInDays() != null ? subscription.getDurationInDays() : 0;
		userSubscription.setExpiryDate(LocalDateTime.now().plusDays(duration));
		getServiceRegistry().getUserSubscriptionService().saveORupdate(userSubscription);

		List<User> users = getServiceRegistry().getUserService()
				.findByRoleAndActiveTrueAndIsSuspendedFalseOrderByIdDesc(UserRoleEnum.CALLER.getRole());

		AdministrativeNotification administrativeNotification = new AdministrativeNotification();
		Map<String, String> payload = new HashMap<>();
		if (userSubscription.getType().equalsIgnoreCase(ApplicationConstants.ADVERTISEMENT)) {

			// Add advertisement in earning history
			EarningHistory earningHistory = new EarningHistory();
			earningHistory.setMetadata("ABC");
			earningHistory.setAmount(subscription.getPrice());
			earningHistory.setPaymentStatus(ApplicationConstants.DEPOSITED);
			earningHistory.setReason(ApplicationConstants.ADVERTISEMENT);
			earningHistory.setUser(user);
			getServiceRegistry().getEarningHistoryService().saveORupdate(earningHistory);

			administrativeNotification.setTitle(ApplicationConstants.PAID_ADVERTISEMENT_ADDED);
			administrativeNotification.setContent(
					"Your " + userSubscription.getSubscription().getType().toLowerCase() + "paid advertisement enable");
			administrativeNotification.setUsers(Collections.singletonList(user));
			administrativeNotification.setTags(ApplicationConstants.PAID_ADVERTISEMENT_ADDED);
			administrativeNotification.setActive(true);
			getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

			payload.put(ApplicationConstants.TITLE, ApplicationConstants.PAID_ADVERTISEMENT_ADDED);
			payload.put(ApplicationConstants.BODY,
					"Your " + userSubscription.getSubscription().getType().toLowerCase() + "paid advertisement enable");
			payload.put(ApplicationConstants.TAG, ApplicationConstants.ADVERTISEMENT_EXPIRE);
			String receiverDeviceToken = user.getDeviceToken();
			if (receiverDeviceToken != null) {
				// Send push notification using FCM
				System.err.println("Receiver device token: " + receiverDeviceToken);
				fcmService.sendPushNotification(receiverDeviceToken, payload);
			}

			administrativeNotification.setTitle("Sponsor listener added");
			administrativeNotification.setContent("New sponsor listener added with name " + user.getCallName());
			administrativeNotification.setUsers(users);
			administrativeNotification.setTags("Sponsor listener added");
			administrativeNotification.setActive(true);
			getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

			payload.put(ApplicationConstants.TITLE, "Sponsor listener added");
			payload.put(ApplicationConstants.BODY, "New sponsor listener added with name " + user.getCallName());
			payload.put(ApplicationConstants.TAG, "Sponsor listener added");
			users.forEach(userToken -> {
				String reciverDevice = userToken.getDeviceToken();
				if (reciverDevice != null) {
					// Send push notification using FCM
					try {
						fcmService.sendPushNotification(reciverDevice, payload);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

		} else if (userSubscription.getType().equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
			administrativeNotification.setTitle(ApplicationConstants.SUBSCRIPTION_ADDED);
			administrativeNotification.setContent(
					"Your  " + userSubscription.getSubscription().getType().toLowerCase() + " subscription enable");
			administrativeNotification.setUsers(Collections.singletonList(user));
			administrativeNotification.setTags(ApplicationConstants.SUBSCRIPTION_ADDED);
			administrativeNotification.setActive(true);
			getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

			payload.put(ApplicationConstants.TITLE, ApplicationConstants.SUBSCRIPTION_ADDED);
			payload.put(ApplicationConstants.BODY,
					"Your  " + userSubscription.getSubscription().getType().toLowerCase() + " subscription enable");
			payload.put(ApplicationConstants.TAG, ApplicationConstants.SUBSCRIPTION_ADDED);
			String receiverDeviceToken = user.getDeviceToken();
			if (receiverDeviceToken != null) {
				// Send push notification using FCM
				fcmService.sendPushNotification(receiverDeviceToken, payload);
			}
		}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(SuccessMsgEnum.PLAN_ADDED_SUCCESSFULLY.getCode()));	

	}

}