package com.wiilisten.request;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Mail {
	
	private String from;
	private String to;
	private String subject;
	private Map<String, Object> content;
	
	public Mail(String from, String to, String subject) {
		super();
		this.from = from;
		this.to = to;
		this.subject = subject;
	}
	
	public Map<String, Object> getContent() {
		return content;
	}
}
