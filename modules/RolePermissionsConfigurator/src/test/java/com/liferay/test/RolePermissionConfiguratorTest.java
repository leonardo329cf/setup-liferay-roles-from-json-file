package com.liferay.test;

import com.liferay.RolePermissionConfigurator;
import com.liferay.models.PermissionDto;
import com.liferay.models.RoleDto;
import com.liferay.portal.kernel.exception.NoSuchResourceActionException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.Portal;

import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RolePermissionConfiguratorTest {
	
	@BeforeClass
	public static void setup() {
		_log = Mockito.mock(Log.class);
		logFactoryUtil = Mockito.mockStatic(LogFactoryUtil.class);
		logFactoryUtil.when(() -> LogFactoryUtil.getLog(RolePermissionConfigurator.class))
		.thenReturn(_log);
	}
	
	@AfterClass
	public static void close() {
		logFactoryUtil.close();
	}
	
	@Test
	public void configRole_callsAddResourcePermission_after_addingResourceAction() throws PortalException {
	
		long companyId = 1L;
		
		String roleName = "roleName";
		long roleId = 1L;
		String actionKey = "actionKey";
		String resource = "resource";
		
		PermissionDto permission = new PermissionDto(actionKey, resource);
		
		ArrayList<PermissionDto> permissions = new ArrayList<PermissionDto>();
		permissions.add(permission);
		
		RoleDto roleDto = new RoleDto(roleName, permissions);
		
		Mockito.when(_roleLocalService.fetchRole(companyId, roleName)).thenReturn(_role);
		
		Mockito.when(_role.getCompanyId()).thenReturn(companyId);
		
		Mockito.when(_role.getRoleId()).thenReturn(roleId);
		
		Mockito.when(_resourceActionLocalService.getResourceAction(
						Mockito.anyString(), 
						Mockito.anyString())).thenThrow(new NoSuchResourceActionException());
		
		Mockito.when(_resourceActionLocalService.addResourceAction(
				Mockito.anyString(),
				Mockito.anyString(),
				Mockito.anyLong())
				).thenReturn(_resourceAction);
			
		
		_rolePermissionConfigurator.configRole(companyId, roleDto);
		
		
		Mockito.verify(_resourcePermissionLocalService)
		.addResourcePermission(
				companyId, 
				resource,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId),
				roleId,
				actionKey);
	}
	
	@Test
	public void configRole_callsAddResourceAction_when_noSuchResourceActionException() throws PortalException {
	
		long companyId = 1L;
		
		String roleName = "roleName";
		long roleId = 1L;
		String actionKey = "actionKey";
		String resource = "resource";
		
		PermissionDto permission = new PermissionDto(actionKey, resource);
		
		ArrayList<PermissionDto> permissions = new ArrayList<PermissionDto>();
		permissions.add(permission);
		
		RoleDto roleDto = new RoleDto(roleName, permissions);
		
		Mockito.when(_roleLocalService.fetchRole(companyId, roleName)).thenReturn(_role);
		
		Mockito.when(_role.getCompanyId()).thenReturn(companyId);
		
		Mockito.when(_role.getRoleId()).thenReturn(roleId);
		
		Mockito.when(_resourceActionLocalService.getResourceAction(
						Mockito.anyString(), 
						Mockito.anyString())).thenThrow(new NoSuchResourceActionException());
		
			
		_rolePermissionConfigurator.configRole(companyId, roleDto);
		
		
		Mockito.verify(_resourceActionLocalService)
			.addResourceAction(
				resource,
				actionKey,
				1L);
	}
	
	@Test
	public void configRole_callsAddResourcePermission_when_resourceActionExists() throws PortalException {
	
		long companyId = 1L;
		
		String roleName = "roleName";
		long roleId = 1L;
		String actionKey = "actionKey";
		String resource = "resource";
		
		PermissionDto permission = new PermissionDto(actionKey, resource);
		
		ArrayList<PermissionDto> permissions = new ArrayList<PermissionDto>();
		permissions.add(permission);
		
		RoleDto roleDto = new RoleDto(roleName, permissions);
		
		Mockito.when(_roleLocalService.fetchRole(companyId, roleName)).thenReturn(_role);
		
		Mockito.when(_role.getCompanyId()).thenReturn(companyId);
		
		Mockito.when(_role.getRoleId()).thenReturn(roleId);
		
		Mockito.when(_resourceActionLocalService.getResourceAction(
						Mockito.anyString(), 
						Mockito.anyString())).thenReturn(_resourceAction);
		
			
		_rolePermissionConfigurator.configRole(companyId, roleDto);
		
		
		Mockito.verify(_resourcePermissionLocalService)
			.addResourcePermission(
				companyId, 
				resource,
				ResourceConstants.SCOPE_COMPANY,
				String.valueOf(companyId),
				roleId,
				actionKey);
	}
	
	private static MockedStatic<LogFactoryUtil> logFactoryUtil;
	
	private static Log _log;
	
	@Mock
	private ResourceAction _resourceAction;
	
	@Mock
	private Role _role;
	
	@Mock
	private ResourceActionLocalService _resourceActionLocalService;
	
	@Mock
	private ResourcePermissionLocalService
		_resourcePermissionLocalService;
	
	@Mock
	private RoleLocalService _roleLocalService;
	
	@Mock
	private Portal _portal;
	
	@InjectMocks
	private RolePermissionConfigurator _rolePermissionConfigurator;
}
