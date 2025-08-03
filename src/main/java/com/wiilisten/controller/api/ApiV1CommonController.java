package com.wiilisten.controller.api;

import java.time.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.BlockedUser;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CommissionRate;
import com.wiilisten.entity.Coupon;
import com.wiilisten.entity.ListenerAvailability;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.Subscription;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.BlockedUserRequestDto;
import com.wiilisten.request.BookedCallDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.IdStatusRequestDto;
import com.wiilisten.request.PaginationAndSortingDetails;
import com.wiilisten.request.TimeSlotDto;
import com.wiilisten.request.TypeRequestDto;
import com.wiilisten.response.AvailabilityResponseDto;
import com.wiilisten.response.CommissionRateResponseDto;
import com.wiilisten.response.CouponResponseDto;
import com.wiilisten.response.FileResponseDto;
import com.wiilisten.response.ReviewsAndRatingsResponseDto;
import com.wiilisten.response.SubscriptionResponseDto;
import com.wiilisten.response.TotalReviewAndRatingResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.COMMON)
public class ApiV1CommonController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1CommonController.class);

	@PostMapping(ApplicationURIConstants.BLOCKED)
	public ResponseEntity<Object> blockedUser(@RequestBody BlockedUserRequestDto blockedUserRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User blockerUser = getLoggedInUser();
			User blockedUser = getServiceRegistry().getUserService().findOne(blockedUserRequestDto.getId());

			BlockedUser block = new BlockedUser();
			BeanUtils.copyProperties(blockedUserRequestDto, block);
			block.setBlockerUser(blockerUser);
			block.setBlockedUser(blockedUser);
			block.setActive(true);
			getServiceRegistry().getBlockedUserService().saveORupdate(block);

			if (blockedUserRequestDto.getType().equalsIgnoreCase(ApplicationConstants.BLOCKED)) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.BLOCKED_SUCCESSFULLY.getCode()));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.REPORTED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}


	@PostMapping(ApplicationURIConstants.COUPON + ApplicationURIConstants.VERIFY)
	public ResponseEntity<Object> appliedCoupon(@RequestBody IdStatusRequestDto idStatusRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Coupon coupon = getServiceRegistry().getCouponService()
					.findByCodeAndActiveTrue(idStatusRequestDto.getType());
			if (coupon == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_COUPON_CODE.getCode()));
			}
			CouponResponseDto response = new CouponResponseDto();
			LOGGER.info("amount is {}" + coupon.getAmount());
			response.setAmount(coupon.getAmount());

			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.VIEW + ApplicationURIConstants.REVIEWS_AND_RATINGS)
	public ResponseEntity<Object> getReviewAndRatings(@RequestBody PaginationAndSortingDetails requestDetails,
			@RequestParam Long id) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			if (ApplicationUtils.isEmpty(requestDetails.getSortBy()))
				requestDetails.setSortBy("id");
			if (ApplicationUtils.isEmpty(requestDetails.getSortType()))
				requestDetails.setSortType("DESC");
			Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);

			User user = getServiceRegistry().getUserService().findByIdAndActiveTrueAndIsSuspendedFalse(id);
			TotalReviewAndRatingResponseDto responseDto = new TotalReviewAndRatingResponseDto();
			List<ReviewsAndRatingsResponseDto> response = new ArrayList<>();

			Page<UserRatingAndReview> reviewAndRatings = getServiceRegistry().getUserRatingAndReviewService()
					.findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(user, pageable);

			List<UserRatingAndReview> reviewForAverage = getServiceRegistry().getUserRatingAndReviewService()
					.findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(user);

			if (!reviewAndRatings.isEmpty()) {
				reviewAndRatings.forEach(reviewRating -> {
					ReviewsAndRatingsResponseDto dto = new ReviewsAndRatingsResponseDto();
					BeanUtils.copyProperties(reviewRating, dto);
					User user1 = reviewRating.getReviewerUser();
					dto.setReviewerId(user1.getId());
					dto.setReviewerName(user1.getCallName());
					dto.setContact(user1.getContactNumber());
					dto.setEmail(user1.getEmail());
					if (user1.getProfilePicture() == null) {
						dto.setProfile(
								"https://wiilisten.s3.amazonaws.com/wiilisten/user/profile_images/imgprofile.jpeg");
					} else {
						dto.setProfile(user1.getProfilePicture());
					}

					response.add(dto);
				});
			}

			OptionalDouble averageRating = reviewForAverage.stream().mapToInt(UserRatingAndReview::getRating).average();
			Double averageRatings = averageRating.isPresent() ? averageRating.getAsDouble() : 0.0;
			responseDto.setAverageRatings(averageRatings);
			responseDto.setTotalReview(reviewForAverage.size());
			responseDto.setReviewsAndRatings(response);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseDto));
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.SUBSCRIPTION)
	public ResponseEntity<Object> getSubscriptionList(@RequestBody TypeRequestDto typeRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			LOGGER.info("size is inside{}");
			User user = getLoggedInUser();
			LOGGER.info("size is inside{}" + user);
			LOGGER.info("user.getDeviceOs(){}" + user.getDeviceOs());
			List<Subscription> subscriptions = new ArrayList<>();
			if (user.getDeviceOs().equalsIgnoreCase("Android")) {
				LOGGER.info("inside if ");
				subscriptions = getServiceRegistry().getSubscriptionService()
						.findByDeviceOsAndIsDeletedFalseAndActiveTrueAndCategoryOrderByIdDesc("Android", typeRequestDto.getType());
				LOGGER.info("size inside if is {}" + subscriptions.size());
			} else if (user.getDeviceOs().equalsIgnoreCase("iOS")) {
				LOGGER.info("inside else");
				subscriptions = getServiceRegistry().getSubscriptionService()
						.findByDeviceOsAndIsDeletedFalseAndActiveTrueAndCategoryOrderByIdDesc("iOS", typeRequestDto.getType());
			}
			LOGGER.info("size is {}" + subscriptions.size());
			if (subscriptions.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUBSCRIPTION_PLAN_NOT_EXIST.getCode()));
			}
			LOGGER.info("inside is ");
			List<SubscriptionResponseDto> response = new ArrayList<>();
			subscriptions.sort(new SubscriptionComparator());
			subscriptions.forEach(subscription -> {
				SubscriptionResponseDto dto = new SubscriptionResponseDto();
				LOGGER.info("subscription is {}" + subscription);
				BeanUtils.copyProperties(subscription, dto);
				response.add(dto);
			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	public List<AvailabilityResponseDto> convertAvailableTimeToTimezone1(List<AvailabilityResponseDto> responses, String targetTimeZone) {
		// Validate target timezone
		ZoneId targetZoneId;
		try {
			targetZoneId = ZoneId.of(targetTimeZone);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid target timezone: " + targetTimeZone);
		}

		for (AvailabilityResponseDto response : responses) {
			// Extract source timezone from startTime
			ZoneId sourceZoneId = response.getStartTime().getZone();

			// Process each TimeSlotDto in availbleTime
			for (TimeSlotDto slot : response.getAvailbleTime()) {
				// Use response's date for conversion
				ZonedDateTime startZoned = ZonedDateTime.of(response.getDate(), slot.getStartTime(), sourceZoneId);
				ZonedDateTime endZoned = ZonedDateTime.of(response.getDate(), slot.getEndTime(), sourceZoneId);

				// Convert to target timezone
				ZonedDateTime startTargetZoned = startZoned.withZoneSameInstant(targetZoneId);
				ZonedDateTime endTargetZoned = endZoned.withZoneSameInstant(targetZoneId);

				// Update slot with converted times (no date field)
				slot.setStartTime(startTargetZoned.toLocalTime());
				slot.setEndTime(endTargetZoned.toLocalTime());
			}
		}

		return responses;
	}

	public List<AvailabilityResponseDto> convertAvailableTimeToTimezone(List<AvailabilityResponseDto> responses, String targetTimeZone) {
		ZoneId targetZoneId;
		try {
			targetZoneId = ZoneId.of(targetTimeZone);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid target timezone: " + targetTimeZone);
		}

		// Map to group slots by target date
		HashMap<LocalDate, List<TimeSlotDto>> dateToSlots = new HashMap<>();
		ZoneId sourceZoneId = ZoneId.of("UTC");

		for (AvailabilityResponseDto response : responses) {
			for (TimeSlotDto slot : response.getAvailbleTime()) {
				LocalDate effectiveDate = response.getDate();
				if (slot.getStartTime().isBefore(LocalTime.MIDNIGHT) || slot.getStartTime().equals(LocalTime.MIDNIGHT)) {
					effectiveDate = response.getDate().plusDays(1);
				}
				ZonedDateTime startZoned = ZonedDateTime.of(effectiveDate, slot.getStartTime(), sourceZoneId);
				ZonedDateTime endZoned = ZonedDateTime.of(effectiveDate, slot.getEndTime(), sourceZoneId);
				ZonedDateTime startTargetZoned = startZoned.withZoneSameInstant(targetZoneId);
				ZonedDateTime endTargetZoned = endZoned.withZoneSameInstant(targetZoneId);
				TimeSlotDto newSlot = new TimeSlotDto(startTargetZoned.toLocalTime(), endTargetZoned.toLocalTime());
				LocalDate targetDate = startTargetZoned.toLocalDate();
				dateToSlots.computeIfAbsent(targetDate, k -> new ArrayList<>()).add(newSlot);
			}
		}

		// Create new AvailabilityResponseDto objects
		List<AvailabilityResponseDto> result = new ArrayList<>();
		for (Map.Entry<LocalDate, List<TimeSlotDto>> entry : dateToSlots.entrySet()) {
			AvailabilityResponseDto dto = new AvailabilityResponseDto();
			dto.setDate(entry.getKey());
			dto.setAvailbleTime(entry.getValue());
			// Set startTime/endTime (use original or adjust as needed)
			dto.setStartTime(ZonedDateTime.of(entry.getKey(), LocalTime.MIDNIGHT, targetZoneId));
			dto.setEndTime(ZonedDateTime.of(entry.getKey().plusDays(1), LocalTime.MIDNIGHT, targetZoneId));
			result.add(dto);
		}
		return result;
	}
	@PostMapping(ApplicationURIConstants.DATELIST)
	public ResponseEntity<Object> listenerAvailabilityDates(@RequestBody BookedCallDto idRequestDto) {
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		LOGGER.info("Entered listenerAvailabilityDates API with ListenerId: {}", idRequestDto.getListenerId());

		try {
			User user = getLoggedInUser();
			LOGGER.info("Logged in user fetched: {}", user.getId());

			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getListenerId());
			LOGGER.info("Listener profile fetched for listenerId {}: {}", idRequestDto.getListenerId(), listener.getId());
			String listenerTimeZone = listener.getUser().getTimeZone();
			List<ListenerAvailability> availability = getServiceRegistry().getListenerAvailabilityService()
					.findByUserAndActiveTrue(listener.getUser());
			LOGGER.info("Listener availability count: {}", availability.size());

			String timeZone = idRequestDto.getTimeZone();
			LocalDate today = LocalDate.now();
			int date = today.getDayOfMonth();
			LocalDate endDate = today.plusMonths(1).withDayOfMonth(date);

			List<AvailabilityResponseDto> response = new ArrayList<>();
			List<AvailabilityResponseDto> response1 = new ArrayList<>();

			for (ListenerAvailability listenerAvailability : availability) {
				LOGGER.info("Processing availability for weekday: {}", listenerAvailability.getWeekDay());

				DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(listenerAvailability.getWeekDay());
				LocalDate currentDate = getNextValidDate(today, targetDayOfWeek);

				while (!currentDate.isAfter(endDate)) {
					LOGGER.info("Checking availability for date: {}", currentDate);

					List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
							.findByBookingDateTimeAndListenerAndActiveTrue(currentDate, listener);
					LOGGER.info("Booked calls on {}: {}", currentDate, bookedCalls.size());

					List<TimeSlotDto> allBookedSlots = converBookcallsToTimeSlot(bookedCalls);

					List<TimeSlotDto> generatedSlots = generateAvailableTimeSlots(
							currentDate, listenerAvailability, idRequestDto, timeZone);
					LOGGER.info("Generated time slots on {}: {}", currentDate, generatedSlots.size());

					List<TimeSlotDto> listenerAvailableSlots = findAvailableSlots(generatedSlots, allBookedSlots);
					LOGGER.info("Available time slots on {}: {}", currentDate, listenerAvailableSlots.size());


					AvailabilityResponseDto availabilityResponseDto = new AvailabilityResponseDto();
					availabilityResponseDto.setDate(currentDate);
					availabilityResponseDto.setAvailbleTime(listenerAvailableSlots);
					availabilityResponseDto.setStartTime(currentDate.atTime(listenerAvailability.getStartTime()).atZone(ZoneId.of(timeZone)));
					availabilityResponseDto.setEndTime(currentDate.atTime(listenerAvailability.getEndTime()).atZone(ZoneId.of(timeZone)));

					response.add(availabilityResponseDto);

					currentDate = currentDate.plusWeeks(1);
				}
			}
			LOGGER.info("response :" + response);
			response1 = convertAvailableTimeToTimezone(response, timeZone);
			LOGGER.info("Successfully processed listener availability dates");
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKeyAndData(
					SuccessMsgEnum.DATE_LIST_SUCCESSFULLY.getCode(), response1));

		} catch (Exception e) {
			LOGGER.error("Error occurred while processing listener availability dates", e);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

//	@PostMapping(ApplicationURIConstants.DATELIST)
//	public ResponseEntity<Object> listnerAvailabilityDates(@RequestBody BookedCallDto idRequestDto) {
//		LOGGER.info(ApplicationConstants.ENTER_LABEL);
//
//		try {
//			User user = getLoggedInUser();
//			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
//					.findByIdAndActiveTrue(idRequestDto.getListenerId());
//
//			System.err.println("listener : "+listener);
//			List<ListenerAvailability> availability = getServiceRegistry().getListenerAvailabilityService()
//					.findByUserAndActiveTrue(listener.getUser());
//
//			System.err.println("availability : "+availability);
//
//			String timeZone = idRequestDto.getTimeZone();
//			String listenerTimeZone = listener.getUser().getTimeZone();
//			LocalDate today = LocalDate.now();
//
//			int date = today.getDayOfMonth();
//
//			LocalDate endDate = today.plusMonths(1).withDayOfMonth(date);
//			List<AvailabilityResponseDto> response = new ArrayList<>();
//
//			// Iterate over listener availability
//			for (ListenerAvailability listenerAvailability : availability) {
//				DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(listenerAvailability.getWeekDay());
//
//				LocalDate currentDate = getNextValidDate(today, targetDayOfWeek);
//
//				while (!currentDate.isAfter(endDate)) {
//					List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
//							.findByBookingDateTimeAndListenerAndActiveTrue(currentDate, listener);
//
//					List<TimeSlotDto> allBookedSlots = converBookcallsToTimeSlot(bookedCalls);
//
//					// Generate time slots with proper timezone conversion
//					CrossDayTimeSlots crossDaySlots = generateAvailableTimeSlotsWithTimezone(
//							currentDate, listenerAvailability, idRequestDto, timeZone, listenerTimeZone);
//
//					// Handle first day slots
//					if (!crossDaySlots.getFirstDaySlots().isEmpty()) {
//						List<TimeSlotDto> firstDayAvailableSlots = findAvailableSlots(crossDaySlots.getFirstDaySlots(), allBookedSlots);
//
//						if (!firstDayAvailableSlots.isEmpty()) {
//							AvailabilityResponseDto firstDayResponse = new AvailabilityResponseDto();
//							firstDayResponse.setDate(currentDate);
//							firstDayResponse.setAvailbleTime(firstDayAvailableSlots);
//							firstDayResponse.setStartTime(crossDaySlots.getStartTime());
//							firstDayResponse.setEndTime(crossDaySlots.getEndTime());
//							firstDayResponse.setIsCrossDaySlot(crossDaySlots.getIsCrossDay());
//
//							if (crossDaySlots.getIsCrossDay()) {
//								firstDayResponse.setNextDayDate(currentDate.plusDays(1));
//								firstDayResponse.setNextDayTimeSlots(crossDaySlots.getSecondDaySlots());
//							}
//
//							response.add(firstDayResponse);
//						}
//					}
//
//					// Handle second day slots for cross-day availability
//					if (crossDaySlots.getIsCrossDay() && !crossDaySlots.getSecondDaySlots().isEmpty()) {
//						LocalDate nextDay = currentDate.plusDays(1);
//						List<BookedCalls> nextDayBookedCalls = getServiceRegistry().getBookedCallsService()
//								.findByBookingDateTimeAndListenerAndActiveTrue(nextDay, listener);
//						List<TimeSlotDto> nextDayBookedSlots = converBookcallsToTimeSlot(nextDayBookedCalls);
//
//						List<TimeSlotDto> secondDayAvailableSlots = findAvailableSlots(crossDaySlots.getSecondDaySlots(), nextDayBookedSlots);
//
//						if (!secondDayAvailableSlots.isEmpty()) {
//							AvailabilityResponseDto secondDayResponse = new AvailabilityResponseDto();
//							secondDayResponse.setDate(nextDay);
//							secondDayResponse.setAvailbleTime(secondDayAvailableSlots);
//							secondDayResponse.setStartTime(crossDaySlots.getNextDayStartTime());
//							secondDayResponse.setEndTime(crossDaySlots.getNextDayEndTime());
//							secondDayResponse.setIsCrossDaySlot(false);
//
//							response.add(secondDayResponse);
//						}
//					}
//
//					currentDate = currentDate.plusWeeks(1);
//				}
//			}
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKeyAndData(
//					SuccessMsgEnum.DATE_LIST_SUCCESSFULLY.getCode(), response));
//
//		} catch (Exception e) {
//			LOGGER.error("Error occurred while processing listener availability dates", e);
//			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
//		}
//	}

	private CrossDayTimeSlots generateAvailableTimeSlotsWithTimezone(LocalDate currentDate, ListenerAvailability slot,
																	 BookedCallDto idRequestDto, String requestTimeZone, String listenerTimeZone) {

		CrossDayTimeSlots crossDaySlots = new CrossDayTimeSlots();

		// Get the listener's availability times in their timezone
		LocalDateTime startDateTime = currentDate.atTime(slot.getStartTime());
		LocalDateTime endDateTime = currentDate.atTime(slot.getEndTime());

		// Convert to requested timezone
		ZoneId listenerZone = ZoneId.of(listenerTimeZone);
		ZoneId requestZone = ZoneId.of(requestTimeZone);

		ZonedDateTime startZoned = startDateTime.atZone(listenerZone);
		ZonedDateTime endZoned = endDateTime.atZone(listenerZone);

		ZonedDateTime startInRequestZone = startZoned.withZoneSameInstant(requestZone);
		ZonedDateTime endInRequestZone = endZoned.withZoneSameInstant(requestZone);

		// Check if the slot spans across days in the requested timezone
		LocalDate startDate = startInRequestZone.toLocalDate();
		LocalDate endDate = endInRequestZone.toLocalDate();

		crossDaySlots.setStartTime(startInRequestZone);
		crossDaySlots.setEndTime(endInRequestZone);

		if (startDate.equals(endDate)) {
			// Same day slot
			crossDaySlots.setIsCrossDay(false);
			LocalTime startTime = startInRequestZone.toLocalTime();
			LocalTime endTime = endInRequestZone.toLocalTime();

			List<TimeSlotDto> allSlots;
			if (currentDate.equals(LocalDate.now())) {
				allSlots = getCommonServices().generateTimeSlotsForCurrentDate(startTime, endTime,
						idRequestDto.getDurationInMinutes(), requestTimeZone);
			} else {
				allSlots = getCommonServices().generateTimeSlots(startTime, endTime, idRequestDto.getDurationInMinutes());
			}
			crossDaySlots.setFirstDaySlots(allSlots);

		} else {
			// Cross-day slot - split into two separate dates
			crossDaySlots.setIsCrossDay(true);
			LocalTime startTime = startInRequestZone.toLocalTime();
			LocalTime endTime = endInRequestZone.toLocalTime();

			// First day slots (from start time to midnight)
			LocalTime midnight = LocalTime.of(23, 59, 59);
			List<TimeSlotDto> firstDaySlots;
			if (currentDate.equals(LocalDate.now())) {
				firstDaySlots = getCommonServices().generateTimeSlotsForCurrentDate(startTime, midnight,
						idRequestDto.getDurationInMinutes(), requestTimeZone);
			} else {
				firstDaySlots = getCommonServices().generateTimeSlots(startTime, midnight, idRequestDto.getDurationInMinutes());
			}
			crossDaySlots.setFirstDaySlots(firstDaySlots);

			// Second day slots (from midnight to end time)
			LocalTime startOfDay = LocalTime.of(0, 0);
			List<TimeSlotDto> secondDaySlots = getCommonServices().generateTimeSlots(startOfDay, endTime, idRequestDto.getDurationInMinutes());
			crossDaySlots.setSecondDaySlots(secondDaySlots);

			// Set next day start and end times
			LocalDate nextDay = startDate.plusDays(1);
			crossDaySlots.setNextDayStartTime(ZonedDateTime.of(nextDay, startOfDay, requestZone));
			crossDaySlots.setNextDayEndTime(endInRequestZone);
		}

		return crossDaySlots;
	}

	// Helper class to handle cross-day time slots
	private static class CrossDayTimeSlots {
		private List<TimeSlotDto> firstDaySlots = new ArrayList<>();
		private List<TimeSlotDto> secondDaySlots = new ArrayList<>();
		private Boolean isCrossDay = false;
		private ZonedDateTime startTime;
		private ZonedDateTime endTime;
		private ZonedDateTime nextDayStartTime;
		private ZonedDateTime nextDayEndTime;

		// Getters and setters
		public List<TimeSlotDto> getFirstDaySlots() {
			return firstDaySlots;
		}

		public void setFirstDaySlots(List<TimeSlotDto> firstDaySlots) {
			this.firstDaySlots = firstDaySlots;
		}

		public List<TimeSlotDto> getSecondDaySlots() {
			return secondDaySlots;
		}

		public void setSecondDaySlots(List<TimeSlotDto> secondDaySlots) {
			this.secondDaySlots = secondDaySlots;
		}

		public Boolean getIsCrossDay() {
			return isCrossDay;
		}

		public void setIsCrossDay(Boolean isCrossDay) {
			this.isCrossDay = isCrossDay;
		}

		public ZonedDateTime getStartTime() {
			return startTime;
		}

		public void setStartTime(ZonedDateTime startTime) {
			this.startTime = startTime;
		}

		public ZonedDateTime getEndTime() {
			return endTime;
		}

		public void setEndTime(ZonedDateTime endTime) {
			this.endTime = endTime;
		}

		public ZonedDateTime getNextDayStartTime() {
			return nextDayStartTime;
		}

		public void setNextDayStartTime(ZonedDateTime nextDayStartTime) {
			this.nextDayStartTime = nextDayStartTime;
		}

		public ZonedDateTime getNextDayEndTime() {
			return nextDayEndTime;
		}

		public void setNextDayEndTime(ZonedDateTime nextDayEndTime) {
			this.nextDayEndTime = nextDayEndTime;
		}
	}

	private LocalDate getNextValidDate(LocalDate currentDate, DayOfWeek targetDayOfWeek) {
	    int daysToAdd = targetDayOfWeek.getValue() - currentDate.getDayOfWeek().getValue();
	    if (daysToAdd < 0) {
	        daysToAdd += 7;
	    }
	    return currentDate.plusDays(daysToAdd);
	}

	private List<TimeSlotDto> generateAvailableTimeSlots(LocalDate currentDate, ListenerAvailability slot,
														 BookedCallDto idRequestDto, String timeZone) {

		LOGGER.info("Generating available time slots for date: {}, duration: {} minutes, timezone: {}",
				currentDate, idRequestDto.getDurationInMinutes(), timeZone);

		LocalDateTime startDateTime = currentDate.atTime(slot.getStartTime());
		LocalTime startTime = getCommonServices()
				.UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(startDateTime, timeZone);

		LocalDateTime endDateTime = currentDate.atTime(slot.getEndTime());
		LocalTime endTime = getCommonServices()
				.UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(endDateTime, timeZone);

		LOGGER.info("Start time in zone {}: {}", timeZone, startTime);
		LOGGER.info("End time in zone {}: {}", timeZone, endTime);

		List<TimeSlotDto> allDurationsinMinslots;
		if (currentDate.equals(LocalDate.now())) {
			LOGGER.info("Generating slots for current date: {}", currentDate);
			allDurationsinMinslots = getCommonServices().generateTimeSlotsForCurrentDate(
					startTime, endTime, idRequestDto.getDurationInMinutes(), timeZone);
		} else {
			LOGGER.info("Generating slots for future date: {}", currentDate);
			allDurationsinMinslots = getCommonServices().generateTimeSlots(
					startTime, endTime, idRequestDto.getDurationInMinutes());
		}

		LOGGER.info("Total time slots generated (in local time): {}", allDurationsinMinslots.size());

		List<TimeSlotDto> convertedUtcDurations = new ArrayList<>();
		for (TimeSlotDto timeSlot : allDurationsinMinslots) {
			TimeSlotDto dto = new TimeSlotDto();

			LocalDateTime slotStartDateTime = currentDate.atTime(timeSlot.getStartTime());
			LocalDateTime slotEndDateTime = currentDate.atTime(timeSlot.getEndTime());

			dto.setStartTime(getCommonServices().localDateTimeToUtcTime(slotStartDateTime, timeZone));
			dto.setEndTime(getCommonServices().localDateTimeToUtcTime(slotEndDateTime, timeZone));

			convertedUtcDurations.add(dto);

			LOGGER.info("Converted slot to UTC -> Start: {}, End: {}", dto.getStartTime(), dto.getEndTime());
		}

		LOGGER.info("Final list of available UTC slots returned: {}", convertedUtcDurations.size());
		return convertedUtcDurations;
	}

	private List<TimeSlotDto> findAvailableSlots(List<TimeSlotDto> allDurationsInMinSlots,
												 List<TimeSlotDto> allBookedSlots) {
		LOGGER.info("Finding available slots...");
		LOGGER.info("Total possible duration slots: {}", allDurationsInMinSlots.size());
		LOGGER.info("Total booked slots: {}", allBookedSlots.size());

		List<TimeSlotDto> availableSlots = new ArrayList<>();

		for (TimeSlotDto timeSlot : allDurationsInMinSlots) {
			boolean isAvailable = true;

			LOGGER.info("Checking slot: {} - {}", timeSlot.getStartTime(), timeSlot.getEndTime());

			for (TimeSlotDto bookedSlot : allBookedSlots) {
				if (timeSlot.overlaps(bookedSlot)) {
					LOGGER.info("Slot {} - {} overlaps with booked slot {} - {}",
							timeSlot.getStartTime(), timeSlot.getEndTime(),
							bookedSlot.getStartTime(), bookedSlot.getEndTime());
					isAvailable = false;
					break;
				}
			}

			if (isAvailable) {
				availableSlots.add(timeSlot);
				LOGGER.info("Slot {} - {} is available", timeSlot.getStartTime(), timeSlot.getEndTime());
			}
		}

		LOGGER.info("Total available slots found: {}", availableSlots.size());
		return availableSlots;
	}


	private List<TimeSlotDto> converBookcallsToTimeSlot(List<BookedCalls> listOfbookingDateCalls) {
		List<TimeSlotDto> availableList = new ArrayList<>();
		LOGGER.info("Converting booked calls to time slots. Total bookings: {}", listOfbookingDateCalls.size());
		for (BookedCalls call : listOfbookingDateCalls) {
			LocalTime startTime = getCommonServices().getTimeFromDate(call.getBookingDateTime());
			LocalTime endTime = startTime.plusMinutes(call.getDurationInMinutes());

			TimeSlotDto slot = new TimeSlotDto(startTime, endTime);
			availableList.add(slot);

			LOGGER.info("Converted booking - Start: {}, End: {}, Duration: {} mins",
					startTime, endTime, call.getDurationInMinutes());
		}
		LOGGER.info("Total converted time slots: {}", availableList.size());
		return availableList;
	}


	@PostMapping(ApplicationURIConstants.COMMENT + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getSelectedComments(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User user = getServiceRegistry().getUserService()
					.findByIdAndActiveTrueAndIsSuspendedFalse(idRequestDto.getId());
			List<UserRatingAndReview> ratingAndReviews = getServiceRegistry().getUserRatingAndReviewService()
					.findByReviewedUserAndActiveTrueAndIsTopCommentTrueOrderByCreatedAtDesc(user);

			TotalReviewAndRatingResponseDto responseDto = new TotalReviewAndRatingResponseDto();
			List<ReviewsAndRatingsResponseDto> response = new ArrayList<>();

			List<UserRatingAndReview> reviewForAverage = getServiceRegistry().getUserRatingAndReviewService()
					.findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(user);

			if (!ratingAndReviews.isEmpty()) {
				ratingAndReviews.forEach(reviewRating -> {
					ReviewsAndRatingsResponseDto dto = new ReviewsAndRatingsResponseDto();
					BeanUtils.copyProperties(reviewRating, dto);
					User user1 = reviewRating.getReviewerUser();
					dto.setReviewerId(user1.getId());
					dto.setReviewerName(user1.getCallName());
					dto.setContact(user1.getContactNumber());
					dto.setEmail(user1.getEmail());
					if (user1.getProfilePicture() == null) {
						dto.setProfile(
								"https://wiilisten.s3.amazonaws.com/wiilisten/user/profile_images/imgprofile.jpeg");
					} else {
						dto.setProfile(user1.getProfilePicture());
					}

					response.add(dto);
				});
			}

			OptionalDouble averageRating = reviewForAverage.stream().mapToInt(UserRatingAndReview::getRating).average();
			Double averageRatings = averageRating.isPresent() ? averageRating.getAsDouble() : 0.0;
			responseDto.setAverageRatings(averageRatings);
			responseDto.setTotalReview(reviewForAverage.size());
			responseDto.setReviewsAndRatings(response);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseDto));

		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
	
	@PostMapping(ApplicationURIConstants.PAYMENT + ApplicationURIConstants.SLIP)
	public ResponseEntity<Object> getPaymentSlip(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			BookedCalls bookedCalls = getServiceRegistry().getBookedCallsService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			byte[] pdf=null;
			FileResponseDto response=new FileResponseDto();
			if(bookedCalls.getType().equals(ApplicationConstants.SCHEDULED)) {
				LOGGER.info("inside if ");
				pdf = getServiceRegistry().getSendPdfService().generatePdfForSchedule(bookedCalls.getId(), bookedCalls.getType());
			}else {
				LOGGER.info("inside else ");
				pdf = getServiceRegistry().getSendPdfService().generatePdfForOnDemand(bookedCalls.getId(), bookedCalls.getType());
			}
			response.setFile(pdf);
			
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.COMMENT)
	public ResponseEntity<Object> selectComments(@RequestBody IdStatusRequestDto idStatusRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			UserRatingAndReview ratingAndReview = getServiceRegistry().getUserRatingAndReviewService()
					.findOne(idStatusRequestDto.getId());
			if (idStatusRequestDto.getType().equals("ADD")) {
				ratingAndReview.setIsTopComment(true);
			}
			if (idStatusRequestDto.getType().equals("REMOVE")) {
				ratingAndReview.setIsTopComment(false);
			}

			getServiceRegistry().getUserRatingAndReviewService().saveORupdate(ratingAndReview);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.COMMENT_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.COMMISSION)
	public ResponseEntity<Object> getCommissionRate() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			CommissionRate commissionRate = getServiceRegistry().getCommissionRateService().findOne(1L);
			CommissionRateResponseDto response = new CommissionRateResponseDto();
			response.setRate(commissionRate.getRate());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	private static final List<String> ORDER = Arrays.asList("DAILY", "WEEKLY", "MONTHLY");

	private static class SubscriptionComparator implements Comparator<Subscription> {
		@Override
		public int compare(Subscription s1, Subscription s2) {
			int index1 = ORDER.indexOf(s1.getName());
			int index2 = ORDER.indexOf(s2.getName());

			// If the subscription name is not found in the ORDER list, put it at the end
			if (index1 == -1)
				index1 = ORDER.size();
			if (index2 == -1)
				index2 = ORDER.size();

			return Integer.compare(index1, index2);
		}
	}
//	 filteredBookedCalls = bookedcalls.getContent().stream()
//    .filter(bookedCall -> bookedCall.getBookingDateTime().plusMinutes(bookedCall.getDurationInMinutes()).isBefore(LocalDateTime.now()))
//    .collect(Collectors.toList());
}
