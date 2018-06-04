package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/goods")
public class GoodsController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private GoodsService goodsService;

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
		return map;
	}

	@RequestMapping("/goods-edit")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_EDIT})
	public void goodsEdit(Goods goods, HttpServletRequest request){
		returnResult(goodsService.goodsEdit(goods, request));
	}

	@RequestMapping("/goods-delete")
	@Powers({PowerConsts.GOODSMOUDULE_COMMON_DELETE})
	public void goodsDelete(Goods goods){
		returnResult(goodsService.goodsDelete(goods));
	}
}
