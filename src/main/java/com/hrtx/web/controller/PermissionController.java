package com.hrtx.web.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.PermissionAllocateForm;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Role;
import com.hrtx.web.pojo.User;
import com.hrtx.web.service.PermissionService;

@RestController
public class PermissionController {

	@Autowired PermissionService permissionService;
	
	@RequestMapping("/user/query-role")
    @Powers({PowerConsts.SYSTEMMOUULE_PERMISSION})
    public ModelAndView queryRole(User user) {
        return new ModelAndView("admin/user/query-role");
    }
	
	@RequestMapping("/list-role")
    @Powers({PowerConsts.SYSTEMMOUULE_PERMISSION})
    public Result listRole(String name) {
    	return permissionService.listRole(name);
    }
    
    @RequestMapping("/permission-tree-node")
    @Powers({PowerConsts.SYSTEMMOUULE_PERMISSION})
    public Result getPermissionTreeNode(PermissionAllocateForm permissionAllocateForm) {
    	List<Map> list = permissionService.getTreeNode(permissionAllocateForm);
    	return new Result(Result.OK, list);
    }
    
    @RequestMapping("/add-role")
    @Powers({PowerConsts.SYSTEMMOUULE_ADDROLE_PERMISSION})
    public Result addRole(String name) {
		if(!name.equals("")){
			List<Role> list = permissionService.findRoleName(name);
			if(list.size()>0) throw new ServiceException("角色["+name+"]已存在");
			permissionService.addRole(name);
		}
		return new Result(Result.OK, "");
    }
    
    @RequestMapping("/update-permission")
    @Powers({PowerConsts.SYSTEMMOUULE_PERMISSION})
    public Result updatePermission(PermissionAllocateForm permissionAllocateForm) {
    	return permissionService.updatePermission(permissionAllocateForm);
    }
}
