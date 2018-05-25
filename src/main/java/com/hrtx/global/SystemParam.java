package com.hrtx.global;

import com.hrtx.web.service.SystemService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class SystemParam {
	private final static Logger log = LoggerFactory.getLogger(SystemParam.class);
	
	private final static Map<String, String> params = new HashMap<String, String>();
	
	/**
	 * 加载配置文件
	 * @return
	 */
	public final static void load() {
		params.clear();
		try {
			SystemService systemService  = (SystemService) ContextUtils.getContext().getBean("systemService");
			List<Map> list = systemService.findSystemParam();
			for (Map map : list) {
				params.put(ObjectUtils.toString(map.get("key")), ObjectUtils.toString(map.get("value")));
			}
		} catch (Exception e) {
			log.error("",e);
		}
	}
	
	/**
	 * 获取参数值
	 * @param key
	 * @return
	 */
	public final static String get(String key){
		String obj = params.get(key);
		return obj == null? "":obj;
	}
	
	/**
	 * 获取参数值
	 * @param key
	 * @return
	 */
	public final static String[] getArray(String key){
		return get(key) == null? new String[]{}: get(key).split(",");
	}
	/**
	 * 获取参数值
	 * @param key
	 * @return
	 */
	public final static boolean contains(String key,String value){
		return get(key).contains(value);
	}
	
	/**
	 * 判断配置参数是否等于指定参数
	 * @param key
	 * @param compareValue
	 * @return
	 */
	public final static boolean equal(String key, String compareValue){
		String value = params.get(key);
		if(StringUtils.isBlank(value))return false;
		return value.equals(compareValue);
	}
	
	/**
	 * 获取JSON的配置参数
	 * @return
	 */
	public final static String getJSON(){
		return JSONObject.fromObject(params).toString();
	}
	/**
	 * 获取所有的KEY
	 * @return
	 */
	public final static Iterator<String> getKeys(){
		return params.keySet().iterator();
	}
}
