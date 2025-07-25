package com.wiilisten.controller.api;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;

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

	@PostMapping(ApplicationURIConstants.DATELIST)
	public ResponseEntity<Object> listnerAvailabilityDates(@RequestBody BookedCallDto idRequestDto) {
	    LOGGER.info(ApplicationConstants.ENTER_LABEL);

	    try {
	        User user = getLoggedInUser();
	        ListenerProfile listener = getServiceRegistry().getListenerProfileService()
	                .findByIdAndActiveTrue(idRequestDto.getListenerId());


					System.err.println("listener : "+listener);
	        List<ListenerAvailability> availability = getServiceRegistry().getListenerAvailabilityService()
	                .findByUserAndActiveTrue(listener.getUser());


					System.err.println("availability : "+availability);

	        String timeZone = idRequestDto.getTimeZone();
	        LocalDate today = LocalDate.now();
	        
	        int date = today.getDayOfMonth();
	        
	        LocalDate endDate = today.plusMonths(1).withDayOfMonth(date); // Adjust to next month's 7th day
	        List<AvailabilityResponseDto> response = new ArrayList<>();

	        // Iterate over listener availability
	        for (ListenerAvailability listenerAvailability : availability) {
	            DayOfWeek targetDayOfWeek = DayOfWeek.valueOf(listenerAvailability.getWeekDay());

	            LocalDate currentDate = getNextValidDate(today, targetDayOfWeek);

	            while (!currentDate.isAfter(endDate)) {
	                List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
	                        .findByBookingDateTimeAndListenerAndActiveTrue(currentDate, listener);

	                List<TimeSlotDto> allBookedSlots = converBookcallsToTimeSlot(bookedCalls);

	                List<TimeSlotDto> availableSlots = new ArrayList<>();

	                    List<TimeSlotDto> generatedSlots = generateAvailableTimeSlots(
	                            currentDate, listenerAvailability, idRequestDto, timeZone);

	                    List<TimeSlotDto> listenerAvailableSlots = findAvailableSlots(generatedSlots, allBookedSlots);
	                    availableSlots.addAll(listenerAvailableSlots);

	                AvailabilityResponseDto availabilityResponseDto = new AvailabilityResponseDto();
	                availabilityResponseDto.setDate(currentDate);
	                availabilityResponseDto.setAvailbleTime(availableSlots);
	                availabilityResponseDto.setStartTime(currentDate.atTime(listenerAvailability.getStartTime()).atZone(ZoneId.of(timeZone)));
	                availabilityResponseDto.setEndTime(currentDate.atTime(listenerAvailability.getEndTime()).atZone(ZoneId.of(timeZone)));

	                response.add(availabilityResponseDto);

	                currentDate = currentDate.plusWeeks(1);
	            }
	        }
			
	        LOGGER.info(ApplicationConstants.EXIT_LABEL);
	        return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKeyAndData(
	                SuccessMsgEnum.DATE_LIST_SUCCESSFULLY.getCode(), response));

	    } catch (Exception e) {
	        LOGGER.error("Error occurred while processing listener availability dates", e);
	        return ResponseEntity.ok(getCommonServices().generateFailureResponse());
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
	    LocalDateTime startDateTime = currentDate.atTime(slot.getStartTime());
	    LocalTime startTime = getCommonServices()
	            .UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(startDateTime, timeZone);

	    LocalDateTime endDateTime = currentDate.atTime(slot.getEndTime());
	    LocalTime endTime = getCommonServices()
	            .UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(endDateTime, timeZone);

	    List<TimeSlotDto> allDurationsinMinslots;
	    if (currentDate.equals(LocalDate.now())) {
	        allDurationsinMinslots = getCommonServices().generateTimeSlotsForCurrentDate(startTime, endTime,
	                idRequestDto.getDurationInMinutes(), timeZone);
	    } else {
	        allDurationsinMinslots = getCommonServices().generateTimeSlots(startTime, endTime,
	                idRequestDto.getDurationInMinutes());
	    }

	    List<TimeSlotDto> convertedUtcDurations = new ArrayList<>();
	    allDurationsinMinslots.forEach(timeSlot -> {
	        TimeSlotDto dto = new TimeSlotDto();
	        LocalDateTime slotStartDateTime = currentDate.atTime(timeSlot.getStartTime());
	        LocalDateTime slotEndDateTime = currentDate.atTime(timeSlot.getEndTime());

	        dto.setStartTime(getCommonServices().localDateTimeToUtcTime(slotStartDateTime, timeZone));
	        dto.setEndTime(getCommonServices().localDateTimeToUtcTime(slotEndDateTime, timeZone));
	        convertedUtcDurations.add(dto);
	    });

	    return convertedUtcDurations;
	}

	private List<TimeSlotDto> findAvailableSlots(List<TimeSlotDto> allDurationsinMinslots,
	        List<TimeSlotDto> allBookedSlots) {
	    List<TimeSlotDto> availableSlots = new ArrayList<>();
	    for (TimeSlotDto timeSlot : allDurationsinMinslots) {
	        boolean isAvailable = true;
	        for (TimeSlotDto bookedSlot : allBookedSlots) {
	            if (timeSlot.overlaps(bookedSlot)) {
	                isAvailable = false;
	                break;	
	            }
	        }
	        if (isAvailable) {
	            availableSlots.add(timeSlot);
	        }
	    }
	    return availableSlots;
	}

	private List<TimeSlotDto> converBookcallsToTimeSlot(List<BookedCalls> listOfbookingDateCalls) {
	    List<TimeSlotDto> availableList = new ArrayList<>();
	    for (BookedCalls call : listOfbookingDateCalls) {
	        LocalTime startTime = getCommonServices().getTimeFromDate(call.getBookingDateTime());
	        LocalTime endTime = getCommonServices().getTimeFromDate(call.getBookingDateTime())
	                .plusMinutes(call.getDurationInMinutes());
	        TimeSlotDto slot = new TimeSlotDto(startTime, endTime);
	        availableList.add(slot);
	    }
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
