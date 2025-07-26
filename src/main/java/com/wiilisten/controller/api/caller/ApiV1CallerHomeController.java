package com.wiilisten.controller.api.caller;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ListenerAnalytic;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.ProfileStatusEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.MonthlyRequestDto;
import com.wiilisten.request.SubCategoryPaginationRequestDto;
import com.wiilisten.response.FavoriteListenerDetailsDto;
import com.wiilisten.response.ListenerProfileResponseDto;
import com.wiilisten.response.LocationResponseDto;
import com.wiilisten.response.MonthlyResponseDto;
import com.wiilisten.response.ResponseWithDataAndPagination;
import com.wiilisten.response.UserReviewDetailsResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.CALLER
		+ ApplicationURIConstants.HOME)
public class ApiV1CallerHomeController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1CallerHomeController.class);

	@GetMapping(ApplicationURIConstants.LISTENER)
	public ResponseEntity<Object> getSpecificListenerBy(@RequestParam(required = false) Long id) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();

			List<ListenerProfileResponseDto> responseData = new ArrayList<>();

			if (!ApplicationUtils.isEmpty(id)) {
				ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByIdAndActiveTrue(id);
				if (listener == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
				}

				User listenerUser = listener.getUser();
				Boolean isFavListener = getServiceRegistry().getFavoriteListenerService()
						.existsByCallerIdAndListenerIdAndActiveTrue(user.getId(), listenerUser.getId());

				responseData.add(convertEntityToDto(listenerUser, listener, isFavListener));

			}

