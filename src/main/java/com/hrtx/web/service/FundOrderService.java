package com.hrtx.web.service;

import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.web.mapper.*;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FundOrderService {
	
//	@Autowired private FundOrderMapper fundOrderMapper;
//	@Autowired private FundDetailMapper fundDetailMapper;
	@Autowired private PinganService pinganService;
	@Autowired private ApiSessionUtil apiSessionUtil;

	/**
	 *
	 *
	 *
	 *
	 *
	 *
	 */

	public Result c(){
		String out_no="1806110003";
		String pmtTag="WeixinOL";
		String order_name="测试";
		String notify_url="http://hr1.tunnel.qydev.com/api/test-pay-result";
		String sub_appid="wxb6164ee4e09e26e1";
		String sub_openid="oKvRM5Z9QN1d7mxfIQt_rgMyX9i4";
		String trade_type="JSAPI";
		int original_amount=1;
		Map<String,String> _map=new HashMap<>();
		Result result=pinganService.payOrderWXXCX(out_no,pmtTag,order_name,original_amount,original_amount,null,notify_url,sub_appid,sub_openid,trade_type);
		if(result.getCode()!=Result.OK)return new Result(Result.ERROR,"创建支付失败");
		System.out.println(result.getData());
		JSONObject json1=(JSONObject)result.getData();
		String[] keys={"appid","noncestr","timestamp","signType","package","paysign"};
		for (String key:keys) {
			if(!json1.containsKey(key))return new Result(Result.ERROR,"接口返回参数错误");
		}
		_map.put("appid",json1.getString("appid"));
		_map.put("noncestr",json1.getString("noncestr"));
		_map.put("timestamp",json1.getString("timestamp"));
		_map.put("signType",json1.getString("signType"));
		_map.put("package",json1.getString("package"));
		_map.put("paysign",json1.getString("paysign"));
		return new Result(Result.OK,_map);
	}




}
