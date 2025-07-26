package com.wiilisten.service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailService {
	
	private final Logger log = LoggerFactory.getLogger(EmailService.class);
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Value("${spring.mail.username}")
	private  String sender;
	
	@Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
      
        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(sender);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        }  catch (MailException | MessagingException e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }
	
	
	public boolean sendEmailWithThymeLeaf(String to, String subject, String bodyTemplate,
			Map<String, Object> properties) throws MessagingException {
		try {

			MimeMessage mimeMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,
					MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

			mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(sender);
			mimeMessageHelper.setTo(to);
			mimeMessageHelper.setSubject(subject);

			Context context = new Context();

			properties.put(bodyTemplate, context);

			context.setVariables(properties);

			String html = templateEngine.process(bodyTemplate, context);
			mimeMessageHelper.setText(html, true);

			javaMailSender.send(mimeMessage);

			return true;
		} catch (Exception e) {
			System.err.println("in mail service exception");
			e.printStackTrace();
			return false;
		}

	}

}
