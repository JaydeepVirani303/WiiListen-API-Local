package com.wiilisten.controller.api.listener;

import java.time.LocalDate;
import java.time.ZoneOffset;
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
import com.stripe.model.Account;
import com.stripe.model.BankAccount;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccount;
import com.stripe.model.PaymentMethod;
import com.stripe.model.PaymentSource;
import com.stripe.model.SetupIntent;
import com.stripe.model.Token;
import com.stripe.param.BankAccountVerifyParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.PaymentSourceCollectionCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import com.stripe.param.SetupIntentVerifyMicrodepositsParams;
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
import com.wiilisten.request.AvailabilityDTO;
import com.wiilisten.request.ListnerProfileDTO;
import com.wiilisten.request.TypeRequestDto;
import com.wiilisten.request.UserDetail;
import com.wiilisten.request.UserProfileDto;
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
						(listener.getCurrentTrainingVideo() == getCommonServices().getCountOfTrainingVideos())
								? TrainingVideoProgressStatusEnum.COMPLETED.getStatus()
								: TrainingVideoProgressStatusEnum.PENDING.getStatus());

				if (listener.getTrainingVideoProgress().equals(TrainingVideoProgressStatusEnum.COMPLETED.getStatus()))
					listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_5.getValue());

				getServiceRegistry().getListenerProfileService().saveORupdate(listener);
				break;

			case STEP_5:

				listener.setIdProof(requestProfileDetails.getIdproof());
