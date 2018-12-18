package com.hrtx.web.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.dto.PermissionAllocateForm;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.PermissionMapper;
import com.hrtx.web.mapper.UserMapper;
import com.hrtx.web.pojo.Permission;
import com.hrtx.web.pojo.Role;
import com.hrtx.web.pojo.RolePermission;
import com.hrtx.web.pojo.UserPermission;

@Service
public class PermissionService {
	
	@Autowired private PermissionMapper permissionMapper;
	@Autowired private UserMapper userMapper;
	
	public Result listRole(String name) {
		Page<Object> list = this.permissionMapper.listRole(name);
		PageInfo<Object> pm = new PageInfo<Object>(list);
		return new Result(Result.OK, pm);
	}

	public List<Map> getTreeNode(PermissionAllocateForm permissionAllocateForm) {
		int roleId = permissionAllocateForm.getNodeId();
//		List<Permission> ps = permissionMapper.findPermission(1);
		Permission permission = new Permission();
		permission.setPid(1);
		List<Permission> ps = permissionMapper.select(permission);
		return toTreeNode(ps, roleId, permissionAllocateForm.getNodeValue());
	}
	
	//将对象转成树节点对象
	@SuppressWarnings("unchecked")
	private List<Map> toTreeNode(List<Permission> node, int roleId, String type ){
		Map permission = new HashMap();
		for (Permission p : node) {
			String permissionId = p.getId()+"";
			String permissionName = p.getName();
			Map first = new HashMap();
			first.put("id", permissionId);
			first.put("text", permissionName);
			first.put("value", permissionId);
			first.put("hasChildren", true);
			first.put("complete", true);
			first.put("showcheck", true);
			first.put("isexpand", true);
			first.put("ChildNodes", new HashMap());
			permission.put(permissionId, first);
			Map ChildNodes1 = (Map) first.get("ChildNodes");
			
			Permission permission2 = new Permission();
			permission2.setPid(p.getId());
			List<Permission> Childrens1 = permissionMapper.select(permission2);
//			Set<Permission> Childrens1 = p.getChildrens();
			for (Permission child : Childrens1) {
				Map second = new HashMap();
				second.put("id", child.getId()+"");
				second.put("text", child.getName());
				second.put("value", child.getId()+"");
				if(child.getLeaf() == 1){
					second.put("hasChildren", false);
				}else {
					second.put("hasChildren", true);
				}
				second.put("complete", true);
				second.put("showcheck", true);
				second.put("ChildNodes", new HashMap());
				ChildNodes1.put(child.getId()+"", second);
				Map ChildNodes2 = (Map) second.get("ChildNodes");
				
				Permission permission3 = new Permission();
				permission3.setPid(child.getId());
				List<Permission> Childrens2 = permissionMapper.select(permission3);
//				Set<Permission> Childrens2 = child.getChildrens();
				for (Permission child1 : Childrens2) {
					Map third = new HashMap();
					third.put("id", child1.getId()+"");
					third.put("text", child1.getName());
					third.put("value", child1.getId()+"");
					third.put("hasChildren", false);
					third.put("complete", true);
					third.put("showcheck", true);
					ChildNodes2.put(child1.getId()+"", third);
				}
				
			}
		}
		List permissionNodes = new ArrayList(permission.values());
		for (Object permissionObject : permissionNodes) {
			Map permissionNode = (Map) permissionObject;
			Map seconds = (Map) permissionNode.get("ChildNodes");
			List secondNodes = new ArrayList(seconds.values());
			permissionNode.put("checkstate", checkState(secondNodes,roleId, type));
			permissionNode.put("ChildNodes", secondNodes);
			for (Object secondNodeObject : secondNodes) {
				Map secondNode = (Map) secondNodeObject;
				Map thirds = (Map) secondNode.get("ChildNodes");
				List thirdNodes = new ArrayList(thirds.values());
				if((Boolean) secondNode.get("hasChildren")){
					secondNode.put("checkstate", checkState(thirdNodes, roleId, type));
				}else {
					List temp = new ArrayList<Map>();
					Map temp1 = new HashMap<String, Object>();
					temp1.put("id", (String) secondNode.get("id"));
					temp.add(temp1);
					secondNode.put("checkstate", checkState(temp, roleId, type));
				}
				secondNode.put("ChildNodes", thirdNodes);
				for (Object thirdNodeObject : thirdNodes) {
					Map thirdNode = (Map) thirdNodeObject;
					List temp = new ArrayList<Map>();
					Map temp1 = new HashMap<String, Object>();
					temp1.put("id", (String) thirdNode.get("id"));
					temp.add(temp1);
					thirdNode.put("checkstate", checkState(temp, roleId, type));
				}
			}
		}
		return permissionNodes;
	}
	
