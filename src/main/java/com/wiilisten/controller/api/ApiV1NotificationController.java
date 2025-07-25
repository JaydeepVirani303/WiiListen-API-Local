package com.wiilisten.controller.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.AdministrativeNotification;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.NotificationHistory;
import com.wiilisten.entity.User;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.PaginationAndSortingDetails;
import com.wiilisten.request.TypeAndPaginationRequestDto;
import com.wiilisten.response.AdminNotificationResponseDto;
import com.wiilisten.response.AllNotificationResponseDto;
import com.wiilisten.response.CallNotificationResponseDto;
import com.wiilisten.response.NotificationCallDto;
import com.wiilisten.response.PaymentNotificationResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.COMMON)
public class ApiV1NotificationController extends BaseController {
	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1NotificationController.class);

	@PostMapping(ApplicationURIConstants.NOTIFICATION_HISTORY)
	public ResponseEntity<Object> getCallNotifications(@RequestBody PaginationAndSortingDetails requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		Page<NotificationHistory> notifications = null;
		List<NotificationCallDto> responseData = new ArrayList<NotificationCallDto>();
		if (ApplicationUtils.isEmpty(requestDetails.getSortType()))
			requestDetails.setSortType("DESC");
		Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);

		try {
			User user = getLoggedInUser();
			notifications = getServiceRegistry().getNotificationHistoryService()
					.findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(user, pageable);
			if (notifications == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			} else {
				responseData = convertBeanToDtoForNotificationHistory(notifications.getContent());
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseData));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.NOTIFICATION_HISTORY + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getNotificationList(@RequestBody TypeAndPaginationRequestDto requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			if (ApplicationUtils.isEmpty(requestDetails.getSortType()))
				requestDetails.setSortType("DESC");
			Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);
			User user = getLoggedInUser();
			CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				if (requestDetails.getType().equals(ApplicationConstants.ADMIN)) {
					Page<AdministrativeNotification> administrativeNotifications = getServiceRegistry()
							.getAdministrativeNotificationService().findByUserId(user.getId(), pageable);
					if (administrativeNotifications.isEmpty()) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices()
								.generateBadResponseWithMessageKey(ErrorDataEnum.NOTIFICATION_NOT_FOUND.getCode()));
					}
					List<AdminNotificationResponseDto> response = convertBeanToDtoForAdminNotification(
							administrativeNotifications, user);

					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
				} else if (requestDetails.getType().equals(ApplicationConstants.REMINDER)) {
					Page<NotificationHistory> notificationHistories = getServiceRegistry()
							.getNotificationHistoryService()
							.findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(user, pageable);
					if (notificationHistories.isEmpty()) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices()
								.generateBadResponseWithMessageKey(ErrorDataEnum.NOTIFICATION_NOT_FOUND.getCode()));
					}
					List<CallNotificationResponseDto> response = convertBeantToDtoForReminderHistory(
							notificationHistories);
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
				} else if (requestDetails.getType().equals(ApplicationConstants.PAYMENT)) {
					// TODO:Added admin side payment response/Payment refunded
					Page<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
							.findByPaymentStatusAndActiveTrueAndCallerOrderByCreatedAtDesc(ApplicationConstants.PAID,
									caller, pageable);
					if (bookedCalls.isEmpty()) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices()
								.generateBadResponseWithMessageKey(ErrorDataEnum.NO_CALLS_FOUND.getCode()));
					}
					List<PaymentNotificationResponseDto> response = convertBeanToDtoForPaymentNotification(bookedCalls);

					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
				} else if (requestDetails.getType().equals(ApplicationConstants.ALL)) {
					Page<AdministrativeNotification> administrativeNotifications = getServiceRegistry()
							.getAdministrativeNotificationService().findByUserId(user.getId(), pageable);
					Page<NotificationHistory> notificationHistories = getServiceRegistry()
							.getNotificationHistoryService()
							.findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(user, pageable);
					Page<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
							.findByPaymentStatusAndActiveTrueAndCallerOrderByCreatedAtDesc(ApplicationConstants.PAID,
									caller, pageable);
					List<AdminNotificationResponseDto> adminResponse = convertBeanToDtoForAdminNotification(
							administrativeNotifications, user);
					List<PaymentNotificationResponseDto> paymentResponse = convertBeanToDtoForPaymentNotification(
							bookedCalls);
					List<CallNotificationResponseDto> reminderResponse = convertBeantToDtoForReminderHistory(
							notificationHistories);

					List<AllNotificationResponseDto> response = mergeAndSortNotifications(adminResponse,
							reminderResponse, paymentResponse);

					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
				}
			}
			if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
				if (requestDetails.getType().equals(ApplicationConstants.CALL)) {
					Page<NotificationHistory> notificationHistories = getServiceRegistry()
							.getNotificationHistoryService()
							.findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(user, pageable);
					if (notificationHistories.isEmpty()) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices()
								.generateBadResponseWithMessageKey(ErrorDataEnum.NOTIFICATION_NOT_FOUND.getCode()));
					}
					List<CallNotificationResponseDto> response = convertBeantToDtoForReminderHistory(
							notificationHistories);
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
				} else if (requestDetails.getType().equals(ApplicationConstants.REMINDER)) {
					Page<AdministrativeNotification> administrativeNotifications = getServiceRegistry()
							.getAdministrativeNotificationService().findByUserId(user.getId(), pageable);// Ignore if
																											// type
																											// contains

					if (administrativeNotifications.isEmpty()) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
					}
					List<AdminNotificationResponseDto> response = convertBeanToDtoForAdminNotification(
							administrativeNotifications, user);

					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
				}
				// TODO:Administrative notification
				else if (requestDetails.getType().equals(ApplicationConstants.ADMIN)) {
					Page<AdministrativeNotification> administrativeNotifications = getServiceRegistry()
							.getAdministrativeNotificationService().findByUserId(user.getId(), pageable);

					List<AdminNotificationResponseDto> response = convertBeanToDtoForAdminNotificationForListenerAdvertisement(
							administrativeNotifications);

					if (response.isEmpty()) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
					}

					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
				} else if (requestDetails.getType().equals(ApplicationConstants.ALL)) {
					Page<AdministrativeNotification> administrativeNotifications = getServiceRegistry()
							.getAdministrativeNotificationService().findByUserId(user.getId(), pageable);
					Page<NotificationHistory> notificationHistories = getServiceRegistry()
							.getNotificationHistoryService()
							.findByRecipientIdAndActiveTrueOrderByCreatedAtDesc(user, pageable);
					Page<AdministrativeNotification> adminNotifications = getServiceRegistry()
							.getAdministrativeNotificationService().findByUserId(user.getId(), pageable);
					List<AdminNotificationResponseDto> adminResponse = convertBeanToDtoForAdminNotificationForListenerAdvertisement(
							administrativeNotifications);// for ADMIN
					List<AdminNotificationResponseDto> reminderResponse = convertBeanToDtoForAdminNotification(
							adminNotifications, user);
					List<CallNotificationResponseDto> callResponse = convertBeantToDtoForReminderHistory(
							notificationHistories);// for CALL

					List<AllNotificationResponseDto> response = mergeAndSortNotificationsForListener(adminResponse,
							callResponse, reminderResponse);

					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
				}
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	public static AllNotificationResponseDto fromAdminNotification(AdminNotificationResponseDto adminNotification) {
		AllNotificationResponseDto dto = new AllNotificationResponseDto();
		dto.setId(adminNotification.getId());
		dto.setTitle(adminNotification.getTitle());
		dto.setContent(adminNotification.getContent());
		if (adminNotification.getTitle().toLowerCase().contains(ApplicationConstants.NEW_REVIEW_ADDED.toLowerCase())) {
			String content = adminNotification.getContent().replace("\n", "");
			dto.setReview(content.substring(1));
			dto.setRatings(content.charAt(0));
		} else {
			dto.setReview(null);
			dto.setRatings(null);
		}
		dto.setTags(adminNotification.getTags());
		dto.setActive(adminNotification.getActive());
		dto.setCreatedAt(adminNotification.getCreatedAt());
		dto.setUpdatedAt(adminNotification.getUpdatedAt());
		dto.setUsers(adminNotification.getUsers());
		dto.setNotificationType("ADMIN");
		return dto;
	}

	// Method to populate from CallNotificationResponseDto
	public static AllNotificationResponseDto fromCallNotification(CallNotificationResponseDto callNotification) {
		AllNotificationResponseDto dto = new AllNotificationResponseDto();
		dto.setId(callNotification.getId());
		dto.setName(callNotification.getName());
		dto.setDurationInMinutes(callNotification.getDurationInMinutes());
		dto.setEvent(callNotification.getEvent());
		dto.setType(callNotification.getType());
		dto.setPayableAmount(callNotification.getPayableAmount());
		dto.setBookingDateTime(callNotification.getBookingDateTime());
		dto.setActive(callNotification.getActive());
		dto.setCreatedAt(callNotification.getCreatedAt());
		dto.setUpdatedAt(callNotification.getUpdatedAt());
		dto.setNotificationType("REMINDER");
		return dto;
	}

	// Method to populate from PaymentNotificationResponseDto
	public static AllNotificationResponseDto fromPaymentNotification(
			PaymentNotificationResponseDto paymentNotification) {
		AllNotificationResponseDto dto = new AllNotificationResponseDto();
		dto.setId(paymentNotification.getId());
		dto.setName(paymentNotification.getName());
		dto.setDurationInMinutes(paymentNotification.getDurationInMinutes());
		dto.setType(paymentNotification.getType());
		dto.setPayableAmount(paymentNotification.getPayableAmount());
		dto.setCallStartTime(paymentNotification.getCallStartTime());
		dto.setCallDuration(paymentNotification.getCallDuration());
		dto.setActive(paymentNotification.getActive());
		dto.setCreatedAt(paymentNotification.getCreatedAt());
		dto.setUpdatedAt(paymentNotification.getUpdatedAt());
		dto.setNotificationType("PAYMENT");
		return dto;
	}

	public static List<AllNotificationResponseDto> mergeAndSortNotificationsForListener(
			List<AdminNotificationResponseDto> adminNotifications, List<CallNotificationResponseDto> callNotifications,
			List<AdminNotificationResponseDto> reminderResponses) {
		List<AllNotificationResponseDto> allNotifications = new ArrayList<>();

		// Convert Admin notifications
		for (AdminNotificationResponseDto adminNotification : adminNotifications) {
			AllNotificationResponseDto allNotification = fromAdminNotification(adminNotification);
			if (allNotification.getCreatedAt() != null) {
				allNotifications.add(allNotification);
			}
		}

		// Convert Call notifications
		for (CallNotificationResponseDto callNotification : callNotifications) {
			AllNotificationResponseDto allNotification = fromCallNotification(callNotification);
			if (allNotification.getCreatedAt() != null) {
				allNotifications.add(allNotification);
			}
		}

		// Convert Reminder notifications
		for (AdminNotificationResponseDto reminderResponse : reminderResponses) {
			AllNotificationResponseDto allNotification = fromAdminNotification(reminderResponse);
			if (allNotification.getCreatedAt() != null) {
				allNotifications.add(allNotification);
			}
		}

//		// Convert Payment notifications
//		for (PaymentNotificationResponseDto paymentNotification : paymentNotifications) {
//			AllNotificationResponseDto allNotification = fromPaymentNotification(paymentNotification);
//			if (allNotification.getCreatedAt() != null) {
//				allNotifications.add(allNotification);
//			}
//		}
		// Sort the combined list by createdAt in descending order
		Collections.sort(allNotifications,
				Comparator.nullsLast(Comparator.comparing(AllNotificationResponseDto::getCreatedAt).reversed()));
		return allNotifications;
	}

	public static List<AllNotificationResponseDto> mergeAndSortNotifications(
			List<AdminNotificationResponseDto> adminNotifications, List<CallNotificationResponseDto> callNotifications,
			List<PaymentNotificationResponseDto> paymentNotifications) {
		List<AllNotificationResponseDto> allNotifications = new ArrayList<>();

		// Convert Admin notifications
		for (AdminNotificationResponseDto adminNotification : adminNotifications) {
			AllNotificationResponseDto allNotification = fromAdminNotification(adminNotification);
			if (allNotification.getCreatedAt() != null) {
				allNotifications.add(allNotification);
			}
		}

		// Convert Call notifications
		for (CallNotificationResponseDto callNotification : callNotifications) {
			AllNotificationResponseDto allNotification = fromCallNotification(callNotification);
			if (allNotification.getCreatedAt() != null) {
				allNotifications.add(allNotification);
			}
		}
		// Convert Payment notifications
		for (PaymentNotificationResponseDto paymentNotification : paymentNotifications) {
			AllNotificationResponseDto allNotification = fromPaymentNotification(paymentNotification);
			if (allNotification.getCreatedAt() != null) {
				allNotifications.add(allNotification);
			}
		}
		// Sort the combined list by createdAt in descending order
		Collections.sort(allNotifications,
				Comparator.nullsLast(Comparator.comparing(AllNotificationResponseDto::getCreatedAt).reversed()));
		return allNotifications;
	}

	public List<PaymentNotificationResponseDto> convertBeanToDtoForPaymentNotification(Page<BookedCalls> bookedCalls) {
		List<PaymentNotificationResponseDto> response = new ArrayList<>();
		bookedCalls.forEach(call -> {
			PaymentNotificationResponseDto dto = new PaymentNotificationResponseDto();
			if (call.getCallerJoinedAt() != null && call.getListenerJoinedAt() != null) {
				LocalDateTime startAt = call.getCallerJoinedAt().isAfter(call.getListenerJoinedAt())
						? call.getCallerJoinedAt()
						: call.getListenerJoinedAt();
				Duration duration = Duration.between(startAt, call.getListenerLeavedAt());
				Long totalSeconds = duration.getSeconds();
				Long hours = totalSeconds / 3600;
				Long minutes = (totalSeconds % 3600) / 60;
				Long seconds = totalSeconds % 60;
				String timeDifference = String.format("%02d:%02d:%02d", hours, minutes, seconds);
				dto.setName(call.getListener().getUserName());
				dto.setCallStartTime(startAt);
				dto.setCallDuration(timeDifference);
			}

			BeanUtils.copyProperties(call, dto);

			dto.setNotificationType(ApplicationConstants.PAYMENT);
			response.add(dto);
		});
		return response;
	}

	public List<AdminNotificationResponseDto> convertBeanToDtoForAdminNotification(
			Page<AdministrativeNotification> administrativeNotifications, User user) {
		List<AdminNotificationResponseDto> response = new ArrayList<>();
		for (AdministrativeNotification notification : administrativeNotifications.getContent()) {
			if (user.getRole().equalsIgnoreCase(UserRoleEnum.LISTENER.getRole())
	                && !notification.getTags().toLowerCase().contains(ApplicationConstants.ADVERTISEMENT_EXPIRE.toLowerCase())) {
	            continue;
	        }
			LOGGER.info("inside" + notification.getTitle());
			AdminNotificationResponseDto dto = new AdminNotificationResponseDto();
			BeanUtils.copyProperties(notification, dto);
			dto.setUsers(null);
			if (notification.getTitle().toLowerCase().contains(ApplicationConstants.NEW_REVIEW_ADDED.toLowerCase())) {
				String content = notification.getContent().replace("\n", "");
				dto.setReview(content.substring(1));
				dto.setRatings(content.charAt(0));
			} else {
				dto.setReview(null);
				dto.setRatings(null);
			}
			LOGGER.info("response {}" + dto.getTags());
			dto.setNotificationType(ApplicationConstants.ADMIN);
			response.add(dto);
		}

		return response;
	}

	public List<AdminNotificationResponseDto> convertBeanToDtoForAdminNotificationForListenerAdvertisement(
			Page<AdministrativeNotification> administrativeNotifications) {
		List<AdminNotificationResponseDto> response = new ArrayList<>();
		administrativeNotifications.forEach(notification -> {
			if (notification.getTags().toLowerCase().contains(ApplicationConstants.ADVERTISEMENT.toLowerCase())
					|| notification.getTitle().toLowerCase()
							.contains(ApplicationConstants.PROFILE_UPDATED.toLowerCase())
					|| notification.getTitle().toLowerCase()
							.contains(ApplicationConstants.NOTIFICATION_STATUS.toLowerCase())
					|| notification.getTitle().toLowerCase()
							.contains(ApplicationConstants.PASSWORD_CHANGED.toLowerCase())
					|| notification.getTitle().toLowerCase()
							.contains(ApplicationConstants.TWO_FACTOR_VERIFICATION.toLowerCase())
					|| notification.getTitle().toLowerCase()
							.contains(ApplicationConstants.NEW_REVIEW_ADDED.toLowerCase())
					|| notification.getTitle().toLowerCase().contains(ApplicationConstants.APP_STATUS.toLowerCase())

			) {
				AdminNotificationResponseDto dto = new AdminNotificationResponseDto();
				BeanUtils.copyProperties(notification, dto);
				dto.setUsers(null);
				if (notification.getTitle().toLowerCase()
						.contains(ApplicationConstants.NEW_REVIEW_ADDED.toLowerCase())) {
					String content = notification.getContent().replace("\n", "");
					dto.setReview(content.substring(1));
					dto.setRatings(content.charAt(0));
				} else {
					dto.setReview(null);
					dto.setRatings(null);
				}
				dto.setNotificationType(ApplicationConstants.ADMIN);
				response.add(dto);
			}
		});
		return response;
	}

	public List<CallNotificationResponseDto> convertBeantToDtoForReminderHistory(
			Page<NotificationHistory> notificationHistories) {
		List<CallNotificationResponseDto> response = new ArrayList<>();
		notificationHistories.forEach(notification -> {
			CallNotificationResponseDto dto = new CallNotificationResponseDto();

			BeanUtils.copyProperties(notification.getBookingId(), dto);
			BeanUtils.copyProperties(notification, dto);
			dto.setName(notification.getSenderId().getCallName());
			dto.setNotificationType(ApplicationConstants.REMINDER);
			response.add(dto);
		});
		return response;
	}

	private List<NotificationCallDto> convertBeanToDtoForNotificationHistory(List<NotificationHistory> historyList) {
		// TODO Auto-generated method stub
		List<NotificationCallDto> response = new ArrayList<NotificationCallDto>();
		for (NotificationHistory history : historyList) {
			NotificationCallDto dto = new NotificationCallDto();
			dto.setBookingId(history.getBookingId().getId());
			dto.setBookingDateTime(history.getBookingId().getBookingDateTime());
			dto.setCallName(history.getSenderId().getCallName());
			dto.setContent(history.getContent());
			dto.setDurationInMinutes(history.getBookingId().getDurationInMinutes());
			dto.setEvent(history.getEvent());
			dto.setPrice(history.getBookingId().getPrice());
			dto.setSubTotal(history.getBookingId().getSubTotal());
			dto.setSenderId(history.getSenderId().getId());
			dto.setRecipientId(history.getRecipientId().getId());

			response.add(dto);
		}

		return response;
	}

	private List<NotificationCallDto> convertBookCallForNotificationHistory(List<BookedCalls> bookedCalls) {
		// TODO Auto-generated method stub
		List<NotificationCallDto> response = new ArrayList<NotificationCallDto>();
		for (BookedCalls call : bookedCalls) {
			NotificationCallDto dto = new NotificationCallDto();
			dto.setBookingId(call.getId());
			dto.setBookingDateTime(call.getBookingDateTime());
			dto.setCallName(call.getListener().getUserName());
			dto.setContent(ApplicationConstants.CALL_START_1DAY);
			dto.setDurationInMinutes(call.getDurationInMinutes());
			dto.setEvent(ApplicationConstants.SCHEDULED);
			dto.setPrice(call.getPrice());
			dto.setSubTotal(call.getSubTotal());
			dto.setSenderId(call.getListener().getId());
			dto.setRecipientId(call.getCaller().getId());

			response.add(dto);
		}

		return response;
	}
}
