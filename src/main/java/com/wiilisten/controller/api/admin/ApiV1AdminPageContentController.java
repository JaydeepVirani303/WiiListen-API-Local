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
import com.wiilisten.entity.ContactUs;
import com.wiilisten.entity.Faq;
import com.wiilisten.entity.PageContent;
import com.wiilisten.entity.TrainingMaterial;
import com.wiilisten.enums.ErrorDataEnum;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.request.ContactUsReplyRequestDto;
import com.wiilisten.request.FaqRequestDto;
import com.wiilisten.request.IdRequestDto;
import com.wiilisten.request.PageContentRequestDto;
import com.wiilisten.request.TrainingMaterialRequestDto;
import com.wiilisten.request.TypeRequestDto;
import com.wiilisten.response.ContactUsResponseDto;
import com.wiilisten.response.FaqDetailsDto;
import com.wiilisten.response.PageContentResponseDto;
import com.wiilisten.response.TrainingMaterialResponseDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

import io.swagger.v3.oas.annotations.Hidden;

@RestController
@RequestMapping(value = ApplicationURIConstants.API + ApplicationURIConstants.V1 + ApplicationURIConstants.ADMIN
		+ ApplicationURIConstants.PAGE_CONTENT)
public class ApiV1AdminPageContentController extends BaseController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ApiV1AdminPageContentController.class);

	@PostMapping(ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addPageContent(@RequestBody PageContentRequestDto pageContentRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			PageContent pageContent = getServiceRegistry().getPageContentService()
					.findByTypeAndActiveTrue(pageContentRequestDto.getType());
			if (pageContent != null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.PAGE_CONTENT_EXIST.getCode()));
			}
			PageContent content = new PageContent();
			content.setContent(pageContentRequestDto.getContent());
			content.setType(pageContentRequestDto.getType());
			content.setActive(true);
			getServiceRegistry().getPageContentService().saveORupdate(content);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PAGE_CONTENT_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updatePageContent(@RequestBody PageContentRequestDto pageContentRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			PageContent pageContent = getServiceRegistry().getPageContentService()
					.findByTypeAndActiveTrue(pageContentRequestDto.getType());
			if (pageContent == null) {
				PageContent page = new PageContent();
				page.setContent(pageContentRequestDto.getContent());
				page.setType(pageContentRequestDto.getType());
				page.setActive(true);
				getServiceRegistry().getPageContentService().saveORupdate(page);

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
						SuccessMsgEnum.PAGE_CONTENT_ADDED_SUCCESSFULLY.getCode()));
			}
			pageContent.setContent(pageContentRequestDto.getContent());
			getServiceRegistry().getPageContentService().saveORupdate(pageContent);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PAGE_CONTENT_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getPageContentList(@RequestBody TypeRequestDto typeRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			PageContent pageContent = getServiceRegistry().getPageContentService()
					.findByTypeAndActiveTrue(typeRequestDto.getType());
			PageContentResponseDto response = new PageContentResponseDto();
			BeanUtils.copyProperties(pageContent, response);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteContent(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			PageContent pageContent = getServiceRegistry().getPageContentService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (pageContent == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}
			pageContent.setActive(false);
			getServiceRegistry().getPageContentService().saveORupdate(pageContent);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.PAGE_CONTENT_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.TRAINING_MATERIAL + ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addTrainingMaterial(@RequestBody TrainingMaterialRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			TrainingMaterial trainingMaterial = new TrainingMaterial();
			BeanUtils.copyProperties(requestDto, trainingMaterial);
			trainingMaterial.setActive(true);
			getServiceRegistry().getTrainingMaterialService().saveORupdate(trainingMaterial);
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.TRAINING_MATERIAL_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.TRAINING_MATERIAL + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getMaterialList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<TrainingMaterial> trainingMaterials = getServiceRegistry().getTrainingMaterialService()
					.findByActiveTrueOrderByIdDesc();
			if (trainingMaterials.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.TRAINING_MATERIAL_NOT_EXIST.getCode()));
			}
			List<TrainingMaterialResponseDto> response = new ArrayList<>();
			trainingMaterials.forEach(material -> {
				TrainingMaterialResponseDto dto = new TrainingMaterialResponseDto();
				BeanUtils.copyProperties(material, dto);
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

	@PostMapping(ApplicationURIConstants.TRAINING_MATERIAL + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateTrainingMaterial(@RequestBody TrainingMaterialRequestDto requestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			TrainingMaterial trainingMaterial = getServiceRegistry().getTrainingMaterialService()
					.findByIdAndActiveTrue(requestDto.getId());
			if (trainingMaterial == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.TRAINING_MATERIAL_NOT_EXIST.getCode()));
			}
			BeanUtils.copyProperties(requestDto, trainingMaterial,
					getCommonServices().getNullPropertyNames(requestDto));
			getServiceRegistry().getTrainingMaterialService().saveORupdate(trainingMaterial);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.TRAINING_MATERIAL_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.TRAINING_MATERIAL + ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteTrainingMaterial(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			TrainingMaterial trainingMaterial = getServiceRegistry().getTrainingMaterialService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (trainingMaterial == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices()
						.generateBadResponseWithMessageKey(ErrorDataEnum.TRAINING_MATERIAL_NOT_EXIST.getCode()));
			}
			trainingMaterial.setActive(false);
			getServiceRegistry().getTrainingMaterialService().saveORupdate(trainingMaterial);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateSuccessResponseWithMessageKey(
					SuccessMsgEnum.TRAINING_MATERIAL_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.FAQ + ApplicationURIConstants.ADD)
	public ResponseEntity<Object> addFaq(@RequestBody FaqRequestDto faqRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Faq faq = new Faq();
			BeanUtils.copyProperties(faqRequestDto, faq);
			faq.setActive(true);
			getServiceRegistry().getFaqService().saveORupdate(faq);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.FAQ_ADDED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}

	}

	@PostMapping(ApplicationURIConstants.FAQ + ApplicationURIConstants.UPDATE)
	public ResponseEntity<Object> updateFaq(@RequestBody FaqRequestDto faqRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Faq faq = getServiceRegistry().getFaqService().findByIdAndActiveTrue(faqRequestDto.getId());
			if (faq == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.FAQ_NOT_FOUND.getCode()));
			}
			BeanUtils.copyProperties(faqRequestDto, faq, getCommonServices().getNullPropertyNames(faq));
			getServiceRegistry().getFaqService().saveORupdate(faq);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.FAQ_UPDATED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.FAQ + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getFaq() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {

			List<Faq> faqs = getServiceRegistry().getFaqService().findByActiveTrueOrderByIdDesc();
			if (!ApplicationUtils.isEmpty(faqs)) {
				List<FaqDetailsDto> response = new ArrayList<FaqDetailsDto>();

				faqs.forEach(faq -> {
					FaqDetailsDto dto = new FaqDetailsDto();
					BeanUtils.copyProperties(faq, dto);

					response.add(dto);
				});

				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(getCommonServices().generateGenericSuccessResponse(response));
			}

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());

		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.FAQ + ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteFaq(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			Faq faq = getServiceRegistry().getFaqService().findByIdAndActiveTrue(idRequestDto.getId());
			if (faq == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.FAQ_NOT_FOUND.getCode()));
			}
			faq.setActive(false);
			getServiceRegistry().getFaqService().saveORupdate(faq);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.FAQ_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@PostMapping(ApplicationURIConstants.CONTACT_US + ApplicationURIConstants.LIST)
	public ResponseEntity<Object> getContactUsList() {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			List<ContactUs> contactUs = getServiceRegistry().getContactUsService().findByActiveTrueOrderByIdDesc();
			if (contactUs.isEmpty()) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}
			List<ContactUsResponseDto> response = new ArrayList<>();
			contactUs.forEach(contact -> {
				ContactUsResponseDto dto = new ContactUsResponseDto();
				if(contact.getUser()!=null) {
					BeanUtils.copyProperties(contact.getUser(), dto);
				}
				
				BeanUtils.copyProperties(contact, dto);
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

	@PostMapping(ApplicationURIConstants.CONTACT_US + ApplicationURIConstants.DELETE)
	public ResponseEntity<Object> deleteContactUs(@RequestBody IdRequestDto idRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			ContactUs contactUs = getServiceRegistry().getContactUsService()
					.findByIdAndActiveTrue(idRequestDto.getId());
			if (contactUs == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}
			contactUs.setActive(false);
			getServiceRegistry().getContactUsService().saveORupdate(contactUs);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.CONTACT_US_DELETED_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

	@Hidden
	@PostMapping(ApplicationURIConstants.CONTACT_US + ApplicationURIConstants.RESPONSE)
	public ResponseEntity<Object> replyContactUs(@RequestBody ContactUsReplyRequestDto contactUsReplyRequestDto) {

		LOGGER.info(ApplicationConstants.ENTER_LABEL);

		try {
			ContactUs contactUs = getServiceRegistry().getContactUsService()
					.findByIdAndActiveTrue(contactUsReplyRequestDto.getId());
			if (contactUs == null) {
				LOGGER.info(ApplicationConstants.EXIT_LABEL);
				return ResponseEntity.ok(
						getCommonServices().generateBadResponseWithMessageKey(ErrorDataEnum.NO_DATA_FOUND.getCode()));
			}
			contactUs.setAdminResponse(contactUsReplyRequestDto.getAdminResponse());
			getServiceRegistry().getContactUsService().saveORupdate(contactUs);

			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices()
					.generateSuccessResponseWithMessageKey(SuccessMsgEnum.RESPONSE_SENT_SUCCESSFULLY.getCode()));
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			return ResponseEntity.ok(getCommonServices().generateFailureResponse());
		}
	}

}
