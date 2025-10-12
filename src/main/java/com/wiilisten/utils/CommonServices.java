package com.wiilisten.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import com.wiilisten.entity.*;
import com.wiilisten.enums.CouponType;
import com.wiilisten.repo.CouponsRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.AvailabilityDTO;
import com.wiilisten.request.DutyTimeRequestDto;
import com.wiilisten.request.PaginationAndSortingDetails;
import com.wiilisten.request.TimeSlotDto;
import com.wiilisten.response.BookedCallDetailsDto;
import com.wiilisten.response.FavoriteListenerDetailsDto;

/**
 * The <code>CommonServices</code> is used to holds all the common methods to be
 * used in the application.
 *
 * @author Hyperlink Infosystem
 */
@Service
public class CommonServices {

	@Autowired
	private ServiceRegistry serviceRegistry;

	@Autowired
	private CouponsRepository couponsRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonServices.class);

	private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private MessageSource messageSource;

	/**
	 * The <code>convertToBcrypt</code> is used to convert the plaintext to bcrypt
	 * encoded text.
	 *
	 * @param plainText
	 * @return
	 */
	public static String convertToBcrypt(final String plainText) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);

		return bCryptPasswordEncoder.encode(plainText);
	}

	/**
	 * This method <code>generateJSONFromObject</code> is responsible to generate
	 * JSON from a given object.
	 *
	 * @param object
	 * @return
	 */
	public static String generateJSONFromObject(final Object object) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		final ObjectMapper objectMapper = new ObjectMapper();

		String jsonString = null;

		try {
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

			/**
			 * Store JSON in to jsonString
			 */
			jsonString = objectMapper.writeValueAsString(object);

		} catch (final Exception exception) {

			LOGGER.error(ApplicationConstants.EXCEPTION_LABEL, exception);
		}

		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return jsonString;
	}

	/**
	 * The <code>matchesWithBcrypt</code> is used to compare plaintext and
	 * bcryptText
	 *
	 * @param plainText
	 * @param bcryptText
	 * @return
	 */
	public static boolean matchesWithBcrypt(final String plainText, final String bcryptText) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return bCryptPasswordEncoder.matches(plainText, bcryptText);
	}

	/**
	 * The <code>generateBadResponseWithMessageKey</code> is used to generate bad
	 * response with messageKey
	 *
	 * @param messageKey
	 * @return
	 */
	public GenericResponse generateBadResponseWithMessageKey(final String messageKey) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new GenericResponse(ApplicationResponseConstants.INVALID_REQUEST, getMessageByCode(messageKey));
	}

	/**
	 * The <code>generateFailureResponse</code> is used to send the failure response
	 * in case of BAD REQUESTS.
	 *
	 * @return
	 */
	public GenericResponse generateFailureResponse() {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new GenericResponse(ApplicationResponseConstants.INVALID_REQUEST,
				ApplicationResponseConstants.INVALID_REQUEST_MESSAGE, null);
	}

	/**
	 * The <code>generateFailureResponse</code> is used to send the failure response
	 * with custome messages.
	 *
	 * @return
	 */
	public GenericResponse generateFailureResponse(String message) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new GenericResponse(ApplicationResponseConstants.INVALID_REQUEST, message, null);
	}

	/**
	 * The <code>generateGenericSuccessResponse</code> is used to send the generic
	 * success response in case of 200 OK.
	 *
	 * @param object
	 * @return
	 */
	public GenericResponse generateGenericSuccessResponse(final Object object) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new GenericResponse(ApplicationResponseConstants.SUCCESS_RESPONSE,
				ApplicationResponseConstants.SUCCESS_RESPONSE_MESSAGE, object);

	}

	public GenericResponse generateGenericSuccessResponse(String message) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new GenericResponse(ApplicationResponseConstants.SUCCESS_RESPONSE,
				message);

	}

	/**
	 * The <code>generateResponseForNoDataFound</code> is used to set Generic
	 * Response for no data found.
	 *
	 * @return
	 */
	public GenericResponse generateResponseForNoDataFound() {
		return new GenericResponse(ApplicationResponseConstants.NO_DATA_FOUND,
				getMessageByCode(ErrorDataEnum.NO_DATA_FOUND.getCode()));
	}

	public GenericResponse generateGenericFailResponse(String message) {
		return new GenericResponse(ApplicationResponseConstants.INVALID_REQUEST,
				message);
	}

	/**
	 * The <code>generateResponseWithCodeAndMessage</code> is used to generate
	 * generic response based on code and message key
	 *
	 * @param code
	 * @param keyForMessage
	 * @return
	 */
	public GenericResponse generateResponseWithCodeAndMessage(final String code, final String keyForMessage) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new GenericResponse(code, getMessageByCode(keyForMessage));
	}

	/**
	 * The <code>generateSuccessResponseWithMessageKey</code> is used to generate
	 * success response with message key
	 *
	 * @param code
	 * @return
	 */
	public GenericResponse generateSuccessResponseWithMessageKey(final String code) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new GenericResponse(ApplicationResponseConstants.SUCCESS_RESPONSE, getMessageByCode(code));
	}

	public GenericResponse generateSuccessResponseWithMessageKeyAndData(final String code, final Object object) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new GenericResponse(ApplicationResponseConstants.SUCCESS_RESPONSE, getMessageByCode(code), object);
	}

	/**
	 * The <code>getMessageByCode</code> is used to get the Message according to the
	 * key.
	 *
	 * @param string
	 * @return
	 */
	public String getMessageByCode(final String string) {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return getMessageSource().getMessage(string, null, Locale.getDefault());
	}

	/**
	 * @return the messageSource
	 */
	public MessageSource getMessageSource() {
		return messageSource;
	}

	/**
	 * This method <code>getRandomAlphaNumericString</code> is used to generate
	 * Random Alpha numeric String.
	 *
	 * @return
	 */
	public String getRandomAlphaNumericString() {

		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

//    public UserOTP generateOTPForRegisterUser(final User user) {
//
//		LOGGER.info(ApplicationConstants.ENTER_LABEL);
//
//		final UserOTP userOtp = new UserOTP();
//		userOtp.setOtp(RandomStringUtils.randomNumeric(4));
//		userOtp.setEmail(user.getEmail());
//		
//		Date currentTime = new Date();
//
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(currentTime);
//
//        calendar.add(Calendar.MINUTE, 2);
//        userOtp.setExpirtime(calendar.getTime());
//        userOtp.setExpired(Boolean.FALSE);
//
//		LOGGER.info(ApplicationConstants.EXIT_LABEL);
//		return userOtp;
//
//	}

	public String generate4DigitOtp() {
		String otp = RandomStringUtils.randomNumeric(4);

		OtpHistory otpHistory = serviceRegistry.getOtpHistoryService().findByOtpAndActiveTrue(otp);

		while (otpHistory != null) {
			otp = RandomStringUtils.randomNumeric(4);
			otpHistory = serviceRegistry.getOtpHistoryService().findByOtpAndActiveTrue(otp);
		}
		;

		return otp;
	}

	public String generateReferralCode() {
		String referralCode = RandomStringUtils.randomNumeric(6);

		User user = serviceRegistry.getUserService().findByReferralCodeAndActiveTrue(referralCode);

		while (user != null) {
			referralCode = RandomStringUtils.randomNumeric(6);
			user = serviceRegistry.getUserService().findByReferralCodeAndActiveTrue(referralCode);
		}
		;

		return referralCode;
	}

	public Long getCountOfTrainingVideos() {
		return serviceRegistry.getTrainingMaterialService().countByContentTypeAndActiveTrue("TRAINING");
	}

	public Long getCountOfListenerAndTrainingVideos() {
		return serviceRegistry.getTrainingMaterialService().countByContentTypeAndSubCategoryAndActiveTrue("TRAINING", "LISTENER");
	}

	/**
	 * This method used to check for unique username
	 * 
	 * @param username
	 * @return 1 for duplicate, 0 for unique (default)
	 */
	public int checkListenerUniqueUsername(String username) {

		ListenerProfile listener = serviceRegistry.getListenerProfileService().findByUserNameAndActiveTrue(username);
		if (listener != null)
			return 1;

		return 0;
	}

	/**
	 * This method fetches listener's availability and returns API response DTO
	 * object
	 * 
	 * @param User
	 */
	public List<AvailabilityDTO> generateResponseForListenerAvailability(User user) {

		List<ListenerAvailability> listenerAvailabilities = serviceRegistry.getListenerAvailabilityService()
				.findByUserAndActiveTrue(user);
		if (!ApplicationUtils.isEmpty(listenerAvailabilities)) {

			Map<String, List<DutyTimeRequestDto>> dutyDetailsMap = new HashMap<>();

			listenerAvailabilities.forEach(dutyDetail -> {

				String weekDay = dutyDetail.getWeekDay();

				DutyTimeRequestDto dutyTime = new DutyTimeRequestDto();
				dutyTime.setStartTime(dutyDetail.getStartTime().toString());
				dutyTime.setEndTime(dutyDetail.getEndTime().toString());

				if (!dutyDetailsMap.containsKey(weekDay)) {
					dutyDetailsMap.put(weekDay, Arrays.asList(dutyTime));
				} else {
					List<DutyTimeRequestDto> test = new ArrayList<>();
					test.addAll(dutyDetailsMap.get(weekDay));
					test.add(dutyTime);
					dutyDetailsMap.put(weekDay, test);
				}

			});

			List<AvailabilityDTO> dutyDetailsList = new ArrayList<AvailabilityDTO>();
			dutyDetailsMap.forEach((weekDay, dutyTimings) -> {

				AvailabilityDTO testData = new AvailabilityDTO();
				testData.setDay(weekDay);
				testData.setDutyTimings(dutyTimings);

				dutyDetailsList.add(testData);
			});

			return dutyDetailsList;
		}

		return null;
	}

	public Double getOnDemandCallsCommisionRate() {
		CommissionRate commissionRate = serviceRegistry.getCommissionRateService().findByActiveTrue();
		if (commissionRate == null)
			return null;
		return commissionRate.getRate();
	}

	/**
	 * This method converts Entity class to DTO class to create API response
	 * 
	 * @param favListener
	 * @param listener
	 * @param commisionRate
	 * @return
	 */
	public FavoriteListenerDetailsDto convertListenerProfileEntityToDtoForCardLayout(ListenerProfile listener) {
		FavoriteListenerDetailsDto tempFavListener = new FavoriteListenerDetailsDto();

		tempFavListener.setListenerId(listener.getId());
		tempFavListener.setListenerUserId(listener.getUser().getId());
		tempFavListener.setCallName(listener.getUser().getCallName());
		tempFavListener.setCurrentRatings(listener.getUser().getCurrentRating());
		tempFavListener.setAppActiveStatus(listener.getAppActiveStatus());
		tempFavListener.setUserName(listener.getUserName());
		tempFavListener.setProfilePicture(listener.getUser().getProfilePicture());
		tempFavListener.setRatePerMinuteForSchedule(listener.getRatePerMinute());
		tempFavListener.setRatePerMinuteForOnDemand(1.2D);
		tempFavListener.setMaxDuration(listener.getCallMaxDuration());

		return tempFavListener;
	}

	public Boolean checkBlockerHasBlockedUserOrNot(User blocker, User blocked) {
		BlockedUser blockedUser = serviceRegistry.getBlockedUserService()
				.findByBlockerUserAndBlockedUserAndActiveTrue(blocker, blocked);
		if (blockedUser == null)
			return false;

		return true;
	}

	public Pageable convertRequestToPageableObject(Object request) {

		if (request instanceof PaginationAndSortingDetails) {
			PaginationAndSortingDetails tempRequest = (PaginationAndSortingDetails) request;
			return PageRequest.of(tempRequest.getPageNumber(), tempRequest.getPageSize(),
					Sort.by(tempRequest.getSortType().toUpperCase().equals("ASC") ? Order.asc(tempRequest.getSortBy())
							: Order.desc(tempRequest.getSortBy())));

		}

		return null;
	}

	public String getWeekDayFromDate(LocalDate bookingDate) {

		DayOfWeek weekDay = bookingDate.getDayOfWeek();

		return weekDay.toString();

	}

	public LocalTime getTimeFromDate(LocalDateTime bookingDate) {

		LocalTime time = bookingDate.toLocalTime();

		return time;

	}

	public LocalTime UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(LocalDateTime datetime, String timezone) {

		LocalTime localTime = null;
		try {
			LOGGER.info("Converting UTC datetime [{}] to local time in timezone [{}]", datetime, timezone);

			ZoneId requestZoneId = ZoneId.of(timezone);
			LocalDateTime localDateTime = datetime;
			ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC);
			ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(requestZoneId);
			localTime = utcDateTime.toLocalTime();

		} catch (final Exception e) {
			LOGGER.error("Error while converting UTC to local time. Input datetime: {}, timezone: {}. Error: {}",
					datetime, timezone, e.getMessage(), e);
		}
		LOGGER.info("Converted time: {}", localTime);
		return localTime;
	}

	public LocalTime localDateTimeToUtcTime(LocalDateTime now, String zone) {
		// LOGGER.info(ApplicationConstants.ENTER_LABEL);
		LocalTime utcLocalDateTime = null;
		try {
			ZonedDateTime localDateTimeWithZone = now.atZone(ZoneId.of(zone));
			ZonedDateTime utcDateTime = localDateTimeWithZone.withZoneSameInstant(ZoneId.of("UTC"));

			utcLocalDateTime = utcDateTime.toLocalTime();
		} catch (final Exception e) {
//			LOGGER.error(ApplicationConstants.ERROR_LABEL, e);
		}

		// LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return utcLocalDateTime;
	}

