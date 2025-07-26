
package com.wiilisten.controller.api.caller;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.User;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.request.CallerProfileDto;
import com.wiilisten.request.UserDetail;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.CALLER + ApplicationURIConstants.PROFILE)
public class ApiV1CallerProfileController extends BaseController{

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1CallerProfileController.class);
	
	@PostMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> editProfile(@RequestBody CallerProfileDto requestProfileDetails) {
		
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		
		try {
			
			User user = getLoggedInUser();
			
			Boolean isEmailUpdated = false;
			if(!user.getEmail().equals(requestProfileDetails.getEmail())){
				User tempUser = getServiceRegistry().getUserService().findByEmailAndActiveTrue(requestProfileDetails.getEmail());
				if(tempUser != null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_EXIST.getCode()));
				}
				isEmailUpdated = true;
			}
			
			user.setProfilePicture(requestProfileDetails.getProfilePicture());
			user.setCallName(requestProfileDetails.getCallName());
			if(isEmailUpdated)
				user.setEmail(requestProfileDetails.getEmail());

//			if(!ApplicationUtils.isEmpty(requestProfileDetails.getCountryCode()) &&
//					!ApplicationUtils.isEmpty(requestProfileDetails.getContactNumber())) {
//				user.setContactNumber(requestProfileDetails.getContactNumber());
//				user.setCountryCode(requestProfileDetails.getCountryCode());
//				
//			}
			getServiceRegistry().getUserService().saveORupdate(user);
			
			CallerProfileDto response = new CallerProfileDto();
			response.setProfilePicture(user.getProfilePicture());
			response.setCallName(user.getCallName());
			response.setEmail(user.getEmail());
//			response.setContactNumber(requestProfileDetails.getContactNumber());
//			response.setCountryCode(requestProfileDetails.getCountryCode());
			
			if(isEmailUpdated) {
				SecurityContextHolder.getContext().setAuthentication(
						new UsernamePasswordAuthenticationToken(new UserDetail(user.getEmail(),user.getPassword(),user.getRole()), null, new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(user.getRole()))))
						);
				String token = getTokenUtil().generateToken();
				response.setToken(token);
			}
			
			getCommonServices().sendProfileUpdatedNotification(user);
			
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
			
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
