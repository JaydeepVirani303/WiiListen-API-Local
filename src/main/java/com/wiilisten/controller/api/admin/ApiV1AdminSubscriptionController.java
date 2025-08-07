package com.wiilisten.controller.api.admin;

import java.util.ArrayList;
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

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.Subscription;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.SubscriptionRequestDto;
import com.wiilisten.response.SubscriptionResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.SUBSCRIPTION)
public class ApiV1AdminSubscriptionController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminSubscriptionController.class);

	@PostMapping(ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addSubscription(@RequestBody SubscriptionRequestDto subscriptionRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Subscription subscription = new Subscription();
			BeanUtils.copyProperties(subscriptionRequestDto, subscription);
			subscription.setActive(true);
			subscription.setIsDeleted(false);
			subscription.setCategory(ApplicationConstants.SUBSCRIPTION);
			subscription.setDurationInDays(subscriptionRequestDto.getDurationInDays());
			// if value is not added from front end
			if (subscriptionRequestDto.getDurationInDays() == 0) {
				Map<String, Integer> durationMap = Map.of(
						ApplicationConstants.MONTHLY, 30,
						ApplicationConstants.YEARLY, 365,
						ApplicationConstants.WEEKLY, 7
				);

				String type = subscriptionRequestDto.getType();
				int duration = durationMap.getOrDefault(type, 0);
				subscription.setDurationInDays(duration);
			}
			getServiceRegistry().getSubscriptionService().saveORupdate(subscription);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.SUBSCRIPTION_PLAN_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getSubscriptionList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<Subscription> subscriptions = getServiceRegistry().getSubscriptionService()
					.findByIsDeletedFalseOrderByIdDesc();
			if (subscriptions.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
			}
			List<SubscriptionResponseDto> response = new ArrayList<>();
			subscriptions.forEach(subscription -> {
				SubscriptionResponseDto dto = new SubscriptionResponseDto();
				BeanUtils.copyProperties(subscription, dto);
				response.add(dto);
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
	public ResponseEntity<Object> getSpecificSubscription(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Subscription subscription = getServiceRegistry().getSubscriptionService()
					.findByIdAndIsDeletedFalse(idRequestDto.getId());
			if (subscription == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
			}
			SubscriptionResponseDto response = new SubscriptionResponseDto();
			BeanUtils.copyProperties(subscription, response);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateSubscription(@RequestBody SubscriptionRequestDto subscriptionRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Subscription subscription = getServiceRegistry().getSubscriptionService()
					.findByIdAndIsDeletedFalse(subscriptionRequestDto.getId());
			if (subscription == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
			}
			BeanUtils.copyProperties(subscriptionRequestDto, subscription,
					getCommonServices().getNullPropertyNames(subscriptionRequestDto));
			subscription.setDurationInDays(subscriptionRequestDto.getDurationInDays());
			// if value is not added from front end
			if (subscriptionRequestDto.getDurationInDays() == 0) {
				Map<String, Integer> durationMap = Map.of(
						ApplicationConstants.MONTHLY, 30,
						ApplicationConstants.YEARLY, 365,
						ApplicationConstants.WEEKLY, 7
				);

				String type = subscriptionRequestDto.getType();
				int duration = durationMap.getOrDefault(type, 0);
				subscription.setDurationInDays(duration);
			}
			getServiceRegistry().getSubscriptionService().saveORupdate(subscription);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.SUBSCRIPTION_PLAN_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.ACTIVE_STATUS + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateActiveStatus(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Subscription subscription = getServiceRegistry().getSubscriptionService()
					.findByIdAndIsDeletedFalse(idRequestDto.getId());
			if (subscription == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
			}

			// If subscription is active then inactive it or vice-versa
			if (subscription.getActive()) {
				subscription.setActive(false);
				getServiceRegistry().getSubscriptionService().saveORupdate(subscription);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
						SuccessMsgEnum.SUBSCRIPTION_PLAN_INACTIVE_SUCCESSFULLY.getCode()));
			}

			else if (!subscription.getActive()) {
				subscription.setActive(true);
				getServiceRegistry().getSubscriptionService().saveORupdate(subscription);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
						SuccessMsgEnum.SUBSCRIPTION_PLAN_ACTIVE_SUCCESSFULLY.getCode()));

			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteSubscription(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Subscription subscription = getServiceRegistry().getSubscriptionService()
					.findByIdAndIsDeletedFalse(idRequestDto.getId());
			if (subscription == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
			}
			subscription.setIsDeleted(true);
			getServiceRegistry().getSubscriptionService().saveORupdate(subscription);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.SUBSCRIPTION_PLAN_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
}
