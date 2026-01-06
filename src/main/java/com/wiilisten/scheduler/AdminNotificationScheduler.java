package com.wiilisten.scheduler;

import java.time.LocalDateTime;
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

	@Scheduled(cron = "0 * * * * *")
	public void scheduleSubscriptionExpiration() {
		LOGGER.info("Minute-by-minute subscription check started");
		try {
			List<UserSubscription> activeSubscriptions = getServiceRegistry().getUserSubscriptionService()
					.findByActiveTrue();
			LocalDateTime now = LocalDateTime.now();

			for (UserSubscription userSubscription : activeSubscriptions) {
				User user = userSubscription.getUser();
				LocalDateTime expiryDate = userSubscription.getExpiryDate();

				if (expiryDate == null) {
					continue;
				}

				if (now.isAfter(expiryDate)) {
					// Expired
					userSubscription.setActive(false);
					getServiceRegistry().getUserSubscriptionService().saveORupdate(userSubscription);
					expiredUser(user, userSubscription.getType());
					saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRED, user,
							userSubscription.getType());
				} else if (now.toLocalDate().isEqual(expiryDate.toLocalDate())) {
					// Ends Today - Send notification at exactly 4:00 PM UTC to match behavior
					// We only send it once when minute is 0 to avoid spamming every minute
					if (now.getHour() == 16 && now.getMinute() == 0) {
						String content = userSubscription.getType().equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)
								? ApplicationConstants.EXPIRE_TODAY
								: ApplicationConstants.ADVERTISEMENT_EXPIRE_TODAY;
						saveAdministrativeNotificationForSubsExpire(content, user, userSubscription.getType());
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error in scheduleSubscriptionExpiration", e);
		}
		LOGGER.info("Minute-by-minute subscription check completed");
	}

	public void expiredUser(User user, String type) {
		if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
			CallerProfile callerProfile = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			if (callerProfile != null) {
				callerProfile.setSearchSubscriptionStatus(ApplicationConstants.EXPIRED);
				getServiceRegistry().getCallerProfileService().saveORupdate(callerProfile);
			}
		}
		if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByUserAndActiveTrue(user);
			if (listenerProfile != null) {
				if (type.equals(ApplicationConstants.SUBSCRIPTION)) {
					listenerProfile.setIsEligibleForPremiumCallSearch(false);
				} else if (type.equals(ApplicationConstants.ADVERTISEMENT)) {
					listenerProfile.setIsAdvertisementActive(false);
				}
				getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);
			}
		}
	}

	public void saveAdministrativeNotificationForSubsExpire(String content, User user, String type) {
		try {
			if (user.getNotificationStatus() && user.getIsLoggedIn()) {
				AdministrativeNotification administrativeNotification = new AdministrativeNotification();
				administrativeNotification.setContent(content);
				administrativeNotification.setUsers(Collections.singletonList(user));
				administrativeNotification.setActive(true);

				if (type.equalsIgnoreCase(ApplicationConstants.SUBSCRIPTION)) {
					administrativeNotification.setTags(ApplicationConstants.SUBSCRIPTION_EXPIRE);
					administrativeNotification.setTitle(ApplicationConstants.SUBSCRIPTION_EXPIRE);
				} else if (type.equalsIgnoreCase(ApplicationConstants.ADVERTISEMENT)) {
					administrativeNotification.setTags(ApplicationConstants.ADVERTISEMENT_EXPIRE);
					administrativeNotification.setTitle(ApplicationConstants.ADVERTISEMENT_EXPIRE);
				}

				getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

				// Send push notification
				Map<String, String> payload = new HashMap<>();
				payload.put(ApplicationConstants.TITLE, ApplicationConstants.SUBSCRIPTION_EXPIRE);
				payload.put(ApplicationConstants.BODY, content);
				payload.put(ApplicationConstants.TAG, ApplicationConstants.SUBSCRIPTION_EXPIRE_NOTIFICATION);
				String receiverDeviceToken = user.getDeviceToken();
				if (receiverDeviceToken != null) {
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

	// public static boolean isDateGone(Date date) {
	// LocalDate givenDate =
	// date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	// LocalDate currentDate = LocalDate.now();
	// return givenDate.isBefore(currentDate);
	// }

	// public void saveAdministrativeNotificationForSubsExpire(String content,
	// List<User> users) {
	//
	// AdministrativeNotification administrativeNotification = new
	// AdministrativeNotification();
	// administrativeNotification.setContent(content);
	// administrativeNotification.setTags(ApplicationConstants.SUBSCRIPTION_EXPIRE);
	// administrativeNotification.setTitle(ApplicationConstants.SUBSCRIPTION_EXPIRE);
	// administrativeNotification.setUsers(users);
	// getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);
	//
	// // send push notification
	// Map<String, String> payload = new HashMap<>();
	// payload.put(ApplicationConstants.TITLE,
	// ApplicationConstants.SUBSCRIPTION_EXPIRE);
	// payload.put(ApplicationConstants.BODY, content);
	// User user = users.get(0);
	// String receiverDeviceToken = user.getDeviceToken();
	// if (receiverDeviceToken != null) {
	// // Send push notification using FCM
	// fcmService.sendPushNotification(receiverDeviceToken, payload);
	// }
	// }

	// check any call starting in every minute
	// LOGGER.info("scheduled method call for one day");
	// User user = getLoggedInUser();
	// LOGGER.info("LoggedIn user is {}" + user);
	// UserSubscription userSubscription =
	// getServiceRegistry().getUserSubscriptionService()
	// .findByUserAndActiveTrue(user);
	// Subscription subscription = userSubscription.getSubscription();
	// Date date = new Date();
	// Date createdAt = userSubscription.getCreatedAt();
	// if (subscription.getType().equalsIgnoreCase(ApplicationConstants.DAILY)) {
	// Date expireDate = getCommonServices().addDaysInDate(createdAt, 1);
	// if (isDateGone(expireDate)) {
	// expireSubscription(userSubscription);
	// }
	// saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY,
	// user);
	// } else if
	// (subscription.getType().equalsIgnoreCase(ApplicationConstants.WEEKLY)) {
	// Date expireDate = getCommonServices().addDaysInDate(createdAt, 7);
	// if (isDateGone(expireDate)) {
	// expireSubscription(userSubscription);
	// }
	// if (isSameDay(date, expireDate)) {
	// saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY,
	// user);
	// }
	// String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
	// saveAdministrativeNotificationForSubsExpire(ApplicationConstants.SUBSCRIPTION_ENDS
	// + inMonthFormat, user);
	// } else if
	// (subscription.getType().equalsIgnoreCase(ApplicationConstants.MONTHLY)) {
	// Date expireDate = getCommonServices().addDaysInDate(createdAt, 31);
	// if (isDateGone(expireDate)) {
	// expireSubscription(userSubscription);
	// }
	// if (isSameDay(date, expireDate)) {
	// saveAdministrativeNotificationForSubsExpire(ApplicationConstants.EXPIRE_TODAY,
	// user);
	// }
	// String inMonthFormat = getCommonServices().getDateInMMMMFormat(expireDate);
	// saveAdministrativeNotificationForSubsExpire(ApplicationConstants.SUBSCRIPTION_ENDS
	// + inMonthFormat, user);
	// }

	// public void expireSubscription(UserSubscription userSubscription) {
	// userSubscription.setActive(false);
	// getServiceRegistry().getUserSubscriptionService().saveORupdate(userSubscription);

	// saveAdministrativeNotificationForSubsExpire(ApplicationConstants.SUBSCRIPTION_EXPIRED,
	// getLoggedInUser());
	// }
}
