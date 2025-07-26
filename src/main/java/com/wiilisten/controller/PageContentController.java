package com.wiilisten.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.wiilisten.entity.Faq;
import com.wiilisten.entity.OpenEndedQuestion;
import com.wiilisten.enums.PageContentTypesEnum;
import com.wiilisten.response.FaqDetailsDto;
import com.wiilisten.utils.ApplicationConstants;
import com.wiilisten.utils.ApplicationURIConstants;
import com.wiilisten.utils.ApplicationUtils;

import io.swagger.v3.oas.annotations.Hidden;

@Hidden
@Controller
@RequestMapping(ApplicationURIConstants.PAGE_CONTENT)
public class PageContentController extends BaseController{

	private static final Logger LOGGER = LoggerFactory.getLogger(PageContentController.class);
			
	@GetMapping(ApplicationURIConstants.ABOUT_US)
	public ModelAndView aboutUs() {
		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new ModelAndView("about-us.html", "content", getServiceRegistry().getPageContentService().findByTypeAndActiveTrue(PageContentTypesEnum.ABOUT_US.getType()).getContent());
	}
	
	@GetMapping(ApplicationURIConstants.TERMS_AND_CONDITION)
	public ModelAndView termsAndCondition() {
		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new ModelAndView("terms-and-condition.html", "content", getServiceRegistry().getPageContentService().findByTypeAndActiveTrue(PageContentTypesEnum.TERMS_AND_CONDITION.getType()).getContent());
	}
	
	@GetMapping(ApplicationURIConstants.PRIVACY_POLICY)
	public ModelAndView privacyPolicy() {
		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new ModelAndView("privacy-policy.html", "content", getServiceRegistry().getPageContentService().findByTypeAndActiveTrue(PageContentTypesEnum.PRIVACY_POLICY.getType()).getContent());
	}
	
	@GetMapping(ApplicationURIConstants.FAQ)
	public ModelAndView faq() {
		
		List<Faq> faqs = getServiceRegistry().getFaqService().findByActiveTrueOrderByIdDesc();
		if(!ApplicationUtils.isEmpty(faqs)) {
			List<FaqDetailsDto> faqResponse = new ArrayList<FaqDetailsDto>();
			
			faqs.forEach(faq -> {
				FaqDetailsDto testFaq = new FaqDetailsDto();
				testFaq.setQuestion(faq.getQuestion());
				testFaq.setAnswer(faq.getAnswer());
				
				faqResponse.add(testFaq);
			});
		}
		
		LOGGER.info(ApplicationConstants.CALLED_LABEL);
		return new ModelAndView("faq.html", "faqs", faqs);
	}
	@GetMapping(ApplicationURIConstants.OPEN_ENDED_QUESTIONS)
	 public ModelAndView  getOpenEndedQuestion() {
		
		LOGGER.info(ApplicationConstants.ENTER_LABEL);
		List<FaqDetailsDto> faqResponse = new ArrayList<FaqDetailsDto>();
	
			List<OpenEndedQuestion> oeqs = getServiceRegistry().getOpenEndedQuestionService().findByActiveTrue();
			if(!ApplicationUtils.isEmpty(oeqs)) {
				
				
				
				oeqs.forEach(oeq -> {
					FaqDetailsDto testFaq = new FaqDetailsDto();
					testFaq.setQuestion(oeq.getQuestion());
					testFaq.setAnswer(oeq.getAnswer());
					
					faqResponse.add(testFaq);
				});
				
				
				
			}
			LOGGER.info(ApplicationConstants.EXIT_LABEL);
			 return new ModelAndView("faq.html", "faqs", faqResponse);

//			LOGGER.info(ApplicationConstants.EXIT_LABEL);
//			return ResponseEntity.ok(getCommonServices().generateResponseForNoDataFound());
			
	
	}
}
