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
	 * 根据tags获取号码
	 * @param number
	 * @param request
	 * @return
	 */
	@GetMapping("/number")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberList(Number number, HttpServletRequest request){
		return apiNumberService.numberList(number, request);
	}

	@GetMapping("/numberTypeList")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberTypeList(Number number, HttpServletRequest request){
		return apiNumberService.numberTypeList(number, request);
	}

	@GetMapping("/number/{id}")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberInfo(@PathVariable("id") String id, HttpServletRequest request){
		return apiNumberService.numberInfo(id, request);
	}


	/**
	 * 根据号码模糊查询获取号码列表
	 * @param number
	 * @param request
	 * @return
	 */
	@GetMapping("/fand-number")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberListByNum(Number number, HttpServletRequest request){
		return apiNumberService.numberListByNum(number, request);
	}
}
