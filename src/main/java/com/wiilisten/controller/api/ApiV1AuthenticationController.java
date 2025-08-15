package com.wiilisten.controller.api;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import com.wiilisten.entity.*;
import com.wiilisten.response.PlanPurchaseDetailResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.OtpReasonEnum;
import com.wiilisten.enums.OtpTypeEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.TwoStepVerificationStatusEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.AdministrationAuthorityRequestDto;
import com.wiilisten.request.AvailabilityDTO;
import com.wiilisten.request.ChangePasswordRequestDto;
import com.wiilisten.request.LoginRequest;
import com.wiilisten.request.RegisterRequest;
import com.wiilisten.request.SendOtpDto;
import com.wiilisten.request.TwoStepVerificationDetailsDto;
import com.wiilisten.request.TypeRequestDto;
import com.wiilisten.request.UserDetail;
import com.wiilisten.request.UserOtpDTO;
import com.wiilisten.request.UserProfileDto;
import com.wiilisten.response.AdminDetailsResponseDto;
import com.wiilisten.response.LoginResponse;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationResponseConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;
import com.wiilisten.utils.CommonServices;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.AUTH)
public class ApiV1AuthenticationController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AuthenticationController.class);

	@Autowired
	ApiV1AuthenticationController apiV1Authentication;

	@GetMapping(ApplicationURIConstants.CHECK_USERNAME)
	public ResponseEntity<Object> checkUniqueUsername(
			@RequestParam(name = ApplicationConstants.USERNAME_LABEL) String userName) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			if (getCommonServices().checkListenerUniqueUsername(userName) == 1) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.USERNAME_EXISTS.getCode()));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.USERNAME_NOT_EXITS.getCode()));

		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LOGIN)
	public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getServiceRegistry().getUserService().findByEmailAndActiveTrue(loginRequest.getEmail());
			Administration administration = getServiceRegistry().getAdministrationService().findByEmailAndActiveTrue(loginRequest.getEmail());
//			if (user == null) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(
//						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_NOT_EXIST.getCode()));
//			}
			if (user != null) {
				if (!CommonServices.matchesWithBcrypt(loginRequest.getPassword(), user.getPassword())) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.BAD_CREDENTIALS_MSG.getCode()));
				}

				User checkBanUser = getServiceRegistry().getUserService()
						.findByEmailAndIsSuspendedTrueAndActiveTrue(loginRequest.getEmail());
				if (checkBanUser != null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.YOU_ARE_BANNED.getCode()));
				}
				ListenerProfile listener = getServiceRegistry().getListenerProfileService()
						.findByUserAndActiveTrue(user);

				SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(
								new UserDetail(user.getEmail(), user.getPassword(), user.getRole()), user.getPassword(),
								new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(user.getRole())))));

				String token = getTokenUtil().generateToken();

//				UPDATING USER DEVICE DETAILS
				user.setDeviceUUID(loginRequest.getDeviceUUID());
				user.setDeviceToken(loginRequest.getDeviceToken());
				user.setVoipToken(loginRequest.getVoipToken());
				user.setDeviceVersion(loginRequest.getVersion());
				user.setDeviceOs(loginRequest.getDeviceOs());
				user.setIsLoggedIn(true);
				user.setJwtToken(token);
//				update login status for listener if profile set
//				if(!user.getRole().equals(UserRoleEnum.LISTENER.getRole()))
//					user.setIsLoggedIn(true);
//				else{
//					user.setIsLoggedIn( (user.getIsProfileSet()) ? true : false );
//					
//					if(user.getIsProfileSet()){
//						ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByUserAndActiveTrue(user);
//						listener.setAppActiveStatus(true);
//						getServiceRegistry().getListenerProfileService().saveORupdate(listener);
//					}
//				}

				if (user.getRole().equals(UserRoleEnum.LISTENER.getRole()) && user.getIsProfileSet()) {
					user.setNotificationStatus(true);

					listener.setAppActiveStatus(true);
					getServiceRegistry().getListenerProfileService().saveORupdate(listener);
				} else if (user.getRole().equals(UserRoleEnum.CALLER.getRole()) && user.getIsProfileSet())
					user.setNotificationStatus(true);
