package com.liferay.models;

import com.liferay.models.exceptions.ModelConversionException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RoleDto {

	private String roleName;
	
	private List<PermissionDto> permissions = new ArrayList<PermissionDto>();

	public RoleDto(String roleName, List<PermissionDto> permissions) {
		this.roleName = roleName;
		this.permissions.addAll(permissions);
	}
	
	public RoleDto(JSONObject roleJson) throws ModelConversionException {
		this.roleName = roleJson.getString(ModelJSONConstants.ROLE_NAME);

		JSONArray permissionsJsonArray = roleJson.getJSONArray(ModelJSONConstants.PERMISSIONS);
		
		if(Validator.isNull(roleName)) {
			throw new ModelConversionException("Invalid role: roleName missing.");
		}
		
		try {
		permissionsJsonArray.forEach(
				(Object permissionJsonObject) -> permissions.add(new PermissionDto((JSONObject)permissionJsonObject)));
		}
		catch(Exception e) {
			throw new ModelConversionException("Invalid Permission: one or more permission entries can't be converted to JSONObject.");
		}
	}

	public String getName() {
		return roleName;
	}

	public void setName(String name) {
		this.roleName = name;
	}

	public List<PermissionDto> getPermissions() {
		return permissions;
	}
}
