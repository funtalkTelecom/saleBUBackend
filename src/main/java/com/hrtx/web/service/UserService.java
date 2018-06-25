package com.hrtx.web.service;

import java.io.UnsupportedEncodingException;
import java.lang.System;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.github.abel533.entity.Example;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.global.*;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Menu;
import com.hrtx.dto.Result;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Service
public class UserService {
	
	@Autowired SessionUtil sessionUtil;
	@Autowired private UserMapper userMapper;
	@Autowired private ConsumerLogMapper consumerLogMapper;
	@Autowired private ConsumerMapper consumerMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private UserService userService;
	@Autowired private CorporationService corporationService;
	@Autowired private IccidMapper iccidMapper;

	public void test() {
        List allImeis = new ArrayList();
        List imeis = Arrays.asList(new String[]{"A1234","B1234"});
        allImeis.add(CommonMap.create("iccids",imeis).put("itemId", "111111").getData());
        allImeis.add(CommonMap.create("iccids",Arrays.asList(new String[]{"C1234","D1234"})).put("itemId", "222222").getData());
//        System.out.println(iccidMapper.batchInsertTemp(allImeis));
//        User u = new User(10l);
//        userMapper.insert(u);
//	    try{
//	        userService.paytest();
//        }catch (Exception e) {
//	        e.printStackTrace();
//        }
//		User u = new User(10l);
//		userMapper.insert(u);
//		Example example = new Example(User.class);
//		List list = new ArrayList();
//		for (int i = 0; i<2000; i++) {
//			list.add("周元强");
//		}
//		example.createCriteria().andIn("name", list);
//		List<User> users = userMapper.selectByExample(example);
//        System.out.println(users.size()+"----------------------------");
//		userService.paytest("b","b");
//		if(1==1) throw new ServiceException("手动异常");
//		List<User> list=this.userMapper.select(null);
//		for (User user : list) {
//			System.out.println("----"+user.getName());
//		}
	}

    public void newtest() {
        for (int i = 0; i<3; i++) {
            User u = new User(((Integer)(i+3)).longValue());
		    userMapper.insert(u);
		    if(i == 2) throw  new ServiceException("test");
		}
    }

    public void paytest() {
//		User u = new User(11l);
//		userMapper.insert(u);
//		if(1==1) throw new ServiceException("手动异常");
        User u = new User(9l);
        userMapper.insert(u);
        if(true) throw  new ServiceException("test");
//        user.setCorpId(10l);
//        userMapper.updateByPrimaryKey(user);
//        user.setLoginName("eee");
//        Example example = new Example(User.class);
//        example.createCriteria().andEqualTo("corpId",10l);
//        userMapper.updateByPrimaryKeySelective(user);
//        userMapper.updateByExampleSelective(user, example);

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
		//获取storage_id和company_id
		Corporation corporation = corporationService.findCorporationById(u.getCorpId());
//		u.setStorageId();
//		u.setCompanyId();
		
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
		sessionUtil.getSession().setAttribute("corporation", corporation);
		u.setRoles(StringUtils.join(userMapper.findRoles(u.getId()),","));
		info.put("user", u);
		return info;
	}

}