	private String checkState(List nodes, int roleId, String type){
		StringBuffer sb = new StringBuffer();
		int len = nodes.size();
		for (int i = 0; i < len; i++) {
			Map node = (Map) nodes.get(i);
			String permissionIdStr = (String) node.get("id");
			sb.append(permissionIdStr+",");
		}
		int count = 0;
		if("1".equals(type)){//角色
//			count = rolePermissionDao.count(sb.toString(), roleId);
			count = permissionMapper.rolePermissionCount(sb.toString(), roleId);
		}else{//用户
//			System.err.println(sb.toString()+"-------------------------------"+roleId);
//			count = rolePermissionDao.userPermissionCount(sb.toString(), roleId);
			count = permissionMapper.userPermissionCount(sb.toString(), roleId);
		}
		if(count >0 && count <len) {
			return "2";
		}
		if(count == len && count !=0 ) {
			return "1";
		}else {
			return "0";
		}

	}

	public List<Role> findRoleName(String name) {
		return permissionMapper.findRoleName(name);
	}

	public void addRole(String name) {
		permissionMapper.addRole(new Role(name, 1, 0));
	}

	public Result updatePermission(PermissionAllocateForm permissionAllocateForm) {
		int roleId = permissionAllocateForm.getRoleId();
		int property = permissionAllocateForm.getProperty();
		if(roleId == 0l || (property != 1 && property != 2)) throw new ServiceException("参数异常");
		if(property == 1){
			permissionMapper.deleByRoleId(roleId);
			String permissionIds = permissionAllocateForm.getPermissionIds();
			String[] ids = permissionIds.split(",");
			if(ids.length > 1) {
				for(String id : ids) {
					RolePermission rp = new RolePermission();
					rp.setRoleId(roleId);
					rp.setPermission(NumberUtils.toInt(id));
					permissionMapper.addRolePermission(rp);
				}
			}
		}else{
			permissionMapper.deleByUserId(roleId);
			String permissionIds = permissionAllocateForm.getPermissionIds();
			String[] ids = permissionIds.split(",");
			if(ids.length > 1) {
				for(String id : ids) {
					UserPermission up = new UserPermission();
					up.setUserId(roleId);
					up.setPermission(NumberUtils.toInt(id));
					permissionMapper.addUserPermission(up);
				}
			}
		}
		return null;
	}

	public void checkOrInsertPermission() {
		Map<Integer, Object> map = new HashMap<Integer, Object>();
		List<Permission> permissions = permissionMapper.select(null);
		for (Permission permission : permissions) {
			map.put(permission.getId(), null);
		}
		PowerConsts[] powers = PowerConsts.values();
		for (PowerConsts power : powers) {
			if(!map.containsKey(power.getId())){
//				roleDao.inserPermission(power);
				permissionMapper.insert(new Permission(power.getId(), power.getPid(), power.getPowerName(), "", power.getSeq(), power.name(), power.getLeaf(), power.getGrade(), power.getUrl()));
			}
		}
	}

	public void distributeRole(int userId, String roles) {
		userMapper.deleteRoleByUserId(userId);
		String[] rolesArray=StringUtils.split(roles ,",");
		if(rolesArray == null||rolesArray.length==0)return;
		for(String s :rolesArray){
			int roleId = NumberUtils.toInt(s.trim());
			userMapper.insertUr(roleId, userId);
		}
	}
}
