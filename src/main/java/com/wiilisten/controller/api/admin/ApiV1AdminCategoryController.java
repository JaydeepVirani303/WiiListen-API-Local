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
import com.wiilisten.entity.Category;
import com.wiilisten.entity.SubCategory;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.CategoryDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.SubCategoryRequestDto;
import com.wiilisten.response.CategoryListResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.CATEGORY)
public class ApiV1AdminCategoryController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminCategoryController.class);

	@PostMapping(ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addCategory(@RequestBody CategoryDto categoryDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			Category category = new Category();
			BeanUtils.copyProperties(categoryDto, category);
			category.setActive(true);
			getServiceRegistry().getCategoryService().saveORupdate(category);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CATEGORY_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.LIST)
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

	@PostMapping(ApplicationURIConstants.FORWARD_SLASH)
	public ResponseEntity<Object> getSpecificCategory(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			Category category = getServiceRegistry().getCategoryService().findOne(idRequestDto.getId());
			if (category == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CATEGORY_NOT_EXIST.getCode()));
			}


			List<SubCategory> subCategories = getServiceRegistry().getSubCategoryService()
					.findByCategoryId(idRequestDto.getId());
			CategoryListResponseDto response = new CategoryListResponseDto();
			BeanUtils.copyProperties(category, response);
			List<SubCategoryRequestDto> subCategoryRequestDtos = new ArrayList<>();

			subCategories.forEach(subCategory -> {
				SubCategoryRequestDto subCategoryRequestDto = new SubCategoryRequestDto();
				subCategoryRequestDto.setId(subCategory.getId());
				subCategoryRequestDto.setName(subCategory.getName());
				subCategoryRequestDto.setDescription(subCategory.getDescription());
				subCategoryRequestDtos.add(subCategoryRequestDto);
			});
			response.setSubCategories(subCategoryRequestDtos);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateCategory(@RequestBody CategoryDto categoryDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			Category category = getServiceRegistry().getCategoryService().findOne(categoryDto.getId());
			if (category == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CATEGORY_NOT_EXIST.getCode()));
			}
			BeanUtils.copyProperties(categoryDto, category, getCommonServices().getNullPropertyNames(categoryDto));
			getServiceRegistry().getCategoryService().saveORupdate(category);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CATEGORY_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.ACTIVE_STATUS + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> activeAndInActiveCategory(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			Category category = getServiceRegistry().getCategoryService().findOne(idRequestDto.getId());
			if (category == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CATEGORY_NOT_EXIST.getCode()));
			}

			// If category is banned then unbanned it or vice-versa
			if (category.getActive()) {
				category.setActive(false);
				getServiceRegistry().getCategoryService().saveORupdate(category);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
						SuccessMsgEnum.CATEGORY_INACTIVE_SUCCESSFULLY.getCode()));
			}

			else if (!category.getActive()) {
				category.setActive(true);
				getServiceRegistry().getCategoryService().saveORupdate(category);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CATEGORY_ACTIVE_SUCCESSFULLY.getCode()));

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
	public ResponseEntity<Object> hardDeleteCategory(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			Category category = getServiceRegistry().getCategoryService().findOne(idRequestDto.getId());
			if (category == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.CATEGORY_NOT_EXIST.getCode()));
			}

			getServiceRegistry().getCategoryService().deleteById(idRequestDto.getId());

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CATEGORY_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

}
