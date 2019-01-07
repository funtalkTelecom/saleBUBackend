package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.SessionUtil;
import com.hrtx.web.mapper.MealMapper;
import com.hrtx.web.pojo.Meal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MealService {
	
	@Autowired SessionUtil sessionUtil;
	@Autowired private MealMapper mealMapper;

	public Result pageMeal(Meal meal) {
		PageHelper.startPage(meal.startToPageNum(),meal.getLimit());
		meal.setSellerId(SessionUtil.getUser().getCorpId());
		Page<Object> ob=this.mealMapper.queryPageList(meal);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Meal findMealByMealId(String mealid) {
		Meal meal = mealMapper.findMealInfo(mealid);
		return meal;
	}

	public void insertBatch(List<Meal> mealList) {
		this.mealMapper.insertBatch(mealList);
	}

	public Meal findMealById(Integer id) {
		Meal meal = mealMapper.findMealById(id);
		return meal;
	}

	public Result mealEdit(Meal meal) {
		if (meal.getMid() != null && meal.getMid() > 0) {
			if (!checkMealIdIsExist(meal)) {
				meal.setUpdateDate(new Date());
				meal.setUpdateBy(SessionUtil.getUserId());
				mealMapper.mealEdit(meal);
			} else {
				return new Result(Result.ERROR, "套餐ID已存在");
			}
		} else {
			return new Result(Result.ERROR, "未找到这条记录");
		}

		return new Result(Result.OK, "提交成功");
	}

	private boolean checkMealIdIsExist(Meal meal) {
		boolean b = true;
		int num = this.mealMapper.checkMealIdIsExist(meal);
		if(num <= 0) b=false;

		return b;
	}

	public Result mealDelete(Meal meal) {
		mealMapper.mealDelete(meal);
		return new Result(Result.OK, "删除成功");
	}
}