//	public List<TimeSlotDto> generateTimeSlots(LocalTime startTime, LocalTime endTime, int slotDuration) {
//		List<TimeSlotDto> timeSlots = new ArrayList<>();
//
//		// Loop from start time to end time, adding slots at specified interval
//		LocalTime currentTime = startTime;
//		while (currentTime.plusMinutes(slotDuration).isBefore(endTime) || currentTime.equals(endTime)) {
//			// Define the end time for the current time slot
//			LocalTime slotEndTime = currentTime.plusMinutes(slotDuration);
//
//			// Create a TimeSlot object for the current time slot
//			TimeSlotDto timeSlot = new TimeSlotDto(currentTime, slotEndTime);
//
//			// Add the time slot to the list
//			timeSlots.add(timeSlot);
//
//			// Move to the start of the next time slot
//			currentTime = slotEndTime;
//		}
//
//		return timeSlots;
//	}

//	public List<TimeSlotDto> generateTimeSlotsForCurrentDate(LocalTime startTime, LocalTime endTime, int slotDuration,
//			String zoneId) {
//		List<TimeSlotDto> timeSlots = new ArrayList<>();
//
//		// Determine the current time
//		ZoneId zone = ZoneId.of(zoneId);
//
//		LocalTime currentTime = ZonedDateTime.now(zone).toLocalTime();
//
//		// Loop from start time to end time, adding slots at specified interval
//		while (startTime.isBefore(endTime) || startTime.equals(endTime)) {
//			// Skip slots before the current time
//			if (startTime.isBefore(currentTime)) {
//				startTime = startTime.plusMinutes(slotDuration);
//				continue;
//			}
//
//			// Define the end time for the current time slot
//			LocalTime slotEndTime = startTime.plusMinutes(slotDuration);
//
//			// Create a TimeSlot object for the current time slot
//			TimeSlotDto timeSlot = new TimeSlotDto(startTime, slotEndTime);
//
//			// Add the time slot to the list
//			timeSlots.add(timeSlot);
//
//			// Move to the start of the next time slot
//			startTime = slotEndTime;
//		}
//		if (!timeSlots.isEmpty()) {
//			timeSlots.remove(timeSlots.size() - 1);
//		}
//
//		return timeSlots;
//	}


	public List<TimeSlotDto> generateTimeSlots(LocalTime startTime, LocalTime endTime, int slotDuration) {
		LOGGER.info("Generating time slots...");
		LOGGER.info("Input parameters - StartTime: {}, EndTime: {}, SlotDuration: {} minutes", startTime, endTime, slotDuration);

		List<TimeSlotDto> timeSlots = new ArrayList<>();

		if (startTime == null || endTime == null || slotDuration <= 0) {
			LOGGER.error("Invalid input parameters: startTime={}, endTime={}, slotDuration={}", startTime, endTime, slotDuration);
			return timeSlots;
		}

		if (endTime.isBefore(startTime)) {
			LOGGER.error("End time {} must be after start time {}", endTime, startTime);
			return timeSlots;
		}

		LocalTime currentTime = startTime;

		while (true) {
			LocalTime slotEndTime = currentTime.plusMinutes(slotDuration);

			if (slotEndTime.isAfter(endTime)) {
				LOGGER.info("Reached end time. Stopping slot generation.");
				break;
			}

			if (slotEndTime.isBefore(currentTime)) {
				LOGGER.info("Detected possible time wrap around. Stopping slot generation.");
				break;
			}

			TimeSlotDto slot = new TimeSlotDto(currentTime, slotEndTime);
			timeSlots.add(slot);

			LOGGER.info("Added time slot: {} - {}", slot.getStartTime(), slot.getEndTime());

			currentTime = slotEndTime;
		}

		LOGGER.info("Total slots generated: {}", timeSlots.size());
		return timeSlots;
	}

	public List<TimeSlotDto> generateTimeSlotsForCurrentDate(LocalTime startTime, LocalTime endTime, int slotDuration, String zoneId) {
		LOGGER.info("Generating time slots for current date...");
		LOGGER.info("Input Parameters - startTime: {}, endTime: {}, slotDuration: {}, zoneId: {}", startTime, endTime, slotDuration, zoneId);

		List<TimeSlotDto> timeSlots = new ArrayList<>();

		try {
			ZoneId zone = ZoneId.of(zoneId);
			LocalTime now = ZonedDateTime.now(zone).toLocalTime();
			LOGGER.info("Current time in zone {} is {}", zoneId, now);

			// Align start time to be at or after 'now'
			LocalTime current = startTime;
			while (current.plusMinutes(slotDuration).isBefore(now)) {
				current = current.plusMinutes(slotDuration);
			}

			// Adjust current to be rounded up to next aligned slot if needed
			if (now.isAfter(current)) {
				long minutesSinceStart = Duration.between(current, now).toMinutes();
				long remainder = minutesSinceStart % slotDuration;

				if (remainder != 0) {
					current = now.plusMinutes(slotDuration - remainder);
					LOGGER.info("Adjusted current to next aligned slot: {}", current);
				} else {
					current = now;
					LOGGER.info("Current aligned with now: {}", current);
				}
			}

			while (true) {
				LocalTime slotEnd = current.plusMinutes(slotDuration);

				if (slotEnd.isAfter(endTime) || slotEnd.isBefore(current)) {
					LOGGER.info("Stopping slot generation. Next slot end {} is invalid (past endTime or time wrap).", slotEnd);
					break;
				}

				TimeSlotDto slot = new TimeSlotDto(current, slotEnd);
				timeSlots.add(slot);
				LOGGER.info("Generated slot: {} - {}", slot.getStartTime(), slot.getEndTime());

				current = slotEnd;
			}

			LOGGER.info("Total time slots generated: {}", timeSlots.size());
		} catch (Exception e) {
			LOGGER.error("Error while generating time slots: {}", e.getMessage(), e);
			throw e; // rethrow to propagate the error
		}

		return timeSlots;
	}

	public List<BookedCallDetailsDto> convertBeanToDtoForBookedCall(List<BookedCalls> bookedcalls, String type) {
		// TODO Auto-generated method stub
		List<BookedCallDetailsDto> response = new ArrayList<BookedCallDetailsDto>();
		for (BookedCalls call : bookedcalls) {
			System.err.println("Id >>>>>>>>>>>>>   " + call.getId());
			System.err.println("Duration >>>>>>>>>>>>>>>    " + call.getDurationInMinutes());
			
			if (call.getBookingDateTime().plusMinutes(call.getDurationInMinutes())
					.isBefore(LocalDateTime.now(ZoneOffset.UTC)) && !type.equals(ApplicationConstants.HISTORY)) { 
																													
				LOGGER.info("inside if" + call.getId());
				continue;
			}

			User callerUser = call.getCaller().getUser();
			User listenerUser = call.getListener().getUser();

			if (isBlocked(callerUser, listenerUser) || isBlocked(listenerUser, callerUser)) {
				continue;
			}
			BookedCallDetailsDto dto = new BookedCallDetailsDto();
			dto.setBookingId(call.getId());
			dto.setBookingDateTime(call.getBookingDateTime());
			dto.setCallerUserId(call.getCaller().getUser().getId());
			dto.setType(call.getType());
			dto.setListenerUserId(call.getListener().getUser().getId());
			dto.setCallRequestStatus(call.getCallRequestStatus());
			dto.setDurationInMinutes(call.getDurationInMinutes());
			dto.setMaxDuration(call.getListener().getCallMaxDuration());
			dto.setPrice(call.getPayableAmount());
			dto.setRatePerMinute(call.getListener().getRatePerMinute());
			dto.setListenerId(call.getListener().getId());
			dto.setUserName(call.getListener().getUserName());
			dto.setImg(call.getListener().getUser().getProfilePicture());
			dto.setCallName(call.getListener().getUser().getCallName());
			dto.setRating(call.getListener().getUser().getCurrentRating());
			dto.setCreatedAt(call.getCreatedAt());
			dto.setRating(call.getListener().getUser().getCurrentRating());
			dto.setTotalReviews(call.getListener().getUser().getTotalReviews());
			response.add(dto);
		}
		return response;
	}

	public List<BookedCallDetailsDto> convertBeanToDtoForBookedCallForHistory(List<BookedCalls> bookedcalls,
			String type) {
		// TODO Auto-generated method stub
		List<BookedCallDetailsDto> response = new ArrayList<BookedCallDetailsDto>();
		for (BookedCalls call : bookedcalls) {
			if (call.getBookingDateTime().plusMinutes(call.getDurationInMinutes())
					.isBefore(LocalDateTime.now(ZoneOffset.UTC)) && !type.equals(ApplicationConstants.HISTORY)) {
				continue;
			}

			BookedCallDetailsDto dto = new BookedCallDetailsDto();
			dto.setBookingId(call.getId());
			dto.setBookingDateTime(call.getBookingDateTime());
			dto.setCallerUserId(call.getCaller().getUser().getId());
			dto.setType(call.getType());
			dto.setListenerUserId(call.getListener().getUser().getId());
			dto.setCallRequestStatus(call.getCallRequestStatus());
			dto.setDurationInMinutes(call.getDurationInMinutes());
			dto.setMaxDuration(call.getListener().getCallMaxDuration());
			dto.setPrice(call.getPayableAmount());
			dto.setRatePerMinute(call.getListener().getRatePerMinute());
			dto.setListenerId(call.getListener().getId());
			dto.setUserName(call.getListener().getUserName());
			dto.setImg(call.getListener().getUser().getProfilePicture());
			dto.setCallName(call.getListener().getUser().getCallName());
			dto.setRating(call.getListener().getUser().getCurrentRating());
			dto.setCreatedAt(call.getCreatedAt());
			dto.setRating(call.getListener().getUser().getCurrentRating());
			dto.setTotalReviews(call.getListener().getUser().getTotalReviews());
			response.add(dto);
		}
		return response;
	}

	public List<BookedCallDetailsDto> convertBeanToDtoForBookedCallListener(List<BookedCalls> bookedcalls) {
		// TODO Auto-generated method stub
		LOGGER.info("upcoming size {}" + bookedcalls.size());
		List<BookedCallDetailsDto> response = new ArrayList<BookedCallDetailsDto>();
		for (BookedCalls call : bookedcalls) {
			LOGGER.info("id {}" + call.getId());
			if (call.getBookingDateTime().plusMinutes(call.getDurationInMinutes())
					.isBefore(LocalDateTime.now(ZoneOffset.UTC))) {
				LOGGER.info("inside if");
				continue;
			}

			User callerUser = call.getCaller().getUser();
			User listenerUser = call.getListener().getUser();

			if (isBlocked(callerUser, listenerUser) || isBlocked(listenerUser, callerUser)) {
				continue;
			}
			BookedCallDetailsDto dto = new BookedCallDetailsDto();
			dto.setBookingId(call.getId());
			dto.setBookingDateTime(call.getBookingDateTime());
			dto.setCallRequestStatus(call.getCallRequestStatus());
			dto.setDurationInMinutes(call.getDurationInMinutes());
			dto.setCallerUserId(call.getCaller().getUser().getId());
			dto.setListenerUserId(call.getListener().getUser().getId());
			dto.setMaxDuration(call.getListener().getCallMaxDuration());
			dto.setPrice(call.getPrice());
			dto.setRatePerMinute(call.getListener().getRatePerMinute());
			dto.setListenerId(call.getListener().getId());
			dto.setUserName(call.getListener().getUserName());
			dto.setImg(call.getCaller().getUser().getProfilePicture());
			dto.setCallName(call.getCaller().getUser().getCallName());
			dto.setRating(call.getCaller().getUser().getCurrentRating());
			dto.setCreatedAt(call.getCreatedAt());
			dto.setRating(call.getCaller().getUser().getCurrentRating());
			dto.setTotalReviews(call.getCaller().getUser().getTotalReviews());
			dto.setCallerId(call.getCaller().getId());
			response.add(dto);
		}
		return response;
	}

	public BookedCallDetailsDto convertToDtoForBookedCallListener(BookedCalls call) {
		// TODO Auto-generated method stub

		BookedCallDetailsDto dto = new BookedCallDetailsDto();
		dto.setBookingId(call.getId());
		dto.setBookingDateTime(call.getBookingDateTime());
		dto.setCallRequestStatus(call.getCallRequestStatus());
		dto.setDurationInMinutes(call.getDurationInMinutes());
		dto.setMaxDuration(call.getListener().getCallMaxDuration());
		dto.setPrice(call.getPrice());
		dto.setRatePerMinute(call.getListener().getRatePerMinute());
		dto.setListenerId(call.getListener().getId());
		dto.setCallerUserId(call.getCaller().getUser().getId());
		dto.setListenerUserId(call.getListener().getUser().getId());
		dto.setUserName(call.getListener().getUserName());
		dto.setImg(call.getListener().getUser().getProfilePicture());
		dto.setCallName(call.getListener().getUser().getCallName());
		dto.setRating(call.getListener().getUser().getCurrentRating());
		dto.setCreatedAt(call.getCreatedAt());
		dto.setRating(call.getListener().getUser().getCurrentRating());
		dto.setTotalReviews(call.getListener().getUser().getTotalReviews());
		dto.setCallerId(call.getCaller().getId());
		dto.setListenerUserId(call.getListener().getUser().getId());
		dto.setCallerUserId(call.getCaller().getUser().getId());

		return dto;
	}

	public BookedCallDetailsDto convertToDtoForBookedCallCaller(BookedCalls call) {
		// TODO Auto-generated method stub

		BookedCallDetailsDto dto = new BookedCallDetailsDto();
		dto.setBookingId(call.getId());
		dto.setBookingDateTime(call.getBookingDateTime());
		dto.setCallRequestStatus(call.getCallRequestStatus());
		dto.setDurationInMinutes(call.getDurationInMinutes());
		dto.setMaxDuration(call.getListener().getCallMaxDuration());
		dto.setPrice(call.getPrice());
		dto.setRatePerMinute(call.getListener().getRatePerMinute());
		dto.setListenerId(call.getListener().getId());
		dto.setCallerUserId(call.getCaller().getId());
		dto.setListenerUserId(call.getListener().getId());
		dto.setUserName(call.getCaller().getUser().getCallName());
		dto.setImg(call.getCaller().getUser().getProfilePicture());
		dto.setCallName(call.getCaller().getUser().getCallName());
		dto.setRating(call.getCaller().getUser().getCurrentRating());
		dto.setCreatedAt(call.getCreatedAt());
		dto.setRating(call.getCaller().getUser().getCurrentRating());
		dto.setTotalReviews(call.getCaller().getUser().getTotalReviews());
		dto.setCallerId(call.getCaller().getId());
		dto.setListenerUserId(call.getListener().getUser().getId());
		dto.setCallerUserId(call.getCaller().getUser().getId());

		return dto;
	}

	public Boolean isBlocked(User blocker, User blocked) {
		BlockedUser blockedUser = serviceRegistry.getBlockedUserService()
				.findByBlockerUserAndBlockedUserAndActiveTrueAndType(blocker, blocked, ApplicationConstants.BLOCKED);
		return blockedUser != null;
	}

	public String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null)
				emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	public Date addDaysInDate(Date date, Integer days) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate newLocalDate = localDate.plusDays(days);
		Date newDate = Date.from(newLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		return newDate;
	}

	public String getDateInMMMMFormat(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
		String formattedDate = formatter.format(date);
		return formattedDate;
	}

	public LocalDate calculateStartDateOfAge(int age) {
		return LocalDate.now().minusYears(age + 1).plusDays(1);
	}

	public LocalDate calculateEndDateOfAge(int age) {
		return LocalDate.now().minusYears(age);
	}

	public LocalDate calculateBirthDateForAge(int age) {
		return LocalDate.now().minusYears(age);
	}

	public void saveEarning(BookedCalls bookedcall) {
		CommissionRate commissionRate = serviceRegistry.getCommissionRateService().findOne(1L);
		//add here
		Optional<Coupons> coupon = couponsRepository.findById(bookedcall.getCouponId());
		if (coupon.isPresent()) {
			Coupons optionalCoupon = coupon.get();
			CouponType couponType = optionalCoupon.getCouponType();
			double couponDiscount = 0;
			double adminPercent = 0;
			double totalAmount = 0;
			if (couponType.equals(CouponType.FLAT)) {
				adminPercent = (bookedcall.getPayableAmount() * commissionRate.getRate()) / 100;
				couponDiscount = optionalCoupon.getCouponAmount();
				totalAmount = adminPercent - couponDiscount;
			} else if (couponType.equals(CouponType.PERCENTAGE)) {
				couponDiscount = optionalCoupon.getCouponAmount();
				double discount = ((bookedcall.getPayableAmount()) * couponDiscount) / 100;
				adminPercent = (bookedcall.getPayableAmount() * commissionRate.getRate()) / 100;
				totalAmount = adminPercent - discount;
			}

			EarningHistory earningHistory = new EarningHistory();
			earningHistory.setUser(bookedcall.getListener().getUser());
			earningHistory.setAmount(bookedcall.getPayableAmount() - totalAmount);
			earningHistory.setPaymentStatus(ApplicationConstants.DEPOSITED);
			earningHistory.setReason(ApplicationConstants.CALL);
			earningHistory.setActive(true);
			serviceRegistry.getEarningHistoryService().saveORupdate(earningHistory);

			// To find total calls
			List<EarningHistory> earningHistories = serviceRegistry.getEarningHistoryService()
					.findByActiveTrueAndUserOrderByCreatedAtDesc(bookedcall.getListener().getUser());
			Long size = (long) earningHistories.size();

			ListenerProfile listenerProfile = serviceRegistry.getListenerProfileService()
					.findOne(bookedcall.getListener().getId());
			listenerProfile
					.setTotalEarning(listenerProfile.getTotalEarning() + bookedcall.getPayableAmount() - totalAmount);
			listenerProfile.setTotalCompletedMinutes(
					listenerProfile.getTotalCompletedMinutes() + bookedcall.getDurationInMinutes());
			listenerProfile.setTotalAttendedCalls(size);
			listenerProfile.setTotalCommission(listenerProfile.getTotalEarning() * (commissionRate.getRate() / 100));
			serviceRegistry.getListenerProfileService().saveORupdate(listenerProfile);
		} else {

			Double adminPercent = (bookedcall.getPayableAmount() * commissionRate.getRate()) / 100;
			EarningHistory earningHistory = new EarningHistory();
			earningHistory.setUser(bookedcall.getListener().getUser());
			earningHistory.setAmount(bookedcall.getPayableAmount() - adminPercent);
			earningHistory.setPaymentStatus(ApplicationConstants.DEPOSITED);
			earningHistory.setReason(ApplicationConstants.CALL);
			earningHistory.setActive(true);
			serviceRegistry.getEarningHistoryService().saveORupdate(earningHistory);

			// To find total calls
			List<EarningHistory> earningHistories = serviceRegistry.getEarningHistoryService()
					.findByActiveTrueAndUserOrderByCreatedAtDesc(bookedcall.getListener().getUser());
			Long size = (long) earningHistories.size();

			ListenerProfile listenerProfile = serviceRegistry.getListenerProfileService()
					.findOne(bookedcall.getListener().getId());
			listenerProfile
					.setTotalEarning(listenerProfile.getTotalEarning() + bookedcall.getPayableAmount() - adminPercent);
			listenerProfile.setTotalCompletedMinutes(
					listenerProfile.getTotalCompletedMinutes() + bookedcall.getDurationInMinutes());
			listenerProfile.setTotalAttendedCalls(size);
			listenerProfile.setTotalCommission(listenerProfile.getTotalEarning() * (commissionRate.getRate() / 100));
			serviceRegistry.getListenerProfileService().saveORupdate(listenerProfile);
		}
	}

	public void sendProfileUpdatedNotification(User user)throws IOException {
		if (user.getNotificationStatus()) {
			AdministrativeNotification administrativeNotification = new AdministrativeNotification();
			Map<String, String> payload = new HashMap<>();

			administrativeNotification.setTitle(ApplicationConstants.PROFILE_UPDATED);
			administrativeNotification.setContent(ApplicationConstants.PROFILE_UPDATED_SUCCESSFULLY);
			administrativeNotification.setUsers(Collections.singletonList(user));
			administrativeNotification.setTags(ApplicationConstants.PROFILE_UPDATED);
			administrativeNotification.setActive(true);
			serviceRegistry.getAdministrativeNotificationService().saveORupdate(administrativeNotification);

			payload.put(ApplicationConstants.TITLE, ApplicationConstants.PROFILE_UPDATED);
			payload.put(ApplicationConstants.BODY, ApplicationConstants.PROFILE_UPDATED_SUCCESSFULLY);
			payload.put(ApplicationConstants.TAG, ApplicationConstants.PROFILE_UPDATED);
			String receiverDeviceToken = user.getDeviceToken();
			if (receiverDeviceToken != null) {
				// Send push notification using FCM
				serviceRegistry.getFcmService().sendPushNotification(receiverDeviceToken, payload);
			}
		}

	}

	public Boolean isAccountSwitched(User user) {
		CallerProfile callerProfile = serviceRegistry.getCallerProfileService().findByUserAndActiveTrue(user);
		ListenerProfile listenerProfile = serviceRegistry.getListenerProfileService().findByUserAndActiveTrue(user);
		if (callerProfile != null && listenerProfile != null) {
			return true;
		} else {
			return false;
		}
	}

	public List<ListenerProfile> filterBlockedListeners(User blockerUser, List<ListenerProfile> listeners) {
		// Get users blocked by the caller
		List<User> blockedUsersByCaller = serviceRegistry.getBlockedUserService()
				.findByBlockerUserAndActiveTrueAndType(blockerUser, ApplicationConstants.BLOCKED).stream()
				.map(BlockedUser::getBlockedUser).collect(Collectors.toList());

		// Get users who have blocked the caller
		List<User> blockedUsersByListeners = serviceRegistry.getBlockedUserService()
				.findByBlockedUserAndActiveTrueAndType(blockerUser, ApplicationConstants.BLOCKED).stream()
				.map(BlockedUser::getBlockerUser).collect(Collectors.toList());

		return listeners.stream().filter(listener -> !blockedUsersByCaller.contains(listener.getUser())
				&& !blockedUsersByListeners.contains(listener.getUser())).collect(Collectors.toList());
	}

	public List<FavouriteListener> filterBlockedFavouriteListeners(User blockerUser,
			List<FavouriteListener> listeners) {
		// Get users blocked by the caller
		List<User> blockedUsersByCaller = serviceRegistry.getBlockedUserService()
				.findByBlockerUserAndActiveTrueAndType(blockerUser, ApplicationConstants.BLOCKED).stream()
				.map(BlockedUser::getBlockedUser).collect(Collectors.toList());

		// Get users who have blocked the caller
		List<User> blockedUsersByListeners = serviceRegistry.getBlockedUserService()
				.findByBlockedUserAndActiveTrueAndType(blockerUser, ApplicationConstants.BLOCKED).stream()
				.map(BlockedUser::getBlockerUser).collect(Collectors.toList());

		return listeners.stream().filter(listener -> !blockedUsersByCaller.contains(listener.getListener())
				&& !blockedUsersByListeners.contains(listener.getListener())).collect(Collectors.toList());
	}

