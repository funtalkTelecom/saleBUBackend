package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.MealMapper;
import com.hrtx.web.pojo.Meal;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

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
	public String mealList(Meal meal, @PathVariable("supplier") String supplier, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
			meal.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			meal.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));
			meal.setCreateBy(Long.parseLong(supplier));

			PageHelper.startPage(meal.getPageNum(),meal.getLimit());
			Page<Object> ob=this.mealMapper.queryPageListApi(meal);
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
		}

		return JSONObject.fromObject(pm).toString();
	}
}
