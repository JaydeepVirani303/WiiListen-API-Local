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
public class TrainingMaterialResponseDto {
	
	@JsonProperty("id")
	private Long id;

	@JsonProperty("title")
	private String title;
	
	@JsonProperty("thumbnail_image")
	private String thumbnailImage;
	
	@JsonProperty("content_url")
	private String contentUrl;
	
	@JsonProperty("content_type")
	private String contentType; // TUTORIAL/TRAINING
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("material_file_type")
	private String materialFileType; // VIDEO/PDF
	
	@JsonProperty("active")
	private Boolean active;

	@JsonProperty("created_at")
	private Date createdAt;

	@JsonProperty("updated_at")
	private Date updatedAt;

	@JsonProperty("sub_category")
	private String subCategory;


}
