package com.liferay;

import com.liferay.models.PermissionDto;
import com.liferay.models.RoleDto;
import com.liferay.portal.kernel.exception.NoSuchResourceActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.utils.FileUtilities;

import java.io.File;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * @author leonardo.ferreira
 */
@Component(
	immediate = true,
	property = {
		// TODO enter required service properties
	},
	service = RolePermissionConfigurator.class
)
public class RolePermissionConfigurator {

	@Activate
	@Modified
	protected void activate(Map<Object, Object> properties) throws Exception {		
		configRolesFromConfigFolderRelativePath(CONFIG_FOLDER);
	}
	
	/**
	 * @param path - receives the configurations folder's relative path to liferay home 
	 */
	private void configRolesFromConfigFolderRelativePath(String configFolderRelativePath) {
		String configFolderFullPath = 
				_portal.getPortalProperties().
				getProperty("liferay.home") + "/" + configFolderRelativePath;
		
		File configFolder = new File(configFolderFullPath);
		
		for(File configFile : configFolder.listFiles()) {
			configRoleFromFile(configFile);
		}
	}
	
	private void configRoleFromFile(File file) {
		if(file.isFile() && 
				FileUtilities.getFileExtension(file).equals("json")) {
			
			try {
				JSONObject roleJson = FileUtilities.fileToJSONObject(file);
				
				RoleDto roleDto = new RoleDto(roleJson);
				
				configRole(_portal.getDefaultCompanyId(), roleDto);
			} catch (Exception e) {
				_log.error("Failed to configure Role for file: " + file.getPath(), e);
			}
			
		}
	}
	
	private void configRole(long companyId, RoleDto roleDto) 
			throws PortalException {
		
		int scope = ResourceConstants.SCOPE_COMPANY;
		
		Role role = _roleLocalService.fetchRole(
				companyId, 
				roleDto.getName());
		
		String primKey = String.valueOf(role.getCompanyId());
		
		for(PermissionDto permission : roleDto.getPermissions()) {
			try {
				_resourceActionLocalService.getResourceAction(
						permission.getResourceClass(), 
						permission.getActionKey());
			}
			catch (NoSuchResourceActionException nsrae) {
				_resourceActionLocalService.addResourceAction(
						permission.getResourceClass(), 
						permission.getActionKey(),
						1);

				_resourceActionLocalService.checkResourceActions();
			}
			
			_resourcePermissionLocalService.addResourcePermission(
					companyId, 
					permission.getResourceClass(), 
					scope, 
					primKey, 
					role.getRoleId(), 
					permission.getActionKey());
			
			_logPermissionAdded(
					role, 
					permission.getResourceClass(), 
					permission.getActionKey());
		}
	}
	
	private void _logPermissionAdded(Role role, String name, String actionId) {
		String baseMessage = "Added permission %s actionId %s to role %s";

		String roleName = role.getName();

		String message = String.format(baseMessage, name, actionId, roleName);

		_log.info(message);
	}
	
	
	
	private static final Log _log = LogFactoryUtil.getLog(
			RolePermissionConfigurator.class);
	
	private static final String CONFIG_FOLDER="custom-configs/RolePermissions";
	
	@Reference
	private Portal _portal;
	
	@Reference
	private ResourceActionLocalService _resourceActionLocalService;
	
	@Reference
	private ResourcePermissionLocalService
		_resourcePermissionLocalService;
	
	@Reference
	private RoleLocalService _roleLocalService;
}