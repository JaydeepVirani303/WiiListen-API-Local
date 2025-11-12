package com.wiilisten.controller.api.listener;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import com.stripe.param.*;
import com.wiilisten.request.*;
import com.wiilisten.utils.PdfEncryptionService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.Stripe;
import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.ListenerAvailability;
import com.wiilisten.entity.ListenerBankDetails;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.AdministrativeNotification;
import com.wiilisten.entity.Language;
import com.wiilisten.entity.User;
import com.wiilisten.entity.TrainingMaterial;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.ListenerSignupStepEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.TrainingVideoProgressStatusEnum;
import com.wiilisten.response.TrainingMaterialResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.LISTENER
		+ ApplicationURIConstants.PROFILE)
public class ApiV1ListenerProfileController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1ListenerProfileController.class);

	@Value("${stripe.SecretKey}")
	private String StripeKey;

	@Autowired
	PdfEncryptionService pdfEncryptionService;

	//	TODO
	@PostMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> listnerProfile(@RequestBody ListnerProfileDTO requestProfileDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);

			UserProfileDto response = new UserProfileDto();

			String currentStep = listener.getCurrentSignupStep();
			switch (ListenerSignupStepEnum.valueOf(currentStep)) {

				case STEP_1:
					user.setProfilePicture(requestProfileDetails.getImg());
					// user.setCallName(requestProfileDetails.getCallName());//Remove call name and
					// take user name of listener
					getServiceRegistry().getUserService().saveORupdate(user);

					if (getCommonServices().checkListenerUniqueUsername(requestProfileDetails.getUsername()) == 1) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices()
								.generateBadResponseWithMessageKey(ErrorDataEnum.USERNAME_EXISTS.getCode()));
					}

					listener.setUserName(requestProfileDetails.getUsername());
					listener.setNotableQuote(requestProfileDetails.getQuote());
					listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_2.getValue());
					getServiceRegistry().getListenerProfileService().saveORupdate(listener);
					break;

				case STEP_2:

					listener = updateRateAndAVailability(user, listener, requestProfileDetails);

//					listener.setCallMaxDuration(requestProfileDetails.getMaxDuration());
//					listener.setRatePerMinute(Double.valueOf(requestProfileDetails.getPrice()));
//					
//					requestProfileDetails.getAvailability().forEach(dayWiseDuty -> {
//						
//						dayWiseDuty.getDutyTimings().forEach(dutyTiming -> {
//						
//							ListenerAvailability listenerAvailability = new ListenerAvailability();
//							listenerAvailability.setUser(user);
//							listenerAvailability.setWeekDay(dayWiseDuty.getDay());
//							listenerAvailability.setStartTime( ApplicationUtils.StringToLocalTime(dutyTiming.getStartTime(), ApplicationConstants.TIME_FORMAT_HH_MM));
//							listenerAvailability.setEndTime( ApplicationUtils.StringToLocalTime(dutyTiming.getEndTime(), ApplicationConstants.TIME_FORMAT_HH_MM));
//							listenerAvailability.setActive(true);
//							
//							getServiceRegistry().getListenerAvailabilityService().saveORupdate(listenerAvailability);
//							
//						});
//						
//					});
//					
//					listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_3.getValue());
//					getServiceRegistry().getListenerProfileService().saveORupdate(listener);
					break;

				case STEP_3:

					listener = updatePremiumInformation(user, listener, requestProfileDetails);

