package com.hrtx.web.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.pagehelper.PageInfo;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.pojo.Corporation;
import com.hrtx.web.service.AgentService;
import com.hrtx.web.service.CorporationService;
import com.hrtx.web.service.PermissionService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.CookieHandle;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.User;
import com.hrtx.web.service.UserService;

@RestController
public class UserController {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired private UserService userService;
	@Autowired private PermissionService permissionService;
	@Autowired private CorporationService corporationService;
	@Autowired private AgentService agentService;

	@GetMapping("/")
	@Powers({PowerConsts.NOLOGINPOWER})
	public ModelAndView redirectIndex(HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("redirect:/login-index");
		return mv;
	}

    @GetMapping("/index")
    @Powers({PowerConsts.NOLOGINPOWER})
    public ModelAndView index(HttpServletRequest request) {
        return new ModelAndView("admin/index");
    }

    @GetMapping("/login-index")
    @Powers({PowerConsts.NOLOGINPOWER})
    public ModelAndView loginIndex(HttpServletRequest request) {
        return new ModelAndView("admin/login");
    }

    @RequestMapping("/login")
    @Powers({PowerConsts.NOLOGINPOWER})
    public ModelAndView login(User user, HttpServletRequest request, HttpServletResponse response) throws IOException, NoSuchAlgorithmException{
		int remMe = NumberUtils.toInt(request.getParameter("rem-me"));
		Map<String, Object> map = userService.login(user.getLoginName(), user.getPwd());
		if (map.get("user") == null) {
			return new ModelAndView("/admin/login").addObject("errormsg",(String) map.get("error"));
		}else{
			User u = (User) map.get("user");
			request.getSession().setAttribute("user", u);
			String client_info = String.format("[登录请求] ip:%s ua:%s ",request.getRemoteAddr(),request.getHeader("User-Agent"));
			log.info("用户名："+u.getLoginName()+"|"+client_info);
			CookieHandle ch=new CookieHandle();
			ch.delAdminCookie(request, response);
			if(remMe == 1) {
				ch.addCookie(response, "loginName", user.getLoginName());
			}
			String redirectURL = request.getParameter("redirectURL");
			if(StringUtils.isNotBlank(redirectURL)){
				response.sendRedirect(redirectURL);
				return null;
			}
			return new ModelAndView("redirect:/index");
		}
    	
    }
    
    @GetMapping("/login-out")
    @Powers({PowerConsts.NOLOGINPOWER})
    public ModelAndView loginOut(HttpServletRequest request) {
    	request.getSession().invalidate();
        return new ModelAndView("admin/login");
    }
   
    @RequestMapping("/user/query-user")
    @Powers({PowerConsts.SYSTEMMOUULE_USERLIST_LIST})
    public ModelAndView queryUser(User user, HttpServletRequest request) {
        request.setAttribute("roles",((PageInfo)permissionService.listRole(null).getData()).getList());
        request.setAttribute("corps", ((PageInfo)corporationService.pageCorporation(new Corporation()).getData()).getList());
        List angets = ((PageInfo)agentService.pageAgent(new Agent()).getData()).getList();
        request.setAttribute("agents", angets);
        return new ModelAndView("admin/user/query-user");
    }
    
    @RequestMapping("/list-user")
    @Powers({PowerConsts.SYSTEMMOUULE_USERLIST_LIST})
    public Result listUser(User user) {
		return userService.pageUser(user);
    }

	@RequestMapping("edit-user-index")
	@Powers({PowerConsts.SYSTEMMOUULE_USERLIST_ADD})
	public Result editUserIndex(User user){
		return  new Result(Result.OK, userService.getUser(user.getId()));
	}

	/**
	 * 添加用户
	 */
	@RequestMapping("/add-user")
	@Powers({PowerConsts.SYSTEMMOUULE_USERLIST_ADD})
	public Result addUser(User user){
		user.setStatus(1);//使用中
		return userService.saveUser(user);
	}

	/**
	 * 冻结、解冻用户
	 * @return
	 */
	@RequestMapping("/freeze-user")
	@Powers({PowerConsts.SYSTEMMOUULE_USERLIST_ADD})
	public Result freezeUser(User user){
		return userService.freezeUser(user);
	}

	/**
	 * 重置密码
	 * @return
	 */
	@RequestMapping("/reset-pwd")
	@Powers({PowerConsts.SYSTEMMOUULE_USERLIST_ADD})
	public Result resetPwd(User user){
		return userService.resetPwd(user);
	}

	/**
	 * 重置密码
	 * @return
	 */
	@RequestMapping("/update-pwd-index")
	@Powers({PowerConsts.SYSTEMMOUULE_UPDATE_PWD})
	public ModelAndView updatePwdIndex(User user){
		return new ModelAndView("admin/user/update-pwd");
	}

	/**
	 * 重置密码
	 * @return
	 */
	@RequestMapping("/update-pwd")
	@Powers({PowerConsts.SYSTEMMOUULE_UPDATE_PWD})
	public Result updatePwd(HttpServletRequest request){
	    String pwd = ObjectUtils.toString(request.getParameter("pwd"));
	    String confrimPwd = request.getParameter("confrimPwd");
	    if(!pwd.equals(confrimPwd)) return new Result(Result.ERROR, "新密码与确认密码不一致");
        String originPwd = request.getParameter("originPwd");
	    return userService.updatePwd(originPwd, pwd);
	}
}