//				else if (user.getRole().equals(UserRoleEnum.ADMIN.getRole()) && user.getIsProfileSet())
//					user.setNotificationStatus(true);

				getServiceRegistry().getUserService().saveORupdate(user);

				LoginResponse response = new LoginResponse();
				response.setToken(token);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKeyAndData(
						SuccessMsgEnum.LOGIN_SUCCESS_MESSAGE.getCode(), response));
			} else if (administration != null) {
				if (!CommonServices.matchesWithBcrypt(loginRequest.getPassword(), administration.getPassword())) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.BAD_CREDENTIALS_MSG.getCode()));
				}
//				Administration administration = getServiceRegistry().getAdministrationService()
//						.findByEmailAndActiveTrue(loginRequest.getEmail());
				SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(
								new UserDetail(administration.getEmail(), administration.getPassword(),
										administration.getRole()),
								administration.getPassword(),
								new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(administration.getRole())))));

				administration.setIsLoggedIn(true);
				getServiceRegistry().getAdministrationService().saveORupdate(administration);

				//send otp if two fact auth enable
				if (administration.getTwoFactorEnabled() && (administration.getRole().equals(ApplicationConstants.SUBADMIN) || administration.getRole().equals(ApplicationConstants.ADMIN))) {
					apiV1Authentication.processForgotPasswordOtp(administration.getEmail().trim());
				}

				String token = getTokenUtil().generateToken();
//				LoginResponse response = new LoginResponse();				
				AdminDetailsResponseDto response = new AdminDetailsResponseDto();
				BeanUtils.copyProperties(administration, response);
				List<AdminModulePermission> adminModulePermissions = getServiceRegistry()
						.getAdminModulePermissionService()
						.findByAdministrationAndActiveTrueOrderByIdDesc(administration);
				if (administration.getRole().equals(ApplicationConstants.SUBADMIN)) {
					List<AdministrationAuthorityRequestDto> authorities = new ArrayList<>();
					adminModulePermissions.forEach(permission -> {
						AdministrationAuthorityRequestDto dto = new AdministrationAuthorityRequestDto();
						BeanUtils.copyProperties(permission, dto);
						dto.setModuleId(permission.getAdminModule().getId());
						dto.setModuleName(permission.getAdminModule().getName());
						authorities.add(dto);
					});
					response.setAuthorities(authorities);
				}
				response.setToken(token);

				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKeyAndData(
						SuccessMsgEnum.LOGIN_SUCCESS_MESSAGE.getCode(), response));
			} else {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_NOT_EXIST.getCode()));
			}
		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.REGISTER)
	public ResponseEntity<Object> register(@RequestBody RegisterRequest registerRequest) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getServiceRegistry().getUserService().findByEmailAndActiveTrue(registerRequest.getEmail());
			if (user != null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity
						.ok(getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_EXIST.getCode()));
			}

			String role = registerRequest.getRole();
//			if (role.equals(UserRoleEnum.LISTENER.getRole())) {
//				if (getCommonServices().checkListenerUniqueUsername(registerRequest.getUsername()) == 1) {
//					LOGGER.info(ApplicationConstants.EXIT_LABEL);
//					return ResponseEntity.ok(getCommonServices()
//							.generateBadResponseWithMessageKey(ErrorDataEnum.USERNAME_EXISTS.getCode()));
//				}
//			}

//			REFERRAL CODE CHECK FOR CALLER
			if (!ApplicationUtils.isEmpty(registerRequest.getReferralCode())) {

				User referralUser = getServiceRegistry().getUserService()
						.findByReferralCodeAndActiveTrue(registerRequest.getReferralCode());
				if (referralUser == null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_REFERRAL_CODE.getCode()));
				}
			}

			user = new User();
			BeanUtils.copyProperties(registerRequest, user);
			user.setCallName(registerRequest.getCallName());
			user.setPassword(CommonServices.convertToBcrypt(registerRequest.getPassword()));
			user.setReferralCode(getCommonServices().generateReferralCode()); // UNIQUE 6 DIGIT REFERRAL CODE
			user.setProfilePicture(ApplicationConstants.DEFAULT_IMAGE);
			user.setVoipToken(registerRequest.getVoipToken());
			user = getServiceRegistry().getUserService().saveORupdate(user);

			if (role.equals(UserRoleEnum.LISTENER.getRole())) {
				ListenerProfile listenerProfile = new ListenerProfile();
				listenerProfile.setUser(user);
				//listenerProfile.setUserName(registerRequest.getUsername());
				getServiceRegistry().getListenerProfileService().saveORupdate(listenerProfile);

			}
			if (role.equals(UserRoleEnum.CALLER.getRole())) {
				CallerProfile caller = new CallerProfile();
				caller.setUser(user);

				if (!ApplicationUtils.isEmpty(registerRequest.getReferralCode()))
					caller.setReferralCode(registerRequest.getReferralCode());

				getServiceRegistry().getCallerProfileService().saveORupdate(caller);
			}

