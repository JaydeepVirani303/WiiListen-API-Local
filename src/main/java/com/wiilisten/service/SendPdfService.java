package com.wiilisten.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.itextpdf.html2pdf.HtmlConverter;
import com.wiilisten.entity.BookedCalls;
import com.wiilisten.utils.ApplicationConstants;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendPdfService {

	@Autowired
	private SpringTemplateEngine springTemplateEngine;

	@Autowired
	private BookedCallsService bookedCallsService;

	public byte[] generatePdfForSchedule(Long id, String type) throws Exception {
		// Fetch the BookedCalls entity by ID
		BookedCalls bookedCall = bookedCallsService.findByIdAndActiveTrue(id);

		log.info("id is s{}" + bookedCall.getId());
		// Populate the Thymeleaf template with the data
		Context context = new Context();
		// LocalDateTime dateTime = bookedCall.getRequestedDateTime()
		log.info("id is after xontext{}" + bookedCall.getId());
		// Define the formatter
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a");

		// Format the LocalDateTime
		// String formattedDateTime = dateTime.format(formatter);
		LocalDateTime callStartAt = bookedCall.getCallerJoinedAt().isAfter(bookedCall.getListenerJoinedAt())
				? bookedCall.getCallerJoinedAt()
				: bookedCall.getListenerJoinedAt();
		context.setVariable("requestedDateTime", bookedCall.getRequestedDateTime().format(formatter));
		context.setVariable("approvedDateTime", bookedCall.getAcceptedDateTime().format(formatter)); // Replace with the
		log.info("up1");																								// actual field
		context.setVariable("callerName", bookedCall.getCaller().getUser().getCallName());
		context.setVariable("initiatedDateTime", callStartAt.format(formatter));
		context.setVariable("completedDateTime", bookedCall.getCallerLeavedAt().format(formatter));
		context.setVariable("totalMinutesTalked", bookedCall.getDurationInMinutes());
		log.info("up2");
		context.setVariable("listenerName", bookedCall.getListener().getUserName());
		context.setVariable("listenerRatePerMin", bookedCall.getListener().getRatePerMinute());
		context.setVariable("totalCostOfCall", bookedCall.getPayableAmount());
		log.info("up");
		// Generate HTML using Thymeleaf

		String htmlContent = springTemplateEngine.process("booking-confirmation-schedule", context);

		System.out.println(htmlContent);

		// Generate PDF from HTML and return as byte array
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		HtmlConverter.convertToPdf(htmlContent, target);

		return target.toByteArray();
	}

	public byte[] generatePdfForOnDemand(Long id, String type) throws Exception {
		// Fetch the BookedCalls entity by ID
		BookedCalls bookedCall = bookedCallsService.findByIdAndActiveTrue(id);

		log.info("id is o{}" + bookedCall.getId());
		// Populate the Thymeleaf template with the data
		Context context = new Context();
		// LocalDateTime dateTime = bookedCall.getRequestedDateTime()

		// Define the formatter
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a");

		// Format the LocalDateTime
		// String formattedDateTime = dateTime.format(formatter);
		LocalDateTime callStartAt = bookedCall.getCallerJoinedAt().isAfter(bookedCall.getListenerJoinedAt())
				? bookedCall.getCallerJoinedAt()
				: bookedCall.getListenerJoinedAt();
//		context.setVariable("requestedDateTime", bookedCall.getRequestedDateTime().format(formatter));
//		context.setVariable("approvedDateTime", bookedCall.getAcceptedDateTime().format(formatter)); // Replace with the
//																										// actual field
		context.setVariable("callerName", bookedCall.getCaller().getUser().getCallName());
		context.setVariable("initiatedDateTime", callStartAt.format(formatter));
		context.setVariable("completedDateTime", bookedCall.getCallerLeavedAt().format(formatter));
		context.setVariable("totalMinutesTalked", bookedCall.getDurationInMinutes());
		context.setVariable("listenerName", bookedCall.getListener().getUserName());
		context.setVariable("listenerRatePerMin", bookedCall.getListener().getRatePerMinute());
		context.setVariable("totalCostOfCall", bookedCall.getPayableAmount());
		log.info("up");
		// Generate HTML using Thymeleaf

		String htmlContent = springTemplateEngine.process("booking-confirmation-on-demand", context);

		System.out.println(htmlContent);

		// Generate PDF from HTML and return as byte array
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		HtmlConverter.convertToPdf(htmlContent, target);

		return target.toByteArray();
	}

}
