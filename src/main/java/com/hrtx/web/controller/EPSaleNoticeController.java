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
import org.springframework.web.bind.annotation.*;

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
	  当前用户对应该竞拍活动提醒
	 */
	@GetMapping("/api/epSaleNotice/{epSaleId}")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result findEPSaleNoticeListByEPSaleId( @PathVariable("epSaleId") String epSaleId) {
		return new Result(Result.OK,ePSaleNoticeService.findEPSaleNoticeListByEPSaleId(Long.valueOf(epSaleId)));
	}

	/*
	  添加竞拍活动提醒
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
