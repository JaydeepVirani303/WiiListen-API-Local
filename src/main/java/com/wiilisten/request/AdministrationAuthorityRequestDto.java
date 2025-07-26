package com.wiilisten.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdministrationAuthorityRequestDto {

	@JsonProperty("module_id")
	private Long moduleId;
	
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

}
