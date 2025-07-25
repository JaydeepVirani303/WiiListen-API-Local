package com.wiilisten.controller.api.admin;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.entity.CallerProfile;
import com.wiilisten.entity.EarningHistory;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.request.CallManageRequestDto;
import com.wiilisten.request.EarningHistoryRequestDto;
import com.wiilisten.response.DashboardCountResponseDto;
import com.wiilisten.response.DashboardListenerResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.DASHBOARD)
public class ApiV1AdminDashboardController extends BaseController {

	@PersistenceContext
	private EntityManager entityManager;

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminDashboardController.class);

	@PostMapping(ApplicationURIConstants.FORWARD_SLASH)
	public ResponseEntity<Object> getDashBoardContent(@RequestBody CallManageRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			DashboardCountResponseDto response = new DashboardCountResponseDto();
			List<BookedCalls> totalCalls = new ArrayList<>();
			List<BookedCalls> onDemandResponse = new ArrayList<>();
			List<BookedCalls> scheduleResponse = new ArrayList<>();
			if (requestDto.getStartDate() != null && requestDto.getEndDate() != null) {
				totalCalls = getServiceRegistry().getBookedCallsService()
						.findByBookingDateTimeBetweenAndActiveTrueOrderByIdDesc(requestDto.getStartDate(),
								requestDto.getEndDate());

				onDemandResponse = getServiceRegistry().getBookedCallsService()
						.findByBookingDateTimeBetweenAndTypeAndActiveTrueOrderByIdDesc(requestDto.getStartDate(),
								requestDto.getEndDate(), ApplicationConstants.ON_DEMAND);

				scheduleResponse = getServiceRegistry().getBookedCallsService()
						.findByBookingDateTimeBetweenAndTypeAndActiveTrueOrderByIdDesc(requestDto.getStartDate(),
								requestDto.getEndDate(), ApplicationConstants.SCHEDULED);
			}
			if (requestDto.getStartDate() == null && requestDto.getEndDate() == null) {
				totalCalls = getServiceRegistry().getBookedCallsService().findByActiveTrue();
				onDemandResponse = getServiceRegistry().getBookedCallsService()
						.findByTypeAndActiveTrue(ApplicationConstants.ON_DEMAND);
				scheduleResponse = getServiceRegistry().getBookedCallsService()
						.findByTypeAndActiveTrue(ApplicationConstants.SCHEDULED);
			}
			response.setBoth(totalCalls.size());
			response.setOnDemand(onDemandResponse.size());
			response.setSchedule(scheduleResponse.size());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LISTENER + ApplicationURIConstants.COUNT)
	public ResponseEntity<Object> getListenerCount(@RequestBody EarningHistoryRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
	        List<ListenerProfile> totalListener = new ArrayList<>();
	        List<CallerProfile> callerProfiles = getServiceRegistry().getCallerProfileService()
	                .findByActiveTrueOrderByIdDesc();
	        DashboardListenerResponseDto response = new DashboardListenerResponseDto();
	        totalListener = getServiceRegistry().getListenerProfileService().findByActiveTrueOrderByIdDesc();
	        response.setListenerCount(totalListener.size());
	        response.setCallerCount(callerProfiles.size());

	        StringBuilder earningBuilder = new StringBuilder(
	                "SELECT e FROM EarningHistory e JOIN e.user u JOIN ListenerProfile lp ON lp.user.id = u.id");
	        StringBuilder premiumListenerBuilder = new StringBuilder("SELECT lp FROM ListenerProfile lp ");
	        List<String> premiumListenerConditions = new ArrayList<>();
	        List<Object> premiumListenerParameters = new ArrayList<>();
	        List<String> earningConditions = new ArrayList<>();
	        List<Object> earningParameters = new ArrayList<>();

	        earningConditions.add("e.active = true");
	        earningConditions.add("lp.active = true");
	        premiumListenerConditions.add("lp.active = true");
	        premiumListenerConditions.add("lp.isEligibleForPremiumCallSearch = true");

	        int paramIndex = 1; // Initialize the parameter index

	        if (requestDto.getLocation() != null) {
	            earningConditions.add("lp.location = ?" + paramIndex);
	            premiumListenerConditions.add("lp.location = ?" + paramIndex);
	            earningParameters.add(requestDto.getLocation());
	            premiumListenerParameters.add(requestDto.getLocation());
	            paramIndex++;
	        }
	        if (requestDto.getEducation() != null) {
	            earningConditions.add("lp.education = ?" + paramIndex);
	            premiumListenerConditions.add("lp.education = ?" + paramIndex);
	            earningParameters.add(requestDto.getEducation());
	            premiumListenerParameters.add(requestDto.getEducation());
	            paramIndex++;
	        }
	        if (requestDto.getGender() != null) {
	            earningConditions.add("lp.gender = ?" + paramIndex);
	            premiumListenerConditions.add("lp.gender = ?" + paramIndex);
	            earningParameters.add(requestDto.getGender());
	            premiumListenerParameters.add(requestDto.getGender());
	            paramIndex++;
	        }
	        if (requestDto.getLanguage() != null) {
	            earningConditions.add("EXISTS (SELECT lang FROM lp.languages lang WHERE lang.name = ?" + paramIndex + ")");
	            premiumListenerConditions.add("EXISTS (SELECT lang FROM lp.languages lang WHERE lang.name = ?" + paramIndex + ")");
	            earningParameters.add(requestDto.getLanguage());
	            premiumListenerParameters.add(requestDto.getLanguage());
	            paramIndex++;
	        }
	        if (requestDto.getAge() != null) {
	            LocalDate today = LocalDate.now();
	            LocalDate birthDate = today.minusYears(requestDto.getAge());
	            earningConditions.add("lp.dateOfBirth BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
	            premiumListenerConditions.add("lp.dateOfBirth BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
	            earningParameters.add(birthDate.withDayOfYear(1));
	            earningParameters.add(birthDate.withDayOfYear(birthDate.lengthOfYear()));
	            premiumListenerParameters.add(birthDate.withDayOfYear(1));
	            premiumListenerParameters.add(birthDate.withDayOfYear(birthDate.lengthOfYear()));
	            paramIndex += 2;
	        }

	        if (!earningConditions.isEmpty()) {
	            earningBuilder.append(" WHERE ").append(String.join(" AND ", earningConditions));
	        }
	        if (!premiumListenerConditions.isEmpty()) {
	            premiumListenerBuilder.append(" WHERE ").append(String.join(" AND ", premiumListenerConditions));
	        }

	        TypedQuery<EarningHistory> earningQuery = entityManager.createQuery(earningBuilder.toString(), EarningHistory.class);
	        for (int i = 0; i < earningParameters.size(); i++) {
	            earningQuery.setParameter(i + 1, earningParameters.get(i));
	        }

	        List<EarningHistory> histories = earningQuery.getResultList();
	        Double totalAmount = histories.stream().mapToDouble(EarningHistory::getAmount).sum();
	        response.setEarningCount(totalAmount);

	        TypedQuery<ListenerProfile> premiumListenerQuery = entityManager.createQuery(premiumListenerBuilder.toString(), ListenerProfile.class);
	        for (int i = 0; i < premiumListenerParameters.size(); i++) {
	            premiumListenerQuery.setParameter(i + 1, premiumListenerParameters.get(i));
	        }

	        response.setPremiumListenerCount(premiumListenerQuery.getResultList().size());

	        LOGGER.info(ApplicationConstants.EXIT_LABEL);
	        return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
	    } catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
	/*
	 * 
	 * if (requestDto.getLocation() != null) { LOGGER.info("inside location");
	 * listenerProfiles = getServiceRegistry().getListenerProfileService()
	 * .findByLocationAndActiveTrueOrderByCreatedAtDesc(requestDto.getLocation());
	 * earningHistories = getServiceRegistry().getEarningHistoryService()
	 * .findByListenerProfileLocation(requestDto.getLocation()); premiumListeners =
	 * getServiceRegistry().getListenerProfileService()
	 * .findByLocationAndActiveTrueAndIsEligibleForPremiumCallSearchTrueOrderByCreatedAtDesc(
	 * requestDto.getLocation()); response.setEarningCount(earningHistories.size());
	 * response.setListenerCount(listenerProfiles.size());
	 * response.setPremiumListenerCount(premiumListeners.size());
	 * LOGGER.info("size is {}" + earningHistories.size() + listenerProfiles.size()
	 * + premiumListeners.size()); } else if (requestDto.getAge() != null) {
	 * LOGGER.info("inside age"); LocalDate date =
	 * getCommonServices().calculateBirthDateForAge(requestDto.getAge()); LocalDate
	 * startDate = date.withDayOfYear(1); LocalDate endDate =
	 * date.withDayOfYear(date.lengthOfYear()); listenerProfiles =
	 * getServiceRegistry().getListenerProfileService()
	 * .findByDateOfBirthBetweenAndActiveTrueOrderByCreatedAtDesc(startDate,
	 * endDate); earningHistories = getServiceRegistry().getEarningHistoryService()
	 * .findEarningHistoryByListenerDateOfBirthBetween(startDate, endDate);
	 * 
	 * response.setEarningCount(earningHistories.size());
	 * response.setListenerCount(listenerProfiles.size()); }
	 * 
	 * else if (requestDto.getEducation() != null) {
	 * LOGGER.info("inside education"); listenerProfiles =
	 * getServiceRegistry().getListenerProfileService()
	 * .findByEducationAndActiveTrue(requestDto.getEducation()); earningHistories =
	 * getServiceRegistry().getEarningHistoryService()
	 * .findByListenerProfileEducation(requestDto.getEducation());
	 * response.setEarningCount(earningHistories.size());
	 * response.setListenerCount(listenerProfiles.size()); } else if
	 * (requestDto.getGender() != null) { LOGGER.info("inside gender");
	 * listenerProfiles = getServiceRegistry().getListenerProfileService()
	 * .findByGenderAndActiveTrue(requestDto.getGender());
	 * response.setEarningCount(earningHistories.size()); earningHistories =
	 * getServiceRegistry().getEarningHistoryService()
	 * .findByListenerProfileGender(requestDto.getGender());
	 * response.setEarningCount(earningHistories.size());
	 * response.setListenerCount(listenerProfiles.size()); } else if
	 * (requestDto.getLanguage() != null) { LOGGER.info("inside language");
	 * listenerProfiles = getServiceRegistry().getListenerProfileService()
	 * .findByLanguageNameForPremiumListener(requestDto.getLanguage());
	 * earningHistories = getServiceRegistry().getEarningHistoryService()
	 * .findByListenerProfileLanguage(requestDto.getLanguage());
	 * 
	 * response.setEarningCount(earningHistories.size());
	 * response.setListenerCount(listenerProfiles.size()); } else {
	 * LOGGER.info("inside null"); listenerProfiles =
	 * getServiceRegistry().getListenerProfileService().
	 * findByActiveTrueOrderByIdDesc();
	 * response.setEarningCount(getServiceRegistry().getEarningHistoryService().
	 * countDistinctUserBy()); response.setListenerCount(listenerProfiles.size()); }
	 */

}
