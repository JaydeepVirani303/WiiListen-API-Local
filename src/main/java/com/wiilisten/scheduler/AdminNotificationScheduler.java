package com.wiilisten.scheduler;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.AdministrativeNotification;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.Subscription;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserSubscription;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.FCMService;

@Component
public class AdminNotificationScheduler extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(NotificationScheduler.class);

	@Autowired
	private FCMService fcmService;

	@Scheduled(cron = "0 0 0 * * *")
	// @Scheduled(cron = "0 * * * * *")
	public void scheduleOneDaySubscriptionExpiration() {
		LOGGER.info("Once in a day");
		try {
			List<User> users = getServiceRegistry().getUserService().findByActiveTrueAndIsSuspendedFalseOrderByIdDesc();
			for (User user : users) {
				LOGGER.info("Processing user {}", user);
				UserSubscription userSubscription = getServiceRegistry().getUserSubscriptionService()
						.findByUserAndActiveTrue(user);
				if (userSubscription == null) {
					LOGGER.warn("No active subscription found for user {}", user);
					continue;
				}

				Subscription subscription = userSubscription.getSubscription();
				Date currentDate = new Date();
				Date createdAt = userSubscription.getCreatedAt();
				Calendar cal = Calendar.getInstance();
				cal.setTime(createdAt);

				if (subscription.getType().equalsIgnoreCase(ApplicationConstants.DAILY)) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					Date expireDate = cal.getTime();
					if (checkIfExpired(expireDate)) {
						userSubscription.setActive(false);
						getServiceRegistry().getUserSubscriptionService().saveORupdate(userSubscription);
						expiredUser(user, userSubscription.getType());
						saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRED, user,
								userSubscription.getType());
					} else if (isSameDay(currentDate, expireDate)) {
						if (userSubscription.getType().equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
							saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY, user,
									userSubscription.getType());
						} else {
							saveAdministrativeNotificationForSubsExpire(ApplicationConstants.ADVERTISEMENT_EXPIRE_TODAY,
									user, userSubscription.getType());
						}

					} else {
						if (userSubscription.getType().equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
							String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
							saveAdministrativeNotificationForSubsExpire(
									ApplicationConstants.SUBSCRIPTION_ENDS + inMonthFormat, user,
									userSubscription.getType());
						} else {
							String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
							saveAdministrativeNotificationForSubsExpire(
									ApplicationConstants.ADVERTISEMENT_ENDS + inMonthFormat, user,
									userSubscription.getType());
						}

					}

				} else if (subscription.getType().equalsIgnoreCase(ApplicationConstants.WEEKLY)) {
					cal.add(Calendar.DAY_OF_MONTH, 7);
					Date expireDate = cal.getTime();
					if (checkIfExpired(expireDate)) {
						userSubscription.setActive(false);
						getServiceRegistry().getUserSubscriptionService().saveORupdate(userSubscription);
						expiredUser(user, userSubscription.getType());
						saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRED, user,
								userSubscription.getType());
					} else if (isSameDay(currentDate, expireDate)) {
						if (userSubscription.getType().equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
							saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY, user,
									userSubscription.getType());
						} else {
							saveAdministrativeNotificationForSubsExpire(ApplicationConstants.ADVERTISEMENT_EXPIRE_TODAY,
									user, userSubscription.getType());
						}
					} else {
						if (userSubscription.getType().equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
							String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
							saveAdministrativeNotificationForSubsExpire(
									ApplicationConstants.SUBSCRIPTION_ENDS + inMonthFormat, user,
									userSubscription.getType());
						} else {
							String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
							saveAdministrativeNotificationForSubsExpire(
									ApplicationConstants.ADVERTISEMENT_ENDS + inMonthFormat, user,
									userSubscription.getType());
						}

					}

				} else if (subscription.getType().equalsIgnoreCase(ApplicationConstants.MONTHLY)) {
					cal.add(Calendar.DAY_OF_MONTH, 31);
					Date expireDate = cal.getTime();
					if (checkIfExpired(expireDate)) {
						userSubscription.setActive(false);
						getServiceRegistry().getUserSubscriptionService().saveORupdate(userSubscription);
						expiredUser(user, userSubscription.getType());
						saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRED, user,
								userSubscription.getType());
					} else if (isSameDay(currentDate, expireDate)) {
						if (userSubscription.getType().equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
							saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY, user,
									userSubscription.getType());
						} else {
							saveAdministrativeNotificationForSubsExpire(ApplicationConstants.ADVERTISEMENT_EXPIRE_TODAY,
									user, userSubscription.getType());
						}
					} else {
						if (userSubscription.getType().equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
							String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
							saveAdministrativeNotificationForSubsExpire(
									ApplicationConstants.SUBSCRIPTION_ENDS + inMonthFormat, user,
									userSubscription.getType());
						} else {
							String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
							saveAdministrativeNotificationForSubsExpire(
									ApplicationConstants.ADVERTISEMENT_ENDS + inMonthFormat, user,
									userSubscription.getType());
						}

					}

				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in scheduleOneDaySubscriptionExpiration", e);
		}
		LOGGER.info(ApplicationConstants.EXIT_LABEL);

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

	}

	public void expiredUser(User user, String type) {
		if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
			CallerProfile callerProfile = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			callerProfile.setSearchSubscriptionStatus(ApplicationConstants.EXPIRED);
			getServiceRegistry().getCallerProfileService().saveORupdate(callerProfile);
		}
		if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByUserAndActiveTrue(user);
			if(type.equals(ApplicationConstants.SUBSCRIPTION)) {
				listenerProfile.setIsEligibleForPremiumCallSearch(false);
			}else if (type.equals(ApplicationConstants.SUBSCRIPTION)) {
				listenerProfile.setIsAdvertisementActive(false);
			}
			
			getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);
		}
	}

	public static boolean checkIfExpired(Date expireDate) {
		Date currentDate = new Date();
		return currentDate.after(expireDate);
	}

	public void saveAdministrativeNotificationForSubsExpire(String content, User user, String type) {
		try {
			if(user.getNotificationStatus() && user.getIsLoggedIn()) {
				AdministrativeNotification administrativeNotification = new AdministrativeNotification();
				if (type.equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
					administrativeNotification.setContent(content);
					administrativeNotification.setTags(ApplicationConstants.SUBSCRIPTION_EXPIRE);
					administrativeNotification.setTitle(ApplicationConstants.SUBSCRIPTION_EXPIRE);
					administrativeNotification.setUsers(Collections.singletonList(user));
				} else if (type.equalsIgnoreCase(ApplicationConstants.ADVERTISEMENT)) {
					administrativeNotification.setContent(content);
					administrativeNotification.setTags(ApplicationConstants.ADVERTISEMENT_EXPIRE);
					administrativeNotification.setTitle(ApplicationConstants.ADVERTISEMENT_EXPIRE);
					administrativeNotification.setUsers(Collections.singletonList(user));
				}

				getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

				// Send push notification
				Map<String, String> payload = new HashMap<>();
				payload.put(ApplicationConstants.TITLE, ApplicationConstants.SUBSCRIPTION_EXPIRE);
				payload.put(ApplicationConstants.BODY, content);
				payload.put(ApplicationConstants.TAG, ApplicationConstants.SUBSCRIPTION_EXPIRE_NOTIFICATION);
				String receiverDeviceToken = user.getDeviceToken();
				if (receiverDeviceToken != null) {
					// Send push notification using FCM
					fcmService.sendPushNotification(receiverDeviceToken, payload);
				}
			}
			
		} catch (Exception e) {
			LOGGER.error("Error in saveAdministrativeNotificationForSubsExpire", e);
		}
	}

	private boolean isSameDay(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date1);
		cal2.setTime(date2);
		return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

