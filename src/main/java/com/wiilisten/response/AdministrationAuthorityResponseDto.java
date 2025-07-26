package com.wiilisten.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdministrationAuthorityResponseDto {
	
	private Long id;
	
	@JsonProperty("admin_id")
	private Long adminId;
	
	@JsonProperty("module_id")
	private Long moduleId;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("contact")
	private String contact;
	
	@JsonProperty("module_name")
	private String moduleName;
	
	@JsonProperty("can_add")
	private Boolean canAdd;
	
	@JsonProperty("can_update")
	private Boolean canUpdate;
	
	@JsonProperty("can_delete")
	private Boolean canDelete;
	
	@JsonProperty("can_view")
	private Boolean canView;
	
	@JsonProperty("active")
	private Boolean active;
	
	@JsonProperty("created_at")
	private Date createdAt;
	
	@JsonProperty("updated_at")
	private Date updatedAt;


}
