package com.wiilisten.controller.api.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.AdministrativeNotification;
import com.wiilisten.entity.User;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.NotificationRequestDto;
import com.wiilisten.request.TypeRequestDto;
import com.wiilisten.response.AdminNotificationResponseDto;
import com.wiilisten.response.NotificationResponseDto;
import com.wiilisten.response.UserResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.FCMService;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.NOTIFICATION)
public class ApiV1AdminNotificationController extends BaseController {

	@Autowired
	private FCMService fcmService;

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminNotificationController.class);

	/*
	 * @PostMapping(ApplicationURIConstants.SEND) public ResponseEntity<Object>
	 * sendNotification(@RequestBody NotificationRequestDto notificationRequestDto)
	 * {
	 * 
	 * LOGGER.info(ApplicationConstants.ENTER_LABEL);
	 * 
	 * try { List<User> users = getServiceRegistry().getUserService().
	 * findByActiveTrueAndIsSuspendedFalseOrderByIdDesc(); if (users == null) {
	 * LOGGER.info(ApplicationConstants.EXIT_LABEL); return ResponseEntity.ok(
	 * getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.
	 * NO_DATA_FOUND.getCode())); } AdministrativeNotification
	 * administrativeNotification = new AdministrativeNotification(); Map<String,
	 * String> payload = new HashMap<>(); payload.put("title",
	 * notificationRequestDto.getTittle()); payload.put("body",
	 * notificationRequestDto.getBody());
	 * administrativeNotification.setTitle(notificationRequestDto.getTittle());
	 * administrativeNotification.setContent(notificationRequestDto.getBody());
	 * users.forEach(user -> { String receiverDeviceToken = user.getDeviceToken();
	 * if (receiverDeviceToken != null) { // Send push notification using FCM
	 * fcmService.sendPushNotification(receiverDeviceToken, payload); } });
	 * administrativeNotification.setUsers(users);
	 * getServiceRegistry().getAdministrativeNotificationService().saveORupdate(
	 * administrativeNotification);
	 * 
	 * LOGGER.info(ApplicationConstants.EXIT_LABEL); return
	 * ResponseEntity.ok(getCommonServices()
	 * .generateSuccessResponseWithMessageKey(SuccessMsgEnum.
	 * NOTIFICATION_SENT_SUCCESSFULLY.getCode())); } catch (Exception e) {
	 * e.printStackTrace(); LOGGER.info(ApplicationConstants.EXIT_LABEL); return
	 * ResponseEntity.ok(getCommonServices().generateFailureResponse()); }
	 * 
	 * }
	 */
	@PostMapping(ApplicationURIConstants.USER + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getUsetType(@RequestBody TypeRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<User> users = new ArrayList<>();
			if (requestDto.getType().equals(ApplicationConstants.ALL)) {
				users = getServiceRegistry().getUserService().findByActiveTrueAndIsSuspendedFalseOrderByIdDesc();
				if (users == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
				}
			} else {
				users = getServiceRegistry().getUserService()
						.findByRoleAndActiveTrueAndIsSuspendedFalseOrderByIdDesc(requestDto.getType());
				if (users == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
				}
			}
			List<NotificationResponseDto> response = new ArrayList<>();
			users.forEach(user -> {
				NotificationResponseDto notificationResponseDto = new NotificationResponseDto();
				BeanUtils.copyProperties(user, notificationResponseDto);
				response.add(notificationResponseDto);
			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.SEND)
	public ResponseEntity<Object> sendNotification(@RequestBody NotificationRequestDto notificationRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			AdministrativeNotification administrativeNotification = new AdministrativeNotification();
			Map<String, String> payload = new HashMap<>();
			payload.put(ApplicationConstants.TITLE, notificationRequestDto.getTitle());
			payload.put(ApplicationConstants.BODY, notificationRequestDto.getBody());
			administrativeNotification.setTitle(notificationRequestDto.getTitle());
			administrativeNotification.setContent(notificationRequestDto.getBody());
			List<Long> ids = notificationRequestDto.getIds();
			List<User> users = new ArrayList<>();
			ids.forEach(id -> {
				User user = getServiceRegistry().getUserService().findByIdAndActiveTrueAndIsSuspendedFalse(id);
				String receiverDeviceToken = user.getDeviceToken();
				if (receiverDeviceToken != null && user.getNotificationStatus()) {
					// Send push notification using FCM
					try {
						fcmService.sendPushNotification(receiverDeviceToken, payload);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if(user.getNotificationStatus()) {
					users.add(user);
				}
				
			});
			administrativeNotification.setTags(ApplicationConstants.ADVERTISEMENT);
			administrativeNotification.setUsers(users);
			administrativeNotification.setActive(true);
			getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.NOTIFICATION_SENT_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getNotificationList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<AdministrativeNotification> administrativeNotifications = getServiceRegistry()
					.getAdministrativeNotificationService().findByActiveTrueOrderByIdDesc();
			if (administrativeNotifications.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.NOTIFICATION_NOT_FOUND.getCode()));
			}
			List<AdminNotificationResponseDto> response = new ArrayList<>();
			administrativeNotifications.forEach(notification -> {
				AdminNotificationResponseDto dto = new AdminNotificationResponseDto();
				BeanUtils.copyProperties(notification, dto);
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

	@PostMapping(ApplicationURIConstants.VIEW)
	public ResponseEntity<Object> getSpecifcNotification(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			AdministrativeNotification administrativeNotification = getServiceRegistry()
					.getAdministrativeNotificationService().findByIdAndActiveTrue(idRequestDto.getId());
			if (administrativeNotification == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.NOTIFICATION_NOT_FOUND.getCode()));
			}
			List<User> users = administrativeNotification.getUsers();
			List<UserResponseDto> userResponse = new ArrayList<>();
			
			AdminNotificationResponseDto response=new AdminNotificationResponseDto();
			BeanUtils.copyProperties(administrativeNotification, response);
			users.forEach(user -> {
				UserResponseDto dto = new UserResponseDto();
				BeanUtils.copyProperties(user, dto);
				userResponse.add(dto);
			});
			response.setUsers(userResponse);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteNotification(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			AdministrativeNotification administrativeNotification = getServiceRegistry()
					.getAdministrativeNotificationService().findByIdAndActiveTrue(idRequestDto.getId());
			if (administrativeNotification == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.NOTIFICATION_NOT_FOUND.getCode()));
			}
			administrativeNotification.setActive(false);
			getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.NOTIFICATION_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
