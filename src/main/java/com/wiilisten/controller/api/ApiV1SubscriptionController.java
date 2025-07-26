package com.wiilisten.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.Subscription;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserSubscription;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.IdStatusRequestDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.SUBSCRIPTION)
public class ApiV1SubscriptionController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1SubscriptionController.class);

	@PostMapping(ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addSubscription(@RequestBody IdStatusRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Subscription subscription = getServiceRegistry().getSubscriptionService()
					.findByIdAndIsDeletedFalseAndActiveTrue(idRequestDto.getId());
			if (subscription == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
			}
			User user = getLoggedInUser();
			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				CallerProfile callerProfile = getServiceRegistry().getCallerProfileService()
						.findByUserAndActiveTrue(user);
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
				if (listenerProfile.getIsEligibleForPremiumCallSearch()) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PLAN_ALREADY_SUBSCRIBED.getCode()));
				}else if(listenerProfile.getIsAdvertisementActive()) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PLAN_ALREADY_SUBSCRIBED.getCode()));
				}
				
				if(idRequestDto.getType().equals(ApplicationConstants.SUBSCRIPTION)) {
					listenerProfile.setIsEligibleForPremiumCallSearch(true);
				}else if(idRequestDto.getType().equals(ApplicationConstants.ADVERTISEMENT)) {
					listenerProfile.setIsAdvertisementActive(true);
				}
				
				getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);
			}
			UserSubscription userSubscription = new UserSubscription();
			userSubscription.setUser(user);
			userSubscription.setSubscription(subscription);
			userSubscription.setType(idRequestDto.getType());
			userSubscription.setActive(false);

			userSubscription=getServiceRegistry().getUserSubscriptionService().saveORupdate(userSubscription);
			
			IdRequestDto response=new IdRequestDto();
			response.setId(userSubscription.getId());
			
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

//	@PostMapping(ApplicationURIConstants.LIST)
//	public ResponseEntity<Object> getSubscriptionList() {
//
//		LOGGER.info(ApplicationConstants.ENTER_LABEL);
//
//		try {
//			List<Subscription> subscriptions = getServiceRegistry().getSubscriptionService()
//					.findByIsDeletedFalseAndActiveTrueOrderByIdDesc();
//			if (subscriptions.isEmpty()) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices()
//						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
//		}
//	}

}
