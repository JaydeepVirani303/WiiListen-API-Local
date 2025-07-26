package com.wiilisten.utils;


import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The <code>GenericMessageResponse</code> is used to send the generic response to
 * user.
 *
 * @author Hyperlink Infosystem
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class GenericMessageResponse  {
	
	private String code;

	private String message;


	public GenericMessageResponse() {

	}

	public GenericMessageResponse(String code, String message) {
		this.code = code;
		this.message = message;
	}


	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}


	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}


	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
