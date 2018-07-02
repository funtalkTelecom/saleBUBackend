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
		Result result = this.consumerService.getOpenId(getcode);
		if(result.getCode()==Result.OK)result= consumerService.isOpenid(String.valueOf(result.getData()));
		return result;
	}
}
