package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.MealMapper;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.service.ApiMealService;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiMealController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ApiMealService apiMealService;

	/**
	 * 套餐接口-查询对应供应商套餐信息
	 * @param meal
	 * @param supplier
	 * @param request
	 * @return
	 */
	@GetMapping("/meal/{supplier}")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result mealList(Meal meal, @PathVariable("supplier") String supplier, HttpServletRequest request){
		return apiMealService.mealList(meal, supplier, request);
	}

	/**
	 * 套餐接口-根据号码id查询相关运营商套餐
	 * @param id
	 * @param request
	 * @return
	 */
	@GetMapping("/meal/n{id}")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result mealListForNum(@PathVariable("id") String id, HttpServletRequest request){
		return apiMealService.mealListForNum(id, request);
	}
}
