package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.service.DictService;
import com.hrtx.web.service.FundOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FundOrderController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private FundOrderService fundOrderService;

	@RequestMapping("/test-pay")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result dictQuery(){
		Result result= null;
		try {
			result = fundOrderService.c();
		} catch (Exception e) {
			log.error("创建支付请求失败",e);
			result=new Result(Result.ERROR,String.format("创建支付请求失败[%s]",e.getMessage()));
		}
		return result;
	}
	@RequestMapping("/test-pay-result")
	@Powers({PowerConsts.NOLOGINPOWER})
	public void dictresult(HttpServletRequest request){
		System.out.println("==========================result===================");
		Map<String,String> params = new HashMap<String,String>();
		Enumeration<?> e= request.getParameterNames();
		while (e.hasMoreElements()) {
			String object = (String) e.nextElement();
			String[] values = (String[])request.getParameterValues(object);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			params.put(object, valueStr);
			log.info(String.format("key:%s    ==  value:%s ",object,valueStr));
		}
		System.out.println(params);
	}

}
