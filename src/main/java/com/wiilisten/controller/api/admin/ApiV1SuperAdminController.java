package com.wiilisten.controller.api.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.AdminModule;
import com.wiilisten.entity.AdminModulePermission;
import com.wiilisten.entity.Administration;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.AdminModuleRequestDto;
import com.wiilisten.request.AdministrationAuthorityRequestDto;
import com.wiilisten.request.AuthorityUpdateDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.SubAdminRegisterRequestDto;
import com.wiilisten.response.AdminProfileResponseDto;
import com.wiilisten.response.ModuleResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.CommonServices;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN)
public class ApiV1SuperAdminController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1SuperAdminController.class);

	private String password = "SubAdmin@1234";

	//TODO:Rechange the default password
	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.ADD)
	public ResponseEntity<Object> administrationRegister(
			@RequestBody SubAdminRegisterRequestDto subAdminRegisterRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		
		try {
			Administration administrationEmail = getServiceRegistry().getAdministrationService()
					.findByEmailAndActiveTrue(subAdminRegisterRequestDto.getEmail());
			if (administrationEmail != null) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity
						.ok(getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_EXIST.getCode()));
			}
			Administration administrationContact = getServiceRegistry().getAdministrationService()
					.findByContactAndActiveTrueAndCountryCode(subAdminRegisterRequestDto.getContact(),
							subAdminRegisterRequestDto.getCountryCode());
			if (administrationContact != null) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.CONTACT_EXIST.getCode()));
			}
			final Administration administration = new Administration();
			BeanUtils.copyProperties(subAdminRegisterRequestDto, administration);
			administration.setRole(UserRoleEnum.SUBADMIN.name());
			administration.setActive(true);
			administration.setIsLoggedIn(false);
			administration.setPassword(CommonServices.convertToBcrypt(password));

			sendDefaultPassword(administration.getEmail(), administration.getName(), password);
			getServiceRegistry().getAdministrationService().saveORupdate(administration);

			List<AdministrationAuthorityRequestDto> authorityRequestDtos = subAdminRegisterRequestDto.getAuthorities();
			authorityRequestDtos.forEach(authority -> {
				AdminModulePermission adminModulePermission = new AdminModulePermission();
				BeanUtils.copyProperties(authority, adminModulePermission);
				AdminModule adminModule = getServiceRegistry().getAdminModuleService()
						.findByIdAndActiveTrue(authority.getModuleId());
				adminModulePermission.setAdminModule(adminModule);
				adminModulePermission.setAdministration(administration);
				adminModulePermission.setActive(true);
				getServiceRegistry().getAdminModulePermissionService().saveORupdate(adminModulePermission);
			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.SUB_ADMIN_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateAuthority(@RequestBody AuthorityUpdateDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Administration administration = getServiceRegistry().getAdministrationService()
					.findByIdAndActiveTrue(requestDto.getAdminId());
			if (administration == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_ADMIN_NOT_EXIST.getCode()));
			}
			if (!administration.getEmail().equals(requestDto.getEmail())) {
				Administration administrationEmail = getServiceRegistry().getAdministrationService()
						.findByEmailAndActiveTrue(requestDto.getEmail());
				if (administrationEmail != null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(
							getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.EMAIL_EXIST.getCode()));
				}
			}
			if (!administration.getContact().equals(requestDto.getContact())) {
				Administration administrationContact = getServiceRegistry().getAdministrationService()
						.findByContactAndActiveTrueAndCountryCode(requestDto.getContact(), requestDto.getCountryCode());
				if (administrationContact != null) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.CONTACT_EXIST.getCode()));
				}
			}
			BeanUtils.copyProperties(requestDto, administration, getCommonServices().getNullPropertyNames(requestDto));
			getServiceRegistry().getAdministrationService().saveORupdate(administration);

			List<AdministrationAuthorityRequestDto> authorities = requestDto.getAuthorities();
			if (!authorities.isEmpty()) {
				authorities.forEach(authority -> {
					AdminModulePermission adminModulePermission = getServiceRegistry().getAdminModulePermissionService()
							.findByAdministrationAndAdminModuleIdAndActiveTrue(administration, authority.getModuleId());
					if (adminModulePermission != null) {
						BeanUtils.copyProperties(authority, adminModulePermission,
								getCommonServices().getNullPropertyNames(authority));
						getServiceRegistry().getAdminModulePermissionService().saveORupdate(adminModulePermission);
					} else {
						AdminModulePermission modulePermission = new AdminModulePermission();
						BeanUtils.copyProperties(authority, modulePermission);
						modulePermission.setAdministration(administration);
						AdminModule adminModule = getServiceRegistry().getAdminModuleService()
								.findByIdAndActiveTrue(authority.getModuleId());
						modulePermission.setAdminModule(adminModule);
						modulePermission.setActive(true);
						getServiceRegistry().getAdminModulePermissionService().saveORupdate(modulePermission);
					}
				});
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.SUB_ADMIN_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getSubAdminList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<Administration> administrations = getServiceRegistry().getAdministrationService()
					.findByRoleAndActiveTrueOrderByIdDesc("SUBADMIN");
			if (administrations.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_ADMIN_NOT_EXIST.getCode()));
			}
			List<AdminProfileResponseDto> response = new ArrayList<>();
			administrations.forEach(admin -> {
				AdminProfileResponseDto dto = new AdminProfileResponseDto();
				BeanUtils.copyProperties(admin, dto);
				dto.setAdminName(admin.getName());
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

	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.FORWARD_SLASH)
	public ResponseEntity<Object> getSubAdmin(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Administration administration = getServiceRegistry().getAdministrationService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (administration == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_ADMIN_NOT_EXIST.getCode()));
			}

			List<AdminModulePermission> adminModulePermissions = getServiceRegistry().getAdminModulePermissionService()
					.findByAdministrationIdAndActiveTrueOrderByIdDesc(administration.getId());
			AuthorityUpdateDto response = new AuthorityUpdateDto();

			List<AdministrationAuthorityRequestDto> authorities = new ArrayList<>();
			adminModulePermissions.forEach(permission -> {
				AdministrationAuthorityRequestDto dto = new AdministrationAuthorityRequestDto();
				BeanUtils.copyProperties(permission, dto);
				dto.setModuleId(permission.getAdminModule().getId());
				dto.setModuleName(permission.getAdminModule().getName());
				authorities.add(dto);
			});
			BeanUtils.copyProperties(administration, response);
			response.setAdminId(administration.getId());
			response.setAuthorities(authorities);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteAuthority(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Administration administration = getServiceRegistry().getAdministrationService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (administration == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_ADMIN_NOT_EXIST.getCode()));
			}
			administration.setActive(false);
			getServiceRegistry().getAdministrationService().saveORupdate(administration);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.SUB_ADMIN_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.MODULE + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getModuleList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<AdminModule> adminModules = getServiceRegistry().getAdminModuleService()
					.findByActiveTrueOrderByIdDesc();
			if (adminModules.isEmpty()) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.MODULE_NOT_FOUND.getCode()));
			}
			List<ModuleResponseDto> response = new ArrayList<>();
			adminModules.forEach(module -> {
				ModuleResponseDto dto = new ModuleResponseDto();
				BeanUtils.copyProperties(module, dto);
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

	@PostMapping(ApplicationURIConstants.MODULE + ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addModule(@RequestBody AdminModuleRequestDto adminModuleRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			AdminModule adminModule = new AdminModule();
			BeanUtils.copyProperties(adminModuleRequestDto, adminModule);
			adminModule.setActive(true);
			getServiceRegistry().getAdminModuleService().saveORupdate(adminModule);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.MODULE_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	private void sendDefaultPassword(String email, String callName, String password) throws MessagingException {

		Map<String, Object> properties = new HashMap<>();
		properties.put("admin", callName);
		properties.put("password", password);
		properties.put("email", email);

		getServiceRegistry().getEmailService().sendEmailWithThymeLeaf(email, "WiiListen - Password",
				"registerSucess.html", properties);

	}

//	@PostMapping(ApplicationURIConstants.COUNTRY+ApplicationURIConstants.LIST)
//	public ResponseEntity<Object> getCountryList(){
//		
//		LOGGER.info(ApplicationConstants.ENTER_LABEL);
//		
//		try {
//			List<CountryResponseDto> countries=new ArrayList<>();
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
//		}
//	}

//	AdminModule adminModule = getServiceRegistry().getAdminModuleService()
//			.findByIdAndActiveTrue(requestDto.getId());
////	if (adminModule == null) {
////		LOGGER.info(ApplicationConstants.EXIT_LABEL);
////		return ResponseEntity.ok(getCommonServices()
////				.generateBadResponseWithMessageKey(ErrorDataEnum.MODULE_NOT_FOUND.getCode()));
////	}
//	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.AUTHORITY + ApplicationURIConstants.LIST)
//	public ResponseEntity<Object> getAuthroityList() {
//
//		LOGGER.info(ApplicationConstants.ENTER_LABEL);
//
//		try {
//
////			Page<AdminModulePermission> adminPermissions = getServiceRegistry().getAdminModulePermissionService()
////					.findByActiveTrue(pageable);
//			List<AdminModulePermission> adminPermissions = getServiceRegistry().getAdminModulePermissionService()
//					.findByActiveTrueOrderByIdDesc();
//			List<AdministrationAuthorityResponseDto> response = new ArrayList<>();
//			adminPermissions.forEach(permission -> {
//				AdministrationAuthorityResponseDto administrationAuthorityResponseDto = new AdministrationAuthorityResponseDto();
//				BeanUtils.copyProperties(permission.getAdministration(), administrationAuthorityResponseDto);
//				BeanUtils.copyProperties(permission, administrationAuthorityResponseDto);
////				administrationAuthorityResponseDto.setAdminEmail(admin.getAdministration().getEmail());
////				administrationAuthorityResponseDto.setAdminName(admin.getAdministration().getName());
////				administrationAuthorityResponseDto.setAdminModule(admin.getAdminModuleEnum().name());
//				administrationAuthorityResponseDto.setAdminId(permission.getAdministration().getId());
//				administrationAuthorityResponseDto.setModuleId(permission.getAdminModule().getId());
//				administrationAuthorityResponseDto.setModuleName(permission.getAdminModule().getName());
//				response.add(administrationAuthorityResponseDto);
//			});
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
//		}
//
//	}

//	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.AUTHORITY + ApplicationURIConstants.ADD)
//	public ResponseEntity<Object> addAuthority(@RequestBody AdministrationAuthorityRequestDto requestDto) {
//contact
//		LOGGER.info(ApplicationConstants.ENTER_LABEL);
//		try {
//			Administration administration = getServiceRegistry().getAdministrationService()
//					.findByIdAndActiveTrue(requestDto.getAdminId());
//			if (administration == null) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(
//						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.ADMIN_NOT_EXIST.getCode()));
//			}
//			// AdminModuleEnum adminModuleEnum =
//			// AdminModuleEnum.valueOf(requestDto.getAdminModule());
//			// LOGGER.info("adminModuleEnum is {}" + adminModuleEnum);
//			// checking that if Admin already set authority for that SubAdmin for particular
//			// module
////			AdminModulePermission modulePermission = getServiceRegistry().getAdminModulePermissionService()
////					.findByAdminModuleEnumAndAdministrationAndActiveTrue(adminModuleEnum, administration);
////			LOGGER.info("modulePermission is {}" + modulePermission);
//			AdminModule adminModule = getServiceRegistry().getAdminModuleService()
//					.findByIdAndActiveTrue(requestDto.getModuleId());
//			if (adminModule == null) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices()
//						.generateBadResponseWithMessageKey(ErrorDataEnum.MODULE_NOT_FOUND.getCode()));
//			}
//
//			AdminModulePermission modulePermission = getServiceRegistry().getAdminModulePermissionService()
//					.findByAdministrationAndAdminModuleIdAndActiveTrue(administration, requestDto.getModuleId());
//			if (modulePermission != null) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices()
//						.generateBadResponseWithMessageKey(SuccessMsgEnum.AUTHORITY_ALREADY_SET.getCode()));
//			}contact
//
//			AdminModulePermission adminModulePermission = new AdminModulePermission();
//			BeanUtils.copyProperties(requestDto, adminModulePermission);
//			adminModulePermission.setAdminModule(adminModule);
//			adminModulePermission.setAdministration(administration);
//			adminModulePermission.setActive(true);
//			getServiceRegistry().getAdminModulePermissionService().saveORupdate(adminModulePermission);
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices()
//					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.AUTHORITY_ALREADY_SET.getCode()));
//		} catch (Exception e) {
//			e.printStackTrace();
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
//		}
//	}

//	@PostMapping(ApplicationURIConstants.SUB_ADMIN + ApplicationURIConstants.AUTHORITY)
//	public ResponseEntity<Object> getParticularAuthority(@RequestBody IdRequestDto idRequestDto) {
//
//		LOGGER.infohttps://www.figma.com/file/QeY7tBwbpCcDr6QjOjKAS6/WiiListen--final?type=design&node-id=3-46&mode=design(ApplicationConstants.ENTER_LABEL);
//
//		try {
//			AdminModulePermission adminModulePermission = getServiceRegistry().getAdminModulePermissionService()
//					.findByIdAndActiveTrue(idRequestDto.getId());
//			if (adminModulePermission == null) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(
//						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
//			}
//			AdministrationAuthorityResponseDto response = new AdministrationAuthorityResponseDto();
//
//			BeanUtils.copyProperties(adminModulePermission, response);
//			response.setAdminEmail(adminModulePermission.getAdministration().getEmail());
//			response.setAdminName(adminModulePermission.getAdministration().getName());
//			response.setAdminModule(adminModulePermission.getAdminModuleEnum().name());
//			response.setAdminId(adminModulePermission.getAdministration().getId());
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
//		} catch (Exception e) {https://www.figma.com/file/QeY7tBwbpCcDr6QjOjKAS6/WiiListen--final?type=design&node-id=3-46&mode=design
//			e.printStackTrace();
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
//		}
//
//	}

}
