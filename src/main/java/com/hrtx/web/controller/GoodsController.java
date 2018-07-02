package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.FileMapper;
import com.hrtx.web.pojo.File;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.service.FileService;
import com.hrtx.web.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private FileService fileService;

	@RequestMapping("/goods-query")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_QUEYR})
	public ModelAndView goodsQuery(Goods goods){
		return new ModelAndView("admin/goods/goods-query");
	}

	@RequestMapping("/goods-list")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_QUEYR})
	public Result listGoods(Goods goods){
		return goodsService.pageGoods(goods);
	}

	@RequestMapping("/goods-info")
	@ResponseBody
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_QUEYR})
	public Map goodsInfo(Goods goods){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", Result.OK);
		map.put("data", goodsService.findGoodsById(goods.getgId()));
        map.put("kindeditor", Utils.kindeditorReader(goods.getgId() + ".txt", SystemParam.get("kindedtiorDir")));
        map.put("goodsPics", fileService.findFilesByRefid(goods.getgId().toString()));
		return map;
	}

	@RequestMapping("/goods-edit")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_EDIT})
	public Result goodsEdit(Goods goods, @RequestParam(name = "file",required = false) MultipartFile[] files, HttpServletRequest request){
		if(StringUtils.isBlank(goods.getgType1()) || "-1".equals(goods.getgType1())) return (new Result(Result.ERROR, "请选择大类"));
		if(StringUtils.isBlank(goods.getgType2()) || "-1".equals(goods.getgType2())) return (new Result(Result.ERROR, "请选择小类"));
		if(StringUtils.isBlank(goods.getgName())) return (new Result(Result.ERROR, "商品名称不能为空"));
		if(StringUtils.isBlank(goods.getgAd())) return (new Result(Result.ERROR, "宣传语不能为空"));
		if(StringUtils.isBlank(goods.getgSaleCity())) return (new Result(Result.ERROR, "销售地市不能为空"));
		if(StringUtils.isBlank(goods.getgIsAuc())) return (new Result(Result.ERROR, "请选择是否竞拍"));
		if(StringUtils.isBlank(goods.getgIsPack())) return (new Result(Result.ERROR, "请选择是否打包"));
		if("1".equals(goods.getgIsAuc())){
			if(goods.getgActive() == null) return (new Result(Result.ERROR, "请选择活动"));
			if(StringUtils.isBlank(goods.getgLoopTime())) return (new Result(Result.ERROR, "轮询时间不能为空"));
			if(StringUtils.isBlank(goods.getgStartNum())) return (new Result(Result.ERROR, "起拍人数不能为空"));
			if(StringUtils.isBlank(goods.getgDeposit())) return (new Result(Result.ERROR, "保证金不能为空"));
			if(StringUtils.isBlank(goods.getgPriceUp())) return (new Result(Result.ERROR, "每次加价不能为空"));
		}
		if(goods.getgStartTime()==null) return (new Result(Result.ERROR, "有效期开始时间不能为空"));
		if(goods.getgEndTime()==null) return (new Result(Result.ERROR, "有效期结束时间不能为空"));
		if(goodsService.checkGnameIsExist(goods)) return (new Result(Result.ERROR, "商品名称已存在"));

        return (goodsService.goodsEdit(goods, request, files));
	}

	@RequestMapping("/goods-unsale")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_EDIT})
	public void goodsUnsale(Goods goods, HttpServletRequest request){
        returnResult(goodsService.goodsUnsale(goods, request));
	}

	@RequestMapping("/goods-delete")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_DELETE})
	public void goodsDelete(Goods goods){
		returnResult(goodsService.goodsDelete(goods));
	}

	@RequestMapping("/goods-repoGoods")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_QUEYR})
	public void repoGoods(HttpServletRequest request){
		returnResult(goodsService.repoGoods(request));
	}
}
