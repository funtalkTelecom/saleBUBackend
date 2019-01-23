package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.mapper.AuctionDepositMapper;
import com.hrtx.web.mapper.AuctionMapper;
import com.hrtx.web.pojo.*;
import com.hrtx.web.service.*;
import com.hrtx.web.websocket.WebSocketServer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.System;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@RestController
public class ActivityController extends BaseReturn{

	@Resource
	private CityService cityService;
	@Resource
	private AgentService agentService;
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ActivityService activityService;
	@Autowired
	private DictService dictService;

	/**
	 * 初始化活动界面
	 * @param request
	 * @return
	 */
	@RequestMapping("activity/activity-query")
	@Powers({PowerConsts.ACTIVITYMOUDULE})
	public ModelAndView activityQuery(HttpServletRequest request){
		 agentService.queryAgentBySellerId(SessionUtil.getUser().getCorpId());
		request.setAttribute("agentList",agentService.queryAgentBySellerId(SessionUtil.getUser().getCorpId()));
		request.setAttribute("beginHList",dictService.findDictByGroup("activityTimeS"));
		request.setAttribute("endHList",dictService.findDictByGroup("activityTimeE"));
		return new ModelAndView("admin/activity/activity-query");
	}

	/***
	 * 活动列表
	 * @param activity
	 * @return
	 */
	@RequestMapping("activity/activity-list")
	@Powers({PowerConsts.ACTIVITYMOUDULE_COMMON_QUEYR})
	public Result listEPSale(Activity activity){
		return activityService.pageActivity(activity);
	}

	@RequestMapping("activity/activity-check")
	@Powers({PowerConsts.ACTIVITYMOUDULE_COMMON_ADD})
	public  Result checkNum (Activity activity,  HttpServletRequest request){
		String skuSaleNum = request.getParameter("saleNums");
		Integer agentId = NumberUtils.toInt(request.getParameter("agentId"));
		return activityService.findListSaleNums(skuSaleNum,agentId);
	}

	@RequestMapping("activity/activity-edit")
	@Powers({PowerConsts.ACTIVITYMOUDULE_COMMON_ADD})
	public Result activityEdit(Activity activity, HttpServletRequest request) throws ParseException{
		return activityService.activityEdit(activity,request);
	}


	@PostMapping("activity/find-activity-by-id")
	@ResponseBody
	@Powers({PowerConsts.ACTIVITYMOUDULE_COMMON_ADD})
	public Map findActivityById(Activity activity){
		Map<String, Object> map = new HashMap<String, Object>();
		Map m = new HashMap();
		Activity at=activityService.findActivityById(activity.getId());
		List list =activityService.findActivityItemList(activity.getId());
		m.put("at",at);
		m.put("activityItemList",list);
		map.put("code", Result.OK);
		map.put("data", m);
		return map;
	}

	@PostMapping("activity/activity-cancel")
	@ResponseBody
	@Powers({PowerConsts.ACTIVITYMOUDULE_COMMON_CANCEL})
	public Result activityCancel(Activity activity){
		return  activityService.activityCancel(activity);
	}





}
