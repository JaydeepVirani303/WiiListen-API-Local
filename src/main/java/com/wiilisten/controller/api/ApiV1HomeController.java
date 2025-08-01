package com.wiilisten.controller.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.config.S3BucketProperties;
import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.AdministrativeNotification;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.ContactUs;
import com.wiilisten.entity.Faq;
import com.wiilisten.entity.FavouriteListener;
import com.wiilisten.entity.Language;
import com.wiilisten.entity.ListenerAvailability;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.NewsLetter;
import com.wiilisten.entity.NotificationHistory;
import com.wiilisten.entity.OpenEndedQuestion;
import com.wiilisten.entity.PageContent;
import com.wiilisten.entity.TrainingMaterial;
import com.wiilisten.entity.User;
import com.wiilisten.entity.UserRatingAndReview;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.ListenerSignupStepEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.BookedCallDto;
import com.wiilisten.request.ContactUsRequestDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.IdStatusRequestDto;
import com.wiilisten.request.NewsLetterRequestDto;
import com.wiilisten.request.PaginationAndSortingDetails;
import com.wiilisten.request.TimeSlotDto;
import com.wiilisten.request.TypeRequestDto;
import com.wiilisten.response.BookedCallDetailsDto;
import com.wiilisten.response.FaqDetailsDto;
import com.wiilisten.response.FavoriteListenerDetailsDto;
import com.wiilisten.response.HomePageAllRequestsDto;
import com.wiilisten.response.LanguageDetailsResponseDto;
import com.wiilisten.response.OpenEndedQuestionResponseDto;
import com.wiilisten.response.PageContentResponseDto;
import com.wiilisten.response.S3BucketDto;
import com.wiilisten.response.TrainingMaterialResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationResponseConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;
import com.wiilisten.utils.FCMService;
import com.wiilisten.utils.GenericResponse;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1)
public class ApiV1HomeController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1HomeController.class);
	@Autowired
	private FCMService fcmService;
	@Autowired
	S3BucketProperties s3BucketProperties;

	@GetMapping(ApplicationURIConstants.LANGUAGE)
	public ResponseEntity<Object> getLanguageList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			List<Language> languages = getServiceRegistry().getLanguageService().findByActiveTrue();
			if (!ApplicationUtils.isEmpty(languages)) {
				List<LanguageDetailsResponseDto> languageListResponse = new ArrayList<LanguageDetailsResponseDto>();

				Iterator<Language> itrLanguages = languages.iterator();
				while (itrLanguages.hasNext()) {
					Language language = itrLanguages.next();
					LanguageDetailsResponseDto languageResponse = new LanguageDetailsResponseDto();
					BeanUtils.copyProperties(language, languageResponse);
					languageListResponse.add(languageResponse);
				}

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(languageListResponse));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.NOTIFICATION_STATUS + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateNotificationStatus() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			user.setNotificationStatus(user.getNotificationStatus() ? false : true);
			getServiceRegistry().getUserService().saveORupdate(user);

			AdministrativeNotification administrativeNotification = new AdministrativeNotification();
			Map<String, String> payload = new HashMap<>();
			if (user.getNotificationStatus() && user.getIsLoggedIn()) {

				administrativeNotification.setTitle(ApplicationConstants.NOTIFICATION_STATUS);
				administrativeNotification.setContent(ApplicationConstants.NOTIFICATION_ON);
				administrativeNotification.setUsers(Collections.singletonList(user));
				administrativeNotification.setTags(ApplicationConstants.NOTIFICATION_STATUS);
				administrativeNotification.setActive(true);

				payload.put(ApplicationConstants.TITLE, ApplicationConstants.NOTIFICATION_STATUS);
				payload.put(ApplicationConstants.BODY, ApplicationConstants.NOTIFICATION_ON);
				payload.put(ApplicationConstants.TAG, ApplicationConstants.NOTIFICATION_STATUS);
				String receiverDeviceToken = user.getDeviceToken();
				if (receiverDeviceToken != null) {
					// Send push notification using FCM
					getServiceRegistry().getFcmService().sendPushNotification(receiverDeviceToken, payload);
				}
			} else {
				administrativeNotification.setTitle(ApplicationConstants.NOTIFICATION_STATUS);
				administrativeNotification.setContent(ApplicationConstants.NOTIFICATION_OFF);
				administrativeNotification.setUsers(Collections.singletonList(user));
				administrativeNotification.setTags(ApplicationConstants.NOTIFICATION_STATUS);
				administrativeNotification.setActive(true);

				payload.put(ApplicationConstants.TITLE, ApplicationConstants.NOTIFICATION_STATUS);
				payload.put(ApplicationConstants.BODY, ApplicationConstants.NOTIFICATION_OFF);
				payload.put(ApplicationConstants.TAG, ApplicationConstants.NOTIFICATION_STATUS);
				String receiverDeviceToken = user.getDeviceToken();
				if (receiverDeviceToken != null) {
					// Send push notification using FCM
					getServiceRegistry().getFcmService().sendPushNotification(receiverDeviceToken, payload);
				}

				getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.NOTIFICATION_STATUS_UPDATED_SUCCESSFULLY.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.TUTORIALS)
	public ResponseEntity<Object> getTutorialMaterials(@RequestBody TypeRequestDto typeRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			List<TrainingMaterial> trainingMaterials = getServiceRegistry().getTrainingMaterialService()
					.findByContentTypeAndSubCategoryAndActiveTrue(typeRequestDto.getType(), typeRequestDto.getSubCategory());

			if (ApplicationUtils.isEmpty(trainingMaterials)) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			List<TrainingMaterialResponseDto> responseData = new ArrayList<TrainingMaterialResponseDto>();
			trainingMaterials.forEach(material -> {
				TrainingMaterialResponseDto data = new TrainingMaterialResponseDto();
				BeanUtils.copyProperties(material, data);
				responseData.add(data);
			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseData));

		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.FAQ)
	public ResponseEntity<Object> getFaq() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			List<Faq> faqs = getServiceRegistry().getFaqService().findByActiveTrueOrderByIdDesc();
			if (!ApplicationUtils.isEmpty(faqs)) {
				List<FaqDetailsDto> faqResponse = new ArrayList<FaqDetailsDto>();

				faqs.forEach(faq -> {
					FaqDetailsDto testFaq = new FaqDetailsDto();
					testFaq.setQuestion(faq.getQuestion());
					testFaq.setAnswer(faq.getAnswer());
					BeanUtils.copyProperties(faq, testFaq);

					faqResponse.add(testFaq);
				});

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(faqResponse));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.OPEN_ENDED_QUESTIONS)
	public ResponseEntity<Object> getOpenEndedQuestionList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			// Page<OpenEndedQuestion> oeqs =
			// getServiceRegistry().getOpenEndedQuestionService()
			// .findByActiveTrue(pageable);
			List<OpenEndedQuestion> oeqs = getServiceRegistry().getOpenEndedQuestionService()
					.findByActiveTrueOrderByIdDesc();
			List<OpenEndedQuestionResponseDto> response = new ArrayList<>();
			oeqs.forEach(oeq -> {
				OpenEndedQuestionResponseDto responseDto = new OpenEndedQuestionResponseDto();
				BeanUtils.copyProperties(oeq, responseDto);
				// if(oeq.getSubCategory().getName()!=null) {
				// responseDto.setSubCategoryName(oeq.getSubCategory().getName());
				// }

				response.add(responseDto);
			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.PAGE_CONTENT)
	public ResponseEntity<Object> getPageContent(@RequestParam String type) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			PageContent pageContent = getServiceRegistry().getPageContentService().findByTypeAndActiveTrue(type);
			if (pageContent == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			if (type.equals(pageContent))

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(pageContent.getContent()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.CONTACT_US + ApplicationURIConstants.SAVE)
	public ResponseEntity<Object> saveContactUs(@RequestBody ContactUsRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			ContactUs contactUs = new ContactUs();
			contactUs.setDescription(requestDto.getDescription());
			contactUs.setSubject(requestDto.getSubject());
			contactUs.setUser(getLoggedInUser());
			getServiceRegistry().getContactUsService().saveORupdate(contactUs);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CONTACT_US_SAVED.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.USER + ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteUser(@RequestParam String type) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();

			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);
			if (listener != null) {
				listener.setActive(false);
				getServiceRegistry().getListenerProfileService().saveORupdate(listener);
			}

			CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			if (caller != null) {
				caller.setActive(false);
				getServiceRegistry().getCallerProfileService().saveORupdate(caller);
			}

			user.setActive(false);
			getServiceRegistry().getUserService().saveORupdate(user);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(
					getCommonServices().generateSuccessResponseWithMessageKey(SuccessMsgEnum.USER_DELETED.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping("/add-review")
	public ResponseEntity<Object> addReview(@RequestParam int rating, @RequestParam String review,
			@RequestParam Long id) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getServiceRegistry().getUserService().findByIdAndActiveTrueAndIsSuspendedFalse(id);
			Double oldRating = user.getCurrentRating();
			Long oldTotalReviews = user.getTotalReviews();
			oldRating = oldRating * oldTotalReviews;

			Long newTotalReviews = oldTotalReviews + 1;
			Double newRating = (oldRating + rating) / (newTotalReviews);
			// newRating = new BigDecimal(user.getCurrentRating()).setScale(1,
			// RoundingMode.HALF_UP).doubleValue();

			user.setTotalReviews(newTotalReviews);
			user.setCurrentRating(newRating);
			getServiceRegistry().getUserService().saveORupdate(user);

			UserRatingAndReview ratingAndReview = new UserRatingAndReview();
			ratingAndReview.setActive(true);
			ratingAndReview.setRating(rating);
			ratingAndReview.setReview(review);
			ratingAndReview.setReviewedUser(user);
			ratingAndReview.setReviewerUser(getLoggedInUser());
			ratingAndReview.setIsTopComment(false);
			getServiceRegistry().getUserRatingAndReviewService().saveORupdate(ratingAndReview);

			// Adding data in Administrative notification
			List<User> users = new ArrayList<>();
			users.add(user);
			if (user.getNotificationStatus() && user.getIsLoggedIn()) {
				String ratingReview = rating + review;
				AdministrativeNotification administrativeNotification = new AdministrativeNotification();
				administrativeNotification.setActive(true);
				administrativeNotification.setUsers(users);
				administrativeNotification.setTags(ApplicationConstants.REVIEW);
				administrativeNotification.setTitle(ApplicationConstants.NEW_REVIEW_ADDED);
				administrativeNotification.setContent(ratingReview);
				getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

				// Sending push notification
				String body = "You get " + rating + " rating and review " + review;
				Map<String, String> payload = new HashMap<>();
				payload.put(ApplicationConstants.TITLE, ApplicationConstants.NEW_REVIEW_ADDED);
				payload.put(ApplicationConstants.BODY, body);
				String receiverDeviceToken = user.getDeviceToken();
				if (receiverDeviceToken != null && user.getNotificationStatus()) {
					// Send push notification using FCM
					fcmService.sendPushNotification(receiverDeviceToken, payload);
				}
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse("Review added"));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping("/remove-review")
	public ResponseEntity<Object> removeReview(@RequestParam Long id) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			UserRatingAndReview review = getServiceRegistry().getUserRatingAndReviewService().findByIdAndActiveTrue(id);
			review.setActive(false);
			getServiceRegistry().getUserRatingAndReviewService().saveORupdate(review);

			User user = review.getReviewedUser();
			Long totalReview = user.getTotalReviews();
			Double currentRating = user.getCurrentRating();

			Long newTotalReview = totalReview - 1;
			Double totalRatings = totalReview * currentRating;
			Double newRating = (totalRatings - review.getRating()) / newTotalReview;

			user.setCurrentRating(newRating);
			user.setTotalReviews(newTotalReview);
			getServiceRegistry().getUserService().saveORupdate(user);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse("Review removed"));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.SWITCH_ACCOUNT)
	public ResponseEntity<Object> switchUserAccount(@RequestParam String role) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();

			// turning off the notification and logged-in flags
			user.setNotificationStatus(false);
			user.setIsLoggedIn(false);

			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUser(user);
			CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUser(user);

			if (role.equals(UserRoleEnum.LISTENER.getRole())) {

				// creating new listener if doesn't exist
				if (listener == null) {
					listener = new ListenerProfile();
					listener.setUser(user);

					user.setIsProfileSet(false);
				} else {

					// default user profile set if listener exist
					if (!listener.getCurrentSignupStep().equals(ListenerSignupStepEnum.STEP_7.getValue()))
						user.setIsProfileSet(false);
					else
						user.setIsProfileSet(true);

					listener.setActive(true);
				}

				getServiceRegistry().getListenerProfileService().saveORupdate(listener);

				// removing user as a caller
				// caller.setActive(false);
				getServiceRegistry().getCallerProfileService().saveORupdate(caller);

			} else if (role.equals(UserRoleEnum.CALLER.getRole())) {

				// creating new caller profile if doesn't exist
				if (caller == null) {
					caller = new CallerProfile();
					caller.setUser(user);

				} else
					caller.setActive(true);

				getServiceRegistry().getCallerProfileService().saveORupdate(caller);

				// default user profile set
				user.setIsProfileSet(true);

				// removing user as a listener
				// listener.setActive(false);
				listener.setAppActiveStatus(false);
				getServiceRegistry().getListenerProfileService().saveORupdate(listener);
			}

			user.setRole(UserRoleEnum.valueOf(role).getRole());
			getServiceRegistry().getUserService().saveORupdate(user);

			if (user.getRole().equals(UserRoleEnum.CALLER.getRole()) && user.getNotificationStatus()
					&& user.getIsLoggedIn()) {
				AdministrativeNotification administrativeNotification = new AdministrativeNotification();
				Map<String, String> payload = new HashMap<>();

				administrativeNotification.setTitle(ApplicationConstants.SWITCH_ACCOUNT);
				administrativeNotification.setContent("Switch account successfully");
				administrativeNotification.setUsers(Collections.singletonList(user));
				administrativeNotification.setTags(ApplicationConstants.SWITCH_ACCOUNT);
				administrativeNotification.setActive(true);

				payload.put(ApplicationConstants.TITLE, ApplicationConstants.SWITCH_ACCOUNT);
				payload.put(ApplicationConstants.BODY, "Switch account successfully");
				payload.put(ApplicationConstants.TAG, ApplicationConstants.SWITCH_ACCOUNT);
				String receiverDeviceToken = user.getDeviceToken();
				if (receiverDeviceToken != null) {
					// Send push notification using FCM
					getServiceRegistry().getFcmService().sendPushNotification(receiverDeviceToken, payload);
				}
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.ACCOUNT_SWITCHED_SUCCESSFULLY.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.PENDINGREQUESTS)
	public ResponseEntity<Object> getPendingBookedCallRequests(
			@RequestBody PaginationAndSortingDetails requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		Page<BookedCalls> bookedcalls = null;
		try {

			User user = getLoggedInUser();
			List<BookedCallDetailsDto> responseData = new ArrayList<BookedCallDetailsDto>();
			CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			if (ApplicationUtils.isEmpty(requestDetails.getSortType()))
				requestDetails.setSortType("DESC");
			Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);

			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				LOGGER.info("inside caller");
				List<String> listStatus = Arrays.asList(ApplicationConstants.PENDING, ApplicationConstants.RESCHEDULED);
				bookedcalls = getServiceRegistry().getBookedCallsService()
						.findByCallerProfileAndCallRequestStatusAndActiveTrue(caller, listStatus, pageable);

				if (bookedcalls == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
				} else {
					responseData = getCommonServices().convertBeanToDtoForBookedCall(bookedcalls.getContent(),
							ApplicationConstants.CALL_REQUEST);
				}
			}
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);
			if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
				LOGGER.info("inside listener");
				bookedcalls = getServiceRegistry().getBookedCallsService()
						.findByListenerProfileAndCallRequestStatusAndActiveTrue(listener, ApplicationConstants.PENDING,
								pageable);
				if (bookedcalls == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
				} else {
					responseData = getCommonServices().convertBeanToDtoForBookedCallListener(bookedcalls.getContent());
				}
			}

			if (responseData.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseData));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.UPCOMINGCALLREQUESTS)
	public ResponseEntity<Object> getUpcomingCallRequests(@RequestBody PaginationAndSortingDetails requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		Page<BookedCalls> bookedcalls = null;
		if (ApplicationUtils.isEmpty(requestDetails.getSortType()))
			requestDetails.setSortType("DESC");
		Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);
		try {

			User user = getLoggedInUser();
			List<BookedCallDetailsDto> responseData = new ArrayList<BookedCallDetailsDto>();
			CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);

			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				LOGGER.info("inside caller");
				bookedcalls = getServiceRegistry().getBookedCallsService()
						.findByCallerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(caller,
								ApplicationConstants.ACCEPTED, ApplicationConstants.SCHEDULED, pageable);
				if (bookedcalls == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
				} else {
					responseData = getCommonServices().convertBeanToDtoForBookedCall(bookedcalls.getContent(),
							ApplicationConstants.CALL_REQUEST);
				}
			}
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);
			if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
				LOGGER.info("inside listener");
				bookedcalls = getServiceRegistry().getBookedCallsService()
						.findByListenerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(listener,
								ApplicationConstants.ACCEPTED, ApplicationConstants.SCHEDULED, pageable);
				if (bookedcalls == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
				} else {
					responseData = getCommonServices().convertBeanToDtoForBookedCallListener(bookedcalls.getContent());
				}
			}

			if (responseData.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(responseData));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.HOMEREQUESTLIST)
	public ResponseEntity<Object> getAllRequestListForHomePage() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			List<BookedCallDetailsDto> upcomingDtoList = new ArrayList<BookedCallDetailsDto>();
			List<BookedCallDetailsDto> pendingDtoList = new ArrayList<BookedCallDetailsDto>();
			List<FavoriteListenerDetailsDto> favListnerDtoList = new ArrayList<FavoriteListenerDetailsDto>();
			HomePageAllRequestsDto homePageDto = new HomePageAllRequestsDto();
			CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
			List<BookedCalls> upcomingbookedcalls = null;
			List<BookedCalls> pendingCallRequests = null;
			List<FavouriteListener> favListnereList = null;
			List<FavoriteListenerDetailsDto> sponserListenerList = new ArrayList<>();
			List<ListenerProfile> listenerProfiles = null;
			// find upcoming call list
			// find Pending Request List
			// find favourite Listener list
			List<String> listStatus = Arrays.asList(ApplicationConstants.PENDING, ApplicationConstants.RESCHEDULED);
			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				upcomingbookedcalls = getServiceRegistry().getBookedCallsService()
						.findTop10ByCallerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(caller,
								ApplicationConstants.ACCEPTED, ApplicationConstants.SCHEDULED);
				pendingCallRequests = getServiceRegistry().getBookedCallsService()
						.findTop10ByCallerProfileAndCallRequestStatusAndActiveTrue(caller, listStatus);
				favListnereList = getServiceRegistry().getFavoriteListenerService()
						.findTop10ByCallerIdAndActiveTrue(user.getId());
				favListnereList = getCommonServices().filterBlockedFavouriteListeners(user, favListnereList); // removed
																												// blocked
																												// user
				listenerProfiles = getServiceRegistry().getListenerProfileService()
						.findTop10ByIsAdvertisementActiveTrueAndActiveTrue();
				listenerProfiles = getCommonServices().filterBlockedListeners(user, listenerProfiles);
				upcomingDtoList = getCommonServices().convertBeanToDtoForBookedCall(upcomingbookedcalls,
						ApplicationConstants.CALL_REQUEST);
				pendingDtoList = getCommonServices().convertBeanToDtoForBookedCall(pendingCallRequests,
						ApplicationConstants.CALL_REQUEST);
				LOGGER.info("pending size {}" + pendingDtoList.size() + " upcoming {}" + upcomingDtoList.size());
				if (!ApplicationUtils.isEmpty(favListnereList)) {

					favListnereList.forEach(fav -> {
						ListenerProfile listenerfav = getServiceRegistry().getListenerProfileService()
								.findByUserAndActiveTrue(fav.getListener());
						if (listenerfav != null)
							favListnerDtoList.add(
									getCommonServices().convertListenerProfileEntityToDtoForCardLayout(listenerfav));
					});
				}

				if (!ApplicationUtils.isEmpty(listenerProfiles)) {
					listenerProfiles.forEach(listener -> {
						if (listener != null) {
							if (!listener.getUser().getId().equals(user.getId())) {
								sponserListenerList
										.add(getCommonServices()
												.convertListenerProfileEntityToDtoForCardLayout(listener));
							}
						}

					});
				}
			}
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);
			if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
				upcomingbookedcalls = getServiceRegistry().getBookedCallsService()
						.findTop10ByListenerProfileAndCallRequestStatusAndCallStatusAndActiveTrue(listener,
								ApplicationConstants.ACCEPTED, ApplicationConstants.SCHEDULED);
				pendingCallRequests = getServiceRegistry().getBookedCallsService()
						.findTop10ByListenerProfileAndCallRequestStatusAndActiveTrue(listener, listStatus);
				upcomingDtoList = getCommonServices().convertBeanToDtoForBookedCallListener(upcomingbookedcalls);
				pendingDtoList = getCommonServices().convertBeanToDtoForBookedCallListener(pendingCallRequests);
			}

			homePageDto.setPendingRequestList(pendingDtoList);
			homePageDto.setUpcomigCallList(upcomingDtoList);
			homePageDto.setFavouriteListenerList(favListnerDtoList);
			homePageDto.setSponserListenerList(sponserListenerList);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(homePageDto));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PutMapping(ApplicationURIConstants.BOOKCALLUPDATE)
	public ResponseEntity<Object> bookCallSendRequest(@PathVariable Long id,
			@RequestBody BookedCallDetailsDto bookedCallDetailsDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		LOGGER.info("test {}" + bookedCallDetailsDto.getCallStatus());
		try {
			BookedCalls existingCall = getServiceRegistry().getBookedCallsService().findOne(id);

			if (existingCall == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());

			}

			// Update the existing with the data from the bookedCall
			if (bookedCallDetailsDto.getCallRequestStatus() != null)
				existingCall.setCallRequestStatus(bookedCallDetailsDto.getCallRequestStatus());

			if (bookedCallDetailsDto.getCallStatus() != null)
				existingCall.setCallStatus(bookedCallDetailsDto.getCallStatus());
			if (bookedCallDetailsDto.getCallStatus().equalsIgnoreCase(ApplicationConstants.CANCELLED)
					|| bookedCallDetailsDto.getCallRequestStatus().equalsIgnoreCase(ApplicationConstants.REJECTED)) {
				existingCall.setActive(false);
			}

			if (bookedCallDetailsDto.getCancelationReason() != null)
				existingCall.setCancelationReason(bookedCallDetailsDto.getCancelationReason());
			if (bookedCallDetailsDto.getRejectionReason() != null)
				existingCall.setRejectionReason(bookedCallDetailsDto.getRejectionReason());

			if (bookedCallDetailsDto.getBookingDateTime() != null
					&& bookedCallDetailsDto.getDurationInMinutes() != null) {
				existingCall.setBookingDateTime(bookedCallDetailsDto.getBookingDateTime());
				existingCall.setDurationInMinutes(bookedCallDetailsDto.getDurationInMinutes());
				double subTotal = existingCall.getPrice() * bookedCallDetailsDto.getDurationInMinutes();

				existingCall.setSubTotal(subTotal);
				existingCall.setPayableAmount(subTotal);
			}

			// Construct push notification payload
			Map<String, String> payload = new HashMap<>();
			String receiverDeviceToken;
			// notification-history add while status update .
			NotificationHistory notification = new NotificationHistory();
			notification.setActive(true);
			User user = getLoggedInUser();
			if (user.getRole().equals(UserRoleEnum.CALLER.getRole()) && user.getNotificationStatus()
					&& user.getIsLoggedIn()) {
				LOGGER.info(user.getRole());
				if (bookedCallDetailsDto.getCallStatus().equalsIgnoreCase(ApplicationConstants.CANCELLED)) {
					notification.setEvent(ApplicationConstants.CANCELLED);
					notification.setRecipientId(existingCall.getListener().getUser());
					notification.setSenderId(existingCall.getCaller().getUser());
					notification.setContent(ApplicationConstants.CALL_CANCELLED);
					payload.put("title", ApplicationConstants.CANCELLED_CAPITAL);
					payload.put("body", ApplicationConstants.CALL_CANCELLED);
					payload.put("tag", ApplicationConstants.BOOKED_CALL + bookedCallDetailsDto.getCallRequestStatus()
							+ ApplicationConstants.SUCCESSFULLY);
					receiverDeviceToken = existingCall.getListener().getUser().getDeviceToken();
					// Send push notification using FCM
					fcmService.sendPushNotification(receiverDeviceToken, payload);
				}
				if (bookedCallDetailsDto.getCallRequestStatus().equalsIgnoreCase(ApplicationConstants.RESCHEDULED)) {
					notification.setEvent(ApplicationConstants.RESCHEDULED);
					notification.setSenderId(existingCall.getCaller().getUser());
					notification.setRecipientId(existingCall.getListener().getUser());
					notification.setContent(ApplicationConstants.CALL_RESCHEDULED);
					payload.put("title", ApplicationConstants.RESCHEDULED_CAPITAL);
					payload.put("body", ApplicationConstants.CALL_RESCHEDULED);
					payload.put("tag", ApplicationConstants.BOOKED_CALL + bookedCallDetailsDto.getCallRequestStatus()
							+ ApplicationConstants.SUCCESSFULLY);
					receiverDeviceToken = existingCall.getListener().getUser().getDeviceToken();
					// Send push notification using FCM
					fcmService.sendPushNotification(receiverDeviceToken, payload);
				}
			}

			if (user.getRole().equals(UserRoleEnum.LISTENER.getRole()) && user.getNotificationStatus()
					&& user.getIsLoggedIn()) {
				LOGGER.info(user.getRole());
				if (bookedCallDetailsDto.getCallStatus().equalsIgnoreCase(ApplicationConstants.CANCELLED)) {
					notification.setEvent(ApplicationConstants.CANCELLED);
					notification.setSenderId(existingCall.getListener().getUser());
					notification.setRecipientId(existingCall.getCaller().getUser());
					notification.setContent(ApplicationConstants.CALL_CANCELLED);
					payload.put("title", ApplicationConstants.CANCELLED_CAPITAL);
					payload.put("body", ApplicationConstants.CALL_CANCELLED);
					payload.put("tag", ApplicationConstants.BOOKED_CALL + ApplicationConstants.CANCELLED
							+ ApplicationConstants.SUCCESSFULLY);
					receiverDeviceToken = existingCall.getCaller().getUser().getDeviceToken();
					// Send push notification using FCM
					fcmService.sendPushNotification(receiverDeviceToken, payload);
				}
				if (!bookedCallDetailsDto.getCallStatus().equalsIgnoreCase(ApplicationConstants.CANCELLED)) {
					if (bookedCallDetailsDto.getCallRequestStatus().equalsIgnoreCase(ApplicationConstants.REJECTED)) {
						notification.setEvent(ApplicationConstants.REJECTED);
						notification.setSenderId(existingCall.getListener().getUser());
						notification.setRecipientId(existingCall.getCaller().getUser());
						notification.setContent(ApplicationConstants.CALL_REJECTED);
						payload.put("title", ApplicationConstants.REJECTED_CAPTITAL);
						payload.put("body", ApplicationConstants.CALL_REJECTED);
						payload.put("tag", ApplicationConstants.BOOKED_CALL + ApplicationConstants.REJECTED
								+ ApplicationConstants.SUCCESSFULLY);
						receiverDeviceToken = existingCall.getCaller().getUser().getDeviceToken();
						// Send push notification using FCM
						fcmService.sendPushNotification(receiverDeviceToken, payload);
					}
					if (bookedCallDetailsDto.getCallRequestStatus()
							.equalsIgnoreCase(ApplicationConstants.RESCHEDULED)) {
						notification.setEvent(ApplicationConstants.RESCHEDULED);
						notification.setSenderId(existingCall.getListener().getUser());
						notification.setRecipientId(existingCall.getCaller().getUser());
						notification.setContent(ApplicationConstants.CALL_RESCHEDULED);
						payload.put("title", ApplicationConstants.RESCHEDULED_CAPITAL);
						payload.put("body", ApplicationConstants.CALL_RESCHEDULED);
						payload.put("tag", ApplicationConstants.BOOKED_CALL + ApplicationConstants.RESCHEDULED
								+ ApplicationConstants.SUCCESSFULLY);
						receiverDeviceToken = existingCall.getCaller().getUser().getDeviceToken();
						// Send push notification using FCM
						fcmService.sendPushNotification(receiverDeviceToken, payload);
					}
					if (bookedCallDetailsDto.getCallRequestStatus().equalsIgnoreCase(ApplicationConstants.ACCEPTED)) {
						notification.setEvent(ApplicationConstants.ACCEPTED);
						ZoneId zoneId = ZoneId.of(existingCall.getRequestedTimeZone());

						// Get the current date and time in that zone
						ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

						// Convert ZonedDateTime to LocalDateTime
						LocalDateTime accepted = zonedDateTime.toLocalDateTime();
						existingCall.setAcceptedDateTime(accepted);
						notification.setSenderId(existingCall.getListener().getUser());
						notification.setRecipientId(existingCall.getCaller().getUser());
						notification.setContent(ApplicationConstants.CALL_ACCEPTED);
						payload.put("title", ApplicationConstants.ACCEPTED_CAPITAL);
						payload.put("body", ApplicationConstants.CALL_ACCEPTED);
						payload.put("tag", ApplicationConstants.BOOKED_CALL + ApplicationConstants.ACCEPTED
								+ ApplicationConstants.SUCCESSFULLY);
						receiverDeviceToken = existingCall.getCaller().getUser().getDeviceToken();
						// Send push notification using FCM
						fcmService.sendPushNotification(receiverDeviceToken, payload);
					}
				}
			}
			notification.setBookingId(existingCall);
			existingCall.setNotes(bookedCallDetailsDto.getNotes());
			if (user.getNotificationStatus()) {
				getServiceRegistry().getNotificationHistoryService().saveORupdate(notification);
			}

			getServiceRegistry().getBookedCallsService().saveORupdate(existingCall);

			IdRequestDto response = new IdRequestDto();
			response.setId(id);

			if (bookedCallDetailsDto.getCallRequestStatus().equalsIgnoreCase("pending")
					|| bookedCallDetailsDto.getCallRequestStatus().equalsIgnoreCase("rejected")) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(new GenericResponse(ApplicationResponseConstants.SUCCESS_RESPONSE,
						ApplicationConstants.BOOKED_CALL + "cancelled" + ApplicationConstants.SUCCESSFULLY, response));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(new GenericResponse(
					ApplicationResponseConstants.SUCCESS_RESPONSE, ApplicationConstants.BOOKED_CALL
							+ bookedCallDetailsDto.getCallRequestStatus() + ApplicationConstants.SUCCESSFULLY,
					response));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.NOTES)
	public ResponseEntity<Object> addNotes(@RequestBody IdStatusRequestDto dto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			BookedCalls bookedCalls = getServiceRegistry().getBookedCallsService().findByIdAndActiveTrue(dto.getId());

			if (bookedCalls == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());

			}
			bookedCalls.setNotes(dto.getType());
			getServiceRegistry().getBookedCallsService().saveORupdate(bookedCalls);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.NOTES_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.PRACTICE)
	public ResponseEntity<Object> getAllAvailableTimeSlots(@RequestBody BookedCallDto bookCallDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		List<TimeSlotDto> availableSlots = new ArrayList<TimeSlotDto>();
		try {
			// Add CALLER time zone
			User user = getLoggedInUser();
			user.setTimeZone(bookCallDto.getTimeZone());
			getServiceRegistry().getUserService().saveORupdate(user);

			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(bookCallDto.getListenerId());
			if (listener == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
			}
			String dayOfWeek = getCommonServices().getWeekDayFromDate(bookCallDto.getBookingDateTime());
			List<ListenerAvailability> listsenerAllAvailableSlots = getServiceRegistry()
					.getListenerAvailabilityService().findByUserAndWeekDayAndActiveTrue(listener.getUser(), dayOfWeek);
			if (listsenerAllAvailableSlots.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateBadResponseWithMessageKey(
						ErrorDataEnum.NO_TIME_SLOT_AVAILABLE_FOR_THIS_DAY.getCode()));

			} else {
				List<BookedCalls> listOfbookingDateCalls = getServiceRegistry().getBookedCallsService()
						.findByBookingDateTimeAndListenerAndActiveTrue(bookCallDto.getBookingDateTime(), listener);
				List<TimeSlotDto> allBookedSlots = converBookcallsToTimeSlot(listOfbookingDateCalls);
				for (ListenerAvailability slot : listsenerAllAvailableSlots) {
					LocalTime startTime = slot.getStartTime();

					LocalTime endTime = slot.getEndTime();
					LocalDate curDate = LocalDate.now();
					List<TimeSlotDto> allDurationsinMinslots;
					if (bookCallDto.getBookingDateTime().equals(curDate)) {
						allDurationsinMinslots = getCommonServices().generateTimeSlotsForCurrentDate(startTime, endTime,
								bookCallDto.getDurationInMinutes(), "");
					} else {
						allDurationsinMinslots = getCommonServices().generateTimeSlots(startTime, endTime,
								bookCallDto.getDurationInMinutes());
					}
					List<TimeSlotDto> listeneravailableSlots = findAvailableSlots(allDurationsinMinslots,
							allBookedSlots);
					if (!listeneravailableSlots.isEmpty())
						availableSlots.addAll(listeneravailableSlots);

				}
			}

			if (availableSlots.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateBadResponseWithMessageKey(
						ErrorDataEnum.NO_TIME_SLOT_AVAILABLE_FOR_THIS_DAY.getCode()));
			}
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(availableSlots));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.GETTIMESLOTS)
	public ResponseEntity<Object> getAllAvailableTimeSlotsPractice(@RequestBody BookedCallDto bookCallDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		List<TimeSlotDto> availableSlots = new ArrayList<TimeSlotDto>();
		try {
			// Add CALLER time zone
			User user = getLoggedInUser();
			// user.setTimeZone(bookCallDto.getTimeZone());
			getServiceRegistry().getUserService().saveORupdate(user);
			LocalDate bookingDateTime = bookCallDto.getBookingDateTime();

			ListenerProfile listener = getServiceRegistry().getListenerProfileService()
					.findByIdAndActiveTrue(bookCallDto.getListenerId());

			if (listener == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
			}
			String timeZone = listener.getUser().getTimeZone();
			LOGGER.info("timeZone" + timeZone);
			String dayOfWeek = getCommonServices().getWeekDayFromDate(bookCallDto.getBookingDateTime());
			List<ListenerAvailability> listsenerAllAvailableSlots = getServiceRegistry()
					.getListenerAvailabilityService().findByUserAndWeekDayAndActiveTrue(listener.getUser(), dayOfWeek);

			LOGGER.info("size is {}" + listsenerAllAvailableSlots.size());
			if (listsenerAllAvailableSlots.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateBadResponseWithMessageKey(
						ErrorDataEnum.NO_TIME_SLOT_AVAILABLE_FOR_THIS_DAY.getCode()));

			} else {
				List<BookedCalls> listOfbookingDateCalls = getServiceRegistry().getBookedCallsService()
						.findByBookingDateTimeAndListenerAndActiveTrue(bookCallDto.getBookingDateTime(), listener);
				List<TimeSlotDto> allBookedSlots = converBookcallsToTimeSlot(listOfbookingDateCalls);
				for (ListenerAvailability slot : listsenerAllAvailableSlots) {
					// LocalDate bookingDateTime = bookCallDto.getBookingDateTime();
					LocalDateTime startDateTime = bookingDateTime.atTime(slot.getStartTime());
					LocalTime startTime = getCommonServices()
							.UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(startDateTime, timeZone);
					LOGGER.info("startTime" + startTime + " date" + startDateTime + " slot time" + slot.getStartTime());
					LocalDateTime endDateTime = bookingDateTime.atTime(slot.getEndTime());
					LocalTime endTime = getCommonServices()
							.UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(endDateTime, timeZone);
					LOGGER.info("startTime" + endTime + " date" + endDateTime + " slot time" + slot.getEndTime());
					LocalDate curDate = LocalDate.now();
					List<TimeSlotDto> allDurationsinMinslots;
					if (bookCallDto.getBookingDateTime().equals(curDate)) {
						allDurationsinMinslots = getCommonServices().generateTimeSlotsForCurrentDate(startTime, endTime,
								bookCallDto.getDurationInMinutes(), timeZone);
					} else {
						allDurationsinMinslots = getCommonServices().generateTimeSlots(startTime, endTime,
								bookCallDto.getDurationInMinutes());
					}
					LOGGER.info("allDurationsinMinslots size " + allDurationsinMinslots.size());
					List<TimeSlotDto> convertedUtcDurations = new ArrayList<>();
					allDurationsinMinslots.forEach(timeSlots -> {
						TimeSlotDto dto = new TimeSlotDto();
						LocalDateTime slotStartDateTime = bookingDateTime.atTime(timeSlots.getStartTime());
						// LocalTime startTime =
						// getCommonServices().UTCLocalDateTimeToISOLocalTimeStringWithTimeZone(startDateTime,
						// timeZone);

						LocalDateTime slotEndDateTime = bookingDateTime.atTime(timeSlots.getEndTime());
						// LocalTime endTime =
						dto.setStartTime(getCommonServices().localDateTimeToUtcTime(slotStartDateTime, timeZone));
						dto.setEndTime(getCommonServices().localDateTimeToUtcTime(slotEndDateTime, timeZone));
						convertedUtcDurations.add(dto);
					});
					LOGGER.info("size inside is " + convertedUtcDurations.size());

					List<TimeSlotDto> listeneravailableSlots = findAvailableSlots(convertedUtcDurations,
							allBookedSlots);
					if (!listeneravailableSlots.isEmpty())
						availableSlots.addAll(listeneravailableSlots);

				}
			}

			if (availableSlots.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateBadResponseWithMessageKey(
						ErrorDataEnum.NO_TIME_SLOT_AVAILABLE_FOR_THIS_DAY.getCode()));
			}
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(availableSlots));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	// TODO:Notification list
	// @PostMapping(ApplicationURIConstants.NOTIFICATION +
	// ApplicationURIConstants.LIST)
	// public ResponseEntity<Object> getNotificationList(@RequestBody TypeRequestDto
	// typeRequestDto) {
	//
	// LOGGER.info(ApplicationConstants.ENTER_LABEL);
	//
	// try {
	// List<Object> response = new ArrayList<>();
	// List<AdministrativeNotification> administrativeNotifications =
	// getServiceRegistry()
	// .getAdministrativeNotificationService().findByUserId(getLoggedInUser().getId());
	// if (administrativeNotifications.isEmpty()) {
	// LOGGER.info(ApplicationConstants.EXIT_LABEL);
	// return ResponseEntity.ok(getCommonServices()
	// .generateBadResponseWithMessageKey(ErrorDataEnum.NOTIFICATION_NOT_FOUND.getCode()));
	// }
	// response.add(administrativeNotifications);
	//
	// LOGGER.info(ApplicationConstants.EXIT_LABEL);
	// return
	// ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
	// } catch (Exception e) {
	// e.printStackTrace();
	// LOGGER.info(ApplicationConstants.EXIT_LABEL);
	// return ResponseEntity.ok(getCommonServices().generateFailureResponse());
	// }
	// }

	private List<TimeSlotDto> converBookcallsToTimeSlot(List<BookedCalls> listOfbookingDateCalls) {
		// TODO Auto-generated method stub
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

	private List<TimeSlotDto> findAvailableSlots(List<TimeSlotDto> allDurationsinMinslots,
			List<TimeSlotDto> allBookedSlots) {
		// TODO Auto-generated method stub
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

	@PostMapping(ApplicationURIConstants.NEWS_LETTER_SUBSCRIBE)
	public ResponseEntity<Object> subscribeNewsLetter(@RequestBody NewsLetterRequestDto newsLetterRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			NewsLetter newsLetter = getServiceRegistry().getNewsLetterService()
					.findByEmailAndActiveTrue(newsLetterRequestDto.getEmail());
			if (newsLetter != null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.ALREADY_SUBSCRIBED.getCode()));
			}
			NewsLetter letter = new NewsLetter();
			BeanUtils.copyProperties(newsLetterRequestDto, letter);
			letter.setActive(true);
			getServiceRegistry().getNewsLetterService().saveORupdate(letter);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.NEWS_LETTER_SUBSCRIBED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.PAGE_CONTENT + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getPageContentList(@RequestBody TypeRequestDto typeRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			PageContent pageContent = getServiceRegistry().getPageContentService()
					.findByTypeAndActiveTrue(typeRequestDto.getType());
			PageContentResponseDto response = new PageContentResponseDto();
			BeanUtils.copyProperties(pageContent, response);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.CONTACT_US + ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addContactUs(@RequestBody ContactUsRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			ContactUs contactUs = new ContactUs();
			contactUs.setDescription(requestDto.getDescription());
			contactUs.setSubject(requestDto.getSubject());
			contactUs.setUser(getLoggedInUser());
			contactUs.setActive(true);
			getServiceRegistry().getContactUsService().saveORupdate(contactUs);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CONTACT_US_SAVED.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.S3_CREDENTIALS)
	public ResponseEntity<Object> getS3Credetials() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		S3BucketDto dto = new S3BucketDto();
		dto.setBucket(s3BucketProperties.getBucket());
		dto.setAccessKey(s3BucketProperties.getAccessKey());
		dto.setSecretKey(s3BucketProperties.getSecretKey());
		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(dto));

	}
}
