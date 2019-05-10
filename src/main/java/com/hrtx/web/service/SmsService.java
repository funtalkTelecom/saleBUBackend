package com.hrtx.web.service;

import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.Messager;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class SmsService extends BaseService {
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private RedisUtil redisUtil;
	public static String req_type_pc="pc";
	public static String req_type_api="api";
	public static long cache_time=60*10l;//1分钟

	private String createKey(String type){
		String key=null;
		if(StringUtils.equals(type,req_type_api))key=this.apiSessionUtil.getTokenStr()+"-sms-ack";
		if(StringUtils.equals(type,req_type_pc))key=SessionUtil.getSession().getId()+":sms-ack";
		return key;
	}

	/**
	 * 检查验证码是否正确
	 * @param type	访问位置
	 * @param checkCode	待检测的验证码
	 * @return
	 */
	public Result checkKey(String type,String checkCode){
		return checkKey(type,checkCode,false,null);
	}

	/**
	 * 检查验证码是否正确
	 * @param type	访问位置
	 * @param checkCode	待检测的验证码
	 * @param checkCode	待检测的手机号码
	 * @return
	 */
	public Result checkKey(String type,String checkCode,String checkPhone){
		return checkKey(type,checkCode,true,checkPhone);
	}
	/**
	 * 检查验证码是否正确
	 * @param type	访问位置
	 * @param checkCode	待检测的验证码
	 * @param isCheckPhone	是否检查手机号码
	 * @param checkCode	待检测的手机号码
	 * @return
	 */
	private Result checkKey(String type,String checkCode,boolean isCheckPhone,String checkPhone){
		String key=createKey(type);
		String key_t=createKey(type)+"t";
		Object object=this.redisUtil.get(key);
		if(object==null)return new Result(Result.ERROR,"短信验证码错误");
		Object t=this.redisUtil.get(key_t);
		int query_num=NumberUtils.toInt(ObjectUtils.toString(t))+1;
		this.redisUtil.set(key_t,query_num,cache_time);
		if(query_num>5){
			this.redisUtil.del(key,key_t);
			return new Result(Result.ERROR,"验证码验证次数已超限");
		}
		Map<String,String> _map=(Map<String,String>)object;
		String session_rand=_map.get("rand");
		String session_phone=_map.get("phone");
		if(!StringUtils.equals(session_rand,checkCode))return new Result(Result.ERROR,"短信验证码错误");
		if(isCheckPhone&&!StringUtils.equals(checkPhone,session_phone))return new Result(Result.ERROR,"短信验证码错误");
		this.redisUtil.del(key,key_t);
		return new Result(Result.OK,"短信验证码验证通过");
	}

	public Result sendSmsMessage(String type, String sendPhone){
		if(StringUtils.isEmpty(sendPhone))return new Result(Result.ERROR,"接受的手机号码不存在");
		String rand=Utils.randomNoByDateTime(6);
		Map<String,String> _map=new HashMap<>();
		_map.put("rand",rand);
		_map.put("phone",sendPhone);
		String redis_key=createKey(type);
		this.redisUtil.set(redis_key,_map,cache_time);
		Messager.send(sendPhone,String.format("【%s】您的短信验证码[%s],10分钟内有效，请勿转发给他人","靓号优选",rand));
		String wh=StringUtils.substring(sendPhone,-4);
		return new Result(Result.OK,String.format("验证码已发送到尾号[%s]的手机",wh));
	}
}
