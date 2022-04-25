package com.liferay.models;

import com.liferay.models.exceptions.ModelConversionException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

public class PermissionDto {

	private String actionKey; 
	
	private String resourceClassPath;

	public PermissionDto(String actionKey, String resourceClassPath) {
		this.actionKey = actionKey;
		this.resourceClassPath = resourceClassPath;
	}
	
	public PermissionDto(JSONObject permissionJson) throws ModelConversionException {
		this.actionKey = permissionJson.getString(ModelJSONConstants.ACTION, "");
		this.resourceClassPath = permissionJson.getString(ModelJSONConstants.RESOURCE, "");
		
		if(Validator.isNull(actionKey) || Validator.isNull(resourceClassPath)) {
			throw new ModelConversionException("Invalid permission: action or resource missing.");
		}
	}

	public String getActionKey() {
		return actionKey;
	}

	public void setActionKey(String actionKey) {
		this.actionKey = actionKey;
	}

	public String getResourceClass() {
		return resourceClassPath;
	}

	public void setResourceClass(String resourceClassPath) {
		this.resourceClassPath = resourceClassPath;
	}
}