//					listener.setIsEligibleForPremiumCallSearch(requestProfileDetails.getPremiumCall());
//					listener.setGender(requestProfileDetails.getGender());
//					listener.setEducation(requestProfileDetails.getEducation());
//					listener.setDateOfBirth(LocalDate.ofInstant(requestProfileDetails.getDob().toInstant(), ZoneOffset.UTC));
//					listener.setLocation(requestProfileDetails.getLocation());
//					
//					List<Language> languages = new ArrayList<>();
//					requestProfileDetails.getLanguages().forEach(id -> {
//						Language language = getServiceRegistry().getNewLanguageService().findByIdAndActiveTrue(id);
//						languages.add(language);
//					});
//					listener.setLanguages(languages);
//					
//					listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_4.getValue());
//					getServiceRegistry().getListenerProfileService().saveORupdate(listener);
					break;

				case STEP_4:

					listener.setCurrentTrainingVideo(Long.parseLong(requestProfileDetails.getVideoProgress()));
					listener.setTrainingVideoProgress(
							(listener.getCurrentTrainingVideo() == getCommonServices().getCountOfListenerAndTrainingVideos())
									? TrainingVideoProgressStatusEnum.COMPLETED.getStatus()
									: TrainingVideoProgressStatusEnum.PENDING.getStatus());

					if (listener.getTrainingVideoProgress().equals(TrainingVideoProgressStatusEnum.COMPLETED.getStatus()))
						listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_5.getValue());

					getServiceRegistry().getListenerProfileService().saveORupdate(listener);
					break;

				case STEP_5:

					//put logic here

					if (requestProfileDetails.getIdproof() != null) {
						listener.setIdProof(requestProfileDetails.getIdproof());
						pdfEncryptionService.applyPasswordAndOverwrite(requestProfileDetails.getIdproof());
					}

					if (requestProfileDetails.getW9form() != null) {
						listener.setW9Form(requestProfileDetails.getW9form());
						pdfEncryptionService.applyPasswordAndOverwrite(requestProfileDetails.getW9form());
					}


					//---
					listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_6.getValue());
					getServiceRegistry().getListenerProfileService().saveORupdate(listener);
					break;

				case STEP_6:

					if (user.getStripeCustomerId() == null) {
						Customer customer = getServiceRegistry().getPaymentService().createStripeCustomer(user);
						user.setStripeCustomerId(customer.getId());
						getServiceRegistry().getUserService().saveORupdate(user);
					}

					Map<String, Object> bankAccountParams = new HashMap<>();
					bankAccountParams.put("country", "US");
					bankAccountParams.put("currency", "usd");
					bankAccountParams.put("account_holder_name", requestProfileDetails.getAccountName());
					bankAccountParams.put("account_holder_type", "individual");
					bankAccountParams.put("routing_number", requestProfileDetails.getAbaNumber());
					bankAccountParams.put("account_number", requestProfileDetails.getAccountNumber());

					Map<String, Object> tokenParams = new HashMap<>();
					tokenParams.put("bank_account", bankAccountParams);
					Token token = null;
					try {
						token = Token.create(tokenParams);
					} catch (Exception e) {
						LOGGER.info(ApplicationConstants.EXIT_LABEL);
						return ResponseEntity.ok(getCommonServices()
								.generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_BANK_DETAILS.getCode()));
					}
					Map<String, Object> retrieveParams = new HashMap<>();
					retrieveParams.put("expand", Arrays.asList("sources"));

					Customer customer = Customer.retrieve(user.getStripeCustomerId(), retrieveParams, null);
					LOGGER.info("customer 1 {}" + customer.getSources());
					LOGGER.info("customer 2 {}" + customer);

					Map<String, Object> externalAccountParams = new HashMap<>();
					externalAccountParams.put("source", token.getId());
					customer.getSources().create(externalAccountParams);

					List<Integer> amounts = new ArrayList<>();
					amounts.add(32);
					amounts.add(45);

					LOGGER.info("customer 3 {}" + customer);
					LOGGER.info("cutomer sources2 {}" + customer.getSources());
					if (customer.getSources() != null) {
						BankAccount bankAccount = (BankAccount) customer.getSources().retrieve(token.getBankAccount().getId());
						Map<String, Object> params = new HashMap<>();
						params.put("amounts", amounts);

						bankAccount.verify(params);
						LOGGER.info("Bank account verified successfully.");
					} else {
						LOGGER.error("Customer sources are null. Failed to retrieve bank account.");
					}

					ListenerBankDetails bankDetails = new ListenerBankDetails();
					bankDetails.setAccountNumber(requestProfileDetails.getAccountNumber());
					bankDetails.setAccountType(requestProfileDetails.getAccountType());
					bankDetails.setActive(true);
					bankDetails.setFullName(requestProfileDetails.getAccountName());
					bankDetails.setRoutingOrAbaNumber(requestProfileDetails.getAbaNumber());
					bankDetails.setUser(user);
					getServiceRegistry().getListenerBankDetailsService().saveORupdate(bankDetails);

					user.setIsProfileSet(true);
					user.setNotificationStatus(true);
					user.setIsLoggedIn(true);
					getServiceRegistry().getUserService().saveORupdate(user);

					listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_7.getValue());
					getServiceRegistry().getListenerProfileService().saveORupdate(listener);

					getServiceRegistry().getListenerProfileService().saveORupdate(listener);
					break;

				default:
					break;
			}

			response.setCurrentSignupStep(listener.getCurrentSignupStep());

			if (user.getIsProfileSet() && user.getNotificationStatus() && user.getIsLoggedIn()) {
				AdministrativeNotification administrativeNotification = new AdministrativeNotification();
				Map<String, String> payload = new HashMap<>();

				administrativeNotification.setTitle(ApplicationConstants.PROFILE_UPDATED);
				administrativeNotification.setContent(ApplicationConstants.PROFILE_UPDATED_SUCCESSFULLY);
				administrativeNotification.setUsers(Collections.singletonList(user));
				administrativeNotification.setTags(ApplicationConstants.PROFILE_UPDATED);
				administrativeNotification.setActive(true);
				getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

				payload.put(ApplicationConstants.TITLE, ApplicationConstants.PROFILE_UPDATED);
				payload.put(ApplicationConstants.BODY, ApplicationConstants.PROFILE_UPDATED_SUCCESSFULLY);
				payload.put(ApplicationConstants.TAG, ApplicationConstants.PROFILE_UPDATED);
				String receiverDeviceToken = user.getDeviceToken();
				if (receiverDeviceToken != null) {
					// Send push notification using FCM
					getServiceRegistry().getFcmService().sendPushNotification(receiverDeviceToken, payload);
				}
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.W9FORM)
	public ResponseEntity<Object> updateW9Form(@RequestBody TypeRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User user = getLoggedInUser();
			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
					.findByUserAndActiveTrue(user);
			listenerProfile.setW9Form(requestDto.getType());
			getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CALLER_BANNED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.TRAINING_MATERIAL)
	public ResponseEntity<Object> getTrainingMaterials(@RequestParam("content_type") String contentType, @RequestParam("sub_category") String subCategory) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			List<TrainingMaterial> trainingMaterials = getServiceRegistry().getTrainingMaterialService()
					.findByContentTypeAndSubCategoryAndActiveTrue(contentType, subCategory);

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

	@PostMapping(ApplicationURIConstants.BASIC_INFORMATION + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> basicInformationUpdate(@RequestBody ListnerProfileDTO requestProfileDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);

			Boolean isEmailUpdated = false;
			if (!user.getEmail().equals(requestProfileDetails.getEmail())) {
				User tempUser = getServiceRegistry().getUserService()
						.findByEmailAndActiveTrue(requestProfileDetails.getEmail());
				if (tempUser != null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(
							getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_EXIST.getCode()));
				}
				isEmailUpdated = true;
			}

			if (!listener.getUserName().equals(requestProfileDetails.getUsername())) {
				if (getCommonServices().checkListenerUniqueUsername(requestProfileDetails.getUsername()) == 1) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.USERNAME_EXISTS.getCode()));
				}
			}

			user.setProfilePicture(requestProfileDetails.getImg());
			user.setCallName(requestProfileDetails.getCallName());
			listener.setUserName(requestProfileDetails.getUsername());
			if (isEmailUpdated)
				user.setEmail(requestProfileDetails.getEmail());
			listener.setNotableQuote(requestProfileDetails.getQuote());

			getServiceRegistry().getUserService().saveORupdate(user);
			getServiceRegistry().getListenerProfileService().saveORupdate(listener);

			UserProfileDto response = new UserProfileDto();
			response.setProfilePicture(user.getProfilePicture());
			response.setCallName(user.getCallName());
			response.setUserName(listener.getUserName());
			response.setEmail(user.getEmail());
			response.setNotableQuote(listener.getNotableQuote());

			if (isEmailUpdated) {
				SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(
								new UserDetail(user.getEmail(), user.getPassword(), user.getRole()), null,
								new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(user.getRole())))));
				String token = getTokenUtil().generateToken();
				response.setToken(token);
			}

			if (user.getIsProfileSet()) {
				getCommonServices().sendProfileUpdatedNotification(user);
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	/**
	 * This method updates the listener call rate and listener's week-day wise
	 * availability
	 *
	 * @param
	 * @return ListenerProfile
	 */
	private ListenerProfile updateRateAndAVailability(User user, ListenerProfile listener,
													  ListnerProfileDTO requestProfileDetails) {
		listener.setCallMaxDuration(requestProfileDetails.getMaxDuration());
		listener.setRatePerMinute(Double.valueOf(requestProfileDetails.getPrice()));
		String listenerTimeZone = requestProfileDetails.getRequestedTimeZone();
//		storing new availabilities for new listener
		if (!user.getIsProfileSet()) {
			requestProfileDetails.getAvailability().forEach(dayWiseDuty -> {

				dayWiseDuty.getDutyTimings().forEach(dutyTiming -> {

					ListenerAvailability listenerAvailability = new ListenerAvailability();
					listenerAvailability.setTimeZone(listenerTimeZone);
					listenerAvailability.setUser(user);
					listenerAvailability.setWeekDay(dayWiseDuty.getDay());
					LocalTime startLocalTime = ApplicationUtils.StringToLocalTime(dutyTiming.getStartTime(),
							ApplicationConstants.TIME_FORMAT_HH_MM);
					LocalTime endLocalTime = ApplicationUtils.StringToLocalTime(dutyTiming.getEndTime(),
							ApplicationConstants.TIME_FORMAT_HH_MM);

					LocalTime startUTC = convertLocalTimeToUTC(startLocalTime, listenerTimeZone);
					LocalTime endUTC = convertLocalTimeToUTC(endLocalTime, listenerTimeZone);
					listenerAvailability.setActive(true);
					listenerAvailability.setStartTime(startUTC);
					listenerAvailability.setEndTime(endUTC);
					getServiceRegistry().getListenerAvailabilityService().saveORupdate(listenerAvailability);

				});

			});
		} else {

//			removing existing availabilities			
			List<ListenerAvailability> existingAvailabilities = getServiceRegistry().getListenerAvailabilityService()
					.findByUserAndActiveTrue(user);
			if (!ApplicationUtils.isEmpty(existingAvailabilities)) {

				Iterator<ListenerAvailability> itrListenerAvailability = existingAvailabilities.iterator();
				while (itrListenerAvailability.hasNext()) {
					ListenerAvailability availability = itrListenerAvailability.next();
					availability.setActive(false);
					getServiceRegistry().getListenerAvailabilityService().saveORupdate(availability);
				}
			}

//			creating new availabilities
			requestProfileDetails.getAvailability().forEach(dayWiseDuty -> {
				LOGGER.info("Processing availability for day: {}", dayWiseDuty.getDay());

				dayWiseDuty.getDutyTimings().forEach(dutyTiming -> {
					LOGGER.info("Original StartTime: {}, EndTime: {} (Timezone: {})", dutyTiming.getStartTime(), dutyTiming.getEndTime(), listenerTimeZone);

					ListenerAvailability listenerAvailability = new ListenerAvailability();
					listenerAvailability.setUser(user);
					listenerAvailability.setWeekDay(dayWiseDuty.getDay());
					listenerAvailability.setTimeZone(listenerTimeZone);
					LocalTime startLocalTime = ApplicationUtils.StringToLocalTime(
							dutyTiming.getStartTime(), ApplicationConstants.TIME_FORMAT_HH_MM);
					LocalTime endLocalTime = ApplicationUtils.StringToLocalTime(
							dutyTiming.getEndTime(), ApplicationConstants.TIME_FORMAT_HH_MM);

					LOGGER.debug("Parsed LocalTime - Start: {}, End: {}", startLocalTime, endLocalTime);

					// Convert to UTC
					LocalTime startUTC = convertLocalTimeToUTC(startLocalTime, listenerTimeZone);
					LocalTime endUTC = convertLocalTimeToUTC(endLocalTime, listenerTimeZone);

					LOGGER.info("Converted to UTC - Start: {}, End: {}", startUTC, endUTC);

					listenerAvailability.setStartTime(startUTC);
					listenerAvailability.setEndTime(endUTC);
					listenerAvailability.setActive(true);
					getServiceRegistry().getListenerAvailabilityService().saveORupdate(listenerAvailability);
					LOGGER.info("Saved listener availability for {} - Start: {}, End: {}", dayWiseDuty.getDay(), startUTC, endUTC);
				});
			});
		}

		if (!user.getIsProfileSet())
			listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_3.getValue());

		// Add LISTENER timezone
		user.setTimeZone(listenerTimeZone);
		getServiceRegistry().getUserService().saveORupdate(user);
		listener = getServiceRegistry().getListenerProfileService().saveORupdate(listener);

		return listener;
	}


	public static LocalTime convertLocalTimeToUTC(LocalTime localTime, String timeZone) {
		LOGGER.info("Starting conversion of localTime '{}' from timezone '{}'", localTime, timeZone);

		if (localTime == null || timeZone == null || timeZone.isEmpty()) {
			LOGGER.error("Invalid input: localTime='{}', timeZone='{}'", localTime, timeZone);
			throw new IllegalArgumentException("Invalid input time or time zone");
		}

		LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
		LOGGER.debug("Constructed LocalDateTime: {}", localDateTime);

		ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.of(timeZone));
		LOGGER.debug("ZonedDateTime in original timezone ({}): {}", timeZone, zonedDateTime);

		ZonedDateTime utcDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));
		LOGGER.debug("Converted ZonedDateTime in UTC: {}", utcDateTime);

		LocalTime utcLocalTime = utcDateTime.toLocalTime();
		LOGGER.info("Final UTC time: {}", utcLocalTime);

		return utcLocalTime;
	}

	/**
	 * This method updates the listener premium information
	 *
	 * @param
	 * @return ListenerProfile
	 */
	private ListenerProfile updatePremiumInformation(User user, ListenerProfile listener,
													 ListnerProfileDTO requestProfileDetails) {
		listener.setIsEligibleForPremiumCallSearch(requestProfileDetails.getPremiumCall());

		if (requestProfileDetails.getPremiumCall()) {
			listener.setGender(requestProfileDetails.getGender());
			listener.setEducation(requestProfileDetails.getEducation());
			listener.setDateOfBirth(LocalDate.ofInstant(requestProfileDetails.getDob().toInstant(), ZoneOffset.UTC));
			listener.setLocation(requestProfileDetails.getLocation());

			List<Language> languages = new ArrayList<>();
			requestProfileDetails.getLanguages().forEach(id -> {
				Language language = getServiceRegistry().getLanguageService().findByIdAndActiveTrue(id);
				languages.add(language);
			});
			listener.setLanguages(languages);
		}

		if (!user.getIsProfileSet())
			listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_4.getValue());

		getServiceRegistry().getListenerProfileService().saveORupdate(listener);

		return listener;
	}

	/**
	 * This method updates the user's profile picture
	 *
	 * @param
	 * @return ListenerProfile
	 */
	private User updateProfilePicture(User user, ListnerProfileDTO requestProfileDetails) {
		if (!ApplicationUtils.isEmpty(requestProfileDetails.getImg())) {

			user.setProfilePicture(requestProfileDetails.getImg());
			user = getServiceRegistry().getUserService().saveORupdate(user);
			return user;
		}
		return user;
	}

	private String convertUtcToTimeZone(String time, String targetZone) {
		if (time == null || targetZone == null) return time;

		// Parse only HH:mm
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime localTime = LocalTime.parse(time, timeFormatter);

		// Attach dummy date for conversion (important for crossing midnight)
		LocalDate today = LocalDate.now();
		LocalDateTime localDateTime = LocalDateTime.of(today, localTime);

		// Treat this as UTC
		ZonedDateTime utcZoned = localDateTime.atZone(ZoneOffset.UTC);

		// Convert to target zone
		ZonedDateTime targetZoned = utcZoned.withZoneSameInstant(ZoneId.of(targetZone));

		// Return formatted time
		return targetZoned.toLocalTime().format(timeFormatter);
	}

	@PostMapping(ApplicationURIConstants.RATE_AND_AVAILABILITY + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> ratesAndAvailabilityUpdate(@RequestBody ListnerProfileDTO requestProfileDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			String requestedTimeZone = requestProfileDetails.getRequestedTimeZone();
			User user = getLoggedInUser();
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);

			user = updateProfilePicture(user, requestProfileDetails);
			listener = updateRateAndAVailability(user, listener, requestProfileDetails);

			UserProfileDto response = new UserProfileDto();

			List<AvailabilityDTO> dutyDetailsList = getCommonServices().generateResponseForListenerAvailability(user);
			if (dutyDetailsList != null) {
				for (AvailabilityDTO availability : dutyDetailsList) {
					for (DutyTimeRequestDto duty : availability.getDutyTimings()) {
						duty.setStartTime(
								convertUtcToTimeZone(duty.getStartTime(), requestedTimeZone)
						);
						duty.setEndTime(
								convertUtcToTimeZone(duty.getEndTime(), requestedTimeZone)
						);
					}
				}
				response.setAvailability(dutyDetailsList);
			}


			response.setCallMaxDuration(listener.getCallMaxDuration());
			response.setRatePerMinute(listener.getRatePerMinute());
			response.setAvailability(dutyDetailsList);

			if (user.getIsProfileSet()) {
				getCommonServices().sendProfileUpdatedNotification(user);
			}

			// added Listener time zone
			user.setTimeZone(requestedTimeZone);
			getServiceRegistry().getUserService().saveORupdate(user);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.PREMIUM_INFORMATION + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> premiumInformationUpdate(@RequestBody ListnerProfileDTO requestProfileDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);

			user = updateProfilePicture(user, requestProfileDetails);
			listener = updatePremiumInformation(user, listener, requestProfileDetails);

			UserProfileDto response = new UserProfileDto();
			response.setIsEligibleForPremiumCallSearch(listener.getIsEligibleForPremiumCallSearch());

			if (listener.getIsEligibleForPremiumCallSearch()) {
				response.setGender(listener.getGender());
				response.setEducation(listener.getEducation());
				response.setLocation(listener.getLocation());

				List<Long> languages = listener.getLanguages().stream().map(language -> language.getId()).toList();
				response.setLanguages(languages);
			}

			if (user.getIsProfileSet()) {
				getCommonServices().sendProfileUpdatedNotification(user);
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.BANK_INFORMATION + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> bankInformationUpdate(@RequestBody ListnerProfileDTO requestProfileDetails) {
		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			// 1️⃣ Set your Stripe platform secret key
			Stripe.apiKey = StripeKey;
			User user = getLoggedInUser();

			// 2️⃣ Ensure Stripe Customer exists
			if (user.getStripeCustomerId() == null) {
				Customer customer = getServiceRegistry().getPaymentService().createStripeCustomer(user);
				user.setStripeCustomerId(customer.getId());
				getServiceRegistry().getUserService().saveORupdate(user);
			}

			// 3️⃣ Create or Retrieve Express Connected Account
			Account account;
			if (user.getStripeAccountId() == null) {
				AccountCreateParams accountParams = AccountCreateParams.builder()
						.setType(AccountCreateParams.Type.EXPRESS)
						.setCountry("US")
						.setEmail(user.getEmail())
						.setBusinessType(AccountCreateParams.BusinessType.INDIVIDUAL)
						.setCapabilities(
								AccountCreateParams.Capabilities.builder()
										.setCardPayments(AccountCreateParams.Capabilities.CardPayments.builder()
												.setRequested(true).build())
										.setTransfers(AccountCreateParams.Capabilities.Transfers.builder()
												.setRequested(true).build())
										.build()
						)
						.build();

				account = Account.create(accountParams);
				user.setStripeAccountId(account.getId());
				getServiceRegistry().getUserService().saveORupdate(user);
				LOGGER.info("Created new Stripe account: {}", account.getId());
			} else {
				account = Account.retrieve(user.getStripeAccountId());
				LOGGER.info("Fetched existing Stripe account: {}", account.getId());
			}

			// 4️⃣ Add or Update External Bank Account
			if (requestProfileDetails.getAccountNumber() != null && requestProfileDetails.getAbaNumber() != null) {
				Map<String, Object> bankAccountParams = new HashMap<>();
				bankAccountParams.put("country", "US");
				bankAccountParams.put("currency", "usd");
				bankAccountParams.put("account_holder_name", requestProfileDetails.getAccountName());
				bankAccountParams.put("account_holder_type", "individual");
				bankAccountParams.put("routing_number", requestProfileDetails.getAbaNumber());
				bankAccountParams.put("account_number", requestProfileDetails.getAccountNumber());

				Map<String, Object> tokenParams = Map.of("bank_account", bankAccountParams);
				Token token = Token.create(tokenParams);

				Map<String, Object> externalAccountParams = Map.of("external_account", token.getId());
				RequestOptions accountOptions = RequestOptions.builder()
						.setStripeAccount(account.getId())
						.build();

				if (user.getExternalAccountId() == null) {
					ExternalAccount externalAccount = account.getExternalAccounts().create(externalAccountParams, accountOptions);
					user.setExternalAccountId(externalAccount.getId());
					getServiceRegistry().getUserService().saveORupdate(user);
					LOGGER.info("Added new external bank account for: {}", account.getId());
				} else {
					LOGGER.info("External bank account already exists for: {}", account.getId());
				}
			} else {
				LOGGER.warn("Bank details missing in request, skipping bank update.");
			}

			// 5️⃣ KYC Verification Check (before onboarding)
			boolean detailsSubmitted = Boolean.TRUE.equals(account.getDetailsSubmitted());
			boolean chargesEnabled = Boolean.TRUE.equals(account.getChargesEnabled());
			boolean payoutsEnabled = Boolean.TRUE.equals(account.getPayoutsEnabled());

			Map<String, Object> response = new HashMap<>();
//			response.put("stripeAccountId", account.getId());

			if (detailsSubmitted && chargesEnabled && payoutsEnabled) {
				// ✅ Already verified
				response.put("message", "KYC verification is already complete. You can now receive payouts.");
				LOGGER.info("Stripe account {} already verified", account.getId());
			} else {
				// ❌ Not verified yet → generate onboarding link
				String successUrl = "http://localhost:63342/WiiListen-API-Local/src/main/resources/templates/success.html";  // replace with real domain
				String refreshUrl = "http://localhost:8080/reauth";

				AccountLinkCreateParams linkParams = AccountLinkCreateParams.builder()
						.setAccount(account.getId())
						.setReturnUrl(successUrl)
						.setRefreshUrl(refreshUrl)
						.setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
						.build();

				AccountLink onboardingLink = AccountLink.create(linkParams);

				response.put("kycLink", onboardingLink.getUrl());
				response.put("message", "Please complete onboarding using the KYC link.");
				LOGGER.info("Onboarding link generated for account: {}", account.getId());
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

		} catch (StripeException e) {
			LOGGER.error("Stripe API Error: {}", e.getMessage(), e);
			return ResponseEntity.ok(getCommonServices()
					.generateBadResponseWithMessageKey(ErrorDataEnum.STRIPE_API_ERROR.getCode()));

		} catch (Exception e) {
			LOGGER.error("Unexpected Error: {}", e.getMessage(), e);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
}
