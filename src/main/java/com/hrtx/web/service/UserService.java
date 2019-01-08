package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.dto.Menu;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.mapper.ConsumerLogMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.mapper.IccidMapper;
import com.hrtx.web.mapper.UserMapper;
import com.hrtx.web.pojo.Corporation;
import com.hrtx.web.pojo.User;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserService extends BaseService {
	
	@Autowired SessionUtil sessionUtil;
	@Autowired private UserMapper userMapper;
	@Autowired private ConsumerLogMapper consumerLogMapper;
	@Autowired private ConsumerMapper consumerMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private UserService userService;
	@Autowired private CorporationService corporationService;
	@Autowired private IccidMapper iccidMapper;
	@Autowired private PermissionService permissionService;

	public void test1(int i) {
		User u = new User(i);
		userMapper.insert(u);
//		if(i==16) throw  new ServiceException("test");
	}
	public void test() {
		User u = new User(100);
		userMapper.insert(u);
//		try{
//			userService.paytest();
//		} catch (Exception e) {
//			System.out.println(e.getMessage()+"----------捕捉异常");
//		}
//	    Example example = new Example(FundOrder.class);
//	    example.createCriteria();
//	    List<User> u = userMapper.selectByExample(new Example(User.class).createCriteria().andEqualTo("id", 1));
//        System.out.println(u.get(0).getLoginName());
//		System.out.println("test begain--------------------");
//		userMapper.test();
//		System.out.println("test end--------------------");
//        List allImeis = new ArrayList();
//        List imeis = Arrays.asList(new String[]{"A1234","B1234"});
//        allImeis.add(CommonMap.create("iccids",imeis).put("itemId", "111111").getData());
//        allImeis.add(CommonMap.create("iccids",Arrays.asList(new String[]{"C1234","D1234"})).put("itemId", "222222").getData());
//        System.out.println(iccidMapper.batchInsertTemp(allImeis, 1111111l));
//		User u1 = new User(11l);
//		u1.setLoginName("111");
//		userMapper.insert(u1);
//		System.out.println("aaaaaaaaaaaaaaaa");
	}

    public void newtest(int i) {
//		User u = new User(((Integer)i).longValue());
//		userMapper.insert(u);
		userService.test1(i);
//        for (int i = 0; i<3; i++) {
//            User u = new User(((Integer)(i+3)).longValue());
//		    userMapper.insert(u);
//		    if(i == 2) throw  new ServiceException("test");
//		}
    }

    public void paytest() {
//		User u = new User(11l);
//		userMapper.insert(u);
//		if(1==1) throw new ServiceException("手动异常");
		for (int i = 15; i <17 ; i++) {
			User u = new User(10);
			userMapper.insert(u);
			userService.newtest(i);
			if(i == 15) throw new ServiceException("test");
		}
//        if(true) throw  new ServiceException("test");
//        user.setCorpId(10l);
//        userMapper.updateByPrimaryKey(user);
//        user.setLoginName("eee");
//        Example example = new Example(User.class);
//        example.createCriteria().andEqualTo("corpId",10l);
//        userMapper.updateByPrimaryKeySelective(user);
//        userMapper.updateByExampleSelective(user, example);

	}

	public Result pageUser(User user) {
		PageHelper.startPage(user.startToPageNum(),user.getLimit());
		Page<Object> ob=this.userMapper.queryPageList(user);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Map<String, Object> login(String loginName, String pwd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		Map<String, Object> info = new HashMap<String, Object>();
		User u = null;
//		pwd = Utils.encodeByMD5(pwd);
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
		Map<Integer, Object> permissionMap = new HashMap<Integer, Object>();
		Map<Integer, List<Menu>> childMends = new HashMap<Integer, List<Menu>>();
		
		List<Menu> mainMenus = new ArrayList<Menu>();
		for (Map map : powers) {
			int permission = NumberUtils.toInt(ObjectUtils.toString(map.get("id")));
			permissionMap.put(permission, null);
			int grade = NumberUtils.toInt(ObjectUtils.toString(map.get("grade")));
			int pid = NumberUtils.toInt(ObjectUtils.toString(map.get("pid")));
			if(grade == 1 || grade == 2){
				Menu m = new Menu(ObjectUtils.toString(map.get("name")), ObjectUtils.toString(map.get("url")), NumberUtils.toInt(ObjectUtils.toString(map.get("id"))), pid, grade);
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

    public User getUser(Integer id) {
		User u = userMapper.selectByPrimaryKey(id);
		List<Map> list = userMapper.finRolesByUserId(id);
		String roles="";
		for (int i = 0; i <list.size(); i++) {
			Map map=list.get(i);
			if(map.get("userid")!=null)roles+=map.get("id")+",";
		}
		u.setRoles(roles);
		return u;
    }

	public Result saveUser(User user) {
        String loginName =ObjectUtils.toString(user.getLoginName(), " ");
        int id = NumberUtils.toInt(String.valueOf(user.getId()));
        Example example = new Example(User.class);
        example.createCriteria().andEqualTo("loginName", loginName);
        List<User> list =  userMapper.selectByExample(example);
        User u = list.size() == 0 ? null : list.get(0);
        if(u != null && u.getId() != id && StringUtils.equals(u.getLoginName(),loginName)) return new Result(Result.ERROR,"["+loginName+"]已存在");
        if(id==0){
            if(StringUtils.isBlank(user.getLoginName()) || StringUtils.isBlank(user.getName()) || user.getCorpId() == null || user.getCorpId() == 0 || user.getAgentId() == null || user.getAgentId() == 0 || StringUtils.isBlank(user.getPhone())) {
                return new Result(Result.ERROR,"必填参数未填写");
            }
            if(!user.getPhone().matches(RegexConsts.REGEX_MOBILE_COMMON)) return new Result(Result.ERROR,"请填写正确的手机号码");
            String pwd = Utils.randomNoByDateTime(6);
            try {
                user.setPwd(Utils.encodeByMD5(pwd));
            } catch (Exception e) {
                log.error("",e);
                return new Result(Result.ERROR,"加密异常");
            }
//            user.setId(user.getGeneralId());
            user.setAddUser(SessionUtil.getUserId());
            user.setAddDate(new Date());
            user.setIsDel(0);
            userMapper.insert(user);
            id = user.getId();
            Messager.send(user.getPhone(), "尊敬的用户,系统给您分配的平台账号" + user.getLoginName()+"已生效，密码是："+ pwd + ",感谢您的使用！");
        }else{//修改
            if(StringUtils.isBlank(user.getName()) || user.getCorpId() == null || user.getCorpId() == 0 || user.getAgentId() == null || user.getAgentId() == 0 || StringUtils.isBlank(user.getPhone())) {
                return new Result(Result.ERROR,"必填参数未填写");
            }
            if(!user.getPhone().matches(RegexConsts.REGEX_MOBILE_COMMON)) return new Result(Result.ERROR,"请填写正确的手机号码");
            u = userMapper.selectByPrimaryKey(id);
            if(u == null) return new Result(Result.ERROR, "用户不存在");
            u.setName(user.getName());
            u.setCorpId(user.getCorpId());
            u.setAgentId(user.getAgentId());
            u.setPhone(user.getPhone());
            userMapper.updateByPrimaryKeySelective(u);
        }
        permissionService.distributeRole(id, user.getRoles());//分配角色
        return new Result(Result.OK, "提交成功");
	}

	public Result freezeUser(User user) {
		int status = NumberUtils.toInt(String.valueOf(user.getStatus()));
		if(NumberUtils.toLong(String.valueOf(user.getId())) == 0 || (status !=1 && status != 2)) return new Result(Result.ERROR, "参数异常");
		User updateUser = new User();
		updateUser.setId(user.getId());
		updateUser.setStatus(status);
		int count = userMapper.updateByPrimaryKeySelective(updateUser);
		if(count != 1) return new Result(Result.ERROR, "提交失败");
		return new Result(Result.OK, "提交成功");
	}

    public Result resetPwd(User user) {
	    user = userMapper.selectByPrimaryKey(user.getId());
	    if(user == null) return new Result(Result.ERROR, "用户不存在");
        String pwd = Utils.randomNoByDateTime(6);
        try {
            user.setPwd(Utils.encodeByMD5(pwd));
        } catch (Exception e) {
            log.error("",e);
            return new Result(Result.ERROR,"加密异常");
        }
        int count = userMapper.updateByPrimaryKeySelective(user);
        if(count != 1) return new Result(Result.ERROR,"重置失败");
        Messager.send(user.getPhone(), "尊敬的用户,您分配的平台账号" + user.getLoginName()+"已重置密码，密码是："+ pwd + ",感谢您的使用！");
        return new Result(Result.OK, "重置成功");
    }

    public Result updatePwd(String originPwd, String pwd) {
        User user = userMapper.selectByPrimaryKey(SessionUtil.getUserId());
        if(user == null) return new Result(Result.ERROR, "用户不存在");
        try {
            originPwd = Utils.encodeByMD5(originPwd);
            if(!originPwd.equals(user.getPwd())) return new Result(Result.ERROR, "原始密码错误");
            user.setPwd(Utils.encodeByMD5(pwd));
        } catch (Exception e) {
            log.error("",e);
            return new Result(Result.ERROR,"加密异常");
        }
        int count = userMapper.updateByPrimaryKeySelective(user);
        if(count != 1) return new Result(Result.ERROR,"修改密码失败");
        return new Result(Result.OK, "修改密码成功");
    }
}
