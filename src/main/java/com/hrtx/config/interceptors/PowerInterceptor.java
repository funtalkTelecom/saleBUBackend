package com.hrtx.config.interceptors;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hrtx.config.Utils;
import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;

@Component
public class PowerInterceptor implements HandlerInterceptor {
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final String[] no_redirect =new String[]{"flogout"};//不用重定向地址，部分地址重定向会产生死循环
    public static final String COOKIE_NAME = "TT_TOKEN";

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler)throws Exception {
    	request.setAttribute("_t_start_time",System.currentTimeMillis());
    	boolean _need_login=hasNologin(handler);
		if(_need_login) return true;//有非登陆注解      直接通过
		boolean _bool=Utils.isAjax(request);
		String path=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
		String redirect = request.getRequestURL() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
		redirect = URLEncoder.encode(redirect, "utf-8");
		String no_login="{code:'401',msg:'登录超时或未登录，请登录！'}";
		String actionName=request.getRequestURI();
		System.out.println("Method : "+actionName+"   ===="+request.getQueryString());
		String login_page=path+"login-index"+(ArrayUtils.contains(no_redirect,actionName)?"":"?redirectURL="+redirect);
		String power_info_page=path+"/no-power";
		String no_power="{code:'401',msg:'抱歉，权限不足！'}";
		Object user =request.getSession().getAttribute("user");
		if(user == null) {
			if(_bool)Utils.renderJson(response, no_login);
			else response.sendRedirect(login_page);
			return false;
		}
		boolean hasPower = hasPower(request,handler);
		if(!hasPower){
			if(_bool)Utils.renderJson(response, no_power);
			else response.sendRedirect(power_info_page);
			return false;
		}
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,ModelAndView modelAndView) throws Exception {
    	Long _t_start_time=(Long) request.getAttribute("_t_start_time");
    	log.info(String.format("当前请求耗时[%s]ms",(System.currentTimeMillis()-_t_start_time)));
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,Exception ex) throws Exception {
    }
    
    /**
     * 是否需要登录
     * @param handler
     * @return	true 不需要，false 需要
     */
    public static final boolean hasNologin(Object handler){
    	if(handler == null)return false;
		if(!(handler instanceof HandlerMethod))return false;
		HandlerMethod m=(HandlerMethod) handler;
		System.out.println(m.getMethod().getName()+"   ====");
		Powers powers =m.getMethodAnnotation(Powers.class);
		if(powers == null) return false;
		PowerConsts[] powerConsts=powers.value();
		for (PowerConsts pc :powerConsts) {
    		if(pc.getId()==PowerConsts.NOLOGINPOWER.getId())return true;
		}
		return false;
	}
	
//	当前用户是否有权限操作此方法
	public static final boolean hasPower(HttpServletRequest request,Object handler){
		Map<Integer, Object> userPower = SessionUtil.getPower(request);
		return hasPower(request,handler,userPower);
	}
	//	当前用户是否有权限操作此方法
	public static final boolean hasPower(HttpServletRequest request,Object handler,Map<Integer, Object> userPower){
//		if(SessionUtil.isSuperAdmin())return true;//是超级管理员
		if(handler == null)return false;
		if(!(handler instanceof HandlerMethod))return false;
		HandlerMethod m=(HandlerMethod) handler;
		Powers powers =m.getMethodAnnotation(Powers.class);
		if(powers == null)return true;
		PowerConsts[] powerConsts=powers.value();
		Set<Integer> user_power_set=userPower.keySet();
		for (PowerConsts pc :powerConsts) {
			if(user_power_set.contains(pc.getId()))return true;
			if(pc.getId()==PowerConsts.NOLOGINPOWER.getId())return true;
			if(pc.getId()==PowerConsts.NOPOWER.getId())return true;//powerArray.length == 1
		}
		return false;
	}

}