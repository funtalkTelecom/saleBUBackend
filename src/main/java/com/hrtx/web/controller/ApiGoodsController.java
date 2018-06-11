package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.FileMapper;
import com.hrtx.web.mapper.GoodsMapper;
import com.hrtx.web.mapper.SkuMapper;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.Sku;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGoodsController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private SkuMapper skuMapper;
	@Autowired
	private FileMapper fileMapper;

	/**
	 * 在售商品列表
	 * 根据传入的地市进行查询
	 * @param goods
	 * @param request
	 * @return
	 */
	@GetMapping("/goods")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public String goodsList(Goods goods, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
			goods.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			goods.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));

			PageHelper.startPage(goods.getPageNum(),goods.getLimit());
			Page<Object> ob=this.goodsMapper.queryPageListApi(goods, goods.getgSaleCity().split(","));
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
		}

		return JSONObject.fromObject(pm).toString();
	}

	/**
	 * 商品详情
	 * sku列表
	 * file列表
	 * @param id
	 * @param request
	 * @return
	 */
	@GetMapping("/goods/{id}")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Map goodsDetail(@PathVariable("id") String id, HttpServletRequest request){
		Map returnMap = new HashMap();
		Goods goods = new Goods();
		List skuList = new ArrayList<>();
		List fileList = new ArrayList<>();

		try {
			goods = goodsMapper.findGoodsInfo(Long.parseLong(id));

			Sku sku = new Sku();
			sku.setgId(Long.parseLong(id));
			skuList = skuMapper.queryList(sku);

			fileList = fileMapper.findFilesByRefid(id);
			returnMap.put("code", Result.OK);
		} catch (Exception e) {
			e.printStackTrace();
			goods = new Goods();
			skuList = new ArrayList<>();
			fileList = new ArrayList<>();
			returnMap.put("code", Result.ERROR);
		}


		returnMap.put("goods", goods);
		returnMap.put("skuList", skuList);
		returnMap.put("fileList", fileList);
		return returnMap;
	}
}