//			GENERATING TOKEN
			SecurityContextHolder.getContext()
					.setAuthentication(new UsernamePasswordAuthenticationToken(
							new UserDetail(user.getEmail(), user.getPassword(), user.getRole()), user.getPassword(),
							new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(user.getRole())))));
			String token = getTokenUtil().generateToken();

			user.setJwtToken(token);
//			GENERATING OTP
			String otpValue = getCommonServices().generate4DigitOtp();
			OtpHistory otp = new OtpHistory();
			otp.setEmail(user.getEmail());
			otp.setOtp(otpValue);
			otp.setExpiryDateTime(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5));
			otp.setReason(OtpReasonEnum.EMAIL_OTP_FOR_SIGNUP.getValue());
			otp.setType(OtpTypeEnum.EMAIL.getType());
			otp.setUser(user);
			getServiceRegistry().getOtpHistoryService().saveORupdate(otp);

//			SENDING EMAIL
			sendEmailVerificationOtpEmail(user.getEmail(), user.getCallName(), otpValue);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKeyAndData(SuccessMsgEnum.USER_ADDED.getCode(), token));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

//	// remove from permitAll and add the role as admin
//	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.REGISTER)
//	public ResponseEntity<Object> administrationRegister(
//			@RequestBody SubAdminRegisterRequestDto subAdminRegisterRequestDto) {
//
//		LOGGER.info(ApplicationConstants.ENTER_LABEL);
//
//		try {
//			Administration administrationEmail = getServiceRegistry().getAdministrationService()
//					.findByEmailAndActiveTrue(subAdminRegisterRequestDto.getEmail());
//			if (administrationEmail != null) {
//
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity
//						.ok(getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_EXIST.getCode()));
//			}
//			Administration administrationContact = getServiceRegistry().getAdministrationService()
//					.findByContactAndActiveTrue(subAdminRegisterRequestDto.getContact());
//			if (administrationContact != null) {
//
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(
//						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.CONTACT_EXIST.getCode()));
//			}
//			Administration administration = new Administration();
//			BeanUtils.copyProperties(subAdminRegisterRequestDto, administration);
//			administration.setRole(UserRoleEnum.SUBADMIN.name());
//			administration.setActive(true);
//			administration.setPassword(CommonServices.convertToBcrypt(subAdminRegisterRequestDto.getPassword()));
//
//			administration = getServiceRegistry().getAdministrationService().saveORupdate(administration);
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices()
//					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.SUB_ADMIN_ADDED_SUCCESSFULLY.getCode()));
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
//		}
//
//	}

	@PostMapping(ApplicationURIConstants.VERIFY_OTP)
	public ResponseEntity<Object> verifyOTP(@RequestBody final UserOtpDTO otpRequest) {

		LOGGER.info("=== ENTER: verifyOtp ===");
		LOGGER.info("Received OTP verification request for email: {}", otpRequest.getEmail());

		try {
			// Fetch OTP history
			OtpHistory otpHistory = getServiceRegistry().getOtpHistoryService()
					.findByEmailAndOtpAndActiveTrue(otpRequest.getEmail(), otpRequest.getOtp());

			if (otpHistory == null) {
				LOGGER.info("OTP not found or inactive for email: {}", otpRequest.getEmail());
				LOGGER.info("=== EXIT: verifyOtp ===");
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_OTP.getCode()));
			}

			// Check expiry
			if (LocalDateTime.now(ZoneOffset.UTC).isAfter(otpHistory.getExpiryDateTime())) {
				LOGGER.info("OTP expired for email: {}", otpRequest.getEmail());

				otpHistory.setIsExpired(true);
				otpHistory.setIsUtilized(false);
				otpHistory.setActive(false);
				getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);

				LOGGER.info("=== EXIT: verifyOtp ===");
				return ResponseEntity.ok(getCommonServices().generateResponseWithCodeAndMessage(
						ApplicationResponseConstants.OTP_EXPIRED, ErrorDataEnum.OTP_EXPIRED.getCode()));
			}

			// Fetch associated accounts
			Administration administration = getServiceRegistry().getAdministrationService()
					.findByEmailAndActiveTrue(otpRequest.getEmail());
			User currentUser = getServiceRegistry().getUserService()
					.findByEmailAndActiveTrue(otpRequest.getEmail());

			if (administration != null || currentUser != null) {

				if (administration != null &&
						(ApplicationConstants.SUBADMIN.equals(administration.getRole()) ||
								ApplicationConstants.ADMIN.equals(administration.getRole()))) {

					LOGGER.info("OTP verification for Admin/Subadmin: {}", administration.getEmail());

					if (OtpReasonEnum.EMAIL_OTP_FOR_SIGNUP.getValue().equals(otpHistory.getReason())) {
						if (otpHistory.getOtp().equals(otpRequest.getOtp())) {
							administration.setIsLoggedIn(true);
							LOGGER.info("Admin login successful for email: {}", administration.getEmail());
						} else {
							LOGGER.info("Invalid OTP for admin email: {}", administration.getEmail());
							return ResponseEntity.ok(getCommonServices().generateResponseWithCodeAndMessage(
									ApplicationResponseConstants.INVALID_OTP, ErrorDataEnum.INVALID_OTP.getCode()));
						}
					}

				} else {
					LOGGER.info("OTP verification for User: {}", otpRequest.getEmail());

					if (OtpReasonEnum.EMAIL_OTP_FOR_SIGNUP.getValue().equals(otpHistory.getReason())) {
						User user = otpHistory.getUser();

						if (otpHistory.getOtp().equals(otpRequest.getOtp())) {
							user.setIsEmailVerified(true);

							if (UserRoleEnum.CALLER.getRole().equals(user.getRole())) {
								user.setIsProfileSet(true);
								user.setNotificationStatus(true);
								user.setIsLoggedIn(true);
							} else if (UserRoleEnum.LISTENER.getRole().equals(user.getRole())) {
								user.setIsLoggedIn(true);
							}

							getServiceRegistry().getUserService().saveORupdate(user);
							LOGGER.info("User login successful for email: {}", user.getEmail());
						} else {
							LOGGER.info("Invalid OTP for user email: {}", otpRequest.getEmail());
							return ResponseEntity.ok(getCommonServices().generateResponseWithCodeAndMessage(
									ApplicationResponseConstants.INVALID_OTP, ErrorDataEnum.INVALID_OTP.getCode()));
						}
					}
				}

				// Mark OTP as used and expired
				otpHistory.setIsUtilized(true);
				otpHistory.setActive(false);
				otpHistory.setIsExpired(true);
				getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);

				LOGGER.info("OTP successfully verified for email: {}", otpRequest.getEmail());
				LOGGER.info("=== EXIT: verifyOtp ===");
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.OTP_VERIFIED.getCode()));
			}

			LOGGER.error("No matching user or admin found for email: {}", otpRequest.getEmail());
			return ResponseEntity.ok(getCommonServices().generateResponseWithCodeAndMessage(
					ApplicationResponseConstants.NO_DATA_FOUND, ErrorDataEnum.NO_DATA_FOUND.getCode()));

		} catch (Exception e) {
			LOGGER.error("Exception while verifying OTP for email: {}", otpRequest.getEmail(), e);
			LOGGER.info("=== EXIT: verifyOtp ===");
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}


	@PostMapping(ApplicationURIConstants.SEND_OTP)
	public ResponseEntity<Object> sendOtp(@RequestBody final SendOtpDto otpRequest) throws MessagingException {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			String email = otpRequest.getEmail();
			User user = getServiceRegistry().getUserService().findByEmailAndActiveTrue(email);
			Administration administration = getServiceRegistry().getAdministrationService().findByEmailAndActiveTrue(email);
			if (user == null && administration == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_NOT_EXIST.getCode()));
			}

			OtpHistory otpHistory = getServiceRegistry().getOtpHistoryService().findByEmailAndActiveTrue(email);
			if (otpHistory != null) {
				otpHistory.setIsExpired(true);
				otpHistory.setActive(false);
				getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);
			}

			String otpValue = getCommonServices().generate4DigitOtp();
			otpHistory = new OtpHistory();
			otpHistory.setEmail(email);
			otpHistory.setOtp(otpValue);
			otpHistory.setExpiryDateTime(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5));
			otpHistory.setReason(OtpReasonEnum.EMAIL_OTP_FOR_SIGNUP.getValue());
			otpHistory.setType(OtpTypeEnum.EMAIL.getType());
			if (user != null) otpHistory.setUser(user);

			getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);

			String name = "";
			if (user != null && !user.getCallName().trim().isEmpty()) {
				name = user.getCallName();
			} else if (administration != null && !administration.getName().trim().isEmpty()) {
				name = administration.getName();
			}

