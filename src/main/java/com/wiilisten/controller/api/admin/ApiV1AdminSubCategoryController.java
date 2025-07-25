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
import com.wiilisten.entity.Category;
import com.wiilisten.entity.SubCategory;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.CategoryDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.SubCategoryRequestDto;
import com.wiilisten.response.SubCategoryResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.SUB_CATEGORY)
public class ApiV1AdminSubCategoryController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminSubCategoryController.class);

	@GetMapping(ApplicationURIConstants.CATEGORY + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getCategoryList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			
			List<Category> categories = getServiceRegistry().getCategoryService().findAllByOrderByIdDesc();
			if (categories.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CATEGORY_NOT_EXIST.getCode()));
			}

			List<CategoryDto> response = new ArrayList<>();
			categories.forEach(category -> {
				CategoryDto categoryDto = new CategoryDto();
				BeanUtils.copyProperties(category, categoryDto);
				response.add(categoryDto);
			});

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.ADD)
	public ResponseEntity<Object> saveSubCategory(@RequestBody SubCategoryRequestDto subCategoryRequest) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			Category category = getServiceRegistry().getCategoryService()
					.findOne(subCategoryRequest.getCategoryId());
			if (category == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CATEGORY_NOT_EXIST.getCode()));
			}
			SubCategory subCategory = new SubCategory();
			BeanUtils.copyProperties(subCategoryRequest, subCategory);
			subCategory.setActive(true);
			subCategory.setCategory(category);
			getServiceRegistry().getSubCategoryService().saveORupdate(subCategory);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.SUB_CATEGORY_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getSubCategoryList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
		
			List<SubCategory> subCategories = getServiceRegistry().getSubCategoryService()
					.findAllByOrderByIdDesc();
			if (subCategories.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_CATEGORY_NOT_EXIST.getCode()));
			}

			List<SubCategoryResponseDto> response = new ArrayList<>();
			subCategories.forEach(subCategory -> {
				SubCategoryResponseDto subCategoryResponseDto = new SubCategoryResponseDto();
				BeanUtils.copyProperties(subCategory, subCategoryResponseDto);
				subCategoryResponseDto.setCategoryId(subCategory.getCategory().getId());
				subCategoryResponseDto.setCategoryName(subCategory.getCategory().getName());
				response.add(subCategoryResponseDto);
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
	public ResponseEntity<Object> getSpecificSubCategory(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			
			SubCategory subCategory = getServiceRegistry().getSubCategoryService()
					.findOne(idRequestDto.getId());
			if (subCategory == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_CATEGORY_NOT_EXIST.getCode()));
			}

			SubCategoryResponseDto response = new SubCategoryResponseDto();
			BeanUtils.copyProperties(subCategory, response);
			response.setCategoryId(subCategory.getCategory().getId());
			response.setCategoryName(subCategory.getCategory().getName());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateSubCategory(@RequestBody SubCategoryRequestDto subCategoryRequest) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			
			Category category = getServiceRegistry().getCategoryService()
					.findOne(subCategoryRequest.getCategoryId());
			if (category == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CATEGORY_NOT_EXIST.getCode()));
			}
			SubCategory subCategory = getServiceRegistry().getSubCategoryService()
					.findOne(subCategoryRequest.getId());
			if (subCategory == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_CATEGORY_NOT_EXIST.getCode()));
			}
			BeanUtils.copyProperties(subCategoryRequest, subCategory,
					getCommonServices().getNullPropertyNames(subCategoryRequest));
			subCategory.setCategory(category);
			getServiceRegistry().getSubCategoryService().saveORupdate(subCategory);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.SUB_CATEGORY_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.ACTIVE_STATUS + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> activeAndInActiveSubCategory(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			
			SubCategory subCategory = getServiceRegistry().getSubCategoryService().findOne(idRequestDto.getId());
			if (subCategory == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_CATEGORY_NOT_EXIST.getCode()));
			}

			// If category is banned then unbanned it or vice-versa
			if (subCategory.getActive()) {
				subCategory.setActive(false);
				getServiceRegistry().getSubCategoryService().saveORupdate(subCategory);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
						SuccessMsgEnum.SUB_CATEGORY_INACTIVE_SUCCESSFULLY.getCode()));
			}

			else if (!subCategory.getActive()) {
				subCategory.setActive(true);
				getServiceRegistry().getSubCategoryService().saveORupdate(subCategory);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
						SuccessMsgEnum.SUB_CATEGORY_ACTIVE_SUCCESSFULLY.getCode()));

			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(
					getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.CATEGORY_NOT_EXIST.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> hardDeleteSubCategory(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			SubCategory subCategory = getServiceRegistry().getSubCategoryService().findOne(idRequestDto.getId());
			if (subCategory == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.SUB_CATEGORY_NOT_EXIST.getCode()));
			}			
			getServiceRegistry().getSubCategoryService().delete(subCategory);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.SUB_CATEGORY_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
