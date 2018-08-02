package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.EPSaleNotice;
import com.hrtx.web.service.EPSaleNoticeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
//@RequestMapping("/api")
public class EPSaleNoticeController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EPSaleNoticeService ePSaleNoticeService;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	private static Object cjLock = new Object();

	/*
	  添加提醒
	 */
	@PostMapping("/api/epSaleNotice")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void epSaleEdit(EPSaleNotice ePSaleNotice, HttpServletRequest request) {
		returnResult(ePSaleNoticeService.epSaleEdit(ePSaleNotice, request));
	}

	@GetMapping("/api/epSaleNotices")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result listEPSaleNotice(){
		return ePSaleNoticeService.findEPSaleNoticeList();
	}
}