//					TODO Add W9 form details
				listener.setW9Form(requestProfileDetails.getW9Form());
				listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_6.getValue());
				getServiceRegistry().getListenerProfileService().saveORupdate(listener);
				break;

			case STEP_6:
				
				if(user.getStripeCustomerId()==null) {
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
				Token token =null;
				try {
					 token = Token.create(tokenParams);
				}catch(Exception e) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_BANK_DETAILS.getCode()));
				}
				Map<String, Object> retrieveParams = new HashMap<>();
				retrieveParams.put("expand", Arrays.asList("sources"));

				Customer customer = Customer.retrieve(user.getStripeCustomerId(), retrieveParams, null);
				LOGGER.info("customer 1 {}"+customer.getSources());
				LOGGER.info("customer 2 {}"+customer);
			
				Map<String, Object> externalAccountParams = new HashMap<>();
				externalAccountParams.put("source", token.getId());
				customer.getSources().create(externalAccountParams);

				List<Integer> amounts=new ArrayList<>();
				amounts.add(32);
				amounts.add(45);

				LOGGER.info("customer 3 {}"+customer);
				 LOGGER.info("cutomer sources2 {}"+customer.getSources());
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

		} catch (

		Exception e) {
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
	public ResponseEntity<Object> getTrainingMaterials(@RequestParam("content_type") String contentType) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			List<TrainingMaterial> trainingMaterials = getServiceRegistry().getTrainingMaterialService()
					.findByContentTypeAndActiveTrue(contentType);

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

//		storing new availabilities for new listener
		if (!user.getIsProfileSet()) {
			requestProfileDetails.getAvailability().forEach(dayWiseDuty -> {

				dayWiseDuty.getDutyTimings().forEach(dutyTiming -> {

					ListenerAvailability listenerAvailability = new ListenerAvailability();
					listenerAvailability.setUser(user);
					listenerAvailability.setWeekDay(dayWiseDuty.getDay());
					listenerAvailability.setStartTime(ApplicationUtils.StringToLocalTime(dutyTiming.getStartTime(),
							ApplicationConstants.TIME_FORMAT_HH_MM));
					listenerAvailability.setEndTime(ApplicationUtils.StringToLocalTime(dutyTiming.getEndTime(),
							ApplicationConstants.TIME_FORMAT_HH_MM));
					listenerAvailability.setActive(true);

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

				dayWiseDuty.getDutyTimings().forEach(dutyTiming -> {
					ListenerAvailability listenerAvailability = new ListenerAvailability();
					listenerAvailability.setUser(user);
					listenerAvailability.setWeekDay(dayWiseDuty.getDay());
					listenerAvailability.setStartTime(ApplicationUtils.StringToLocalTime(dutyTiming.getStartTime(),
							ApplicationConstants.TIME_FORMAT_HH_MM));
					listenerAvailability.setEndTime(ApplicationUtils.StringToLocalTime(dutyTiming.getEndTime(),
							ApplicationConstants.TIME_FORMAT_HH_MM));
					listenerAvailability.setActive(true);

					getServiceRegistry().getListenerAvailabilityService().saveORupdate(listenerAvailability);

				});

			});
		}

		if (!user.getIsProfileSet())
			listener.setCurrentSignupStep(ListenerSignupStepEnum.STEP_3.getValue());

		// Add LISTENER timezone
		user.setTimeZone(requestProfileDetails.getTimeZone());
		getServiceRegistry().getUserService().saveORupdate(user);
		listener = getServiceRegistry().getListenerProfileService().saveORupdate(listener);

		return listener;
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

	@PostMapping(ApplicationURIConstants.RATE_AND_AVAILABILITY + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> ratesAndAvailabilityUpdate(@RequestBody ListnerProfileDTO requestProfileDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User user = getLoggedInUser();
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);

			user = updateProfilePicture(user, requestProfileDetails);
			listener = updateRateAndAVailability(user, listener, requestProfileDetails);

			List<AvailabilityDTO> dutyDetailsList = getCommonServices().generateResponseForListenerAvailability(user);
//			if(dutyDetailsList == null) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
//			}

			UserProfileDto response = new UserProfileDto();
			response.setCallMaxDuration(listener.getCallMaxDuration());
			response.setRatePerMinute(listener.getRatePerMinute());
			response.setAvailability(dutyDetailsList);

			if (user.getIsProfileSet()) {
				getCommonServices().sendProfileUpdatedNotification(user);
			}

			// added Listener time zone
			user.setTimeZone(requestProfileDetails.getTimeZone());
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
			Stripe.apiKey = StripeKey;
			User user = getLoggedInUser();
			
			if(user.getStripeCustomerId()==null) {
				Customer customer = getServiceRegistry().getPaymentService().createStripeCustomer(user);
				user.setStripeCustomerId(customer.getId());
				getServiceRegistry().getUserService().saveORupdate(user);
			}
			
			ListenerBankDetails bankDetails = getServiceRegistry().getListenerBankDetailsService()
					.findByUserAndActiveTrue(user);
			if (bankDetails == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}
//			Customer customer=Customer.retrieve(user.getStripeCustomerId());
			
//			PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.builder()
//			.setType(PaymentMethodCreateParams.Type.US_BANK_ACCOUNT)
//			.setUsBankAccount(
//			    PaymentMethodCreateParams.UsBankAccount.builder()
//			        .setRoutingNumber("110000000")
//			        .setAccountNumber("000123456789")
//			        .setAccountHolderType(PaymentMethodCreateParams.UsBankAccount.AccountHolderType.INDIVIDUAL)
//			        .build()
//			)
//			.setBillingDetails(
//			    PaymentMethodCreateParams.BillingDetails.builder()
//			        .setName("Test")
//			        .build()
//			)
//			.build();
//			LOGGER.info("before");
			// Create the Payment Method
//			PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);
//			LOGGER.info("before paymentMethod{}"+paymentMethod.getId());
			// Attach the Payment Method to the Customer
//			PaymentMethod paymentMethodAttached = paymentMethod.attach(
//			PaymentMethodAttachParams.builder().setCustomer(user.getStripeCustomerId()).build()
//			);
//			LOGGER.info("before paymentMethod{}"+paymentMethod.getId());
//			SetupIntentCreateParams params = SetupIntentCreateParams.builder()
//				    .addPaymentMethodType("us_bank_account") // Specify the payment method type
//				    .setCustomer(customer.getId()) // Set the customer ID
//				    .setPaymentMethod(paymentMethod.getId()) // Set the payment method ID
//				    .setConfirm(true) // Automatically confirm the SetupIntent
//				    .build();

				

//				   SetupIntentCreateParams params = SetupIntentCreateParams.builder()
//				            .addPaymentMethodType("us_bank_account") // Specify the payment method type
//				            .setCustomer(customerId) // Set the customer ID
//				            .setPaymentMethod(paymentMethodId) // Set the payment method ID
//				            .setConfirm(true) // Automatically confirm the SetupIntent
//				            .setMandateData(
//				                SetupIntentCreateParams.MandateData.builder()
//				                    .setNotificationMethod(SetupIntentCreateParams.MandateData.NotificationMethod.DEFAULT) // Use the default if EMAIL is not valid
//				                    .build()
//				            )
//				            .build();
//			SetupIntentCreateParams params = SetupIntentCreateParams.builder()
//				    .addPaymentMethodType("us_bank_account")
//				    .setCustomer(customer.getId())
//				    .setPaymentMethod(paymentMethod.getId())
//				    .setConfirm(true)
//				    .setMandateData(
//				        SetupIntentCreateParams.MandateData.Builder.class
//				    )
//				    .build();
//			SetupIntent setupIntent = SetupIntent.create(params);
//	        
//	        LOGGER.info("id is {}"+setupIntent.getId());
//	        LOGGER.info("before verify");
//	        SetupIntent resource = SetupIntent.retrieve(setupIntent.getId());
//	        SetupIntentVerifyMicrodepositsParams param =
//	          SetupIntentVerifyMicrodepositsParams.builder()
//	            .addAmount(32L)
//	            .addAmount(45L)
//	            .build();
//	        SetupIntent setupIntents = resource.verifyMicrodeposits(param);
//	        SetupIntent resource = SetupIntent.retrieve(setupIntent.getId());
//
//	        SetupIntentVerifyMicrodepositsParams param = SetupIntentVerifyMicrodepositsParams.builder()
//	            .addAmount(32L) // Replace with the actual amount of the first microdeposit
//	            .addAmount(45L) // Replace with the actual amount of the second microdeposit
//	            .build();
//
//	        SetupIntent setupIntents = resource.verifyMicrodeposits(param);

//			PaymentMethodVerifyParams params = PaymentMethodVerifyParams.builder()
//			    .addAllAmount(amounts) // Amounts are micro-deposits (in cents)
//			    .build();
			
			// Verify the bank account (Payment Method)
		//	PaymentMethod verifiedBankAccount = PaymentMethod.retrieve(paymentMethodAttached.getId()).verify(params);
			
//			LOGGER.info("Bank account with Payment Method ID {} verified successfully.", verifiedBankAccount.getId());
	        
	        
	        
		
			Map<String, Object> bankAccountParams = new HashMap<>();
			bankAccountParams.put("country", "US");
			bankAccountParams.put("currency", "usd");
			bankAccountParams.put("account_holder_name", requestProfileDetails.getAccountName());
			bankAccountParams.put("account_holder_type", "individual");
			bankAccountParams.put("routing_number", requestProfileDetails.getAbaNumber());
			bankAccountParams.put("account_number", requestProfileDetails.getAccountNumber());

			Map<String, Object> tokenParams = new HashMap<>();
			tokenParams.put("bank_account", bankAccountParams);
			Token token =null;
			try {
				 token = Token.create(tokenParams);
			}catch(Exception e) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_BANK_DETAILS.getCode()));
			}
			 // This will generate a bank account token
//			LOGGER.info("id is {}"+token.getId());
//			LOGGER.info("customer source {}"+customer.getSources());
//			LOGGER.info("bank id is {}"+token.getBankAccount().getId());
//			LOGGER.info("id is {}"+token);
//			LOGGER.info("id is {}"+user.getStripeCustomerId());
//			LOGGER.info("customer {}"+customer);
//			
//			//Generate payment source 
////			PaymentSourceCollectionCreateParams params =
////					  PaymentSourceCollectionCreateParams.builder()
////					    .setSource(token.getId())
////					    .build();
////					PaymentSource paymentSource = customer.getSources().create(params);
//			LOGGER.info("before ");
////			LOGGER.info("before {}"+paymentSource.getId());
			Map<String, Object> retrieveParams = new HashMap<>();
			retrieveParams.put("expand", Arrays.asList("sources"));

			Customer customer = Customer.retrieve(user.getStripeCustomerId(), retrieveParams, null);
			LOGGER.info("customer 1 {}"+customer.getSources());
			LOGGER.info("customer 2 {}"+customer);
//			
//			// Use this token to create a Bank Account verification request
			Map<String, Object> externalAccountParams = new HashMap<>();
			externalAccountParams.put("source", token.getId());
			customer.getSources().create(externalAccountParams);
//			LOGGER.info("customer {}"+customer);
//			// Assuming you have a connected Stripe Account for the user
////			ExternalAccount sources=(ExternalAccount) customer.getSources().retrieve(token.getBankAccount().getId());
			List<Integer> amounts=new ArrayList<>();
			amounts.add(32);
			amounts.add(45);
//			Map<String, Object> amount = new HashMap<>();
//			amount.put("amounts", amounts);
////			sources.
//			
//			BankAccount resource =
//					  BankAccount.retrieve("cus_9s6XGDTHzA66Po", "ba_1NAiwl2eZvKYlo2CRdCLZSxO");
//					BankAccountVerifyParams params =
//					  BankAccountVerifyParams.builder().addAmount(32L).addAmount(45L).build();
//					BankAccount bankAccount = resource.verify(params);
//			
//			Map<String, Object> retrieveParam = new HashMap<>();
//			retrieveParams.put("expand", Arrays.asList("sources"));
//
			LOGGER.info("customer 3 {}"+customer);
//			 customer = Customer.retrieve(customer.getId(), retrieveParam, null);
			 LOGGER.info("cutomer sources2 {}"+customer.getSources());
			if (customer.getSources() != null) {
			    BankAccount bankAccount = (BankAccount) customer.getSources().retrieve(token.getBankAccount().getId());
			    Map<String, Object> params = new HashMap<>();
			    params.put("amounts", amounts);

			    bankAccount.verify(params);
			    LOGGER.info("Bank account verified successfully.");
			} else {
			    LOGGER.error("Customer sources are null. Failed to retrieve bank account.");
			}
//			
//			
//			LOGGER.info("bankaccount verify iss {}"+customer);
//
//			Account account = Account.retrieve(token.getId());
//			LOGGER.info("accounti di{}"+account.getId());
//			account.getExternalAccounts().create(externalAccountParams);

			bankDetails.setAccountNumber(requestProfileDetails.getAccountNumber());
			bankDetails.setAccountType(requestProfileDetails.getAccountType());
			bankDetails.setFullName(requestProfileDetails.getAccountName());
			bankDetails.setRoutingOrAbaNumber(requestProfileDetails.getAbaNumber());
			getServiceRegistry().getListenerBankDetailsService().saveORupdate(bankDetails);

			UserProfileDto response = new UserProfileDto();
			response.setAccountNumber(bankDetails.getAccountNumber());
			response.setAccountType(bankDetails.getAccountType());
			response.setFullName(bankDetails.getFullName());
			response.setRoutingOrAbaNumber(bankDetails.getRoutingOrAbaNumber());

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
}

//PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.builder()
//.setType(PaymentMethodCreateParams.Type.US_BANK_ACCOUNT)
//.setUsBankAccount(
//  PaymentMethodCreateParams.UsBankAccount.builder()
//      .setRoutingNumber("110000000")
//      .setAccountNumber("000123456789")
//      .setAccountHolderType(PaymentMethodCreateParams.UsBankAccount.AccountHolderType.INDIVIDUAL)
//      .build()
//)
//.setBillingDetails(
//  PaymentMethodCreateParams.BillingDetails.builder()
//      .setName("Test")
//      .build()
//)
//.build();
//LOGGER.info("before");
////Create the Payment Method
//PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);
//LOGGER.info("before paymentMethod{}"+paymentMethod.getId());
////Attach the Payment Method to the Customer
//PaymentMethod paymentMethodAttached = paymentMethod.attach(
//PaymentMethodAttachParams.builder().setCustomer(user.getStripeCustomerId()).build()
//);
//LOGGER.info("before paymentMethod{}"+paymentMethodAttached.getId());
//
//
//PaymentMethodVerifyParams params = PaymentMethodVerifyParams.builder()
//  .addAllAmount(amounts) // Amounts are micro-deposits (in cents)
//  .build();
//
////Verify the bank account (Payment Method)
//PaymentMethod verifiedBankAccount = PaymentMethod.retrieve(paymentMethodAttached.getId()).verify(params);
//
//LOGGER.info("Bank account with Payment Method ID {} verified successfully.", verifiedBankAccount.getId());
