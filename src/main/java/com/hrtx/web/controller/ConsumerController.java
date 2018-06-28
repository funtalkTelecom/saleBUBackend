package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.HttpUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.service.ConsumerService;
import com.hrtx.web.service.UserService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ConsumerController {

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ConsumerService consumerService;
    @PutMapping(value = "/Consumer")
    @Powers({PowerConsts.NOPOWER})
	public Result InsertConsumer(@RequestParam(value="loginName",required=false) String loginName,
								   @RequestParam(value="livePhone",required=false) String livePhone,
								   @RequestParam(value="nickName",required=false) String nickName,
								   @RequestParam(value="sex",required=false) Long sex,
								   @RequestParam(value="img",required=false) String img,
								   @RequestParam(value="province",required=false) String province,
								   @RequestParam(value="city",required=false) String city) {
		if(loginName==null || livePhone==null  || nickName==null || sex==null || img==null || province==null  || city==null )
			return new Result(Result.ERROR, "请确认参数是否正确。");
		return consumerService.insertConsumer(loginName,livePhone,nickName,sex,img,province,city);
	}
}
