package com.wiilisten.controller.api.caller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.request.PremiumSearchRequestDto;
import com.wiilisten.response.FavoriteListenerDetailsDto;
import com.wiilisten.response.ResponseWithDataAndPagination;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.CALLER
		+ ApplicationURIConstants.SEARCH)
public class ApiV1CallerSearchController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1CallerSearchController.class);

	@PersistenceContext
	private EntityManager entityManager;

	// search for premium listener
	@PostMapping(ApplicationURIConstants.PREMIUM)
	public ResponseEntity<Object> getPremiumSearchList(@RequestBody PremiumSearchRequestDto earningHistoryRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		// TODO:Check if caller have subscription
		try {
			List<FavoriteListenerDetailsDto> response = new ArrayList<>();
			StringBuilder listenerBuilder = new StringBuilder("SELECT lp FROM ListenerProfile lp JOIN lp.user u ");
			List<String> listenerConditions = new ArrayList<>();
			List<Object> listenerParameters = new ArrayList<>();

			if (earningHistoryRequestDto.getName().isEmpty()
					&& earningHistoryRequestDto.getEducation() == null && earningHistoryRequestDto.getGender() == null
					&& earningHistoryRequestDto.getStartDate() == null && earningHistoryRequestDto.getEndDate() == null
					&& earningHistoryRequestDto.getLocation() == null && earningHistoryRequestDto.getIds().isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			listenerConditions.add("lp.active = true");
			listenerConditions.add("lp.isEligibleForPremiumCallSearch = true");
			int paramIndex = 1;
			LOGGER.info("before if ");
			if (!earningHistoryRequestDto.getName().isEmpty()) {
				LOGGER.info("inside name");
				String tranformedName = StringUtils.trimAllWhitespace(earningHistoryRequestDto.getName()).toUpperCase();
				listenerConditions.add("UPPER(REPLACE(lp.userName, ' ', '')) = ?" + paramIndex);
				listenerParameters.add(tranformedName);
				paramIndex++;
			}
			LOGGER.info("after if ");
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
				LOGGER.info("inside gender");
				listenerConditions.add("lp.gender = ?" + paramIndex);
				listenerParameters.add(earningHistoryRequestDto.getGender());
				paramIndex++;
			}

			if (earningHistoryRequestDto.getStartDate() != null && earningHistoryRequestDto.getEndDate() != null) {
				LOGGER.info("inside date");
				listenerConditions.add("lp.dateOfBirth BETWEEN ?" + paramIndex + " AND ?" + (paramIndex + 1));
				listenerParameters.add(earningHistoryRequestDto.getStartDate());
				listenerParameters.add(earningHistoryRequestDto.getEndDate());
				paramIndex += 2;
			}

			if (earningHistoryRequestDto.getIds() != null && !earningHistoryRequestDto.getIds().isEmpty()) {
				listenerConditions.add("EXISTS (" + "  SELECT 1 FROM lp.languages lang WHERE lang.id IN :languageIds "
						+ "  GROUP BY lp.id HAVING COUNT(DISTINCT lang.id) = :languageCount" + ")");
			}

			if (!listenerConditions.isEmpty()) {
				listenerBuilder.append(" WHERE ").append(String.join(" AND ", listenerConditions));
			}

			listenerBuilder.append(" ORDER BY lp.createdAt DESC");

			TypedQuery<ListenerProfile> listenerQuery = entityManager.createQuery(listenerBuilder.toString(),
					ListenerProfile.class);
			for (int i = 0; i < listenerParameters.size(); i++) {
				listenerQuery.setParameter(i + 1, listenerParameters.get(i));
			}

			if (earningHistoryRequestDto.getIds() != null && !earningHistoryRequestDto.getIds().isEmpty()) {
				listenerQuery.setParameter("languageIds", earningHistoryRequestDto.getIds());
				listenerQuery.setParameter("languageCount", earningHistoryRequestDto.getIds().size());
			}

			List<ListenerProfile> totalData = listenerQuery.getResultList();
			// Pagination
			int pageNumber = earningHistoryRequestDto.getPageNumber() != null ? earningHistoryRequestDto.getPageNumber()
					: 0;
			int pageSize = earningHistoryRequestDto.getPageSize() != null ? earningHistoryRequestDto.getPageSize() : 10;
			int firstResult = pageNumber * pageSize;

			listenerQuery.setFirstResult(firstResult);
			listenerQuery.setMaxResults(pageSize);

			List<ListenerProfile> listenerProfiles = listenerQuery.getResultList();
			LOGGER.info("aaaaaaaaaaaaaaa av" + listenerProfiles.size());
			listenerProfiles = getCommonServices().filterBlockedListeners(getLoggedInUser(), listenerProfiles);
			LOGGER.info("aaaaaaaaaaaaaaa av" + listenerProfiles.size());
			if (listenerProfiles.isEmpty()) {
				LOGGER.info("inside");
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
			}

			listenerProfiles.forEach(listener -> {
				response.add(getCommonServices().convertListenerProfileEntityToDtoForCardLayout(listener));

			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(
					new ResponseWithDataAndPagination(response, (long) totalData.size())));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}
}
