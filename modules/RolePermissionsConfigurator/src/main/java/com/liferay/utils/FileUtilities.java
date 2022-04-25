package com.liferay.utils;

import com.liferay.exceptions.JSONConversionException;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.util.StringUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class FileUtilities {	
	
	public static String getFileExtension(File file) {
		if(file.isFile()) {
			int extensionSeparatorIndex = file.getName().lastIndexOf('.');
			if(extensionSeparatorIndex == -1) return "";
			String fileExtension = file.getName().substring(extensionSeparatorIndex + 1);
			return fileExtension;
		}
		return "";
	}
	
	public static JSONObject fileToJSONObject(File file) throws JSONConversionException {
		try(FileInputStream serversStream = new FileInputStream(file))
		{
			return JSONFactoryUtil.createJSONObject(
				StringUtil.read(serversStream));
		}
		catch (FileNotFoundException e) {
			throw new JSONConversionException("Failed to find file.");
		}
		catch (IOException e) {
			throw new JSONConversionException("Failed to read file.");
		}
		catch (JSONException e) {
			throw new JSONConversionException("Failed to convert file to JSON.");
		}
	}
}