//	public List<ListenerProfile> filterBlockedListeners(User blockerUser, List<ListenerProfile> listeners) {
//		List<User> blockedUsers = serviceRegistry.getBlockedUserService().findByBlockerUserAndActiveTrue(blockerUser)
//				.stream().map(BlockedUser::getBlockedUser).collect(Collectors.toList());
	//
//		return listeners.stream().filter(listener -> !blockedUsers.contains(listener.getUser()))
//				.collect(Collectors.toList());
//	}
	//
//	public List<FavouriteListener> filterBlockedFavouriteListeners(User blockerUser, List<FavouriteListener> listeners) {
//		List<User> blockedUsers = serviceRegistry.getBlockedUserService().findByBlockerUserAndActiveTrue(blockerUser)
//				.stream().map(BlockedUser::getBlockedUser).collect(Collectors.toList());
	//
//		return listeners.stream().filter(listener -> !blockedUsers.contains(listener.getListener()))
//				.collect(Collectors.toList());
//	}

//	public Page<ListenerProfile> filterBlockedListeners(User blockerUser, Page<ListenerProfile> listeners,
//			Pageable pageable) {
//		List<User> blockedUsers = serviceRegistry.getBlockedUserService().findByBlockerUserAndActiveTrue(blockerUser)
//				.stream().map(BlockedUser::getBlockedUser).collect(Collectors.toList());
//
//		List<ListenerProfile> filteredListeners = listeners.stream()
//				.filter(listener -> !blockedUsers.contains(listener.getUser())).collect(Collectors.toList());
//
//		// Create a sublist based on the pageable parameters
//		int start = (int) pageable.getOffset();
//		int end = Math.min((start + pageable.getPageSize()), filteredListeners.size());
//		List<ListenerProfile> paginatedFilteredListeners = filteredListeners.subList(start, end);
//
//		// Create and return a new Page object
//		return new PageImpl<>(paginatedFilteredListeners, pageable, filteredListeners.size());
//	}

