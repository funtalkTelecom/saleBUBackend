package com.hrtx.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Permission;
import com.hrtx.web.pojo.Role;
import com.hrtx.web.pojo.RolePermission;
import com.hrtx.web.pojo.UserPermission;

public interface PermissionMapper extends Mapper<Permission>,BaseMapper<Permission>{

	public Page<Object> listRole(String name);

	public int rolePermissionCount(@Param("ids")String ids, @Param("roleId")long roleId);

	public int userPermissionCount(@Param("ids")String ids, @Param("userId")long userId);

	public List<Role> findRoleName(String name);

	public void addRole(@Param("role")Role role);

	public void deleByRoleId(@Param("roleId")long roleId);

	public void deleByUserId(@Param("userId")long userId);

	public void addRolePermission(@Param("param")RolePermission rp);

	public void addUserPermission(@Param("param")UserPermission up);
	
}