//			SENDING EMAIL
			sendEmailVerificationOtpEmail(email, name, otpValue);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.OTP_SENT_EMAIL_SUCCESS.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.PROFILEBYID)
	public ResponseEntity<Object> userProfileById(@PathVariable Long userId) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getServiceRegistry().getUserService().findOne(userId);
			if (user == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_USER.getCode()));
			}

			UserProfileDto response = new UserProfileDto();
			BeanUtils.copyProperties(user, response);

			if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
				ListenerProfile listener = getServiceRegistry().getListenerProfileService()
						.findByUserAndActiveTrue(user);
				if (listener != null)
					response.setProfileId(listener.getId());
				if (listener.getIsEligibleForPremiumCallSearch() != null
						&& listener.getIsEligibleForPremiumCallSearch()) {
					BeanUtils.copyProperties(listener, response);

					List<Long> languages = listener.getLanguages().stream().map(language -> language.getId()).toList();
					response.setLanguages(languages);
				} else
					BeanUtils.copyProperties(listener, response, "gender", "education", "dateOfBirth", "location");

				List<AvailabilityDTO> dutyDetailsList = getCommonServices()
						.generateResponseForListenerAvailability(user);
				if (dutyDetailsList != null)
					response.setAvailability(dutyDetailsList);

				ListenerBankDetails bankDetails = getServiceRegistry().getListenerBankDetailsService()
						.findByUserAndActiveTrue(user);
				if (bankDetails != null) {
					BeanUtils.copyProperties(bankDetails, response);
				}
			}
			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
				if (caller != null)
					response.setProfileId(caller.getId());
			}
			response.setId(user.getId());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.PROFILE)
	public ResponseEntity<Object> userProfile() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			if (user == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_USER.getCode()));
			}
			UserProfileDto response = new UserProfileDto();


			List<UserSubscription> userSubscriptions = getServiceRegistry()
					.getUserSubscriptionService()
					.findAllByUserAndActiveTrue(user);

			if (userSubscriptions != null && !userSubscriptions.isEmpty()) {
				// Latest first
				List<PlanPurchaseDetailResponseDto> planDetails = new ArrayList<>();
				for (UserSubscription sub : userSubscriptions) {
					if (sub.getSubscription() != null && sub.getCreatedAt() != null) {
						PlanPurchaseDetailResponseDto planPurchaseDetailResponseDto = new PlanPurchaseDetailResponseDto(
								sub.getCreatedAt(),
								sub.getSubscription().getDurationInDays(),
								sub.getType()
						);
						planDetails.add(planPurchaseDetailResponseDto);
					}
				}
				planDetails.sort(Comparator.comparing(PlanPurchaseDetailResponseDto::getPurchaseDate).reversed());

				response.setPurchasedPlansDetail(planDetails);
			}

			BeanUtils.copyProperties(user, response);
			if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
				ListenerProfile listener = getServiceRegistry().getListenerProfileService()
						.findByUserAndActiveTrue(user);
				if (listener != null) {
					response.setProfileId(listener.getId());
					response.setIsAdvertisementActive(listener.getIsAdvertisementActive());
				}
					
				if (listener.getIsEligibleForPremiumCallSearch() != null
						&& listener.getIsEligibleForPremiumCallSearch()) {
					BeanUtils.copyProperties(listener, response);

					List<Long> languages = listener.getLanguages().stream().map(language -> language.getId()).toList();
					response.setLanguages(languages);
				} else
					BeanUtils.copyProperties(listener, response, "gender", "education", "dateOfBirth", "location");

				List<AvailabilityDTO> dutyDetailsList = getCommonServices()
						.generateResponseForListenerAvailability(user);
				if (dutyDetailsList != null)
					response.setAvailability(dutyDetailsList);

				ListenerBankDetails bankDetails = getServiceRegistry().getListenerBankDetailsService()
						.findByUserAndActiveTrue(user);
				if (bankDetails != null) {
					BeanUtils.copyProperties(bankDetails, response);
				}
			}
			if (user.getRole().equals(UserRoleEnum.CALLER.getRole())) {
				CallerProfile caller = getServiceRegistry().getCallerProfileService().findByUserAndActiveTrue(user);
				if (caller != null) {
					response.setProfileId(caller.getId());
					response.setSearchSubscriptionStatus(caller.getSearchSubscriptionStatus());
				}

			}
			response.setId(user.getId());
			response.setTimeZone(user.getTimeZone());
			response.setIsAccountSwittched(getCommonServices().isAccountSwitched(user));
			
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	private void sendEmailVerificationOtpEmail(String email, String callName, String otp) throws MessagingException {

		Map<String, Object> properties = new HashMap<>();
		properties.put("user", callName);
		properties.put("userOTP", otp);

		getServiceRegistry().getEmailService().sendEmailWithThymeLeaf(email, "WiiListen - OTP", "otpSentEmail.html",
				properties);

	}

	@PostMapping(ApplicationURIConstants.FORGOT_PASSWORD)
	public ResponseEntity<Object> sendForgotPasswordOtp(@RequestBody final SendOtpDto otpRequest) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			return processForgotPasswordOtp(otpRequest.getEmail());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	public ResponseEntity<Object> processForgotPasswordOtp(String email) throws MessagingException {
		// if email is exist in admin side so send otp
		User user = getServiceRegistry().getUserService().findByEmailAndActiveTrue(email);
		Administration administration = getServiceRegistry().getAdministrationService().findByEmailAndActiveTrue(email);

		if (administration == null && user == null) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(
					getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_NOT_EXIST.getCode()));
		}

		OtpHistory otpHistory = getServiceRegistry().getOtpHistoryService().findByEmailAndActiveTrue(email);
		if (otpHistory != null) {
			otpHistory.setIsExpired(true);
			otpHistory.setActive(false);
			getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);
		}

		String otpValue = getCommonServices().generate4DigitOtp();
		otpHistory = new OtpHistory();
		otpHistory.setEmail(email);
		otpHistory.setOtp(otpValue);
		otpHistory.setExpiryDateTime(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5));
		otpHistory.setReason(OtpReasonEnum.FORGOT_PASSWORD_OTP.getValue());
		otpHistory.setType(OtpTypeEnum.EMAIL.getType());
		otpHistory.setUser(user);
		getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);
		String name = "";
		if (user != null && !user.getCallName().trim().isEmpty()) {
			name = user.getCallName();
		} else if (administration != null && !administration.getName().trim().isEmpty()) {
			name = administration.getName();
		}
