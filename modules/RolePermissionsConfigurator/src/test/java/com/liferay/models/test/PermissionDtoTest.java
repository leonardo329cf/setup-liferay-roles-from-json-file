package com.liferay.models.test;

import com.liferay.models.ModelJSONConstants;
import com.liferay.models.PermissionDto;
import com.liferay.models.exceptions.ModelConversionException;
import com.liferay.portal.kernel.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PermissionDtoTest {
	
	@Test(expected = ModelConversionException.class)
	public void constructor_throwsException_when_permissionActionIsEmpty() {
		Mockito.when(_jsonObject.getString(ModelJSONConstants.ACTION, "")).thenReturn("");
		 
		Mockito.when(_jsonObject.getString(ModelJSONConstants.RESOURCE, "")).thenReturn("validResource");
			 
		 new PermissionDto(_jsonObject);
		
	}
	
	@Test(expected = ModelConversionException.class)
	public void constructor_throwsException_when_resourceIsEmpty() {
		
		Mockito.when(_jsonObject.getString(ModelJSONConstants.ACTION, "")).thenReturn("validAction");
		 
		Mockito.when(_jsonObject.getString(ModelJSONConstants.RESOURCE, "")).thenReturn("");
		 		 
		new PermissionDto(_jsonObject);
		
	}
		 
	@Mock
	private JSONObject _jsonObject;
}