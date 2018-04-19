package com.hrtx.global;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieHandle {
	public static String _admin_login_key="_admin_login_flag";
	public static String _admin_login_value="_admin_login_flag";
	
	public void addAdminCookie(HttpServletResponse response){
		addCookie(response,_admin_login_key,_admin_login_value);
	}
	public void delAdminCookie(HttpServletRequest request,HttpServletResponse response){
		delCookie(request,response,_admin_login_key);
	}
	public boolean hasAdminCookie(HttpServletRequest request){
		Cookie cookie1=getCookieByName(request,_admin_login_key);
		return cookie1!=null;
	}
	
	public void addCookie(HttpServletResponse response,String _key,String _name){
		Cookie cookie = new Cookie(_key,_name);
		cookie.setMaxAge(5*365*24*60*60);//5年
		cookie.setPath("/");
		response.addCookie(cookie);
	}
	
	public void delCookie(HttpServletRequest request,HttpServletResponse response,String name){
		Cookie cookie1=getCookieByName(request,name);
		if(cookie1==null)return;
		cookie1.setValue(null);  
		cookie1.setMaxAge(0);// 立即销毁cookie  
		cookie1.setPath("/");  
        response.addCookie(cookie1);
    }
	
	public Cookie getCookieByName(HttpServletRequest request,String name){  
		Cookie[] cookies = request.getCookies();
        for (int i = 0;cookies!=null&&i<cookies.length; i++) {
			Cookie cookie1 =cookies[i];
			if(!String.valueOf(name).equals(cookie1.getName()))continue;
			return cookie1;
		}
        return null;
    } 
	
	public String getCookieValue(HttpServletRequest request,String name){  
		Cookie cookie = getCookieByName(request, name);
		if(cookie == null) return "";
		return cookie.getValue();
	} 
	
}