//			SENDING EMAIL
		sendEmailVerificationOtpEmail(email, name, otpValue);

		LOGGER.info(ApplicationConstants.EXIT_LABEL);
		return ResponseEntity.ok(getCommonServices()
				.generateSuccessResponseWithMessageKey(SuccessMsgEnum.OTP_SENT_EMAIL_SUCCESS.getCode()));
	}


	@PostMapping(ApplicationURIConstants.NEW_PASSWORD)
	public ResponseEntity<Object> newPassword(@RequestBody final ChangePasswordRequestDto requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			String email = requestDetails.getEmail();

			User user = getServiceRegistry().getUserService().findByEmailAndActiveTrue(email);
			if (user == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_NOT_EXIST.getCode()));
			}
			if (CommonServices.matchesWithBcrypt(requestDetails.getPassword(), user.getPassword())) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.NEW_OLD_PASSWORD_SAME.getCode()));

			}
			user.setPassword(CommonServices.convertToBcrypt(requestDetails.getPassword()));
			getServiceRegistry().getUserService().saveORupdate(user);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PASSWORD_CHANGED_SUCCESSFULLY.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.CHANGE_PASSWORD)
	public ResponseEntity<Object> changePassword(@RequestBody final ChangePasswordRequestDto requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			if (user != null) {
				if (!CommonServices.matchesWithBcrypt(requestDetails.getOldPassword(), user.getPassword())) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_OLD_PASSWORD.getCode()));
				}
				if (CommonServices.matchesWithBcrypt(requestDetails.getPassword(), user.getPassword())) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.NEW_OLD_PASSWORD_SAME.getCode()));

				}
				user.setPassword(CommonServices.convertToBcrypt(requestDetails.getPassword()));
				getServiceRegistry().getUserService().saveORupdate(user);
				
				if(user.getNotificationStatus() && user.getIsLoggedIn()) {
					AdministrativeNotification administrativeNotification = new AdministrativeNotification();
					Map<String, String> payload = new HashMap<>();

					administrativeNotification.setTitle(ApplicationConstants.PASSWORD_CHANGED);
					administrativeNotification.setContent(ApplicationConstants.PASSWORD_CHANGED_SUCCESSFULLY);
					administrativeNotification.setUsers(Collections.singletonList(user));
					administrativeNotification.setTags(ApplicationConstants.PASSWORD_CHANGED);
					getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);

					payload.put(ApplicationConstants.TITLE, ApplicationConstants.PASSWORD_CHANGED);
					payload.put(ApplicationConstants.BODY, ApplicationConstants.PASSWORD_CHANGED_SUCCESSFULLY);
					payload.put(ApplicationConstants.TAG, ApplicationConstants.PASSWORD_CHANGED);
					String receiverDeviceToken = user.getDeviceToken();
					if (receiverDeviceToken != null) {
						// Send push notification using FCM
						getServiceRegistry().getFcmService().sendPushNotification(receiverDeviceToken, payload);
					}
				}
				
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PASSWORD_CHANGED_SUCCESSFULLY.getCode()));
			}
			Administration administration = getLoggedInSubAdmin();
			if (administration != null) {
				if (!CommonServices.matchesWithBcrypt(requestDetails.getOldPassword(), administration.getPassword())) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_OLD_PASSWORD.getCode()));
				}
				if (CommonServices.matchesWithBcrypt(requestDetails.getPassword(), administration.getPassword())) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.NEW_OLD_PASSWORD_SAME.getCode()));

				}
				administration.setPassword(CommonServices.convertToBcrypt(requestDetails.getPassword()));
				getServiceRegistry().getAdministrationService().saveORupdate(administration);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PASSWORD_CHANGED_SUCCESSFULLY.getCode()));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.LOGOUT)
	public ResponseEntity<Object> logout() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			Administration admin = getLoggedInSubAdmin();
			if (user != null) {
				if (user.getRole().equals(UserRoleEnum.LISTENER.getRole())) {
					ListenerProfile listener = getServiceRegistry().getListenerProfileService()
							.findByUserAndActiveTrue(user);
					listener.setAppActiveStatus(false);
					getServiceRegistry().getListenerProfileService().saveORupdate(listener);
				}
				user.setIsLoggedIn(false);
				user.setNotificationStatus(false);
				getServiceRegistry().getUserService().saveORupdate(user);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.LOGOUT_SUCCESS_MESSAGE.getCode()));
			}
			if (admin != null) {
				admin.setIsLoggedIn(false);
				getServiceRegistry().getAdministrationService().saveORupdate(admin);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.LOGOUT_SUCCESS_MESSAGE.getCode()));
			}
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());

		} catch (Exception e) {
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.TWO_STEP_VERIFICATION + ApplicationURIConstants.STATUS
			+ ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> twoFactorVerificationStatusUpdate(@RequestBody TypeRequestDto typeRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User user = getLoggedInUser();
			user.setTwoStepVerificationStatus(typeRequestDto.getType());
			getServiceRegistry().getUserService().saveORupdate(user);

			if (typeRequestDto.getType().equalsIgnoreCase(ApplicationConstants.ENABLED)) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.TWO_STEP_VERIFICATION_ENABLE.getCode()));
			} else {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.TWO_STEP_VERIFICATION_DISABLE.getCode()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.TWO_STEP_VERIFICATION + ApplicationURIConstants.SEND_OTP)
	public ResponseEntity<Object> enabledTwoStepVerification(
			@RequestBody TwoStepVerificationDetailsDto requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			String countryCode = requestDetails.getCountryCode();
			String contactNumber = requestDetails.getContact();
			String concatedContact = String.join("-", countryCode, contactNumber);

//			User user = getServiceRegistry().getUserService()
//					.findByCountryCodeAndContactNumberAndActiveTrue(countryCode, contactNumber);
//			if (user != null) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices()
//						.generateSuccessResponseWithMessageKey(ErrorDataEnum.CONTACT_EXIST.getCode()));
//			}

//			expiring existing OTP
			OtpHistory otpHistory = getServiceRegistry().getOtpHistoryService()
					.findByContactAndActiveTrue(concatedContact);
			if (otpHistory != null) {
				otpHistory.setIsExpired(true);
				otpHistory.setActive(false);
				getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);
			}

