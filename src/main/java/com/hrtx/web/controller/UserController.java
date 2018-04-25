package com.hrtx.web.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	
	@Autowired UserService userService;
	
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
			return new ModelAndView("redirect:/user/query-user");
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
    public ModelAndView queryUser(User user) {
        return new ModelAndView("admin/user/query-user");
    }
    
    @RequestMapping("/list-user")
    @Powers({PowerConsts.SYSTEMMOUULE_USERLIST_LIST})
    public Result listUser(User user) {
//		if(1==1) throw new ServiceException("test");

		return userService.pageUser(user);
    }
}
