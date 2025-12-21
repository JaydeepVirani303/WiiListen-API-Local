package com.wiilisten.controller.api.listener;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.EarningHistory;
import com.wiilisten.entity.ListenerAnalytic;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.request.IdStatusRequestDto;
import com.wiilisten.request.PaginationAndSortingDetails;
import com.wiilisten.request.TypeRequestDto;
import com.wiilisten.response.AnalyticsResponseDto;
import com.wiilisten.response.BookedCallDetailsDto;
import com.wiilisten.response.CallerProfileResponseDto;
import com.wiilisten.response.DateRangeResponseDto;
import com.wiilisten.response.EarningHistoryResponseDto;
import com.wiilisten.response.EarningResponseDto;
import com.wiilisten.response.GraphResponseDto;
import com.wiilisten.response.ListenerDetailsResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.LISTENER
		+ ApplicationURIConstants.EARNING_HISTORY)
public class ApiV1ListenerEarningHistory extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1ListenerEarningHistory.class);

	@PostMapping(ApplicationURIConstants.DETAILS)
	public ResponseEntity<Object> getEarningDetails() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByUserAndActiveTrue(getLoggedInUser());
			ListenerDetailsResponseDto response = new ListenerDetailsResponseDto();
			BeanUtils.copyProperties(listener, response);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getHistoryList(@RequestBody IdStatusRequestDto typeRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User user = getUser(typeRequestDto.getId());
			List<EarningHistory> earningHistories = getServiceRegistry().getEarningHistoryService()
					.findByActiveTrueAndUserOrderByCreatedAtDesc(user);
			if (earningHistories.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}
			Integer size = earningHistories.size();
			Date firstDate = new Date();
			Date lastDate = earningHistories.get(size - 1).getCreatedAt();
			List<Object> allValues = new ArrayList<>();
			if (typeRequestDto.getType().equals(ApplicationConstants.WEEKLY)) {
				allValues = getWeeklyData(earningHistories, user, ApplicationConstants.WEEKLY, null);
			} else if (typeRequestDto.getType().equals(ApplicationConstants.MONTHLY)) {
				List<DateRangeResponseDto> splitDateRangeIntoMonthRanges = splitDateRangeIntoMonthRanges(lastDate,
						firstDate, ApplicationConstants.MONTHLY);
				LOGGER.info("size" + splitDateRangeIntoMonthRanges.size());
				allValues = getMonthlyData(splitDateRangeIntoMonthRanges, user, ApplicationConstants.MONTHLY, null);
			} else if (typeRequestDto.getType().equals(ApplicationConstants.YEARLY)) {
				List<DateRangeResponseDto> splitDateRangeIntoMonthRanges = splitDateRangeIntoMonthRanges(lastDate,
						firstDate, ApplicationConstants.YEARLY);
				allValues = getMonthlyData(splitDateRangeIntoMonthRanges, user, ApplicationConstants.YEARLY, null);
			} else if (typeRequestDto.getType().equals(ApplicationConstants.DAILY)) {
				List<DateRangeResponseDto> splitDateRangeIntoMonthRanges = splitDateRangeIntoMonthRanges(lastDate,
						firstDate, ApplicationConstants.DAILY);
				allValues = getMonthlyData(splitDateRangeIntoMonthRanges, user, ApplicationConstants.DAILY, null);

			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(allValues));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LISTENER + ApplicationURIConstants.ANALYTICS)
	public ResponseEntity<Object> getListenerAnalytics() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {// pp,callname,count
			User user = getLoggedInUser();
			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByUserAndActiveTrue(user);
			List<ListenerAnalytic> listenerAnalytics = getServiceRegistry().getListenerAnalyticService()
					.findByListenerAndActiveTrue(listenerProfile);

			if (listenerAnalytics.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}
			AnalyticsResponseDto response = new AnalyticsResponseDto();
			List<CallerProfileResponseDto> profileResponseDtos = new ArrayList<>();
			listenerAnalytics.forEach(analytics -> {
				CallerProfileResponseDto dto = new CallerProfileResponseDto();
				dto.setCallName(analytics.getCaller().getUser().getCallName());
				dto.setProfilePicture(analytics.getCaller().getUser().getProfilePicture());
				dto.setCallerId(analytics.getCaller().getId());
				dto.setUserId(analytics.getCaller().getUser().getId());
				profileResponseDtos.add(dto);
			});
			response.setCallerDetails(profileResponseDtos);
			response.setCount((long) listenerAnalytics.size());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LISTENER + ApplicationURIConstants.BOOKEDCALLS
			+ ApplicationURIConstants.ANALYTICS)
	public ResponseEntity<Object> getListenerBookingAnalytics(@RequestBody PaginationAndSortingDetails requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {// pp,callname,count
			User user = getLoggedInUser();
			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByUserAndActiveTrue(user);

			Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);
			Page<BookedCalls> listenerAnalytics = getServiceRegistry().getBookedCallsService()
					.findByListenerProfileAndActiveTrueAndSponsoredTrue(listenerProfile, pageable);

			if (listenerAnalytics.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}
			AnalyticsResponseDto response = new AnalyticsResponseDto();
			List<BookedCallDetailsDto> bookedCallDetails = new ArrayList<>();
			listenerAnalytics.forEach(analytics -> {
				BookedCallDetailsDto dto = new BookedCallDetailsDto();
				dto.setBookingId(analytics.getId());
				dto.setBookingDateTime(analytics.getBookingDateTime());
				dto.setCallerUserId(analytics.getCaller().getUser().getId());
				dto.setType(analytics.getType());
				dto.setListenerUserId(analytics.getListener().getUser().getId());
				dto.setCallRequestStatus(analytics.getCallRequestStatus());
				dto.setDurationInMinutes(analytics.getDurationInMinutes());
				dto.setMaxDuration(analytics.getListener().getCallMaxDuration());
				dto.setPrice(analytics.getPayableAmount());
				dto.setRatePerMinute(analytics.getListener().getRatePerMinute());
				dto.setListenerId(analytics.getListener().getId());
				dto.setUserName(analytics.getListener().getUserName());
				dto.setImg(analytics.getListener().getUser().getProfilePicture());
				dto.setCallName(analytics.getListener().getUser().getCallName());
				dto.setRating(analytics.getListener().getUser().getCurrentRating());
				dto.setCreatedAt(analytics.getCreatedAt());
				dto.setRating(analytics.getListener().getUser().getCurrentRating());
				dto.setTotalReviews(analytics.getListener().getUser().getTotalReviews());
				bookedCallDetails.add(dto);

			});
			response.setBookedCallDetails(bookedCallDetails);
			response.setCount((long) listenerAnalytics.getTotalElements());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (

		Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.GRAPH)
	public ResponseEntity<Object> getGraph(@RequestBody TypeRequestDto typeRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User user = getLoggedInUser();
			String timeZone = typeRequestDto.getRequestedTimeZone();
			List<EarningHistory> earningHistories = getServiceRegistry().getEarningHistoryService()
					.findByActiveTrueAndUserOrderByCreatedAtDesc(user);
			if (earningHistories.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}
			List<Object> allValues = new ArrayList<>();
			GraphResponseDto response = new GraphResponseDto();
			if (typeRequestDto.getType().equals(ApplicationConstants.WEEKLY)) { // done
				DateRangeResponseDto rangeResponseDto = getDateRangeForGraph(ApplicationConstants.WEEKLY);
				List<DateRangeResponseDto> splitDateRangeIntoMonthRanges = getDateRanges(ApplicationConstants.WEEKLY,
						convertToLocalDateViaInstant(rangeResponseDto.getStart()),
						convertToLocalDateViaInstant(rangeResponseDto.getEnd()));
				LOGGER.info(
						"weekly start and end date is {}" + convertToLocalDateViaInstant(rangeResponseDto.getStart())
								+ " " + convertToLocalDateViaInstant(rangeResponseDto.getEnd()));
				allValues = getMonthlyData(splitDateRangeIntoMonthRanges, user, ApplicationConstants.DAILY, timeZone);
				Double totalIncomAverage = getTotalIncomAverage(ApplicationConstants.WEEKLY, allValues);
				response.setAllValues(allValues);
				response.setAverageIncome(totalIncomAverage);
			} else if (typeRequestDto.getType().equals(ApplicationConstants.MONTHLY)) { // done
				DateRangeResponseDto rangeResponseDto = getDateRangeForGraph(ApplicationConstants.MONTHLY);
				List<DateRangeResponseDto> splitDateRangeIntoMonthRanges = getDateRanges(ApplicationConstants.MONTHLY,
						convertToLocalDateViaInstant(rangeResponseDto.getStart()),
						convertToLocalDateViaInstant(rangeResponseDto.getEnd()));
				allValues = getMonthlyData(splitDateRangeIntoMonthRanges, user, ApplicationConstants.MONTHLY, timeZone);
				Double totalIncomAverage = getTotalIncomAverage(ApplicationConstants.MONTHLY, allValues);
				response.setAllValues(allValues);
				response.setAverageIncome(totalIncomAverage);
			} else if (typeRequestDto.getType().equals(ApplicationConstants.YEARLY)) {// done
				DateRangeResponseDto rangeResponseDto = getDateRangeForGraph(ApplicationConstants.YEARLY);
				List<DateRangeResponseDto> splitDateRangeIntoMonthRanges = getDateRanges(ApplicationConstants.YEARLY,
						convertToLocalDateViaInstant(rangeResponseDto.getStart()),
						convertToLocalDateViaInstant(rangeResponseDto.getEnd()));
				allValues = getMonthlyData(splitDateRangeIntoMonthRanges, user, ApplicationConstants.YEARLY, timeZone);
				Double totalIncomAverage = getTotalIncomAverage(ApplicationConstants.YEARLY, allValues);
				response.setAllValues(allValues);
				response.setAverageIncome(totalIncomAverage);
			} else if (typeRequestDto.getType().equals(ApplicationConstants.DAILY)) {
				List<DateRangeResponseDto> splitDateRangeIntoMonthRanges = getTwoHourIntervals();
				allValues = getMonthlyData(splitDateRangeIntoMonthRanges, user, ApplicationConstants.DAILY, timeZone);
				Double totalIncomAverage = getTotalIncomAverage(ApplicationConstants.YEARLY, allValues);
				response.setAllValues(allValues);
				response.setAverageIncome(totalIncomAverage);
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	private String formatDate(Date date, String timeZone) {
		if (date == null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (timeZone != null && !timeZone.isEmpty()) {
			sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
		} else {
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		}
		return sdf.format(date);
	}

	public static List<DateRangeResponseDto> getTwoHourIntervals() {
		LocalDate date = LocalDate.now();
		List<DateRangeResponseDto> intervals = new ArrayList<>();

		LocalDateTime startOfDay = date.atStartOfDay();
		for (int hour = 0; hour < 24; hour += 2) {
			LocalDateTime start = startOfDay.plusHours(hour);
			LocalDateTime end = start.plusHours(2);

			Date startDate = convertToDate(start);
			Date endDate = convertToDate(end);
			LOGGER.info("start date {}" + startDate + "end date {}" + endDate);
			intervals.add(new DateRangeResponseDto(startDate, endDate));
		}

		return intervals;
	}

	public static List<DateRangeResponseDto> getDateRanges(String type, LocalDate startDate, LocalDate endDate) {
		List<DateRangeResponseDto> ranges = new ArrayList<>();

		switch (type.toUpperCase()) {
			case "DAILY":
				LocalDateTime current = startDate.atStartOfDay();
				LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
				while (current.isBefore(endDateTime)) {
					LocalDateTime next = current.plusHours(2);
					ranges.add(new DateRangeResponseDto(convertToDate(current), convertToDate(next)));
					current = next;
				}
				break;

			case "WEEKLY":
				LocalDate weekStart = startDate;
				while (!weekStart.isAfter(endDate)) {
					LOGGER.info("start date" + convertToDate(weekStart.atStartOfDay()) + " end dau"
							+ convertToDate(weekStart.atTime(LocalTime.MAX)));
					ranges.add(new DateRangeResponseDto(convertToDate(weekStart.atStartOfDay()),
							convertToDate(weekStart.atTime(LocalTime.MAX))));
					weekStart = weekStart.plusDays(1);
				}
				break;

			case "MONTHLY":
				LocalDate currentYearStart = startDate.with(TemporalAdjusters.firstDayOfYear());
				LocalDate currentYearEnd = startDate.with(TemporalAdjusters.lastDayOfYear());
				LocalDate currentMonthStart = currentYearStart;
				while (!currentMonthStart.isAfter(currentYearEnd)) {
					LocalDate currentMonthEnd = currentMonthStart.with(TemporalAdjusters.lastDayOfMonth());
					ranges.add(new DateRangeResponseDto(convertToDate(currentMonthStart.atStartOfDay()),
							convertToDate(currentMonthEnd.atTime(LocalTime.MAX))));
					currentMonthStart = currentMonthStart.plusMonths(1).with(TemporalAdjusters.firstDayOfMonth());
				}
				break;

			case "YEARLY":
				Year currentYear = Year.from(startDate);
				Year endYear = Year.from(endDate);
				while (!currentYear.isAfter(endYear)) {
					LocalDate yearStart = currentYear.atDay(1);
					LocalDate yearEnd = currentYear.atMonth(12).atEndOfMonth();
					if (currentYear.equals(Year.from(startDate)) && yearStart.isBefore(startDate)) {
						yearStart = startDate;
					}
					if (currentYear.equals(Year.from(endDate)) && yearEnd.isAfter(endDate)) {
						yearEnd = endDate;
					}
					ranges.add(new DateRangeResponseDto(convertToDate(yearStart.atStartOfDay()),
							convertToDate(yearEnd.atTime(LocalTime.MAX))));
					currentYear = currentYear.plusYears(1);
				}
				break;

			default:
				throw new IllegalArgumentException("Invalid type: " + type);
		}

		return ranges;
	}

	private static Date convertToDate(LocalDateTime dateTime) {
		return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	private static Date convertToDate(LocalDate date) {
		return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public DateRangeResponseDto getDateRangeForGraph(String type) {
		LocalDate date = LocalDate.now();
		DateRangeResponseDto responseDto = new DateRangeResponseDto();
		LocalDate startDate = null;
		LocalDate endDate = null;
		if (type.equals(ApplicationConstants.WEEKLY)) {
			startDate = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
			endDate = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
		} else if (type.equals(ApplicationConstants.MONTHLY)) {
			startDate = date.with(TemporalAdjusters.firstDayOfYear());
			endDate = date.with(TemporalAdjusters.lastDayOfYear());
		} else if (type.equals(ApplicationConstants.YEARLY)) {
			startDate = date.minusYears(9).with(TemporalAdjusters.firstDayOfYear());
			endDate = date.with(TemporalAdjusters.lastDayOfYear());
		}
		responseDto.setStart(convertToDate(startDate));
		responseDto.setEnd(convertToDate(endDate));
		return responseDto;
	}

	public static List<DateRangeResponseDto> splitDateRangeIntoMonthRanges(Date startDate, Date endDate, String type) {
		List<DateRangeResponseDto> dateRanges = new ArrayList<>();

		if (startDate.after(endDate)) {
			throw new IllegalArgumentException("Start date must be before or equal to end date.");
		}

		LocalDate startLocalDate = convertToLocalDateViaInstant(startDate);
		LocalDate endLocalDate = convertToLocalDateViaInstant(endDate);

		YearMonth startMonth = YearMonth.from(startLocalDate);
		YearMonth endMonth = YearMonth.from(endLocalDate);

		Year startYear = Year.from(startLocalDate);
		Year endYear = Year.from(endLocalDate);

		if (type.equals(ApplicationConstants.MONTHLY)) {

			YearMonth currentMonth = startMonth;
			while (!currentMonth.isAfter(endMonth)) {
				LocalDate monthStart = currentMonth.atDay(1);
				LocalDate monthEnd = currentMonth.atEndOfMonth();

				// Adjust the start and end dates to fit within the given range
				if (currentMonth.equals(startMonth) && monthStart.isBefore(startLocalDate)) {
					monthStart = startLocalDate;
				}
				if (currentMonth.equals(endMonth) && monthEnd.isAfter(endLocalDate)) {
					monthEnd = endLocalDate;
				}

				dateRanges.add(new DateRangeResponseDto(convertToDateViaInstant(monthStart),
						convertToDateViaInstant(monthEnd)));
				currentMonth = currentMonth.plusMonths(1);
			}
		} else if (type.equals(ApplicationConstants.YEARLY)) {
			Year currentYear = startYear;
			while (!currentYear.isAfter(endYear)) {
				LocalDate yearStart = currentYear.atDay(1);
				LocalDate yearEnd = currentYear.atMonth(12).atEndOfMonth();

				// Adjust the start and end dates to fit within the given range
				if (currentYear.equals(startYear) && yearStart.isBefore(startLocalDate)) {
					yearStart = startLocalDate;
				}
				if (currentYear.equals(endYear) && yearEnd.isAfter(endLocalDate)) {
					yearEnd = endLocalDate;
				}

				dateRanges.add(
						new DateRangeResponseDto(convertToDateViaInstant(yearStart), convertToDateViaInstant(yearEnd)));
				currentYear = currentYear.plusYears(1);
			}

		} else if (type.equals(ApplicationConstants.DAILY)) {
			LocalDate currentDate = startLocalDate;
			while (!currentDate.isAfter(endLocalDate)) {
				ZonedDateTime dayStart = currentDate.atStartOfDay(ZoneId.systemDefault());
				ZonedDateTime dayEnd = currentDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault());

				dateRanges.add(
						new DateRangeResponseDto(convertToDateViaInstant(dayStart), convertToDateViaInstant(dayEnd)));
				currentDate = currentDate.plusDays(1);
			}
		}

		return dateRanges;
	}

	public List<Object> getWeeklyData(List<EarningHistory> earningHistories, User user, String type, String timeZone) {
		Integer size = earningHistories.size();
		Date firstDate = new Date();
		Date lastDate = earningHistories.get(size - 1).getCreatedAt();
		LocalDate startDate = firstDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		startDate = startDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
		LocalDate endDate = lastDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		endDate = endDate.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
		// Date endWithOutSunday =
		// Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Map<Integer, EarningHistoryResponseDto> map = new HashMap<>();

		Map<LocalDate, LocalDate> allWeek = getAllWeek(endDate, startDate);
		Integer count = 0;
		Integer sizeOfMap = allWeek.size();
		Integer currentIndex = 0;
		LOGGER.info("size is {}" + allWeek.size());
		for (Map.Entry<LocalDate, LocalDate> entry : allWeek.entrySet()) {
			LocalDate week = entry.getKey();
			LocalDate end = entry.getValue();
			Date startOfWeek = Date.from(week.atStartOfDay(ZoneId.systemDefault()).toInstant());
			Date endOfWeek = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());
			EarningHistoryResponseDto response = new EarningHistoryResponseDto();
			List<EarningHistory> earnings = new ArrayList<>();
			earnings = getServiceRegistry().getEarningHistoryService()
					.findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(startOfWeek,
							incrementDate(endOfWeek), user);
			response.setStartDate(formatDate(incrementDate(startOfWeek), timeZone));
			response.setEndDate(formatDate(incrementDate(endOfWeek), timeZone));
			LOGGER.info("start and end date is {}" + incrementDate(startOfWeek) + " " + incrementDate(endOfWeek));
			if (currentIndex == 0) {
				LOGGER.info("inside if" + lastDate);
				earnings = getServiceRegistry().getEarningHistoryService()
						.findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(startOfWeek,
								incrementDate(lastDate), user);
				response.setStartDate(formatDate(incrementDate(startOfWeek), timeZone));
				response.setEndDate(formatDate(lastDate, timeZone));
			}
			if (currentIndex == sizeOfMap - 1) {
				LOGGER.info("inside if" + lastDate);
				earnings = getServiceRegistry().getEarningHistoryService()
						.findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(startOfWeek, firstDate, user);
				response.setStartDate(formatDate(incrementDate(startOfWeek), timeZone));
				response.setEndDate(formatDate(firstDate, timeZone));
			}
			List<EarningResponseDto> earningResponseDtos = new ArrayList<>();
			if (!earnings.isEmpty()) {
				earnings.forEach(earning -> {
					EarningResponseDto dto = new EarningResponseDto();
					BeanUtils.copyProperties(earning, dto, "createdAt", "updatedAt");
					dto.setCreatedAt(formatDate(earning.getCreatedAt(), timeZone));
					dto.setUpdatedAt(formatDate(earning.getUpdatedAt(), timeZone));
					earningResponseDtos.add(dto);
				});
			}
			response.setEarnings(earningResponseDtos);
			response.setTotalIncome(getTotalIncome(earnings));
			Double totalIncome = getTotalIncome(earnings);

			if (!earningResponseDtos.isEmpty()) {
				response.setAverageIncome(getAverageIncome(totalIncome, type, incrementDate(startOfWeek)));
				if (currentIndex == sizeOfMap - 1) {
					Long daysDifference = ChronoUnit.DAYS.between(week, end);
					response.setAverageIncome(totalIncome / daysDifference);
				}
			} else {
				response.setAverageIncome(0D);
			}

			LOGGER.info("response data {}" + response.getStartDate() + " end date " + response.getEndDate());
			map.put(count, response);
			count++;
			currentIndex++;
		}

		List<Object> allValues = new ArrayList<>();
		for (int i = map.size(); i > 0; i--) {
			LOGGER.info("i is {}" + i);
			EarningHistoryResponseDto valueCollection = map.get(i - 1); // Keys are 1-based indexed
			allValues.add(valueCollection);
		}

		return allValues;
	}

	public List<Object> getMonthlyData(List<DateRangeResponseDto> splitDateRangeIntoMonthRanges, User user,
									   String type, String timeZone) {
		Integer sizeOfMap = splitDateRangeIntoMonthRanges.size();
		AtomicInteger currentIndex = new AtomicInteger(0);
		List<Object> allValues = new ArrayList<>();
		if (!splitDateRangeIntoMonthRanges.isEmpty()) {
			splitDateRangeIntoMonthRanges.forEach(range -> {
				LOGGER.info("start date and end date {}" + range.getStart() + " " + range.getEnd());
				EarningHistoryResponseDto response = new EarningHistoryResponseDto();
				response.setStartDate(formatDate(incrementDate(range.getStart()), timeZone));
				response.setEndDate(formatDate(incrementDate(range.getEnd()), timeZone));
				List<EarningHistory> earnings = new ArrayList<>();
				earnings = getServiceRegistry().getEarningHistoryService()
						.findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(range.getStart(),
								incrementDate(range.getEnd()), user);
				if (type.equals(ApplicationConstants.DAILY)) {
					earnings = getServiceRegistry().getEarningHistoryService()
							.findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(range.getStart(),
									range.getEnd(), user);
					response.setStartDate(formatDate(range.getStart(), timeZone));
					response.setEndDate(formatDate(range.getEnd(), timeZone));
				}
				List<EarningResponseDto> earningResponseDtos = new ArrayList<>();
				if (!earnings.isEmpty()) {
					earnings.forEach(earning -> {
						EarningResponseDto dto = new EarningResponseDto();
						BeanUtils.copyProperties(earning, dto, "createdAt", "updatedAt");
						dto.setCreatedAt(formatDate(earning.getCreatedAt(), timeZone));
						dto.setUpdatedAt(formatDate(earning.getUpdatedAt(), timeZone));
						earningResponseDtos.add(dto);
					});
				}
				response.setEarnings(earningResponseDtos);
				response.setTotalIncome(getTotalIncome(earnings));
				Double totalIncome = getTotalIncome(earnings);

				if (!earningResponseDtos.isEmpty()) {
					response.setAverageIncome(getAverageIncome(totalIncome, type, range.getStart()));
					if (currentIndex.doubleValue() == sizeOfMap - 1 && type.equals(ApplicationConstants.MONTHLY)) {
						LocalDate startDate = range.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalDate endDate = range.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						Long daysDifference = ChronoUnit.DAYS.between(startDate, endDate) + 1;
						response.setAverageIncome(totalIncome / daysDifference);
					}
					if (currentIndex.doubleValue() == sizeOfMap - 1 && type.equals(ApplicationConstants.YEARLY)) {
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(range.getEnd());

						Integer month = calendar.get(Calendar.MONTH) + 1; // Adding 1 because Calendar.MONTH is
																			// zero-based
						System.out.println("Current month: " + month);
						response.setAverageIncome(totalIncome / month);
					}
				} else {
					response.setAverageIncome(0D);
				}
				LOGGER.info("inside response {}" + response.getStartDate() + " " + response.getEndDate());
				allValues.add(response);
				currentIndex.incrementAndGet();
			});

			Collections.reverse(allValues);
		}
		return allValues;
	}

	public List<Object> getWeeklyDataForGraph(List<DateRangeResponseDto> splitDateRangeIntoMonthRanges, User user,
											  String type, String timeZone) {
		AtomicInteger currentIndex = new AtomicInteger(0);
		List<Object> allValues = new ArrayList<>();
		if (!splitDateRangeIntoMonthRanges.isEmpty()) {
			splitDateRangeIntoMonthRanges.forEach(range -> {
				LOGGER.info("start date and end date {}" + range.getStart() + " " + range.getEnd());
				EarningHistoryResponseDto response = new EarningHistoryResponseDto();
				response.setStartDate(formatDate(incrementDate(range.getStart()), timeZone));
				response.setEndDate(formatDate(incrementDate(range.getEnd()), timeZone));
				List<EarningHistory> earnings = new ArrayList<>();
				earnings = getServiceRegistry().getEarningHistoryService()
						.findByCreatedAtBetweenAndActiveTrueAndUserOrderByCreatedAtDesc(range.getStart(),
								incrementDate(range.getEnd()), user);

				List<EarningResponseDto> earningResponseDtos = new ArrayList<>();
				if (!earnings.isEmpty()) {
					earnings.forEach(earning -> {
						EarningResponseDto dto = new EarningResponseDto();
						BeanUtils.copyProperties(earning, dto, "createdAt", "updatedAt");
						dto.setCreatedAt(formatDate(earning.getCreatedAt(), timeZone));
						dto.setUpdatedAt(formatDate(earning.getUpdatedAt(), timeZone));
						earningResponseDtos.add(dto);
					});
				}
				response.setEarnings(earningResponseDtos);
				response.setTotalIncome(getTotalIncome(earnings));

				LOGGER.info("inside response {}" + response.getStartDate() + " " + response.getEndDate());
				allValues.add(response);
				currentIndex.incrementAndGet();
			});

			Collections.reverse(allValues);
		}
		return allValues;
	}

	public Map<LocalDate, LocalDate> getAllWeek(LocalDate startDate, LocalDate endDate) {
		long weekNumber = ChronoUnit.WEEKS.between(startDate, endDate);
		Map<LocalDate, LocalDate> weeks = new LinkedHashMap<>();
		for (int i = 0; i < weekNumber; i++) {
			LocalDate endOfWeek = startDate.plusDays(6);
			weeks.put(startDate, endOfWeek);
			startDate = endOfWeek.plusDays(1);
		}
		return weeks;
	}

	private static LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
		return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	private static Date convertToDateViaInstant(LocalDate dateToConvert) {
		return Date.from(dateToConvert.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	private static Date convertToDateViaInstant(ZonedDateTime dateTimeToConvert) {
		return Date.from(dateTimeToConvert.toInstant());
	}

	public Date incrementDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}

	public Date decrementDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return calendar.getTime();
	}

	public Double getTotalIncome(List<EarningHistory> earningHistories) {
		Double totalIncome = 0.0;
		for (EarningHistory earningHistory : earningHistories) {
			if (earningHistory.getReason().equals("ADVERTISEMENT")) {
				continue;
			}
			totalIncome += earningHistory.getAmount();
		}
		return totalIncome;
	}

	// Not working
	public Double getAverageIncome(Double totalIncome, String type, Date date) {
		if (type.equals(ApplicationConstants.YEARLY)) {
			return totalIncome / 12;
		} else if (type.equals(ApplicationConstants.WEEKLY)) {
			return totalIncome / 7;
		} else if (type.equals(ApplicationConstants.MONTHLY)) {
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			YearMonth yearMonth = YearMonth.from(localDate);
			int daysInMonth = yearMonth.lengthOfMonth();
			return totalIncome / daysInMonth;
		} else if (type.equals(ApplicationConstants.DAILY)) {
			return totalIncome / 24;
		}
		return null;
	}

	public User getUser(Long id) {
		if (getLoggedInUser() == null) {
			return getServiceRegistry().getUserService().findByIdAndActiveTrueAndIsSuspendedFalse(id);
		} else {
			return getLoggedInUser();
		}
	}

	public Double getTotalIncomAverage(String type, List<Object> allValues) {
		Double totalIncomeSum = 0.0;
		for (Object obj : allValues) {
			if (obj instanceof EarningHistoryResponseDto) {
				EarningHistoryResponseDto earningResponse = (EarningHistoryResponseDto) obj;
				totalIncomeSum += earningResponse.getTotalIncome();
			}
		}
		LOGGER.info("totalIncomeSum is {}" + totalIncomeSum);
		// return totalIncomeSum / getDateTypeValue(type);
		return totalIncomeSum;

	}

	public static int getDateTypeValue(String type) {
		LocalDate date = LocalDate.now();
		switch (type.toUpperCase()) {
			case "WEEKLY":
				return getDayOfWeekValue(date);
			case "MONTHLY":
				return date.getDayOfMonth();
			case "YEARLY":
				return date.getMonthValue();
			default:
				throw new IllegalArgumentException("Invalid type: " + type);
		}
	}

	private static int getDayOfWeekValue(LocalDate date) {
		DayOfWeek dayOfWeek = date.getDayOfWeek();
		switch (dayOfWeek) {
			case SUNDAY:
				return 1;
			case MONDAY:
				return 2;
			case TUESDAY:
				return 3;
			case WEDNESDAY:
				return 4;
			case THURSDAY:
				return 5;
			case FRIDAY:
				return 6;
			case SATURDAY:
				return 7;
			default:
				throw new IllegalStateException("Unexpected value: " + dayOfWeek);
		}
	}

	// List<EarningResponseDto> response = new ArrayList<>();
	// earningHistories.forEach(earning -> {
	// EarningResponseDto dto = new EarningResponseDto();
	// BeanUtils.copyProperties(earning, dto);
	// response.add(dto);
	// });
	// Date todaysDate = new Date();
	// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	// // String formattedTodaysDate = formatter.format(todaysDate);
	//
	// Date monday = getNearestMonday(todaysDate);

	// map.values(); convertDataIntoResponse(monday, todaysDate);
	// public Map<Integer, EarningHistoryResponseDto> convertDataIntoResponse(Date
	// startDate, Date endDate) {
	// List<EarningHistory> earningHistories =
	// getServiceRegistry().getEarningHistoryService()
	// .findByCreatedAtBetween(startDate, endDate);
	// LOGGER.info("earningHistories is {}"+earningHistories);
	// LOGGER.info("start date and end date is {}" + startDate + endDate);
	// Map<Integer, EarningHistoryResponseDto> map = new HashMap<>();
	// // List<EarningHistoryResponseDto> responseDtos=new ArrayList<>();
	// EarningHistoryResponseDto response = new EarningHistoryResponseDto();
	// response.setStartDate(startDate);
	// response.setEndDate(endDate);
	// if (earningHistories.isEmpty()) {
	// return map;
	// }
	// List<EarningResponseDto> earningResponseDtos = new ArrayList<>();
	// earningHistories.forEach(earning -> {
	// EarningResponseDto dto = new EarningResponseDto();
	// BeanUtils.copyProperties(earning, dto);
	// earningResponseDtos.add(dto);
	// });
	// Integer a = 1;
	// response.setEarnings(earningResponseDtos);
	// map.put(a, response);
	// a++;
	// LOGGER.info("map is in method {}"+map);
	// Long millisecondsPerDay = 24 * 60 * 60 * 1000L;
	// Date oneWeekAgo = new Date(startDate.getTime() - (millisecondsPerDay * 7));
	// return convertDataIntoResponse(oneWeekAgo, startDate);
	//
	// }

	// public static Date getNearestMonday(Date date) {
	// Calendar calendar = Calendar.getInstance();
	// calendar.setTime(date);
	// int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
	//
	// // Calculate days to subtract to reach nearest Monday
	// int daysToSubtract = (dayOfWeek == Calendar.MONDAY) ? 0 : ((Calendar.MONDAY -
	// dayOfWeek + 7) % 7);
	//
	// calendar.add(Calendar.DAY_OF_YEAR, -daysToSubtract);
	// return calendar.getTime();
	// }

}
