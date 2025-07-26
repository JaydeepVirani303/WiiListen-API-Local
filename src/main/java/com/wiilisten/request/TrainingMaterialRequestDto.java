package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainingMaterialRequestDto {
	
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

	@JsonProperty("sub_category")
	private String subCategory;

}
