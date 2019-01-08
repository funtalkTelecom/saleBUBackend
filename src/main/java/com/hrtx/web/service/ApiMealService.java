package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.File;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.pojo.Sku;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ApiMealService {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MealMapper mealMapper;

	/**
	 * 套餐接口-查询对应供应商套餐信息
	 * @param meal
	 * @param supplier
	 * @param request
	 * @return
	 */
	public Result mealList(Meal meal, String supplier, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
//			int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
//			int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
//			meal.setStart(limit*(pageNum-1));
//			meal.setLimit(limit);
			meal.setCreateBy(NumberUtils.toInt(supplier));

			PageHelper.startPage(meal.getPageNum(),meal.getLimit());
			Page<Object> ob=this.mealMapper.queryPageListApi(meal);
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			return new Result(Result.ERROR, pm);
		}

		return new Result(Result.OK, pm);
	}

	/**
	 * 套餐接口-根据号码id查询相关运营商套餐
	 * @param id
	 * @param request
	 * @return
	 */
	public Result mealListForNum(String id, HttpServletRequest request){
		List mealList = new ArrayList();
		try {
			mealList = mealMapper.getMealListByNum(id);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			mealList = new ArrayList();
			return new Result(Result.ERROR, mealList);
		}

		return new Result(Result.OK, mealList);
	}

	public List<Meal> mealList() {
		Meal meal = new Meal();
		meal.setIsDel(0);
		return mealMapper.select(meal);
	}

	public List<Integer> mealListByNum(String phone) {
		return mealMapper.mealListByNum(phone);
	}
}
