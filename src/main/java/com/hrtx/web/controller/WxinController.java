package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.HttpUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.service.ConsumerService;
import com.hrtx.web.service.UserService;
import net.sf.json.JSONObject;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@RestController
@RequestMapping("/api")
public class WxinController {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired ConsumerService consumerService;
    @GetMapping("/get_open_id")
    @Powers({PowerConsts.NOPOWER})
	public Result getOpenid(@RequestParam(value="getcode",required=false)String getcode) {
		String code=getcode;
		String appid=SystemParam.get("AppID");
		String appsecret=SystemParam.get("AppSecret");
		//授权（必填）
		String grant_type = "authorization_code";
		//URL
		String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";
		//请求参数
		String params = "appid=" + appid + "&secret=" + appsecret + "&js_code=" + code + "&grant_type=" + grant_type;
		//发送请求
		 String data = HttpUtil.get(requestUrl, params);
		JSONObject json = JSONObject.fromObject(data);
//		用户的唯一标识（openid）
		String Openid =String.valueOf(json.get("openid"));
//		String sessionkey=String.valueOf(json.get("session_key"));
//		JSONObject jsonObject  = HttpUtil.doGetStr(requestUrl, params);
//		String Openid =String.valueOf(jsonObject.getString("openid"));
		Result s  = consumerService.isOpenid(Openid);
		return s;
	}
}