//			user = getLoggedInUser();
//			user.setCountryCode(countryCode);
//			user.setContactNumber(contactNumber);
//			getServiceRegistry().getUserService().saveORupdate(user);

			User user = getLoggedInUser();
//			TODO: remove static 1234 OTP for production
//			String otp = getCommonServices().generate4DigitOtp();
			String otp = requestDetails.getOtp();
			otpHistory = new OtpHistory();
			otpHistory.setContact(concatedContact);
			otpHistory.setOtp("1234");
			otpHistory.setExpiryDateTime(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(5));
			otpHistory.setReason(OtpReasonEnum.ENABLE_TWO_STEP_VERIFICATION.getValue());
			otpHistory.setType(OtpTypeEnum.CONTACT.getType());
			otpHistory.setUser(user);
			getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);

//			TODO: Add Twilio integration to send OTP in SMS 

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.OTP_SENT_PHONE_SUCCESS.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.TWO_STEP_VERIFICATION + ApplicationURIConstants.VERIFY_OTP)
	public ResponseEntity<Object> verifyTwoStepVerification(@RequestBody TwoStepVerificationDetailsDto requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			String countryCode = requestDetails.getCountryCode();
			String contactNumber = requestDetails.getContact();
			String concatedContact = String.join("-", countryCode, contactNumber);
			String requestOtp = requestDetails.getOtp();

//			User user = getServiceRegistry().getUserService()
//					.findByCountryCodeAndContactNumberAndActiveTrue(countryCode, contactNumber);
//			if (user != null) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices()
//						.generateSuccessResponseWithMessageKey(ErrorDataEnum.CONTACT_EXIST.getCode()));
//			}

			OtpHistory otpHistory = getServiceRegistry().getOtpHistoryService()
					.findByContactAndOtpAndActiveTrue(concatedContact, requestOtp);
			if (otpHistory == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity
						.ok(getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.INVALID_OTP.getCode()));
			}

