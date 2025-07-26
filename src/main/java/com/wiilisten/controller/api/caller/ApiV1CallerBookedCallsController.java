package com.wiilisten.controller.api.caller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.units.qual.s;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerAvailability;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.NotificationHistory;
import com.wiilisten.entity.User;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.BookedCallDto;
import com.wiilisten.request.PaginationAndSortingDetails;
import com.wiilisten.request.TimeSlotDto;
import com.wiilisten.response.AvailabilityResponseDto;
import com.wiilisten.response.BookedCallDetailsDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;
import com.wiilisten.utils.FCMService;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.CALLER
		+ ApplicationURIConstants.BOOKEDCALLS)
public class ApiV1CallerBookedCallsController extends BaseController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1CallerBookedCallsController.class);

	@Autowired
	private FCMService fcmService;
	
	// @PostMapping(ApplicationURIConstants.DATELIST)
	// public ResponseEntity<Object> listnerAvailabilityDates(@RequestBody BookedCallDto idRequestDto) {
	//     LOGGER.info(ApplicationConstants.ENTER_LABEL);

	//     try {
	//         User user = getLoggedInUser();
	//         ListenerProfile listener = getServiceRegistry().getListenerProfileService()
	//                 .findByIdAndActiveTrue(idRequestDto.getListenerId());


	// 				System.err.println("listener : "+listener);
	//         List<ListenerAvailability> availability = getServiceRegistry().getListenerAvailabilityService()
	//                 .findByUserAndActiveTrue(listener.getUser());


	// 				System.err.println("availability : "+availability);

	//         String timeZone = idRequestDto.getTimeZone();
	//         LocalDate today = LocalDate.now();
	        
	//         int date = today.getDayOfMonth();
	        
	//         LocalDate endDate = today.plusMonths(1).withDayOfMonth(date); // Adjust to next month's 7th day
	//         List<AvailabilityResponseDto> response = new ArrayList<>();

	//         // Iterate over listener availability
	//         for (ListenerAvailability listenerAvailability : availability) {
	//             DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(listenerAvailability.getWeekDay());

	//             LocalDate currentDate = getNextValidDate(today, targetDayOfWeek);

	//             while (!currentDate.isAfter(endDate)) {
	//                 List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
	//                         .findByBookingDateTimeAndListenerAndActiveTrue(currentDate, listener);

	//                 List<TimeSlotDto> allBookedSlots = converBookcallsToTimeSlot(bookedCalls);

	//                 List<TimeSlotDto> availableSlots = new ArrayList<>();

	//                     List<TimeSlotDto> generatedSlots = generateAvailableTimeSlots(
	//                             currentDate, listenerAvailability, idRequestDto, timeZone);

	//                     List<TimeSlotDto> listenerAvailableSlots = findAvailableSlots(generatedSlots, allBookedSlots);
	//                     availableSlots.addAll(listenerAvailableSlots);

	//                 AvailabilityResponseDto availabilityResponseDto = new AvailabilityResponseDto();
	//                 availabilityResponseDto.setDate(currentDate);
	//                 availabilityResponseDto.setAvailbleTime(availableSlots);
	//                 availabilityResponseDto.setStartTime(currentDate.atTime(listenerAvailability.getStartTime()).atZone(ZoneId.of(timeZone)));
	//                 availabilityResponseDto.setEndTime(currentDate.atTime(listenerAvailability.getEndTime()).atZone(ZoneId.of(timeZone)));

	//                 response.add(availabilityResponseDto);

	//                 currentDate = currentDate.plusWeeks(1);
	//             }
	//         }
			
	//         LOGGER.info(ApplicationConstants.EXIT_LABEL);
	//         return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKeyAndData(
	//                 SuccessMsgEnum.DATE_LIST_SUCCESSFULLY.getCode(), response));

	//     } catch (Exception e) {
	//         LOGGER.error("Error occurred while processing listener availability dates", e);
	//         return ResponseEntity.ok(getCommonServices().generateFailureResponse());
	//     }
	// }

	// private LocalDate getNextValidDate(LocalDate currentDate, DayOfWeek targetDayOfWeek) {
	//     int daysToAdd = targetDayOfWeek.getValue() - currentDate.getDayOfWeek().getValue();
	//     if (daysToAdd < 0) {
	//         daysToAdd += 7;
	//     }
	//     return currentDate.plusDays(daysToAdd);
	// }

	// private List<TimeSlotDto> generateAvailableTimeSlots(LocalDate currentDate, ListenerAvailability slot,
	//         BookedCallDto idRequestDto, String timeZone) {
	//     LocalDateTime startDateTime = currentDate.atTime(slot.getStartTime());
	//     LocalTime startTime = getCommonServices()
	//             .UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(startDateTime, timeZone);

	//     LocalDateTime endDateTime = currentDate.atTime(slot.getEndTime());
	//     LocalTime endTime = getCommonServices()
	//             .UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(endDateTime, timeZone);

	//     List<TimeSlotDto> allDurationsinMinslots;
	//     if (currentDate.equals(LocalDate.now())) {
	//         allDurationsinMinslots = getCommonServices().generateTimeSlotsForCurrentDate(startTime, endTime,
	//                 idRequestDto.getDurationInMinutes(), timeZone);
	//     } else {
	//         allDurationsinMinslots = getCommonServices().generateTimeSlots(startTime, endTime,
	//                 idRequestDto.getDurationInMinutes());
	//     }

	//     List<TimeSlotDto> convertedUtcDurations = new ArrayList<>();
	//     allDurationsinMinslots.forEach(timeSlot -> {
	//         TimeSlotDto dto = new TimeSlotDto();
	//         LocalDateTime slotStartDateTime = currentDate.atTime(timeSlot.getStartTime());
	//         LocalDateTime slotEndDateTime = currentDate.atTime(timeSlot.getEndTime());

	//         dto.setStartTime(getCommonServices().localDateTimeToUtcTime(slotStartDateTime, timeZone));
	//         dto.setEndTime(getCommonServices().localDateTimeToUtcTime(slotEndDateTime, timeZone));
	//         convertedUtcDurations.add(dto);
	//     });

	//     return convertedUtcDurations;
	// }

	// private List<TimeSlotDto> findAvailableSlots(List<TimeSlotDto> allDurationsinMinslots,
	//         List<TimeSlotDto> allBookedSlots) {
	//     List<TimeSlotDto> availableSlots = new ArrayList<>();
	//     for (TimeSlotDto timeSlot : allDurationsinMinslots) {
	//         boolean isAvailable = true;
	//         for (TimeSlotDto bookedSlot : allBookedSlots) {
	//             if (timeSlot.overlaps(bookedSlot)) {
	//                 isAvailable = false;
	//                 break;	
	//             }
	//         }
	//         if (isAvailable) {
	//             availableSlots.add(timeSlot);
	//         }
	//     }
	//     return availableSlots;
	// }

	// private List<TimeSlotDto> converBookcallsToTimeSlot(List<BookedCalls> listOfbookingDateCalls) {
	//     List<TimeSlotDto> availableList = new ArrayList<>();
	//     for (BookedCalls call : listOfbookingDateCalls) {
	//         LocalTime startTime = getCommonServices().getTimeFromDate(call.getBookingDateTime());
	//         LocalTime endTime = getCommonServices().getTimeFromDate(call.getBookingDateTime())
	//                 .plusMinutes(call.getDurationInMinutes());
	//         TimeSlotDto slot = new TimeSlotDto(startTime, endTime);
	//         availableList.add(slot);
	//     }
	//     return availableList;
	// }





	@PostMapping(ApplicationURIConstants.CREATE)
	public ResponseEntity<Object> bookCallSendRequest(@RequestBody BookedCallDetailsDto bookedCallDetailsDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(bookedCallDetailsDto.getListenerId());

			// TODO: Uncomment this at the time of deploy
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

			BookedCalls bookedcalls = new BookedCalls();
			NotificationHistory notification = new NotificationHistory();
			bookedcalls.setBookingDateTime(bookedCallDetailsDto.getBookingDateTime());
			bookedcalls.setCaller(caller);
			bookedcalls.setListener(listener);
			bookedcalls.setDurationInMinutes(bookedCallDetailsDto.getDurationInMinutes());
			bookedcalls.setPrice(listener.getRatePerMinute());
			double subTotal = listener.getRatePerMinute() * bookedCallDetailsDto.getDurationInMinutes();

			bookedcalls.setSubTotal(subTotal);
			bookedcalls.setPayableAmount(subTotal);
			if (bookedCallDetailsDto.getType().equalsIgnoreCase(ApplicationConstants.ON_DEMAND)) {
				bookedcalls.setType(ApplicationConstants.ON_DEMAND);
				notification.setEvent(ApplicationConstants.ON_DEMAND);
				notification.setContent(ApplicationConstants.CALL);
			} else {
				ZoneId zoneId = ZoneId.of(bookedCallDetailsDto.getRequestedTimeZone());

				// Get the current date and time in that zone
				ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

				// Convert ZonedDateTime to LocalDateTime
				LocalDateTime requestedDateTime = zonedDateTime.toLocalDateTime();

				bookedcalls.setRequestedDateTime(requestedDateTime);
				bookedcalls.setRequestedTimeZone(bookedCallDetailsDto.getRequestedTimeZone());
				bookedcalls.setType(ApplicationConstants.SCHEDULED);
				bookedcalls.setCallRequestStatus(ApplicationConstants.PENDING);
				notification.setEvent(ApplicationConstants.CALL_REQUEST);
			}

			bookedcalls.setActive(true);
			if (user.getPaymentIntent() != null) {
				bookedcalls.setPaymentStatus(ApplicationConstants.PAYMENT_BLOCKED);
			}

			bookedcalls.setPaymentIntent(bookedCallDetailsDto.getPaymentIntent());
			bookedcalls.setSponsored(bookedCallDetailsDto.getSponsored());
			getServiceRegistry().getBookedCallsService().saveORupdate(bookedcalls);

			// send notification in app for booking request.
			notification.setActive(true);
			notification.setRecipientId(listener.getUser());
			notification.setSenderId(caller.getUser());
			notification.setBookingId(bookedcalls);
			if (listener.getUser().getNotificationStatus()) {
				getServiceRegistry().getNotificationHistoryService().saveORupdate(notification);
			}

			String receiverDeviceToken = listener.getUser().getDeviceToken();

			// Construct push notification payload
			Map<String, String> payload = new HashMap<>();
			payload.put("title", "Call Request");
			payload.put("body", "Caller scheduled call request");

			// Send push notification using FCM
			if (receiverDeviceToken != null && listener.getUser().getNotificationStatus() && user.getIsLoggedIn()) {
				fcmService.sendPushNotification(receiverDeviceToken, payload);
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.BOOKEDCALL_SUCCESS_MESSAGE.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	// On-demand call
	// @PostMapping(ApplicationURIConstants.ON_DEMAND)
	// public ResponseEntity<Object> getOnDemandCall(@RequestBody IdRequestDto
	// idRequestDto){
	//
	// LOGGER.info(ApplicationConstants.ENTER_LABEL);
	//
	// try {
	// User user = getLoggedInUser();

	// BookedCalls bookedCalls=new BookedCalls();
	// bookedCalls.setCaller(callerProfile);
	// bookedCalls.setListener(listenerProfile);
	// bookedCalls.setType(ApplicationConstants.ON_DEMAND);
	// bookedCalls.setCallRequestStatus(ApplicationConstants.PENDING);
	// bookedCalls.setActive(true);
	// getServiceRegistry().getBookedCallsService().saveORupdate(bookedCalls);
	// } catch (Exception e) {
	// e.printStackTrace();
	// LOGGER.info(ApplicationConstants.EXIT_LABEL);
	// return ResponseEntity.ok(getCommonServices().generateFailureResponse());
	// }
	// return null;
	// }

	@PostMapping(ApplicationURIConstants.HISTORY)
	public ResponseEntity<Object> getCallHistory(@RequestParam(required = false) LocalDate bookingDate,
			@RequestParam(required = false) String searchParam, @RequestBody(required = false) PaginationAndSortingDetails requestDetails) {
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		Page<BookedCalls> bookedcalls = null;
		try {

			User user = getLoggedInUser();
			List<BookedCallDetailsDto> responseData = new ArrayList<BookedCallDetailsDto>();
			CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			if (requestDetails == null) {
				requestDetails = new PaginationAndSortingDetails();
			}
			if (ApplicationUtils.isEmpty(requestDetails.getSortType())) {
				requestDetails.setSortType("DESC");
			}
			Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);

            if (caller != null) {
                LOGGER.info("Caller ID: {}", caller.getId());

                if (bookingDate != null) {
                    LOGGER.info("Booking Date provided: {}", bookingDate);

                    if (searchParam != null && !searchParam.isEmpty()) {
                        LOGGER.info("Search Param provided with booking date: {}", searchParam);
                        bookedcalls = getServiceRegistry().getBookedCallsService()
                                .findByCallerAndBookingDateAndListenerUserCallName(caller, bookingDate, searchParam, pageable);
                    } else {
                        LOGGER.info("No search param provided, filtering only by booking date.");
                        bookedcalls = getServiceRegistry().getBookedCallsService()
                                .findByCallerAndBookingDate(caller, bookingDate, pageable);
                    }

                } else {
                    LOGGER.info("Booking Date not provided.");

                    if (searchParam != null && !searchParam.isEmpty()) {
                        LOGGER.info("Search Param provided without booking date: {}", searchParam);
                        bookedcalls = getServiceRegistry().getBookedCallsService()
                                .findByCallerIdAndListenerCallNameAndActiveTrue(caller, searchParam, pageable);
                    } else {
                        LOGGER.info("No search param or booking date provided. Fetching all active booked calls for caller.");
                        bookedcalls = getServiceRegistry().getBookedCallsService()
                                .findByCallerProfileAndActiveTrue(caller, pageable);
                    }
                }

                if (bookedcalls == null || bookedcalls.getContent().isEmpty()) {
                    LOGGER.info("No booked calls found for the given filters.");
                    LOGGER.info(ApplicationConstants.EXIT_LABEL);
                    return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
                } else {
                    LOGGER.info("Booked Calls found: {}", bookedcalls.getContent().size());
                    responseData = getCommonServices().convertBeanToDtoForBookedCall(bookedcalls.getContent(),
                            ApplicationConstants.HISTORY);
                }
            } else {
                LOGGER.warn("Caller is null. Cannot fetch booked calls.");
            }

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseData));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
