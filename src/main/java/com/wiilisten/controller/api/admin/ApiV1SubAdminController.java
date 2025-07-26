package com.wiilisten.controller.api.admin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.AdminModulePermission;
import com.wiilisten.entity.Administration;
import com.wiilisten.entity.CommissionRate;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.AdminProfileRequestDto;
import com.wiilisten.request.AdministrationAuthorityRequestDto;
import com.wiilisten.request.ChangePasswordRequestDto;
import com.wiilisten.request.CommissionRateRequestDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.response.AdminDetailsResponseDto;
import com.wiilisten.response.AdminProfileResponseDto;
import com.wiilisten.response.CommissionRateResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.CommonServices;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN)
public class ApiV1SubAdminController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1SubAdminController.class);

	@PostMapping()
	public ResponseEntity<Object> getAdminProfile(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Administration administration = getServiceRegistry().getAdministrationService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (administration == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.ADMIN_NOT_EXIST.getCode()));
			}
			AdminProfileResponseDto response = new AdminProfileResponseDto();
			BeanUtils.copyProperties(administration, response);
			response.setAdminName(administration.getName());
			
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateAdminProfile(@RequestBody AdminProfileRequestDto adminProfileRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Administration administration = getServiceRegistry().getAdministrationService()
					.findByIdAndActiveTrue(adminProfileRequestDto.getId());
			if (administration == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.ADMIN_NOT_EXIST.getCode()));
			}
			if (!administration.getEmail().equals(adminProfileRequestDto.getEmail())) {
				Administration administrationEmail = getServiceRegistry().getAdministrationService()
						.findByEmailAndActiveTrue(adminProfileRequestDto.getEmail());
				if (administrationEmail != null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(
							getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_EXIST.getCode()));
				}
			}
			if (!administration.getContact().equals(adminProfileRequestDto.getContact())) {
				Administration administrationContact = getServiceRegistry().getAdministrationService()
						.findByContactAndActiveTrueAndCountryCode(adminProfileRequestDto.getContact(),
								adminProfileRequestDto.getCountryCode());
				if (administrationContact != null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.CONTACT_EXIST.getCode()));
				}
			}
			BeanUtils.copyProperties(adminProfileRequestDto, administration,
					getCommonServices().getNullPropertyNames(adminProfileRequestDto));
			getServiceRegistry().getAdministrationService().saveORupdate(administration);

			AdminDetailsResponseDto response = new AdminDetailsResponseDto();
			BeanUtils.copyProperties(administration, response);
			List<AdminModulePermission> adminModulePermissions = getServiceRegistry().getAdminModulePermissionService()
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

			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKeyAndData(
					SuccessMsgEnum.PROFILE_UPDATE.getCode(), response));
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

			Administration administration = getLoggedInSubAdmin();
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

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.COMMISSION)
	public ResponseEntity<Object> getComission() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			CommissionRate commissionRate = getServiceRegistry().getCommissionRateService().findOne(1L);
			if (commissionRate == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.COMMISSION_RATE_NOT_EXIST.getCode()));
			}
			CommissionRateResponseDto response = new CommissionRateResponseDto();
			BeanUtils.copyProperties(commissionRate, response);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.COMMISSION + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateComission(@RequestBody CommissionRateRequestDto commissionRateRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			CommissionRate commissionRate = getServiceRegistry().getCommissionRateService().findOne(1L);
			commissionRate.setRate(commissionRateRequestDto.getRate());
			getServiceRegistry().getCommissionRateService().saveORupdate(commissionRate);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.COMMISSION_RATE_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