//			TODO: Add Twilio integration to verify OTP

//			OTP EXPIRY CHECK
			if (LocalDateTime.now(ZoneOffset.UTC).isAfter(otpHistory.getExpiryDateTime())) {

				otpHistory.setIsExpired(true);
				otpHistory.setIsUtilized(false);
				otpHistory.setActive(false);
				getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseWithCodeAndMessage(
						ApplicationResponseConstants.OTP_EXPIRED, ErrorDataEnum.OTP_EXPIRED.getCode()));
			}

//			UPDATE USER PROFILE
			User user = getLoggedInUser();
			user = otpHistory.getUser();
			user.setCountryCode(countryCode);
			user.setContactNumber(contactNumber);
			user.setTwoStepVerificationStatus(TwoStepVerificationStatusEnum.ENABLED.getValue());
			getServiceRegistry().getUserService().saveORupdate(user);

//			EXPIRING OTP
			otpHistory.setIsUtilized(true);
			otpHistory.setActive(false);
			otpHistory.setIsExpired(true); // expiring OTP after usage
			getServiceRegistry().getOtpHistoryService().saveORupdate(otpHistory);
			
			AdministrativeNotification administrativeNotification = new AdministrativeNotification();
			Map<String, String> payload = new HashMap<>();

			administrativeNotification.setTitle(ApplicationConstants.TWO_FACTOR_VERIFICATION);
			administrativeNotification.setContent("Two step verification applied successfully");
			administrativeNotification.setUsers(Collections.singletonList(user));
			administrativeNotification.setTags(ApplicationConstants.TWO_FACTOR_VERIFICATION);
			administrativeNotification.setActive(true);

			getServiceRegistry().getAdministrativeNotificationService().saveORupdate(administrativeNotification);
			payload.put(ApplicationConstants.TITLE, ApplicationConstants.TWO_FACTOR_VERIFICATION);
			payload.put(ApplicationConstants.BODY, "Two step verification applied successfully");
			payload.put(ApplicationConstants.TAG, ApplicationConstants.TWO_FACTOR_VERIFICATION);
			String receiverDeviceToken = user.getDeviceToken();
			if (receiverDeviceToken != null) {
				// Send push notification using FCM
				getServiceRegistry().getFcmService().sendPushNotification(receiverDeviceToken, payload);
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(
					getCommonServices().generateSuccessResponseWithMessageKey(SuccessMsgEnum.OTP_VERIFIED.getCode()));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