//			else if(!ApplicationUtils.isEmpty(username)) {
//				
//				List<ListenerProfile> listeners = getServiceRegistry().getListenerProfileService().findByProfileStatusAndUserNameContainingAndActiveTrue(ProfileStatusEnum.APPROVED.getStatus(), username);
//				if(ApplicationUtils.isEmpty(listeners)) {
//					LOGGER.info(ApplicationConstants.EXIT_LABEL);
//					return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
//				}
//				
//				listeners.forEach(listener -> {
//					
//					User listenerUser = listener.getUser();
//					Boolean isFavListener = getServiceRegistry().getFavoriteListenerService().existsByCallerIdAndListenerIdAndActiveTrue(user.getId(), listenerUser.getId());
//					responseData.add(convertEntityToDto(listenerUser, listener, isFavListener));
//					
//				});
//			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseData));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
	

	private ListenerProfileResponseDto convertEntityToDto(User listenerUser, ListenerProfile listener,
			Boolean isFavListener) {

		ListenerProfileResponseDto response = new ListenerProfileResponseDto();
		response.setListenerId(listener.getId());
		response.setProfilePicture(listenerUser.getProfilePicture());
		response.setCallName(listenerUser.getCallName());
		response.setNotableQuote(listener.getNotableQuote());
		response.setCallMaxDuration(listener.getCallMaxDuration());
		response.setRatePerMinuteForOnDemand(listener.getRatePerMinute());
		response.setCurrentRating(listenerUser.getCurrentRating());
		response.setTotalReviews(listenerUser.getTotalReviews());
		response.setAppActiveStatus(listener.getAppActiveStatus());

		response.setIsFavorite((isFavListener) ? true : false);

		List<UserRatingAndReview> listenerReviews = getServiceRegistry().getUserRatingAndReviewService()
				.findByReviewedUserAndActiveTrueOrderByCreatedAtDesc(listener.getUser());
		if (!ApplicationUtils.isEmpty(listenerReviews)) {
			List<UserReviewDetailsResponseDto> reviews = new ArrayList<>();

			listenerReviews.forEach(review -> {

				UserReviewDetailsResponseDto tempReview = new UserReviewDetailsResponseDto();
				tempReview.setCallName(review.getReviewerUser().getCallName());
				tempReview.setProfilePicture(review.getReviewerUser().getProfilePicture());
				tempReview.setRating(review.getRating());
				tempReview.setReview(review.getReview());

				reviews.add(tempReview);
			});

			response.setReviews(reviews);
		}

		return response;

	}

	// Month booking list
	@PostMapping(ApplicationURIConstants.BOOK_MONTH)
	public ResponseEntity<Object> getBookedMonth(@RequestBody MonthlyRequestDto monthlyRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			LOGGER.info("month is {}"+monthlyRequestDto.getMonth());
			User user = getLoggedInUser();
			CallerProfile callerProfile = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			int month = Integer.parseInt(monthlyRequestDto.getMonth());
			LOGGER.info("month is {}"+month);
			YearMonth yearMonth = YearMonth.of(monthlyRequestDto.getYear(), month);
			LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
			LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
			List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
					.findByBookingDateTimeBetweenAndActiveTrueAndCallerOrderByIdDesc(start, end, callerProfile);

			if (bookedCalls.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			List<MonthlyResponseDto> response = new ArrayList<>();
			bookedCalls.forEach(booking -> {
				MonthlyResponseDto dto = new MonthlyResponseDto();
				dto.setBookingDateTime(booking.getBookingDateTime());
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
	
	@PostMapping(ApplicationURIConstants.LISTENER + ApplicationURIConstants.ANALYTICS)
	public ResponseEntity<Object> getAnalytics(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User user = getLoggedInUser();
			CallerProfile callerProfile = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			ListenerAnalytic listenerAnalytic=new ListenerAnalytic();
			listenerAnalytic.setCaller(callerProfile);
			listenerAnalytic.setListener(listenerProfile);
			listenerAnalytic.setVisitingTime(LocalDateTime.now(ZoneOffset.UTC));
			getServiceRegistry().getListenerAnalyticService().saveORupdate(listenerAnalytic);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.ANALYTICS_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	// Find listener by name
	@PostMapping(ApplicationURIConstants.LISTENER + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getListenerList(@RequestBody SubCategoryPaginationRequestDto requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			if (ApplicationUtils.isEmpty(requestDetails.getSortBy()))
				requestDetails.setSortBy("id");
			if (ApplicationUtils.isEmpty(requestDetails.getSortType()))
				requestDetails.setSortType("DESC");

			ListenerProfile listener = null;
			FavoriteListenerDetailsDto response = new FavoriteListenerDetailsDto();
			if (requestDetails.getName() == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateGenericSuccessResponse(new ResponseWithDataAndPagination(response, 0L)));
			} else if (requestDetails.getName() != null) {
//				Page<User> user = getServiceRegistry().getUserService().findByCallNameIgnoreCaseAndActiveTrueAndRole(
//						requestDetails.getName(), pageable, UserRoleEnum.LISTENER.getRole());
//				List<Long> ids = user.getContent().stream().map(User::getId).collect(Collectors.toList());
//				listeners = getServiceRegistry().getListenerProfileService()
//						.findByUserIdInAndProfileStatusAndActiveTrue(ids, ProfileStatusEnum.APPROVED.getStatus(),
//								pageable);
				listener = getServiceRegistry().getListenerProfileService().findByUserNameIgnoringSpaces(
						StringUtils.trimAllWhitespace(requestDetails.getName()).toUpperCase(),
						ProfileStatusEnum.APPROVED.getStatus());
				if (listener == null || getCommonServices().isBlocked(getLoggedInUser(), listener.getUser())
						|| getCommonServices().isBlocked(listener.getUser(), getLoggedInUser())) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
				}

				response = getCommonServices().convertListenerProfileEntityToDtoForCardLayout(listener);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());

//			listeners = getServiceRegistry().getListenerProfileService()
//					.findByProfileStatusAndActiveTrue(ProfileStatusEnum.APPROVED.getStatus(), pageable);
//
//			if (ApplicationUtils.isEmpty(listeners) || ApplicationUtils.isEmpty(listeners.getContent())) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
//			}
//			
//			listeners.getContent().forEach(listener -> {
//
////				TODO: remove blocked and favorite listeners from list
////				if(!getCommonServices().checkBlockerHasBlockedUserOrNot(user, listener.getUser()))
//				response.add(getCommonServices().convertListenerProfileEntityToDtoForCardLayout(listener));
//
//			});
//
//			if (ApplicationUtils.isEmpty(response)) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
//			}
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(
//					new ResponseWithDataAndPagination(response, listeners.getTotalElements())));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
	
	@PostMapping(ApplicationURIConstants.LISTENER + ApplicationURIConstants.UNIQUE_LOCATION)
	public ResponseEntity<Object> getUniqueLocation() {
		
		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<String> locations = getServiceRegistry().getListenerProfileService().findUniqueLocations();
			List<LocationResponseDto> response = new ArrayList<>();
			//response.addAll(locations)
			locations.forEach(location->{
				LocationResponseDto dto = new LocationResponseDto();
				dto.setLocation(location); 
				response.add(dto);
			});
			
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		}  catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
