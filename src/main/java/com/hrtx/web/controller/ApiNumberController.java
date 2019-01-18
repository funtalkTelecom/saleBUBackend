package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.NumberMapper;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.service.ApiNumberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiNumberController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ApiNumberService apiNumberService;

	/**
	 * 手机端超靓号码列表，根据tags获取号码
	 * @param numPrice
	 * @param request
	 * @return
	 */
	@GetMapping("/number")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberList(NumPrice numPrice, HttpServletRequest request){
		return apiNumberService.numberList(numPrice, request);
	}

	@GetMapping("/seckillListTime ")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result seckillListTime(HttpServletRequest request){
		return apiNumberService.seckillListTime(request);
	}

	/***
	 * 手机秒杀列表
	 * @param numPrice
	 * @param request
	 * @return
	 */
	@GetMapping("/numberSeckillList ")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberSeckillList(NumPrice numPrice, HttpServletRequest request){
		return apiNumberService.numberSeckillList(numPrice, request);
	}

	@GetMapping("/number/{id}")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberInfo(@PathVariable("id") String id, HttpServletRequest request){
		return apiNumberService.numberInfo(id, request);
	}

	/**
	 * 手机端普靓号码列表
	 * @param number
	 * @param request
	 * @return
	 */
	@GetMapping("/numberTypeList")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberTypeList(Number number, HttpServletRequest request){
		return apiNumberService.numberTypeList(number, request);
	}

	/**
	 * 根据号码模糊查询获取号码列表
	 * @param numPrice
	 * @param request
	 * @return
	 */
	@GetMapping("/fand-number")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberListByNum(NumPrice numPrice, HttpServletRequest request){
		return apiNumberService.numberListByNum(numPrice, request);
	}


}