//	public static boolean isDateGone(Date date) {
//		LocalDate givenDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate currentDate = LocalDate.now();
//		return givenDate.isBefore(currentDate);
//	}

//	public void saveAdministrativeNotificationForSubsExpire(String content, List<User> users) {
//
//		AdministrativeNotification administrativeNotification = new AdministrativeNotification();
//		administrativeNotification.setContent(content);
//		administrativeNotification.setTags(ApplicationConstants.SUBSCRIPTION_EXPIRE);
//		administrativeNotification.setTitle(ApplicationConstants.SUBSCRIPTION_EXPIRE);
//		administrativeNotification.setUsers(users);
//		getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);
//
//		// send push notification
//		Map<String, String> payload = new HashMap<>();
//		payload.put(ApplicationConstants.TITLE, ApplicationConstants.SUBSCRIPTION_EXPIRE);
//		payload.put(ApplicationConstants.BODY, content);
//		User user = users.get(0);
//		String receiverDeviceToken = user.getDeviceToken();
//		if (receiverDeviceToken != null) {
//			// Send push notification using FCM
//			fcmService.sendPushNotification(receiverDeviceToken, payload);
//		}
//	}

	// check any call starting in every minute
//	LOGGER.info("scheduled method call for one day");
//	User user = getLoggedInUser();
//	LOGGER.info("LoggedIn user is {}" + user);
//	UserSubscription userSubscription = getServiceRegistry().getUserSubscriptionService()
//			.findByUserAndActiveTrue(user);
//	Subscription subscription = userSubscription.getSubscription();
//	Date date = new Date();
//	Date createdAt = userSubscription.getCreatedAt();
//	if (subscription.getType().equalsIgnoreCase(ApplicationConstants.DAILY)) {
//		Date expireDate = getCommonServices().addDaysInDate(createdAt, 1);
//		if (isDateGone(expireDate)) {
//			expireSubscription(userSubscription);
//		}
//		saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY, user);
//	} else if (subscription.getType().equalsIgnoreCase(ApplicationConstants.WEEKLY)) {
//		Date expireDate = getCommonServices().addDaysInDate(createdAt, 7);
//		if (isDateGone(expireDate)) {
//			expireSubscription(userSubscription);
//		}
//		if (isSameDay(date, expireDate)) {
//			saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY, user);
//		}
//		String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
//		saveAdministrativeNotificationForSubsExpire(ApplicationConstants.SUBSCRIPTION_ENDS + inMonthFormat, user);
//	} else if (subscription.getType().equalsIgnoreCase(ApplicationConstants.MONTHLY)) {
//		Date expireDate = getCommonServices().addDaysInDate(createdAt, 31);
//		if (isDateGone(expireDate)) {
//			expireSubscription(userSubscription);
//		}
//		if (isSameDay(date, expireDate)) {
//			saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY, user);
//		}
//		String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
//		saveAdministrativeNotificationForSubsExpire(ApplicationConstants.SUBSCRIPTION_ENDS + inMonthFormat, user);
//	}

//	public void expireSubscription(UserSubscription userSubscription) {
//		userSubscription.setActive(false);
//		getServiceRegistry().getUserSubscriptionService().saveORupdate(userSubscription);

//		saveAdministrativeNotificationForSubsExpire(ApplicationConstants.SUBSCRIPTION_EXPIRED, getLoggedInUser());
//	}
}
