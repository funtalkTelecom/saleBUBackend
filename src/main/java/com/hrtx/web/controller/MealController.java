package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.MealService;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.server.UID;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.hrtx.config.Utils.returnResult;

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
	public ModelAndView queryMeal(Meal meal){
		return new ModelAndView("admin/meal/query-meal");
	}

	@RequestMapping("/meal-edit")
	public void mealEdit(Meal meal){
		returnResult(mealService.mealEdit(meal));
	}

	@RequestMapping("/meal-delete")
	public void mealDelete(Meal meal){
		returnResult(mealService.mealDelete(meal));
	}

	@RequestMapping("/meal-info")
	@ResponseBody
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
		String[] title = new String[]{"套餐ID","套餐名称","套餐描述","销售地市","销售渠道"};
		list.add(title);
//		List<City> provinces = cityService.queryByPidList(0);
//		List ps = new ArrayList();
//		for (City city:provinces) {
//			ps.add(city.getName());
//		}
		list.add(new Object[]{"X0000001","xxxxx","xxxxxx","",""});
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
			thirdCity = cityService.findCityByNameFromThird(arr.get(3));
			if (Integer.parseInt(thirdCity.get("num").toString())!=1) {
				return returnError("数据错误，系统检测到第"+(i+1)+"行的销售地市不正确。");
			}
			m.setSaleCity(Long.parseLong(thirdCity.get("city_id").toString()));
			Meal meal = mealService.findMealByMealId(arr.get(0));
			if(meal != null) {
				return returnError("数据错误，系统检测到第"+(i+1)+"行在库中已存在。");
			}
			mealids.add(arr.get(0));
			m.setMealDesc(arr.get(2));
			m.setSaleType(0);
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

//	private String returnError(String msg) {
//		return JSONObject.fromObject(new Result(Result.ERROR, msg)).toString();
//	}
//
//	private String returnOk() {
//		return JSONObject.fromObject(new Result(Result.OK, "ok")).toString();
//	}
//
//	private void deleteFile(String path, String sourceServerFileName) {
//		try {
//			FileUtils.forceDelete(new File(path+sourceServerFileName));
//		} catch (IOException e) {
//			log.info("删除文件失败",e);
//		}
//	}

	private Result uploadFile(String projectRealPath, String file_suffix_s, MultipartFile file, boolean b, boolean b1) throws IOException {
		// ==========验证文件后缀start==========//
		if(file == null || file.isEmpty()) return new Result(Result.ERROR, "请选择上传的文件");
		String originalFilename = file.getOriginalFilename();
		String suffix_v = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
		if(!(","+file_suffix_s+",").contains(","+suffix_v+",")){
			return new Result(Result.ERROR, "请上次格式为["+file_suffix_s+"]的文件");
		}
		// ==========验证文件后缀end==========//
		Map<String, Object> map = new HashMap<>();
		String bname = this.randomNoByDateTime();
		String sourceServerFileName = bname + "."+suffix_v;
		map.put("sourceServerFileName", sourceServerFileName);  //服务器原文件名称
		map.put("sourceFileName", originalFilename);			//上传文件名
		String fullUrl = projectRealPath + sourceServerFileName;
		File outDir = new File(projectRealPath);
		if (!outDir.exists())  outDir.mkdirs();
		InputStream is = file.getInputStream();
        IOUtils.copy(is, new FileOutputStream(fullUrl));
        if(is!=null) is.close();
//		File originalFile = new File(fullUrl);
//		FileUtils.copyInputStreamToFile(file.getInputStream(), originalFile);

		return new Result(Result.OK, map);
	}


	/**
	 * C + 26个英文 中的1个(随机)+MMddHHmm(时间格式)+ 1位数字(随机)+3位自增数(超过3位归为零)
	 * @return
	 */
	public static String randomNoByDateTime(){
		StringBuffer str=new StringBuffer(15);
		str.append("C");
		str.append(mapTable[(int)(mapTable.length * Math.random())]);
		str.append(Utils.getCurrentDate("yy").substring(1, 2));
		str.append(Utils.getCurrentDate("MMddHHmm"));
		str.append(new Random().nextInt(9));
		str.append(StringUtils.leftPad(String.valueOf(countSeq()),3, "0"));
		return str.toString();
	}

	/**
	 * 3位自增数(超过3位归为归零)
	 * @return
	 */
	private static int count = 0;
	private static synchronized Integer countSeq(){
		if(count>990)count=0;
		count++;
		return count;
	}
}
