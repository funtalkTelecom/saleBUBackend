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
import com.hrtx.web.service.DictService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("/api")

public class ApiNumberController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ApiNumberService apiNumberService;

	@Autowired
	private DictService dictService;

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

	@GetMapping("/seckillListTime")
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
	@GetMapping("/numberSeckillList")
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


	/**
	 * 手机端获取靓号类型AAA
	 * @return
	 */
	@GetMapping("/find-feather_type")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Map findFeatherType(){
		Map<String, Object> map = new HashMap<String, Object>();
		List list =dictService.findDictByGroup("FEATHER_TYPE");
		map.put("code", Result.OK);
		map.put("data", list);
		return  map;
	}

	/***
	 * 手机端获取运营商
	 * @return
	 */
	@GetMapping("/find-haoka_yys")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Map findHaokaYys(){
		Map<String, Object> map = new HashMap<String, Object>();
		List list =dictService.findDictByGroup("haoka_yys");
		map.put("code", Result.OK);
		map.put("data", list);
		return  map;
	}

	/**
	 * 吉利号
	 * @return
	 */
	@GetMapping("/find-num_tags")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Map findNumTags(){
		Map<String, Object> map = new HashMap<String, Object>();
		List list =dictService.findDictByGroup("num_tags");
		map.put("code", Result.OK);
		map.put("data", list);
		return  map;
	}

	@GetMapping("/search-number")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result searchNumberList(NumPrice numPrice,HttpServletRequest request){
		return apiNumberService.searchNumberList(numPrice,request);
	}


}
