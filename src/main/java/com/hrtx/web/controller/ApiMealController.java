package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.MealMapper;
import com.hrtx.web.pojo.Meal;
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
	private MealMapper mealMapper;

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
		PageInfo<Object> pm = null;
		try {
			int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
			int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
			meal.setStart(limit*(pageNum-1));
			meal.setLimit(limit);
			meal.setCreateBy(Long.parseLong(supplier));

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
	@GetMapping("/meal/n{id}")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result mealListForNum(@PathVariable("id") String id, HttpServletRequest request){
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
}
