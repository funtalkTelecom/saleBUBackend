package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.MealService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/meal")
public class MealController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	private static char mapTable[] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','V','W','S','X','Y','Z'};

	@Autowired
	MealService mealService;
	@Autowired
	CityService cityService;

	@RequestMapping("/list-meal")
	@Powers({PowerConsts.MEALMOUDULE_COMMON_QUEYR})
	public Result listMeal(Meal meal){
		return mealService.pageMeal(meal);
	}

	@RequestMapping("/query-meal")
	@Powers({PowerConsts.MEALMOUDULE_COMMON_QUEYR})
	public ModelAndView queryMeal(Meal meal){
		return new ModelAndView("admin/meal/query-meal");
	}

	@RequestMapping("/meal-edit")
	@Powers({PowerConsts.MEALMOUDULE_COMMON_EDIT})
	public void mealEdit(Meal meal){
		returnResult(mealService.mealEdit(meal));
	}

	@RequestMapping("/meal-delete")
	@Powers({PowerConsts.MEALMOUDULE_COMMON_DELETE})
	public void mealDelete(Meal meal){
		returnResult(mealService.mealDelete(meal));
	}

	@RequestMapping("/meal-info")
	@ResponseBody
	@Powers({PowerConsts.MEALMOUDULE_COMMON_QUEYR})
	public Map mealInfo(Meal meal){
//		String json = JSONObject.fromObject(mealService.findMealById(meal.getMid())).toString();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", Result.OK);
		map.put("data", mealService.findMealById(meal.getMid()));
		return map;
	}

//	@RequestMapping("/meal-info")
//	public void mealInfo(Meal meal){
//		returnJson(mealService.findMealById(meal.getMid()));
//	}

	@RequestMapping("download-meal-template")
	@Powers( {PowerConsts.MEALMOUDULE_COMMON_IMPORT })
	public void downloadRecCardTemplate() throws Exception {
		List list = new ArrayList();
		String[] title = new String[]{"套餐ID","套餐名称","套餐描述","销售地市","销售渠道","运营商"};
		list.add(title);
//		List<City> provinces = cityService.queryByPidList(0);
//		List ps = new ArrayList();
//		for (City city:provinces) {
//			ps.add(city.getName());
//		}
		list.add(new Object[]{"X0000001","xxxxx","xxxxxx","","",""});
		List<List<?>> list1 = new ArrayList();
		list1.add(list);
		Utils.export("套餐导入模板.xls",list1, null, null, new String[]{"套餐"});
	}

	@RequestMapping("import-meal")
	@Powers( {PowerConsts.MEALMOUDULE_COMMON_IMPORT })
	public String importRecCard(@RequestParam(name = "file",required = false) MultipartFile file, HttpServletRequest request) throws Exception {
//		String path = "D:\\test\\";
//		Result result = this.uploadFile(path, "xls", file, false, false);
//		if(result.getCode() != Result.OK) return returnResult(result);
//		String sourceServerFileName = ObjectUtils.toString(((Map)result.getData()).get("sourceServerFileName"));
		ArrayList<ArrayList<String>> list = ReadExcelNew.read(file.getInputStream(), file.getOriginalFilename());
		List<Meal> mealList = new ArrayList<Meal>();
		if(list==null || list.size()<=1) {
			return returnError("导入文件无数据");
		}
		List<String> mealids = new ArrayList<>();
		Date now = new Date();
		Map<String, Object> thirdCity = null;
		for(int i=1;i<list.size();i++){
			ArrayList<String> arr=list.get(i);
			Meal m = new Meal();
			if(StringUtils.isBlank(arr.get(0))){
				return returnError("数据错误，系统检测到第"+(i+1)+"行套餐ID为空。");
			}
			m.setMealId(arr.get(0));
			if(StringUtils.isBlank(arr.get(1))){
				return returnError("数据错误，系统检测到第"+(i+1)+"行套餐名称为空。");
			}
			m.setMealName(arr.get(1));
//			if(!arr.get(5).matches("^\\d{4}[\\/-]\\d{1,2}[\\/-]\\d{1,2}$")){
//				deleteFile(path, sourceServerFileName);
//				return returnError("数据错误，系统检测到第"+(i+1)+"行到期时间格式有误。");
//			}
			if(mealids.contains(arr.get(0))) {
				return returnError("数据错误，系统检测到第"+(i+1)+"行的套餐ID在该文件重复。");
			}
			if(arr.get(3)==null || "".equals(arr.get(3))){
				return returnError("数据错误，系统检测到第" + (i + 1) + "行的销售地市为空。");
			}else if ("不区分".equals(arr.get(3))) {
				m.setSaleCity(-99l);
			} else {
				thirdCity = cityService.findCityByNameFromThird(arr.get(3));
				if (Integer.parseInt(thirdCity.get("num").toString())!=1) {
					return returnError("数据错误，系统检测到第"+(i+1)+"行的销售地市不正确。");
				}
				m.setSaleCity(Long.parseLong(thirdCity.get("city_id").toString()));
			}
			Meal meal = mealService.findMealByMealId(arr.get(0));
			if(meal != null) {
				return returnError("数据错误，系统检测到第"+(i+1)+"行在库中已存在。");
			}
			mealids.add(arr.get(0));
			m.setMealDesc(arr.get(2));
			m.setSaleType(arr.get(4));
			m.setTeleType(arr.get(5));
			m.setCreateBy(SessionUtil.getUserId());
			m.setCreateDate(now);
			m.setUpdateBy(SessionUtil.getUserId());
			m.setUpdateDate(now);
			m.setMid(m.getGeneralId());
			mealList.add(m);
		}
//		recCardService.importRecCard(list, sourceServerFileName);
		mealService.insertBatch(mealList);
		return returnOk();
	}
}
