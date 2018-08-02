package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.GoodsFocus;
import com.hrtx.web.service.GoodsFocusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.hrtx.dto.Result;
import javax.servlet.http.HttpServletRequest;

@RestController
//@RequestMapping("/api")
public class GoodsFocusController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private GoodsFocusService goodsFocusService;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	private static Object cjLock = new Object();

	/*
	  添加关注
	 */
	@PostMapping("/api/goodsFocus")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void goodsFocusEdit(GoodsFocus goodsFocus, HttpServletRequest request) {
		returnResult(goodsFocusService.goodsFocusEdit(goodsFocus, request));
	}

	@GetMapping("/api/goodsFocuss")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result listGoodsFocus(){
		return goodsFocusService.findGoodsFocusList();
	}
}
