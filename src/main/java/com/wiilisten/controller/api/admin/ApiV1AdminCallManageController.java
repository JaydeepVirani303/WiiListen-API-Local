package com.wiilisten.controller.api.admin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiilisten.controller.BaseController;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.CallManageRequestDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.IdStatusRequestDto;
import com.wiilisten.response.CallManageResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.CALL_MANAGE)
public class ApiV1AdminCallManageController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminCallManageController.class);

	@PostMapping(ApplicationURIConstants.FILTER + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getCallManageList(@RequestBody CallManageRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<CallManageResponseDto> response = new ArrayList<>();
			if (requestDto.getStartDate() != null && requestDto.getEndDate() != null) {
				List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
						.findByBookingDateTimeBetweenAndActiveTrueOrderByIdDesc(requestDto.getStartDate(),
								requestDto.getEndDate());
				if (bookedCalls.isEmpty()) {

					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.NO_CALLS_FOUND.getCode()));
				}

				bookedCalls.forEach(call -> {
					CallManageResponseDto responseDto = new CallManageResponseDto();
					BeanUtils.copyProperties(call, responseDto);
					responseDto.setCallerId(call.getCaller().getId());
					responseDto.setCallerName(call.getCaller().getUser().getCallName());
					responseDto.setListenerId(call.getListener().getId());
					responseDto.setListenerName(call.getListener().getUserName());
					if (call.getCardDetails() != null) {
						responseDto.setCardId(call.getCardDetails().getId());
						responseDto.setCardType(call.getCardDetails().getType());
						responseDto.setCardNumber(call.getCardDetails().getNumber());
					}

					response.add(responseDto);
				});
			} else {
				List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
						.findByActiveTrueOrderByIdDesc();
				if (bookedCalls.isEmpty()) {

					LOGGER.info(ApplicationConstants.EXIT_LABEL);
					return ResponseEntity.ok(getCommonServices()
							.generateBadResponseWithMessageKey(ErrorDataEnum.NO_CALLS_FOUND.getCode()));
				}
				bookedCalls.forEach(call -> {
					CallManageResponseDto responseDto = new CallManageResponseDto();
					BeanUtils.copyProperties(call, responseDto);
					responseDto.setCallerId(call.getCaller().getId());
					responseDto.setCallerName(call.getCaller().getUser().getCallName());
					responseDto.setListenerId(call.getListener().getId());
					responseDto.setListenerName(call.getListener().getUserName());
					if (call.getCardDetails() != null) {
						responseDto.setCardId(call.getCardDetails().getId());
						responseDto.setCardType(call.getCardDetails().getType());
						responseDto.setCardNumber(call.getCardDetails().getNumber());
					}

					response.add(responseDto);
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

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getAllCallList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
					.findByActiveTrueOrderByIdDesc();
			if (bookedCalls.isEmpty()) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_CALLS_FOUND.getCode()));
			}
			List<CallManageResponseDto> response = new ArrayList<>();
			bookedCalls.forEach(call -> {
				CallManageResponseDto responseDto = new CallManageResponseDto();
				BeanUtils.copyProperties(call, responseDto);
				responseDto.setCallerId(call.getCaller().getId());
				responseDto.setCallerName(call.getCaller().getUser().getCallName());
				responseDto.setListenerId(call.getListener().getId());
				responseDto.setListenerName(call.getListener().getUserName());
				if (call.getCardDetails() != null) {
					responseDto.setCardId(call.getCardDetails().getId());
					responseDto.setCardType(call.getCardDetails().getType());
					responseDto.setCardNumber(call.getCardDetails().getNumber());
				}

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

	@PostMapping(ApplicationURIConstants.FORWARD_SLASH)
	public ResponseEntity<Object> getSpecifcCall(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			BookedCalls bookedCalls = getServiceRegistry().getBookedCallsService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (bookedCalls == null) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_CALLS_FOUND.getCode()));
			}

			CallManageResponseDto response = new CallManageResponseDto();
			BeanUtils.copyProperties(bookedCalls, response);
			response.setCallerId(bookedCalls.getCaller().getId());
			response.setCallerName(bookedCalls.getCaller().getUser().getCallName());
			response.setListenerId(bookedCalls.getListener().getId());
			response.setListenerName(bookedCalls.getListener().getUserName());
			if (bookedCalls.getCardDetails() != null) {
				response.setCardId(bookedCalls.getCardDetails().getId());
				response.setCardType(bookedCalls.getCardDetails().getType());
				response.setCardNumber(bookedCalls.getCardDetails().getNumber());
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.CALL_STATUS)
	public ResponseEntity<Object> getCallByStatus(@RequestBody IdStatusRequestDto idStatusRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<BookedCalls> bookedCalls = getServiceRegistry().getBookedCallsService()
					.findByTypeAndActiveTrueOrderByIdDesc(idStatusRequestDto.getStatus());
			if (bookedCalls.isEmpty()) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_CALLS_FOUND.getCode()));
			}
			List<CallManageResponseDto> response = new ArrayList<>();
			bookedCalls.forEach(call -> {
				CallManageResponseDto responseDto = new CallManageResponseDto();
				BeanUtils.copyProperties(call, responseDto);
				responseDto.setCallerId(call.getCaller().getId());
				responseDto.setCallerName(call.getCaller().getUser().getCallName());
				responseDto.setListenerId(call.getListener().getId());
				responseDto.setListenerName(call.getListener().getUserName());
				if (call.getCardDetails() != null) {
					responseDto.setCardId(call.getCardDetails().getId());
					responseDto.setCardType(call.getCardDetails().getType());
					responseDto.setCardNumber(call.getCardDetails().getNumber());
				}

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

	@PostMapping(ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteCall(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			BookedCalls bookedCalls = getServiceRegistry().getBookedCallsService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (bookedCalls == null) {

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_CALLS_FOUND.getCode()));
			}
			bookedCalls.setActive(false);
			getServiceRegistry().getBookedCallsService().saveORupdate(bookedCalls);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.BOOKED_CALL_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}
}
