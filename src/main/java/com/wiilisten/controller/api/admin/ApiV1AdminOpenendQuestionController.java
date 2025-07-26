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
import com.wiilisten.entity.OpenEndedQuestion;
import com.wiilisten.entity.SubCategory;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.OpenEndedQuestionRequestDto;
import com.wiilisten.response.OpenEndedQuestionResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.OPEN_ENDED_QUESTIONS)
public class ApiV1AdminOpenendQuestionController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminOpenendQuestionController.class);

	@PostMapping(ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addOpenEndendQuestion(@RequestBody OpenEndedQuestionRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			SubCategory subCategory = getServiceRegistry().getSubCategoryService()
					.findByIdAndActiveTrue(requestDto.getSubCategoryId());
			if (subCategory == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_CATEGORY_NOT_EXIST.getCode()));
			}
			OpenEndedQuestion openEndedQuestion = new OpenEndedQuestion();
			BeanUtils.copyProperties(requestDto, openEndedQuestion);
			openEndedQuestion.setSubCategory(subCategory);
			openEndedQuestion.setActive(true);
			getServiceRegistry().getOpenEndedQuestionService().saveORupdate(openEndedQuestion);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.QUESTION_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getOpenEndedQuestionList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

//			Page<OpenEndedQuestion> oeqs = getServiceRegistry().getOpenEndedQuestionService()
//					.findByActiveTrue(pageable);
			List<OpenEndedQuestion> oeqs = getServiceRegistry().getOpenEndedQuestionService()
					.findByActiveTrueOrderByIdDesc();
			List<OpenEndedQuestionResponseDto> response = new ArrayList<>();
			oeqs.forEach(oeq -> {
				OpenEndedQuestionResponseDto responseDto = new OpenEndedQuestionResponseDto();
				BeanUtils.copyProperties(oeq, responseDto);
				responseDto.setSubCategoryName(oeq.getSubCategory().getName());
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

	@PostMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateOpenEndedQuestion(@RequestBody OpenEndedQuestionRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			OpenEndedQuestion oeq = getServiceRegistry().getOpenEndedQuestionService().findOne(requestDto.getId());
			if (oeq == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.QUESTION_NOT_EXIST.getCode()));
			}
			SubCategory subCategory = getServiceRegistry().getSubCategoryService()
					.findByIdAndActiveTrue(requestDto.getSubCategoryId());
			if (subCategory == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_CATEGORY_NOT_EXIST.getCode()));
			}
			BeanUtils.copyProperties(requestDto, oeq, getCommonServices().getNullPropertyNames(requestDto));
			oeq.setSubCategory(subCategory);
			getServiceRegistry().getOpenEndedQuestionService().saveORupdate(oeq);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.QUESTION_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.FORWARD_SLASH)
	public ResponseEntity<Object> getSpecificQuestion(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			OpenEndedQuestion oeq = getServiceRegistry().getOpenEndedQuestionService().findOne(idRequestDto.getId());
			if (oeq == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.QUESTION_NOT_EXIST.getCode()));
			}
			OpenEndedQuestionResponseDto response = new OpenEndedQuestionResponseDto();
			BeanUtils.copyProperties(oeq, response);
			response.setSubCategoryName(oeq.getSubCategory().getName());
			response.setSubCategoryId(oeq.getSubCategory().getId());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteQuestion(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			OpenEndedQuestion oeq = getServiceRegistry().getOpenEndedQuestionService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (oeq == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.QUESTION_NOT_EXIST.getCode()));
			}
			oeq.setActive(false);
			getServiceRegistry().getOpenEndedQuestionService().saveORupdate(oeq);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.QUESTION_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
