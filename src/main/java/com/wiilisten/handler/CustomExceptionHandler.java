package com.wiilisten.handler;

import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.wiilisten.controller.BaseController;
import com.wiilisten.enums.SuccessMsgEnum;
import com.wiilisten.utils.ApplicationResponseConstants;
import com.wiilisten.utils.GenericMessageResponse;

@RestControllerAdvice
public class CustomExceptionHandler extends BaseController{

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> processRuntimeException(RuntimeException e) 
	{
		GenericMessageResponse resp = new GenericMessageResponse();
		if(e.getMessage().equals(getCommonServices().getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()))) 
		{
			resp.setMessage(e.getMessage());
			resp.setCode(ApplicationResponseConstants.INVALID_REQUEST);
			return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(resp);
		}
		if(e.getMessage().equals(getCommonServices().getMessageByCode(SuccessMsgEnum.INVALID_TOKEN_MESSAGE.getCode()))) 
		{
			resp.setMessage(e.getMessage());
			resp.setCode(ApplicationResponseConstants.INVALID_REQUEST);
			return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(resp);
		}
		
		return ResponseEntity.ok(resp);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> accessDeniedExceptionHandler(Exception e) 
	{
		GenericMessageResponse resp = new GenericMessageResponse();
		resp.setMessage(e.getMessage());
		resp.setCode(ApplicationResponseConstants.ACCESS_DENIED);
		
		return ResponseEntity.status(HttpStatusCode.valueOf(401)).body(resp);

	}
}
