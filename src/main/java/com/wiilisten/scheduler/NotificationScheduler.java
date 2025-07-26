package com.wiilisten.scheduler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.NotificationHistory;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.FCMService;

@Component
public class NotificationScheduler extends BaseController {
	private final static Logger LOGGER = LoggerFactory.getLogger(NotificationScheduler.class);
	@Autowired
	private FCMService fcmService;

    @Scheduled(cron = "0 * * * * *") // Trigger every minute
    public void scheduleOneDayNotificationsCaller() throws IOException {
        LOGGER.info("Scheduled method call for one day.");

        List<BookedCalls> bookedCallsInOneDay = getServiceRegistry().getBookedCallsService().findCallsStartingInOneDay();
        for (BookedCalls call : bookedCallsInOneDay) {
            if (!call.getIsNotificationSent1Day()) {
                saveBookCallForNotificationHistory(call, ApplicationConstants.CALL_START_1DAY);
                call.setIsNotificationSent1Day(true);
                getServiceRegistry().getBookedCallsService().saveORupdate(call);
            }
        }
    }

    @Scheduled(cron = "0 * * * * *") // Trigger every minute
    public void scheduleHalfHourNotificationsCaller() throws IOException {
        LOGGER.info("Scheduled method call for half hour.");

        List<BookedCalls> bookedCallsInHalfHour = getServiceRegistry().getBookedCallsService().findCallsStartingInHalfHour();
        for (BookedCalls call : bookedCallsInHalfHour) {
            if (!call.getIsNotificationSent30Min()) {
                saveBookCallForNotificationHistory(call, ApplicationConstants.CALL_START_30MIN);
                call.setIsNotificationSent30Min(true);
                getServiceRegistry().getBookedCallsService().saveORupdate(call);
            }
        }
    }

    @Scheduled(cron = "0 * * * * *") // Trigger every minute
    public void scheduleOneHourNotificationsCaller() throws IOException {
        LOGGER.info("Scheduled method call for one hour.");

        List<BookedCalls> bookedCallsInOneHour = getServiceRegistry().getBookedCallsService().findCallsStartingInOneHour();
        for (BookedCalls call : bookedCallsInOneHour) {
            if (!call.getIsNotificationSent1Hour()) {
                saveBookCallForNotificationHistory(call, ApplicationConstants.CALL_START_1HOUR);
                call.setIsNotificationSent1Hour(true);
                getServiceRegistry().getBookedCallsService().saveORupdate(call);
            }
        }
    }


	private void saveBookCallForNotificationHistory(BookedCalls call, String callStartIn)throws IOException {
		// TODO Auto-generated method stub
		String receiverDeviceToken;
		Map<String, String> notificationPayload = new HashMap<>();
			if(call.getCaller().getUser().getNotificationStatus() && (call.getCaller().getUser().getIsLoggedIn())) {
				NotificationHistory historyForCaller = new NotificationHistory();

				historyForCaller.setContent(callStartIn);
				historyForCaller.setBookingId(call);
				historyForCaller.setSenderId(call.getListener().getUser());
				historyForCaller.setRecipientId(call.getCaller().getUser());
				historyForCaller.setEvent(ApplicationConstants.SCHEDULED);
				historyForCaller.setActive(true);
				getServiceRegistry().getNotificationHistoryService().saveORupdate(historyForCaller);
				notificationPayload.put("title", ApplicationConstants.SCHEDULED_CAPITAL);
				notificationPayload.put("body", callStartIn);
				notificationPayload.put("tag", ApplicationConstants.CALL_STARTED_NOTIFICATION);
				receiverDeviceToken = call.getCaller().getUser().getDeviceToken();
				fcmService.sendPushNotification(receiverDeviceToken, notificationPayload);

				// for listner Push Notification
				NotificationHistory historyForListener = new NotificationHistory();

				historyForListener.setContent(callStartIn);
				historyForListener.setBookingId(call);
				historyForListener.setSenderId(call.getCaller().getUser());
				historyForListener.setRecipientId(call.getListener().getUser());
				historyForListener.setEvent(ApplicationConstants.SCHEDULED);
				historyForListener.setActive(true);
				getServiceRegistry().getNotificationHistoryService().saveORupdate(historyForListener);
				notificationPayload.put("title", ApplicationConstants.SCHEDULED_CAPITAL);
				notificationPayload.put("body", callStartIn);
				notificationPayload.put("tag", ApplicationConstants.CALL_STARTED_NOTIFICATION);
				receiverDeviceToken = call.getListener().getUser().getDeviceToken();
				fcmService.sendPushNotification(receiverDeviceToken, notificationPayload);
			}
			
		

	}
}
