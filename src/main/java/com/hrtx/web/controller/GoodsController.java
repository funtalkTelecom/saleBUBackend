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
	public void goodsEdit(Goods goods, @RequestParam(name = "file",required = false) MultipartFile[] files, HttpServletRequest request){
        returnResult(goodsService.goodsEdit(goods, request, files));
	}

	@RequestMapping("/goods-delete")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_DELETE})
	public void goodsDelete(Goods goods){
		returnResult(goodsService.goodsDelete(goods));
	}
}
