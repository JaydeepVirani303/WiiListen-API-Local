package com.wiilisten.controller.api.caller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.FavouriteListener;
import com.wiilisten.entity.ListenerProfile;
import com.wiilisten.entity.User;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.enums.UserRoleEnum;
import com.wiilisten.request.PaginationAndSortingDetails;
import com.wiilisten.response.FavoriteListenerDetailsDto;
import com.wiilisten.response.ResponseWithDataAndPagination;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.CALLER
		+ ApplicationURIConstants.FAVOURITE + ApplicationURIConstants.LISTENER)
public class ApiV1CallerFavouriteListenerController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiV1CallerFavouriteListenerController.class);

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getFavoriteListenerList(@RequestBody PaginationAndSortingDetails requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			if (ApplicationUtils.isEmpty(requestDetails.getSortBy()))
				requestDetails.setSortBy("listener.currentRating");
			if (ApplicationUtils.isEmpty(requestDetails.getSortType()))
				requestDetails.setSortType("DESC");

			Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);

			Page<FavouriteListener> favoriteListeners = getServiceRegistry().getFavoriteListenerService()
					.findByCallerIdAndActiveTrue(user.getId(), pageable);
			if (ApplicationUtils.isEmpty(favoriteListeners)
					|| ApplicationUtils.isEmpty(favoriteListeners.getContent())) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			// Double commisionRate = getCommonServices().getOnDemandCallsCommisionRate();
			// if (commisionRate == null) {
			// LOGGER.info("Unable to get commision rate for on-demand calls");
			// LOGGER.info(ApplicationConstants.EXIT_LABEL);
			// return
			// ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			// }
			List<FavouriteListener> favouriteListenerList = favoriteListeners.getContent();
			favouriteListenerList = getCommonServices().filterBlockedFavouriteListeners(user, favouriteListenerList);

			if (ApplicationUtils.isEmpty(favouriteListenerList)) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}
			List<FavoriteListenerDetailsDto> favListeners = new ArrayList<>();
			favouriteListenerList.forEach(favListener -> {
				ListenerProfile listener = getServiceRegistry().getListenerProfileService()
						.findByUserAndActiveTrue(favListener.getListener());

				// TODO: Remove blocked listener from list
				// removing blocked listener
				// if(!getCommonServices().checkBlockerHasBlockedUserOrNot(user,
				// listener.getUser()))
				if (listener != null)
					favListeners.add(getCommonServices().convertListenerProfileEntityToDtoForCardLayout(listener));

			});

			if (ApplicationUtils.isEmpty(favListeners)) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(
					new ResponseWithDataAndPagination(favListeners, (long) favouriteListenerList.size())));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.SPONSERED_LISTENER)
	public ResponseEntity<Object> getSponseredListener(@RequestBody PaginationAndSortingDetails requestDetails) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			if (ApplicationUtils.isEmpty(requestDetails.getSortBy()))
				requestDetails.setSortBy("id");
			if (ApplicationUtils.isEmpty(requestDetails.getSortType()))
				requestDetails.setSortType("DESC");

			Pageable pageable = getCommonServices().convertRequestToPageableObject(requestDetails);
			Page<ListenerProfile> listenerProfiles = getServiceRegistry().getListenerProfileService()
					.findByIsAdvertisementActiveTrueAndActiveTrue(pageable);
			if (ApplicationUtils.isEmpty(listenerProfiles)) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}
			List<FavoriteListenerDetailsDto> response = new ArrayList<>();
			listenerProfiles.forEach(listener -> {
				if (!listener.getUser().getId().equals(user.getId())) {
					response.add(getCommonServices().convertListenerProfileEntityToDtoForCardLayout(listener));
				}

			});

			if (ApplicationUtils.isEmpty(response)) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(
					new ResponseWithDataAndPagination(response, (long) response.size())));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@GetMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateFavoriteListenerList(@RequestParam Long id) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			User user = getLoggedInUser();
			ListenerProfile listener = getServiceRegistry().getListenerProfileService().findByIdAndActiveTrue(id);
			if (listener == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.LISTENER_NOT_FOUND.getCode()));
			}

			FavouriteListener favoriteListener = getServiceRegistry().getFavoriteListenerService()
					.findByCallerIdAndListenerId(user.getId(), listener.getUser().getId());

			// updating existing favorite record
			if (favoriteListener != null) {
				favoriteListener.setActive(favoriteListener.getActive() ? false : true);
			}
			// adding listener to favorite
			else {
				favoriteListener = new FavouriteListener();
				favoriteListener.setCaller(user);
				favoriteListener.setListener(listener.getUser());
				favoriteListener.setActive(true);
			}
			getServiceRegistry().getFavoriteListenerService().saveORupdate(favoriteListener);

			String responseMessageKey = (favoriteListener.getActive()) ? SuccessMsgEnum.LISTNER_ADD_FAVORITE.getCode()
					: SuccessMsgEnum.LISTNER_REMOVE_FAVORITE.getCode();

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(responseMessageKey));

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
}
