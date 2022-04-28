package com.liferay;

import com.liferay.models.PermissionDto;
import com.liferay.models.RoleDto;
import com.liferay.portal.kernel.exception.NoSuchResourceActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.CompanyLocalService;
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
		configureRolesFromConfigFolderRelativePath(CONFIG_FOLDER);
	}
	
	/**
	 * @param path - receives the configurations folder's relative path to liferay home 
	 */
	private void configureRolesFromConfigFolderRelativePath(String configFolderRelativePath) {
		String configFolderFullPath = 
				_portal.getPortalProperties().
				getProperty("liferay.home") + "/" + configFolderRelativePath;
		
		File configFolder = new File(configFolderFullPath);
		
		for(File configFile : configFolder.listFiles()) {
			
			if(configFile.isFile() && 
					FileUtilities.getFileExtension(configFile).equals("json")) {
				
				configureRoleFromJSONFile(configFile);
			}
		}
	}
	
	private void configureRoleFromJSONFile(File file) {			
		try {
			JSONObject roleJson = FileUtilities.fileToJSONObject(file);
			
			RoleDto roleDto = new RoleDto(roleJson);
			
			configureRole(roleDto);
		} catch (Exception e) {
			_log.error("Failed to configure Role for file: " + file.getPath(), e);
		}
	}
	
	public void configureRole(RoleDto roleDto) 
			throws PortalException {
		
		long companyId = getDefaultCompanyId();
		
		int scope = ResourceConstants.SCOPE_COMPANY;
		
		Role role = _roleLocalService.fetchRole(
				companyId, 
				roleDto.getName());
		
		String primKey = String.valueOf(role.getCompanyId());
		
		_removeAllResourcePermissionsFromRole(role.getRoleId());
		
		for(PermissionDto permission : roleDto.getPermissions()) {
			_createResourceActionIfNeeded(permission);
			
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
	
	private void _removeAllResourcePermissionsFromRole(long roleId) {
		_resourcePermissionLocalService
			.getRoleResourcePermissions(roleId)
			.forEach(
				(ResourcePermission resourcePermission) -> 
				_resourcePermissionLocalService.deleteResourcePermission(resourcePermission)
			);
	}

	private void _createResourceActionIfNeeded(PermissionDto permission) throws PortalException {
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
	}
	
	private void _logPermissionAdded(Role role, String name, String actionId) {
		String baseMessage = "Added permission %s actionId %s to role %s";

		String roleName = role.getName();

		String message = String.format(baseMessage, name, actionId, roleName);

		_log.info(message);
	}
	
	private long getDefaultCompanyId() {
		long companyId;
		try {
			companyId = _portal.getDefaultCompanyId();
		} catch(ArrayIndexOutOfBoundsException e) {
			Company company = _companyLocalService.getCompanies().get(0);
			companyId = company.getCompanyId();
		}
		return companyId;
	}
	
	
	private static final Log _log = LogFactoryUtil.getLog(
			RolePermissionConfigurator.class);
	
	private static final String CONFIG_FOLDER="custom-configs/RolePermissions";
	
	@Reference
    private CompanyLocalService _companyLocalService;
	
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