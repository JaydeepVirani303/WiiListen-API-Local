package com.wiilisten.controller.api.admin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.EarningHistory;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.request.EarningHistoryRequestDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.response.EarningResponseDto;
import com.wiilisten.response.ListenerResponseDto;
import com.wiilisten.response.ManageEarningResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.MANAGE_EARNING)
public class ApiV1AdminEarningController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminEarningController.class);

	@PersistenceContext
	private EntityManager entityManager;

	@PostMapping(ApplicationURIConstants.FILTER + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getFilterEarningHistory(
			@RequestBody EarningHistoryRequestDto earningHistoryRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<ListenerResponseDto> response = new ArrayList<>();
			List<ListenerProfile> listenerProfiles = new ArrayList<>();
			if (earningHistoryRequestDto.getLocation() != null) {

				listenerProfiles = getServiceRegistry().getListenerProfileService()
						.findByLocationAndActiveTrueOrderByCreatedAtDesc(earningHistoryRequestDto.getLocation());
				if (listenerProfiles.isEmpty()) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
				}
				listenerProfiles.forEach(listener -> {
					ListenerResponseDto listenerProfileDTO = new ListenerResponseDto();
					BeanUtils.copyProperties(listener, listenerProfileDTO);
					BeanUtils.copyProperties(listener.getUser(), listenerProfileDTO);
					listenerProfileDTO.setUserId(listener.getUser().getId());
					listenerProfileDTO.setListnerId(listener.getId());
					response.add(listenerProfileDTO);
				});

			} else if (earningHistoryRequestDto.getLocation() == null) {

				listenerProfiles = getServiceRegistry().getListenerProfileService().findByActiveTrueOrderByIdDesc();

				if (listenerProfiles.isEmpty()) {
					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
				}
				listenerProfiles.forEach(listener -> {
					ListenerResponseDto listenerProfileDTO = new ListenerResponseDto();
					BeanUtils.copyProperties(listener, listenerProfileDTO);
					BeanUtils.copyProperties(listener.getUser(), listenerProfileDTO);
					listenerProfileDTO.setUserId(listener.getUser().getId());
					listenerProfileDTO.setListnerId(listener.getId());
					response.add(listenerProfileDTO);
				});
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.PREMIUM + ApplicationURIConstants.FILTER + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getPremiumFilterEarningHistory(
			@RequestBody EarningHistoryRequestDto earningHistoryRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			ManageEarningResponseDto response = new ManageEarningResponseDto();
			List<ListenerResponseDto> listenerResponseDtos = new ArrayList<>();
			StringBuilder listenerBuilder = new StringBuilder("SELECT lp FROM ListenerProfile lp ");
			List<String> listenerConditions = new ArrayList<>();
			List<Object> listenerParameters = new ArrayList<>();

			listenerConditions.add("lp.active = true");

			int paramIndex = 1;

			if (earningHistoryRequestDto.getLocation() != null) {
				listenerConditions.add("lp.location = ?" + paramIndex);
				listenerParameters.add(earningHistoryRequestDto.getLocation());
				paramIndex++;
			}
			if (earningHistoryRequestDto.getEducation() != null) {
				listenerConditions.add("lp.education = ?" + paramIndex);
				listenerParameters.add(earningHistoryRequestDto.getEducation());
				paramIndex++;
			}
			if (earningHistoryRequestDto.getGender() != null) {
				listenerConditions.add("lp.gender = ?" + paramIndex);
				listenerParameters.add(earningHistoryRequestDto.getGender());
				paramIndex++;
			}
			if (earningHistoryRequestDto.getLanguage() != null) {
				listenerConditions
						.add("EXISTS (SELECT lang FROM lp.languages lang WHERE lang.name = ?" + paramIndex + ")");
				listenerParameters.add(earningHistoryRequestDto.getLanguage());
				paramIndex++;
			}
			if (earningHistoryRequestDto.getAge() != null) {
				LocalDate today = LocalDate.now();
				LocalDate birthDate = today.minusYears(earningHistoryRequestDto.getAge());
				listenerConditions.add("lp.dateOfBirth BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
				listenerParameters.add(birthDate.withDayOfYear(1));
				listenerParameters.add(birthDate.withDayOfYear(birthDate.lengthOfYear()));
				paramIndex += 2;
			}
			
			if (earningHistoryRequestDto.getStartDate() != null && earningHistoryRequestDto.getEndDate() != null) {
				listenerConditions.add("lp.createdAt BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
				listenerParameters.add(earningHistoryRequestDto.getStartDate());
				listenerParameters.add(earningHistoryRequestDto.getEndDate());
				paramIndex += 2;
			}

			if (!listenerConditions.isEmpty()) {
				listenerBuilder.append(" WHERE ").append(String.join(" AND ", listenerConditions));
			}
			
			TypedQuery<ListenerProfile> listenerQuery = entityManager.createQuery(listenerBuilder.toString(),
					ListenerProfile.class);
			for (int i = 0; i < listenerParameters.size(); i++) {
				listenerQuery.setParameter(i + 1, listenerParameters.get(i));
			}
			List<ListenerProfile> listenerProfiles = listenerQuery.getResultList();
			if(listenerProfiles.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
			}
			
			listenerProfiles.forEach(listener -> {
				ListenerResponseDto listenerProfileDTO = new ListenerResponseDto();
				BeanUtils.copyProperties(listener, listenerProfileDTO);
				BeanUtils.copyProperties(listener.getUser(), listenerProfileDTO);
				listenerProfileDTO.setUserId(listener.getUser().getId());
				listenerProfileDTO.setListnerId(listener.getId());
				listenerResponseDtos.add(listenerProfileDTO);
			});
			
				List<Long> ids = listenerProfiles.stream().map(lp -> lp.getUser().getId()).collect(Collectors.toList());
				List<EarningHistory> earningHistories = getServiceRegistry().getEarningHistoryService()
						.findByUserIdInAndActiveTrue(ids);
				Double totalEarning = earningHistories.stream().mapToDouble(EarningHistory::getAmount).sum();
				response.setTotalEarning(totalEarning);
		

			response.setListenerResponse(listenerResponseDtos);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.DETAILS)
	public ResponseEntity<Object> getListenerDetails(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			User user = getServiceRegistry().getUserService()
					.findByIdAndActiveTrueAndIsSuspendedFalse(idRequestDto.getId());
			List<EarningHistory> earningHistories = getServiceRegistry().getEarningHistoryService()
					.findByActiveTrueAndUserOrderByCreatedAtDesc(user);
			if (earningHistories.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.NO_EARNING_FOUND.getCode()));
			}
			List<EarningResponseDto> response = new ArrayList<>();
			earningHistories.forEach(earning -> {
				EarningResponseDto responseDto = new EarningResponseDto();
				ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
						.findByUser(earning.getUser());
				BeanUtils.copyProperties(listenerProfile, responseDto);
				BeanUtils.copyProperties(earning, responseDto);
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

//	public List<EarningResponseDto> convertEarningHistoryIntoResponse(List<EarningHistory> earningHistories,
//			List<EarningResponseDto> response) {
//		earningHistories.forEach(earning -> {
//			EarningResponseDto responseDto = new EarningResponseDto();
//			BeanUtils.copyProperties(earning, responseDto);
//			BeanUtils.copyProperties(earning.getUser(), responseDto);
//			ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
//					.findByUser(earning.getUser());
//			BeanUtils.copyProperties(listenerProfile, responseDto);
//			response.add(responseDto);
//		});
//		return response;
//	}

//	try {
//		List<EarningResponseDto> response = new ArrayList<>();
//		if (earningHistoryRequestDto.getLocation() != null) {
//			List<EarningHistory> earningHistories = getServiceRegistry().getEarningHistoryService()
//					.findByListenerProfileLocation(earningHistoryRequestDto.getLocation());
//
//			if (earningHistories.isEmpty()) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices()
//						.generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
//			}
////			earingHistories.forEach(earning -> {
////				EarningResponseDto responseDto = new EarningResponseDto();
////				BeanUtils.copyProperties(earning, responseDto);
////				ListenerProfile listenerProfile = getServiceRegistry().getListenerProfileService()
////						.findByUser(earning.getUser());
////				responseDto.setListenerName(listenerProfile.getUserName());
////				response.add(responseDto);
////			});
//			convertEarningHistoryIntoResponse(earningHistories, response);
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
//		}
//		if (earningHistoryRequestDto.getGender() != null) {
//			List<EarningHistory> earningHistories = getServiceRegistry().getEarningHistoryService()
//					.findByListenerProfileGender(earningHistoryRequestDto.getGender());
//			if (earningHistories.isEmpty()) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices()
//						.generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
//			}
//
//			convertEarningHistoryIntoResponse(earningHistories, response);
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
//		}
//		if (earningHistoryRequestDto.getEducation() != null) {
//			List<EarningHistory> earningHistories = getServiceRegistry().getEarningHistoryService()
//					.findByListenerProfileEducation(earningHistoryRequestDto.getEducation());
//			if (earningHistories.isEmpty()) {
//				LOGGER.info(ApplicationConstants.EXIT_LABEL);
//				return ResponseEntity.ok(getCommonServices()
//						.generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
//			}
//
//			convertEarningHistoryIntoResponse(earningHistories, response);
//
//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
//		}
//	} catch (Exception e) {
//		e.printStackTrace();
//		LOGGER.info(ApplicationConstants.EXIT_LABEL);
//		return ResponseEntity.ok(getCommonServices().generateFailureResponse());
//	}

//	StringBuilder earningBuilder = new StringBuilder(
//			"SELECT e FROM EarningHistory e JOIN e.user u JOIN ListenerProfile lp ON lp.user.id = u.id");
//	List<String> earningConditions = new ArrayList<>();
//	List<Object> earningParameters = new ArrayList<>();
//
//	earningConditions.add("e.active = true");
//	earningConditions.add("lp.active = true");
//
//	if (earningHistoryRequestDto.getLocation() != null) {
//		earningConditions.add("lp.location = ?1");
//		earningParameters.add(earningHistoryRequestDto.getLocation());
//
//	}
//	if (earningHistoryRequestDto.getEducation() != null) {
//		earningConditions.add("lp.education = ?2");
//		earningParameters.add(earningHistoryRequestDto.getEducation());
//
//	}
//	if (earningHistoryRequestDto.getGender() != null) {
//		earningConditions.add("lp.gender = ?3");
//		earningParameters.add(earningHistoryRequestDto.getGender());
//
//	}
//	if (earningHistoryRequestDto.getLanguage() != null) {
//		earningConditions.add("EXISTS (SELECT lang FROM lp.languages lang WHERE lang.name = ?4)");
//		earningParameters.add(earningHistoryRequestDto.getLanguage());
//
//	}
//	if (earningHistoryRequestDto.getAge() != null) {
//		LocalDate today = LocalDate.now();
//		LocalDate birthDate = today.minusYears(earningHistoryRequestDto.getAge());
//		earningConditions.add("lp.dateOfBirth BETWEEN ?5 AND ?6");
//		earningParameters.add(birthDate.withDayOfYear(1));
//		earningParameters.add(birthDate.withDayOfYear(birthDate.lengthOfYear()));
//	}
//	if (!earningConditions.isEmpty()) {
//		earningBuilder.append(" WHERE ").append(String.join(" AND ", earningConditions));
//	}
//
//	TypedQuery<EarningHistory> earningQuery = entityManager.createQuery(earningBuilder.toString(),
//			EarningHistory.class);
//	for (int i = 0; i < earningParameters.size(); i++) {
//		earningQuery.setParameter(i + 1, earningParameters.get(i));
//	}
//	List<EarningHistory> histories = earningQuery.getResultList();

}
