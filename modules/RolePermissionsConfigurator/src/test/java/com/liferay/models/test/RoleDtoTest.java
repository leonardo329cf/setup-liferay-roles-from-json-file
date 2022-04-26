package com.liferay.models.test;

import com.liferay.models.ModelJSONConstants;
import com.liferay.models.RoleDto;
import com.liferay.models.exceptions.ModelConversionException;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RoleDtoTest {
	
	@Test(expected = ModelConversionException.class)
	public void constructor_throwsException_when_roleNameIsEmpty() {
		 Mockito.when(_jsonObject.getString(ModelJSONConstants.ROLE_NAME)).thenReturn("");
			 
		 new RoleDto(_jsonObject);
		
	}
	
	@Test(expected = ModelConversionException.class)
	public void constructor_throwsException_when_permissionsThrowsException() {
		
		Mockito.when(_jsonObject.getString(ModelJSONConstants.ROLE_NAME)).thenReturn("validName");
		 
		Mockito.when(_jsonObject.getJSONArray(ModelJSONConstants.PERMISSIONS)).thenReturn(_jsonArray);
		 
		Mockito.doThrow(RuntimeException.class).when(_jsonArray).forEach(Mockito.any());
		 
		new RoleDto(_jsonObject);
		
	}
		 
	@Mock
	private JSONObject _jsonObject;
	
	@Mock
	private JSONArray _jsonArray;
}