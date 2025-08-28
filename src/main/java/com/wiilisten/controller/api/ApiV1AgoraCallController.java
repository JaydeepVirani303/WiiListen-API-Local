package com.wiilisten.controller.api;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.model.PaymentIntent;
import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.Coupon;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.NotificationHistory;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserNotes;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.EndCallRequestDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.IdStatusRequestDto;
import com.wiilisten.request.JoinCallDto;
import com.wiilisten.request.QuickCallRequestDto;
import com.wiilisten.request.StartCallRequestDto;
import com.wiilisten.response.BookedCallDetailsDto;
import com.wiilisten.response.CallStartResponseDto;
import com.wiilisten.response.EndCallResponseDto;
import com.wiilisten.response.ReviewsAndRatingsResponseDto;
import com.wiilisten.service.PaymentService;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.FCMService;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.COMMON)
public class ApiV1AgoraCallController extends BaseController {
	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1HomeController.class);

	@Autowired
	private FCMService fcmService;

	@Autowired
	private PaymentService paymentService;

	@PostMapping(ApplicationURIConstants.START_CALL)
	public ResponseEntity<Object> startCall(@RequestBody JoinCallDto joinCallDto) {
		String token = null;
		User user = getLoggedInUser();
		CallStartResponseDto callStartDto = new CallStartResponseDto();
		String channelId = null;
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {

			BookedCalls bookedcall = null;
			// if
			// (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.SCHEDULED))
			// {
			bookedcall = getServiceRegistry().getBookedCallsService().findOne(joinCallDto.getBookingId());
			token = getServiceRegistry().getAgoraService().generateRtcToken(joinCallDto.getChannelId(),
					user.getId().toString());
			callStartDto.setChannel_name(joinCallDto.getChannelId());

			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				bookedcall.setCallerJoinedAt(LocalDateTime.now());
			} else {
				bookedcall.setListenerJoinedAt(LocalDateTime.now());
			}

            // if bookedcall is OnDemand or Active False

            if (bookedcall.getType().equals(ApplicationConstants.ON_DEMAND) && !bookedcall.getActive()) {
                bookedcall.setActive(Boolean.TRUE);
            }


			bookedcall = getServiceRegistry().getBookedCallsService().saveORupdate(bookedcall);
			// }
			System.err.println("Before condition");
			// if
			// (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND)
			// ||
			// joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.QUICK_CALL))
			// {
			// bookedcall = new BookedCalls();
			//
			// // TODO: Uncomment this at the time of deploy
			//
			// ListenerProfile listenerProfile = null;
			// if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
			// CallerProfile callerProfile = getServiceRegistry().getCallerProfileService()
			// .findByUserAndActiveTrue(user);
			// if
			// (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND))
			// {
			// listenerProfile = getServiceRegistry().getListenerProfileService()
			// .findByIdAndActiveTrue(joinCallDto.getListenerId());
			// bookedcall.setType(ApplicationConstants.ON_DEMAND);
			// } else if
			// (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.QUICK_CALL))
			// {
			// Optional<ListenerProfile> findRandomListenerProfile = getServiceRegistry()
			// .getListenerProfileService().findRandomListenerProfile();
			// if (!findRandomListenerProfile.isPresent()) {
			// LOGGER.info(ApplicationConstants.EXIT_LABEL);
			// return
			// ResponseEntity.ok(getCommonServices().generateBadResponseWithMessageKey(
			// ErrorDataEnum.NO_ACTIVE_LISTENER_FOUND.getCode()));
			// }
			// System.err.println("listener id" + findRandomListenerProfile.get().getId());
			// bookedcall.setType(ApplicationConstants.QUICK_CALL);
			// listenerProfile = findRandomListenerProfile.get();
			//
			// }
			//
			// bookedcall.setCaller(callerProfile);
			// bookedcall.setListener(listenerProfile);
			// bookedcall.setActive(true);
			// bookedcall.setCallerJoinedAt(LocalDateTime.now());
			//
			// bookedcall.setBookingDateTime(LocalDateTime.now().withSecond(0).withNano(0));
			// bookedcall.setDurationInMinutes(0L);
			// bookedcall.setCallRequestStatus(ApplicationConstants.PENDING);
			// bookedcall.setCallStatus(ApplicationConstants.SCHEDULED);
			// bookedcall =
			// getServiceRegistry().getBookedCallsService().saveORupdate(bookedcall);
			//
			// channelId = "wiilisten_" + bookedcall.getId();
			//
			// }
			//
			// if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
			// bookedcall =
			// getServiceRegistry().getBookedCallsService().findOne(joinCallDto.getBookingId());
			// channelId = joinCallDto.getChannelId();
			// listenerProfile =
			// getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);
			// // Give points if LISTENER accepts on-demand call
			// listenerProfile.setPoints(listenerProfile.getPoints() + 5);
			// getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);
			// bookedcall.setCallRequestStatus(ApplicationConstants.ACCEPTED);
			// bookedcall.setCallStatus(ApplicationConstants.ON_GOING);
			// bookedcall.setListenerJoinedAt(LocalDateTime.now());
			// bookedcall =
			// getServiceRegistry().getBookedCallsService().saveORupdate(bookedcall);
			// }
			//
			// token = getServiceRegistry().getAgoraService().generateRtcToken(channelId,
			// user.getId().toString());
			//
			// callStartDto.setChannel_name(channelId);
			// }
			System.err.println("After condition");
			// when call start send push notification to receiver
			callStartDto.setToken(token);

			String receiverDeviceToken;
			Map<String, String> payload = new HashMap<>();
			Map<String, String> notificationPayload = new HashMap<>();
			NotificationHistory notification = new NotificationHistory();
			notification.setActive(true);
			notification.setEvent(ApplicationConstants.CALL);
			notification.setBookingId(bookedcall);
			BookedCallDetailsDto dto = new BookedCallDetailsDto();
			System.err.println("Before notification");
			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				System.err.println("Inside caller");
				callStartDto.setProfilePicture(bookedcall.getListener().getUser().getProfilePicture());
				callStartDto.setReciverName(bookedcall.getListener().getUser().getCallName());

				// Give points to Caller for joining
				CallerProfile callerProfile = bookedcall.getCaller();
				if (LocalDateTime.now().isAfter(bookedcall.getBookingDateTime())) {
					callerProfile.setPoints(callerProfile.getPoints() - 5);
				} else {
					callerProfile.setPoints(callerProfile.getPoints() + 5);
				}
				getServiceRegistry().getCallerProfileService().saveORupdate(callerProfile);

				dto = getCommonServices().convertToDtoForBookedCallCaller(bookedcall);
				callStartDto.setBookedCallDto(dto);
				notification.setRecipientId(bookedcall.getListener().getUser());
				notification.setSenderId(bookedcall.getCaller().getUser());
				notification.setContent(ApplicationConstants.CALLER_JOINED);
				notificationPayload.put("title", ApplicationConstants.CALL);
				notificationPayload.put("body", ApplicationConstants.CALLER_JOINED);
				payload.put("title", ApplicationConstants.CALL);
				payload.put("body", ApplicationConstants.CALLER_JOINED);
				payload.put("priority", "high");
				if (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND)
						|| joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.QUICK_CALL)) {
					payload.put("channelName", joinCallDto.getChannelId());
					payload.put("bookingId", bookedcall.getId().toString());
					payload.put("duration", "0");
					if (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND)) {
						payload.put("tag", ApplicationConstants.AGORA_ON_DEMAND_CALL_STARTED);
					} else {
						payload.put("tag", ApplicationConstants.AGORA_QUICK_CALL_STARTED);
					}

					System.err.println("before  image");
				} else {
					payload.put("channelName", joinCallDto.getChannelId());
					payload.put("bookingId", joinCallDto.getBookingId().toString());
					payload.put("duration", callStartDto.getBookedCallDto().getDurationInMinutes().toString());
					payload.put("tag", ApplicationConstants.AGORA_SCHEDULED_CALL_STARTED);

				}

				payload.put("userId", user.getId().toString());
				payload.put("username", user.getCallName());
				payload.put("profile_picture", user.getProfilePicture());

				payload.put("receiverUserId", bookedcall.getListener().getUser().getId().toString());
				payload.put("receiverUsername", bookedcall.getListener().getUser().getCallName());
				payload.put("receiverProfile_picture", bookedcall.getListener().getUser().getProfilePicture());

				System.err.println("inside call method" + user.getProfilePicture());
				if (bookedcall.getListener().getUser().getDeviceOs().equalsIgnoreCase("ios")) {
					receiverDeviceToken = bookedcall.getListener().getUser().getVoipToken();
					System.err.println("in ios message");
					if (joinCallDto.getIsSendNotification()) {
						fcmService.sendPushNotificationForStartCallIOS(receiverDeviceToken, payload,
								notificationPayload);
					}

				} else {
					System.err.println("in Andorid message");
					receiverDeviceToken = bookedcall.getListener().getUser().getDeviceToken();
					System.err.println("token device" + receiverDeviceToken);
					if (joinCallDto.getIsSendNotification()) {
						fcmService.sendPushNotification(receiverDeviceToken, payload);
					}
					// fcmService.sendPushNotificationForStartCall(receiverDeviceToken, payload,
					// notificationPayload);
				}
			}
			if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
				System.err.println("Inside listener");
				callStartDto.setProfilePicture(bookedcall.getCaller().getUser().getProfilePicture());
				callStartDto.setReciverName(bookedcall.getCaller().getUser().getCallName());

				dto = getCommonServices().convertToDtoForBookedCallListener(bookedcall);
				callStartDto.setBookedCallDto(dto);
				notification.setRecipientId(bookedcall.getCaller().getUser());
				notification.setSenderId(bookedcall.getListener().getUser());
				notification.setContent(ApplicationConstants.LISTENER_JOINED);
				notificationPayload.put("title", ApplicationConstants.CALL);
				notificationPayload.put("body", ApplicationConstants.LISTENER_JOINED);
				payload.put("title", ApplicationConstants.CALL);
				payload.put("body", ApplicationConstants.LISTENER_JOINED);
				if (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND)
						|| joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.QUICK_CALL)) {
					payload.put("channelName", joinCallDto.getChannelId());
					payload.put("bookingId", bookedcall.getId().toString());
					payload.put("duration", "0");
					if (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND)) {
						payload.put("tag", ApplicationConstants.AGORA_ON_DEMAND_CALL_STARTED);
					} else {
						payload.put("tag", ApplicationConstants.AGORA_QUICK_CALL_STARTED);
					}

				} else {
					payload.put("channelName", joinCallDto.getChannelId());
					payload.put("bookingId", joinCallDto.getBookingId().toString());
					payload.put("duration", callStartDto.getBookedCallDto().getDurationInMinutes().toString());
					payload.put("tag", ApplicationConstants.AGORA_SCHEDULED_CALL_STARTED);

				}

				payload.put("userId", user.getId().toString());
				payload.put("username", user.getCallName());
				payload.put("profile_picture", user.getProfilePicture());

				payload.put("receiverUserId", bookedcall.getCaller().getUser().getId().toString());
				payload.put("receiverUsername", bookedcall.getCaller().getUser().getCallName());
				payload.put("receiverProfile_picture", bookedcall.getCaller().getUser().getProfilePicture());

				if (bookedcall.getCaller().getUser().getDeviceOs().equalsIgnoreCase("ios")) {
					receiverDeviceToken = bookedcall.getCaller().getUser().getVoipToken();
					if (joinCallDto.getIsSendNotification()) {
						fcmService.sendPushNotificationForStartCallIOS(receiverDeviceToken, payload,
								notificationPayload);
					}
					// fcmService.sendPushNotificationForStartCallIOS(receiverDeviceToken, payload,
					// notificationPayload);
				} else {
					System.err.println("inside android");
					payload.put("title", ApplicationConstants.CALL);
					payload.put("body", ApplicationConstants.CALLER_JOINED);
					if (joinCallDto.getCallType().equalsIgnoreCase(ApplicationConstants.SCHEDULED)) {
						payload.put("tag", ApplicationConstants.AGORA_SCHEDULED_CALL_STARTED);
					} else {
						payload.put("tag", ApplicationConstants.AGORA_ON_DEMAND_CALL_STARTED);
					}
					receiverDeviceToken = bookedcall.getCaller().getUser().getDeviceToken();
					if (joinCallDto.getIsSendNotification()) {
						fcmService.sendPushNotification(receiverDeviceToken, payload);
					}
					// fcmService.sendPushNotificationForStartCall(receiverDeviceToken, payload,
					// notificationPayload);
					// fcmService.sendPushNotificationForStartCall(receiverDeviceToken, payload);
				}

			}
			System.err.println("After condition");

			UserNotes notes = getServiceRegistry().getUserNotesService()
					.findByCallerProfileAndListenerProfileAndActiveTrue(bookedcall.getCaller(),
							bookedcall.getListener());
			if (notes == null) {
				callStartDto.setNotes(null);
			} else {
				callStartDto.setNotes(notes.getNotes());
			}

			getServiceRegistry().getNotificationHistoryService().saveORupdate(notification);
			callStartDto.setBookingId(bookedcall.getId());
			callStartDto.setListenerId(bookedcall.getListener().getId());
			System.out.println(token);

		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(callStartDto));

	}

	@PostMapping(ApplicationURIConstants.QUICK_CALL)
	public ResponseEntity<Object> startQuickCallOnly(@RequestBody QuickCallRequestDto quickCallRequestDto) {
		String token = null;
		User user = getLoggedInUser();
		CallStartResponseDto callStartDto = new CallStartResponseDto();
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		try {
			String channelId = null;
			BookedCalls bookedcall = null;
			bookedcall = getServiceRegistry().getBookedCallsService()
					.findOne(Long.parseLong(quickCallRequestDto.getBookingId()));

			bookedcall.setRejectionReason(quickCallRequestDto.getReason());
			bookedcall.setCallRequestStatus(quickCallRequestDto.getCallStatus());
			NotificationHistory cancellNotification = new NotificationHistory();
			Map<String, String> cancellPayload = new HashMap<>();
			cancellNotification.setActive(true);
			cancellNotification.setEvent(ApplicationConstants.CANCELLED);
			cancellNotification.setSenderId(bookedcall.getListener().getUser());
			cancellNotification.setRecipientId(bookedcall.getCaller().getUser());
			cancellNotification.setContent(ApplicationConstants.CALL_CANCELLED);
			cancellPayload.put("title", ApplicationConstants.CANCELLED_CAPITAL);
			cancellPayload.put("body", ApplicationConstants.CALL_CANCELLED);
			cancellPayload.put("tag", ApplicationConstants.AGORA_CALL_CANCELLED);
			User users = getSpecifcUser(getLoggedInUser(), bookedcall);
			LOGGER.info("before method");
			sendNotification(users, cancellPayload, cancellPayload);
			LOGGER.info("before method");
			String paymenString = bookedcall.getPaymentIntent();
			String paymentlog = bookedcall.getPaymentlog();
			bookedcall.setActive(Boolean.FALSE);
			bookedcall.setDurationInMinutes(0L);

			getServiceRegistry().getBookedCallsService().saveORupdate(bookedcall);

			List<ListenerProfile> listeners = getServiceRegistry().getListenerProfileService()
					.findAllByActiveAndAppActiveStatusAndUserNotIn(Boolean.TRUE, Boolean.TRUE,
							quickCallRequestDto.getListenerUserIds()); // Fetch all active listeners

			if (listeners == null || listeners.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.NO_ACTIVE_LISTENER_FOUND.getCode()));
			}

			for (ListenerProfile listenerProfile : listeners) {

				if (!listenerProfile.getUser().getId().equals(user.getId())) {

					if (!quickCallRequestDto.getListenerUserIds().contains(listenerProfile.getUser().getId())) {
						System.err.println("inside listener data available");
						if (listenerProfile.getAppActiveStatus()) {
							bookedcall = new BookedCalls();
							bookedcall.setListener(listenerProfile);
							bookedcall.setCaller(getServiceRegistry().getCallerProfileService().findByUser(user));
							bookedcall.setType(ApplicationConstants.QUICK_CALL);
							bookedcall.setCallerJoinedAt(LocalDateTime.now());
							bookedcall.setBookingDateTime(LocalDateTime.now());
							bookedcall.setDurationInMinutes(0L);
							bookedcall.setActive(Boolean.TRUE);
							bookedcall.setPaymentIntent(paymenString);
							bookedcall.setPaymentlog(paymentlog);
							bookedcall = getServiceRegistry().getBookedCallsService().saveORupdate(bookedcall);

							channelId = "wiilisten_" + bookedcall.getId();
							callStartDto.setChannel_name("wiilisten_" + bookedcall.getId());
							token = getServiceRegistry().getAgoraService()
									.generateRtcToken(callStartDto.getChannel_name(), user.getId().toString());
							callStartDto.setToken(token);
						}

						callStartDto.setToken(token);
						callStartDto.setBookingId(bookedcall.getId());
						callStartDto.setListenerId(bookedcall.getListener().getId());
						callStartDto.setProfilePicture(bookedcall.getListener().getUser().getProfilePicture());
						callStartDto.setReciverName(bookedcall.getListener().getUser().getCallName());

						UserNotes notes = getServiceRegistry().getUserNotesService()
								.findByCallerProfileAndListenerProfileAndActiveTrue(bookedcall.getCaller(),
										bookedcall.getListener());
						if (notes == null) {
							callStartDto.setNotes(null);
						} else {
							callStartDto.setNotes(notes.getNotes());
						}

						String receiverDeviceToken;
						Map<String, String> payload = new HashMap<>();
						Map<String, String> notificationPayload = new HashMap<>();
						NotificationHistory notification = new NotificationHistory();
						notification.setActive(true);
						notification.setEvent(ApplicationConstants.CALL);
						notification.setBookingId(bookedcall);
						BookedCallDetailsDto dto = new BookedCallDetailsDto();
						System.err.println("Before notification");
						if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
							System.err.println("Inside caller");
							callStartDto.setProfilePicture(bookedcall.getListener().getUser().getProfilePicture());
							callStartDto.setReciverName(bookedcall.getListener().getUser().getCallName());

							// Give points to Caller for joining
							CallerProfile callerProfile = bookedcall.getCaller();
							if (LocalDateTime.now().isAfter(bookedcall.getBookingDateTime())) {
								callerProfile.setPoints(callerProfile.getPoints() - 5);
							} else {
								callerProfile.setPoints(callerProfile.getPoints() + 5);
							}
							getServiceRegistry().getCallerProfileService().saveORupdate(callerProfile);

							dto = getCommonServices().convertToDtoForBookedCallCaller(bookedcall);
							callStartDto.setBookedCallDto(dto);
							notification.setRecipientId(bookedcall.getListener().getUser());
							notification.setSenderId(bookedcall.getCaller().getUser());
							notification.setContent(ApplicationConstants.CALLER_JOINED);
							notificationPayload.put("title", ApplicationConstants.CALL);
							notificationPayload.put("body", ApplicationConstants.CALLER_JOINED);
							payload.put("title", ApplicationConstants.CALL);
							payload.put("body", ApplicationConstants.CALLER_JOINED);

							payload.put("channelName", channelId);
							payload.put("bookingId", bookedcall.getId().toString());
							payload.put("duration", "0");
							payload.put("tag", ApplicationConstants.AGORA_QUICK_CALL_STARTED);

							payload.put("userId", user.getId().toString());
							payload.put("username", user.getCallName());
							payload.put("profile_picture", user.getProfilePicture());

							payload.put("receiverUserId", bookedcall.getListener().getUser().getId().toString());
							payload.put("receiverUsername", bookedcall.getListener().getUser().getCallName());
							payload.put("receiverProfile_picture",
									bookedcall.getListener().getUser().getProfilePicture());

							System.err.println("inside call method" + user.getProfilePicture());
							if (bookedcall.getListener().getUser().getDeviceOs().equalsIgnoreCase("ios")) {
								receiverDeviceToken = bookedcall.getListener().getUser().getVoipToken();
								System.err.println("in ios message");

								fcmService.sendPushNotificationForStartCallIOS(receiverDeviceToken, payload,
										notificationPayload);
							} else {
								System.err.println("in Andorid message");
								receiverDeviceToken = bookedcall.getListener().getUser().getDeviceToken();
								System.err.println("token device" + receiverDeviceToken);

								fcmService.sendPushNotification(receiverDeviceToken, payload);
							}
						}
						break; // Stop the foreach loop if the condition is true
					}
				}

			}
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(callStartDto));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.RTM_TOKEN)
	public ResponseEntity<Object> getRTMToken() {
		String token = null;
		User user = getLoggedInUser();

		try {
			token = getServiceRegistry().getAgoraService().generateRtmToken(user.getId().toString());
		} catch (Exception e) {

			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(token));

	}

	@PostMapping(ApplicationURIConstants.END_CALL)
	public ResponseEntity<Object> endCall(@RequestBody EndCallRequestDto endCallRequestDto) throws Exception {

		BookedCalls bookedcall = getServiceRegistry().getBookedCallsService().findOne(endCallRequestDto.getBookingId());

		if (endCallRequestDto.getCallStatus().equalsIgnoreCase(ApplicationConstants.REJECTED)) {

			// Deduct points if listener rejected the on-demand call
			ListenerProfile listenerProfile = bookedcall.getListener();
			if (listenerProfile.getAppActiveStatus()
					&& endCallRequestDto.getCallType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND)) {
				listenerProfile.setPoints(listenerProfile.getPoints() - 2);
				getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);
			}

			bookedcall.setRejectionReason(endCallRequestDto.getReason());
			NotificationHistory notification = new NotificationHistory();
			Map<String, String> payload = new HashMap<>();
			notification.setActive(true);
			notification.setEvent(ApplicationConstants.REJECTED);
			notification.setSenderId(bookedcall.getListener().getUser());
			notification.setRecipientId(bookedcall.getCaller().getUser());
			notification.setContent(ApplicationConstants.CALL_REJECTED);
			payload.put("title", ApplicationConstants.REJECTED_CAPTITAL);
			payload.put("body", ApplicationConstants.CALL_REJECTED);
			payload.put("tag", ApplicationConstants.AGORA_CALL_REJECTED);
			User user = getSpecifcUser(getLoggedInUser(), bookedcall);
			sendNotification(user, payload, payload);
			bookedcall.setCallRequestStatus(ApplicationConstants.REJECTED);
			if(endCallRequestDto.getDuration() == null){
			bookedcall.setDurationInMinutes(0L);
			} else {
				bookedcall.setDurationInMinutes(Long.parseLong(endCallRequestDto.getDuration()));
			}

			System.err.println(bookedcall.getDurationInMinutes());

			getServiceRegistry().getBookedCallsService().saveORupdate(bookedcall);
			// String receiverDeviceToken =
			// bookedcall.getCaller().getUser().getDeviceToken();
			// Send push notification using FCM
			// fcmService.sendPushNotification(receiverDeviceToken, payload); // added null
			// in end called and also added
			// payload in this
		} else if (endCallRequestDto.getCallStatus().equalsIgnoreCase(ApplicationConstants.CANCELLED)) {
			LOGGER.info("cancelled inside");
			bookedcall.setRejectionReason(endCallRequestDto.getReason());
			bookedcall.setCallRequestStatus(ApplicationConstants.CANCELLED);
			if(endCallRequestDto.getDuration() == null){
			bookedcall.setDurationInMinutes(0L);
			} else {
				bookedcall.setDurationInMinutes(Long.parseLong(endCallRequestDto.getDuration()));
			}
			System.err.println(bookedcall.getDurationInMinutes());
			getServiceRegistry().getBookedCallsService().saveORupdate(bookedcall);
			NotificationHistory notification = new NotificationHistory();
			Map<String, String> payload = new HashMap<>();
			notification.setActive(true);
			notification.setEvent(ApplicationConstants.CANCELLED);
			notification.setSenderId(bookedcall.getListener().getUser());
			notification.setRecipientId(bookedcall.getCaller().getUser());
			notification.setContent(ApplicationConstants.CALL_CANCELLED);
			payload.put("title", ApplicationConstants.CANCELLED_CAPITAL);
			payload.put("body", ApplicationConstants.CALL_CANCELLED);
			payload.put("tag", ApplicationConstants.AGORA_CALL_CANCELLED);
			User user = getSpecifcUser(getLoggedInUser(), bookedcall);
			LOGGER.info("before method");
			sendNotification(user, payload, payload);
			LOGGER.info("before method");

		}

		else if (endCallRequestDto.getCallStatus().equalsIgnoreCase(ApplicationConstants.ACCEPTED)) {
			Double duration = Double.parseDouble(endCallRequestDto.getDuration()) / 60;
			duration = Math.floor(duration);
			System.err.println("first duration is {}" + duration);
			Double seconds = Double.parseDouble(endCallRequestDto.getDuration()) % 60;
			if (seconds > 30) {
				duration++;
			}
			if (duration < 1) {
				duration = 1D;
			}
			LOGGER.info("inside value");
			System.err.println("inside value");
			Double ratePerMinute = bookedcall.getListener().getRatePerMinute();
			if (bookedcall.getType().equals(ApplicationConstants.ON_DEMAND)
					|| bookedcall.getType().equals(ApplicationConstants.QUICK_CALL)) {
				ratePerMinute = 1.20D;
			}
			double subTotal = ratePerMinute * duration;

			LOGGER.info("value is {}" + duration);
			System.err.println("value is {}" + duration);
			Double finalamount = subTotal;
			LOGGER.info("finalamount is {}" + finalamount);
			System.err.println("finalamount is {}" + finalamount);
			if (bookedcall.getTaxValue() != null) {
				finalamount = finalamount + bookedcall.getTaxValue();
			}
			if (bookedcall.getDiscountValue() != null) {
				finalamount = finalamount - bookedcall.getDiscountValue();
			}

			finalamount = Math.round(finalamount * 100.0) / 100.0;
			subTotal = Math.round(subTotal * 100.0) / 100.0;
			LOGGER.info("final amount before " + finalamount);
			System.err.println("final amount before " + finalamount);
			// if (endCallRequestDto.getCallType().equals(ApplicationConstants.SCHEDULED)) {

			Long amount = finalamount.longValue() * 100;
			if (amount < 100) {
				LOGGER.info("inside condition is");
				System.err.println("inside condition is");
				amount = 100L;
			}
			LOGGER.info("amount is {}" + amount);
			System.err.println("amount is {}" + amount);
			LOGGER.info("bookedcall.getPaymentIntent() {}" + bookedcall.getPaymentIntent());
			System.err.println("bookedcall.getPaymentIntent() {}" + bookedcall.getPaymentIntent());
			PaymentIntent charge = paymentService.capturePaymentIntent(bookedcall.getPaymentIntent(), amount);

			System.err.println("capture amount :------ " + charge);
			bookedcall.setPaymentlog((charge.toJson()).toString());
			bookedcall.setPaymentStatus(ApplicationConstants.PAID);

			bookedcall.setCallStatus(ApplicationConstants.COMPLETED);
			bookedcall.setCallRequestStatus(ApplicationConstants.COMPLETED);
			bookedcall.setCallerLeavedAt(LocalDateTime.now());
			bookedcall.setListenerLeavedAt(LocalDateTime.now());
			bookedcall.setDurationInMinutes(duration.longValue());
			bookedcall.setSubTotal(subTotal);
			bookedcall.setPayableAmount(finalamount);

			getCommonServices().saveEarning(bookedcall);

			getServiceRegistry().getBookedCallsService().saveORupdate(bookedcall);

			// Save earnings in Earning history and update data in
			// listener profile

			// Save notes
			UserNotes notes = getServiceRegistry().getUserNotesService()
					.findByCallerProfileAndListenerProfileAndActiveTrue(bookedcall.getCaller(),
							bookedcall.getListener());
			if (notes != null) {
				notes.setNotes(endCallRequestDto.getNotes());
			} else {
				notes = new UserNotes();
				notes.setCallerProfile(bookedcall.getCaller());
				notes.setListenerProfile(bookedcall.getListener());
				notes.setNotes(endCallRequestDto.getNotes());
			}
			getServiceRegistry().getUserNotesService().saveORupdate(notes);

			EndCallResponseDto response = getResponseForEndCall(bookedcall);
			response.setId(bookedcall.getId());
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		}
		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(bookedcall));
	}

	@PostMapping(ApplicationURIConstants.QUICK_CALL_START)
	public ResponseEntity<Object> startQuickCall(@RequestBody StartCallRequestDto startCallRequest) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			CallerProfile callerProfile = getServiceRegistry().getCallerProfileService()
					.findByIdAndActiveTrue(startCallRequest.getCallerId());
			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(startCallRequest.getListenerId());

			BookedCalls bookedCall = new BookedCalls();
			bookedCall.setCaller(callerProfile);
			bookedCall.setListener(listenerProfile);
			bookedCall.setActive(true);
			bookedCall.setCallerJoinedAt(LocalDateTime.now());

			bookedCall.setBookingDateTime(LocalDateTime.now().withSecond(0).withNano(0));
			bookedCall.setDurationInMinutes(0L);
			bookedCall.setCallRequestStatus(ApplicationConstants.PENDING);
			bookedCall.setCallStatus(ApplicationConstants.SCHEDULED);
			bookedCall.setType(startCallRequest.getCallType());

			getServiceRegistry().getBookedCallsService().saveORupdate(bookedCall);

			StartCallRequestDto response = new StartCallRequestDto();
			response.setBookedCallId(bookedCall.getId());
			response.setListenerId(listenerProfile.getId());
			response.setCallMaxDuration(listenerProfile.getCallMaxDuration());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.SUMMARY)
	public ResponseEntity<Object> generateBill(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			BookedCalls bookedCalls = getServiceRegistry().getBookedCallsService().findOne(idRequestDto.getId());

			EndCallResponseDto response = getResponseForEndCall(bookedCalls);
			response.setId(bookedCalls.getId());
			if (bookedCalls.getType().equals(ApplicationConstants.ON_DEMAND)
					|| bookedCalls.getType().equals(ApplicationConstants.QUICK_CALL)) {
				response.setRatePerMinute(1.2D);
			} else {
				response.setRatePerMinute(bookedCalls.getListener().getRatePerMinute());
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.COUPON + ApplicationURIConstants.APPLY)
	public ResponseEntity<Object> applyCoupon(@RequestBody IdStatusRequestDto idStatusRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			BookedCalls bookedCalls = getServiceRegistry().getBookedCallsService().findOne(idStatusRequestDto.getId());
			Coupon coupon = getServiceRegistry().getCouponService()
					.findByCodeAndActiveTrue(idStatusRequestDto.getType());
			if (coupon == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_COUPON_CODE.getCode()));
			}
			bookedCalls.setDiscountValue(coupon.getAmount());
			bookedCalls.setPayableAmount(bookedCalls.getPayableAmount() - coupon.getAmount());
			getServiceRegistry().getBookedCallsService().saveORupdate(bookedCalls);

			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.COUPON_APPLIED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	public User getSpecifcUser(User user, BookedCalls bookedcall) {
		if (user.getRole().equalsIgnoreCase("CALLER")) {
			return bookedcall.getListener().getUser();
		} else {
			return bookedcall.getCaller().getUser();
		}

	}

	public void sendNotification(User user, Map<String, String> payload, Map<String, String> notificationPayload)
			throws Exception {
		String receiverDeviceToken = user.getDeviceToken();
		if (user.getDeviceOs().equalsIgnoreCase("ios")) {
			receiverDeviceToken = user.getVoipToken();
			Map<String, String> map = new HashMap<>();
			map.put("test1", receiverDeviceToken);
			fcmService.sendPushNotificationForStartCallIOS(receiverDeviceToken, payload, map);
		} else {
			System.err.println("inside else ");
			fcmService.sendPushNotification(receiverDeviceToken, payload);
		}

	}

	public EndCallResponseDto getResponseForEndCall(BookedCalls bookedCalls) {
		EndCallResponseDto responseDto = new EndCallResponseDto();
		BeanUtils.copyProperties(bookedCalls, responseDto);
		if (bookedCalls.getDiscountValue() == null)
			responseDto.setDiscountValue(0D);
		List<UserRatingAndReview> reviewAndRatings = getServiceRegistry().getUserRatingAndReviewService()
				.findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(bookedCalls.getListener().getUser());

		List<ReviewsAndRatingsResponseDto> reviewsAndRatingsResponse = new ArrayList<>();
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
		OptionalDouble averageRating = reviewAndRatings.stream().mapToInt(UserRatingAndReview::getRating).average();

		Double averageRatings = averageRating.isPresent() ? averageRating.getAsDouble() : 0.0;
		responseDto.setAverageRatings(averageRatings);
		responseDto.setTotalReview(reviewAndRatings.size());
		// responseDto.setReviewsAndRatings(reviewsAndRatingsResponse);
		responseDto.setReviewsAndRatings(null);
		return responseDto;

	}

	// // Checked caller payment status if he didn't paid the previous one then
	// don't
	// // let him book
	// BookedCalls checkedCallerPaymentStatus =
	// getServiceRegistry().getBookedCallsService()
	// .findByPaymentStatusAndCallStatusAndCallerIdAndActiveTrue(ApplicationConstants.UNPAID,
	// ApplicationConstants.COMPLETED, callerProfile.getId());
	// if (checkedCallerPaymentStatus != null) {
	// LOGGER.info(ApplicationConstants.EXIT_LABEL);
	// return ResponseEntity.ok(getCommonServices()
	// .generateBadResponseWithMessageKey(ErrorDataEnum.PREVIOUS_PAYMENT_PENDING.getCode()));
	// }

	// Caller side coupon
	// @PostMapping(ApplicationURIConstants.COUPON + ApplicationURIConstants.VERIFY)
	// public ResponseEntity<Object> appliedCoupon(@RequestBody IdStatusRequestDto
	// idStatusRequestDto) {
	//
	// LOGGER.info(ApplicationConstants.ENTER_LABEL);
	//
	// try {
	// BookedCalls bookedCalls = getServiceRegistry().getBookedCallsService()
	// .findByIdAndActiveTrue(idStatusRequestDto.getId());
	// Coupon coupon = getServiceRegistry().getCouponService()
	// .findByCodeAndActiveTrue(idStatusRequestDto.getType());
	// if (coupon == null) {
	// LOGGER.info(ApplicationConstants.EXIT_LABEL);
	// return ResponseEntity.ok(getCommonServices()
	// .generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_COUPON_CODE.getCode()));
	// }
	//
	// bookedCalls.setDiscountValue(coupon.getAmount());
	// bookedCalls.setAppliedDiscountCode(coupon.getCode());
	// getServiceRegistry().getBookedCallsService().saveORupdate(bookedCalls);
	//
	// EndCallResponseDto response = getResponseForEndCall(bookedCalls);
	//
	// LOGGER.info(ApplicationConstants.EXIT_LABEL);
	// return
	// ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
	// } catch (Exception e) {
	// LOGGER.info(ApplicationConstants.EXIT_LABEL);
	// return ResponseEntity.ok(getCommonServices().generateFailureResponse());
	// }
	// }

}
