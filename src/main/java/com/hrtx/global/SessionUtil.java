package com.hrtx.global;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hrtx.web.pojo.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * session工具类
 *
 */
@Component
public final class SessionUtil {
	
	private final static String key = "user";
	private final static Logger log = LoggerFactory.getLogger(SessionUtil.class);

	public final static HttpServletRequest getRequest(){
		if(RequestContextHolder.getRequestAttributes()==null)return null;
		HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		return request;
	}

	public final static HttpSession getSession(){
		HttpServletRequest request = getRequest();
		if(request==null)return null;
		return request.getSession();
	}

	/**
	 * 获取放在session中的User对象
	 * @return
	 */
	public final static User getUser(){
		Object obj = getSession().getAttribute(key);
		return obj == null? null:(User)obj ;
	}
	
	/**
	 * 获取放在session中的Corporation对象
	 * @return
	 */
//	public final static Corporation getCorporation(){
//		User user = getUser();
//		return user == null ? null : user.getTbCorporation();
//	}
	
	/**
	 * 获取放在session中的userID
	 * @return
	 */
	public final static long getUserId(){
		User user = getUser();
		return user == null ? -1 : user.getId();
	}
	
	/**
	 * 获取放在session中的corporationId
	 * @return
	 */
//	public final static int getCorporationId(){
//		return getCorporation().getId();
//	}
	
	/**
	 * 获取sesssion中的角色
	 * 
	 */
	public final static String getRoles(){
		return getUser().getRoles();
	}
	
	/**
	 * 判断是否拥有该角色
	 */
	public final boolean hasRole(String roleStr ) {
		return (","+getRoles()+",").indexOf(","+roleStr+",") != -1;
	}
	
	public static final boolean hasPower(long id) {
		Map<Integer,Object> powerMap = getPower();
		if(powerMap == null)return false;
		if(powerMap.containsKey(id)){
			return true;
		}
		return false;
	}
	
	public static final boolean hasPower(PowerConsts power) {
		return hasPower(power.getId());
	}
	
	@SuppressWarnings("unchecked")
	public static final Map<Integer,Object> getPower(){
		Object power = getSession().getAttribute("powers");
		if(power == null)return null;
		return (Map<Integer, Object>) power;
	}
	
	public static Map<Integer,Object> getPower(HttpServletRequest request){
		Object power =request.getSession().getAttribute("powers");
		if(power == null)return null;
		return (Map<Integer, Object>) power;
	}
	
	/**
	 * 是否是超级管理员
	 * @return
	 */
	public final static boolean isSuperAdmin(){
		return getUserId() == 1l;
	}
	
	/**
	 * 是否以AJAX的方式提交的
	 * @return
	 */
	public final static boolean isAjax(){
		String requestType = getRequest().getHeader("X-Requested-With");
		if (requestType != null && requestType.equals("XMLHttpRequest")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 返回JSON数据
	 * @param json
	 */
	public final static void returnJson(String json){
		HttpServletResponse response = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getResponse();
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(json);
		} catch (Exception e) {
			log.error("系统出现异常",e);
		} finally {
			out.flush();
			out.close();
		}
	}
	
	/**
	 * @param city （查询语句中地市id的字段名  eg：c.city）
	 * @param distict （查询语句中区县id的字段名  eg：c.district）
	 * @return  包涵and的sql语句
	 */
	/*public static String getAreaPermission(String city, String distict) {
		Corporation corp = SessionUtil.getCorporation();
		if(corp == null) return " and 1<>1";
		if(SessionUtil.hasPower(PowerConsts.SYSTEMMOUULE_USERLIST_PROVINCE)){
			return " ";
		}
		if(SessionUtil.hasPower(PowerConsts.SYSTEMMOUULE_USERLIST_CITY)){
			return " and "+city+" = "+corp.getCity();
		}
		if(SessionUtil.hasPower(PowerConsts.SYSTEMMOUULE_USERLIST_DISTRICT)){
			return " and "+distict+" = "+corp.getDistrict();
		}
		return " and 1<>1";
	}*/
	
	/**
	 * @param key （查询语句中公司id的字段名  eg：c.id）
	 * @return  包涵and的sql语句
	 */
	/*public static String getAllPermission(String key) {
		Corporation corp = SessionUtil.getCorporation();
		if(corp == null) return " and 1<>1";
		if(SessionUtil.hasPower(PowerConsts.SYSTEMMOUULE_USERLIST_ALL)){
			return " ";
		}else {
			return " and "+key+" = "+corp.getId();
		}
	}*/


	/**
	 * @return 登陆用户真实Ip
	 */
	public static String getUserIp(){
		HttpServletRequest request = getRequest();
		if(request==null)return null;
		String ip = request.getHeader("x-forwarded-for");
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		String[] ips=StringUtils.split(ip,",");
		if(ips.length>1)return ips[0];
		return ip;
	}
	
}
