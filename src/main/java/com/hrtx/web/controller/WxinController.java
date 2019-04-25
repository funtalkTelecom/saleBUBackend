package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.HttpUtil;
import com.hrtx.global.LockUtils;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.service.ConsumerService;
import com.hrtx.web.service.UserService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;
import org.omg.CORBA.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class WxinController {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired ConsumerService consumerService;
    @GetMapping("/get_open_id")
    @Powers({PowerConsts.NOLOGINPOWER})
	public Result getOpenid(@RequestParam(value="getcode",required=false)String getcode,@RequestParam(value="userId",required=false) String userId) {
		Result result = this.consumerService.getOpenId(getcode);
		int userid =NumberUtils.toInt(userId);
		if(result.getCode()==Result.OK){
			String openid  =String.valueOf(result.getData());
			if(!LockUtils.tryLock("goi"+openid)) return new Result(Result.ERROR,"授权异常");
			try{
				result= consumerService.isOpenid(openid,userid);
			}finally {
				LockUtils.unLock("goi"+openid);
			}

		}
		return result;
	}

	@GetMapping("/getQrcode")
	@Powers({PowerConsts.NOLOGINPOWER})
	public void getQrcode(@RequestParam(value="scene",required=false)String scene,
						  @RequestParam(value="page",required=false)String page,
						  @RequestParam(value="width",required=false)String width,
						  @RequestParam(value="auto_color",required=false)String auto_color,
						  @RequestParam(value="line_color",required=false)String line_color,
						  @RequestParam(value="is_hyaline",required=false)String is_hyaline,
						  HttpServletResponse response) {
		Result result =consumerService.getAccess_token();
		if(result.getCode()==Result.OK) consumerService.getQrcode(String.valueOf(result.getData()),scene,page,width,auto_color,line_color,is_hyaline,response);

	}
}
