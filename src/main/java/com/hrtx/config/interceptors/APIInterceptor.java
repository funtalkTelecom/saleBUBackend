package com.hrtx.config.interceptors;

import com.hrtx.config.Utils;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.web.pojo.User;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class APIInterceptor implements HandlerInterceptor {
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private ApiSessionUtil apiSessionUtil;


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler)throws Exception {
    	request.setAttribute("_t_start_time",System.currentTimeMillis());
    	boolean _need_login=PowerInterceptor.hasNologin(handler);
		if(_need_login) return true;//有非登陆注解      直接通过
		User user=apiSessionUtil.getUser();
		if(user == null) {
			Utils.returnResult(new Result(Result.TIME_OUT,"登录超时或未登录，请登录！"));
			return false;
		}
		//token存在更新过期时间
		this.apiSessionUtil.updateExpire(this.apiSessionUtil.getTokenStr());
		Map<Integer, Object> userPower=new HashMap<>();
		boolean hasPower =PowerInterceptor.hasPower(request,handler,userPower);
		if(!hasPower){
			Utils.returnResult(new Result(Result.NOPOWER,"抱歉，权限不足！"));
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

}