//	  public List<ListenerProfile> filterBlockedListeners(User blockerUser, Page<ListenerProfile> listeners) {
//	        List<User> blockedUsers = serviceRegistry.getBlockedUserService().findByBlockerUserAndActiveTrue(blockerUser)
//	                                                       .stream()
//	                                                       .map(BlockedUser::getBlockedUser)
//	                                                       .collect(Collectors.toList());
//
//	        return listeners.stream()
//	                        .filter(listener -> !blockedUsers.contains(listener.getUser()))
//	                        .collect(Collectors.toList());
//	    }

//	public Boolean checkAddAuthority(Administration administration, Long id) {
//		if (administration.getRole().equals(ApplicationConstants.ADMIN))
//			return true;
//		return serviceRegistry.getAdminModulePermissionService()
//				.existsByAdministrationAndAdminModuleIdAndCanAddTrue(administration, id);
//	}
//
//	public Boolean checkUpdateAuthority(Administration administration, Long id) {
//		if (administration.getRole().equals(ApplicationConstants.ADMIN))
//			return true;
//		return serviceRegistry.getAdminModulePermissionService()
//				.existsByAdministrationAndAdminModuleIdAndCanUpdateTrue(administration, id);
//	}
//
//	public Boolean checkViewAuthority(Administration administration, Long id) {
//		if (administration.getRole().equals(ApplicationConstants.ADMIN))
//			return true;
//		return serviceRegistry.getAdminModulePermissionService()
//				.existsByAdministrationAndAdminModuleIdAndCanViewTrue(administration, id);
//	}
//
//	public Boolean checkDeleteAuthority(Administration administration, Long id) {
//		if (administration.getRole().equals(ApplicationConstants.ADMIN))
//			return true;
//		return serviceRegistry.getAdminModulePermissionService()
//				.existsByAdministrationAndAdminModuleIdAndCanDeleteTrue(administration, id);
//	}

}