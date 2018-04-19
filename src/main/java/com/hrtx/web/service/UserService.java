package com.hrtx.web.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Menu;
import com.hrtx.dto.Result;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.UserMapper;
import com.hrtx.web.pojo.User;

@Service
public class UserService {
	
	@Autowired SessionUtil sessionUtil;
	@Autowired private UserMapper userMapper;
	
	public void test() {
		List<User> list=this.userMapper.select(null);
		for (User user : list) {
			System.out.println(user.getName());
		}
	}
	
	public Result pageUser(User user) {
		PageHelper.startPage(user.getStart(),user.getLimit());
		Page<Object> ob=this.userMapper.queryPageList(user);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Map<String, Object> login(String loginName, String pwd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, Object> info = new HashMap<String, Object>();
		User u = null;
		pwd = Utils.encodeByMD5(pwd);
//		u = userMapper.getUserByLoginName(loginName);
		User param = new User();
		param.setLoginName(loginName);
		u = userMapper.selectOne(param);
		if(u == null || !u.getPwd().equals(pwd)){
			info.put("error", "用户不存在或密码错误");
			info.put("user", null);
			return info;
		}
		if(u.getStatus() != 1){
			info.put("error", "用户被冻结");
			info.put("user", null);
			return info;
		}
		List<Map> powers = userMapper.getPower(u.getId());
		if(powers.size()<=0){
			info.put("error", "没有权限");
			info.put("user", null);
			return info;
		}
		
		//加载权限和菜单
		Map<Long, Object> permissionMap = new HashMap<Long, Object>();
		Map<Long, List<Menu>> childMends = new HashMap<Long, List<Menu>>();
		
		List<Menu> mainMenus = new ArrayList<Menu>();
		for (Map map : powers) {
			long permission = NumberUtils.toLong(ObjectUtils.toString(map.get("id")));
			permissionMap.put(permission, null);
			int grade = NumberUtils.toInt(ObjectUtils.toString(map.get("grade")));
			long pid = NumberUtils.toLong(ObjectUtils.toString(map.get("pid")));
			if(grade == 1 || grade == 2){
				Menu m = new Menu(ObjectUtils.toString(map.get("name")), ObjectUtils.toString(map.get("url")), NumberUtils.toLong(ObjectUtils.toString(map.get("id"))), pid, grade);
				if(grade == 1) {
					mainMenus.add(m);
				}else{
					List<Menu> list = childMends.get(pid);
					if(list == null){
						list = new ArrayList<Menu>();
						childMends.put(pid, list);
					}
					list.add(m);
				}
			}
		}
		sessionUtil.getSession().setAttribute("powers", permissionMap);
		sessionUtil.getSession().setAttribute("mainMenus", mainMenus);
		sessionUtil.getSession().setAttribute("childMends", childMends);
		u.setRoles(StringUtils.join(userMapper.findRoles(u.getId()),","));
		info.put("user", u);
		return info;
	}
}
