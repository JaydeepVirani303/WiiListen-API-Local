package com.wiilisten.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenEndedQuestionResponseDto {
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("subCategoryId")
	private Long subCategoryId;
	
	@JsonProperty("sub_category_name")
	private String subCategoryName;
	
	@JsonProperty("question")
	private String question;
	
	@JsonProperty("answer")
	private String answer;
	
	@JsonProperty("active")
	private Boolean active;
	
	@JsonProperty("created_at")
	private Date createdAt;
	
	@JsonProperty("updated_at")
	private Date updatedAt;

}
