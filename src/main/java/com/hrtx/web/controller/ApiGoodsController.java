package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.global.TokenGenerator;
import com.hrtx.web.mapper.FileMapper;
import com.hrtx.web.mapper.GoodsMapper;
import com.hrtx.web.mapper.SkuMapper;
import com.hrtx.web.mapper.SkuPropertyMapper;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.File;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.Sku;
import com.hrtx.web.service.ApiGoodsService;
import com.hrtx.web.service.GoodsService;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiGoodsController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ApiGoodsService apiGoodsService;
	@Autowired
	private GoodsService goodsService;

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
	public Result goodsList(Goods goods, HttpServletRequest request){
		return apiGoodsService.goodsList(goods, request);
	}

	/**
	 * 商品详情
	 * sku列表
	 * file列表
	 * @param id(skuid)
	 * @param request
	 * @return
	 */
	@GetMapping("/goods/{id}")
	@Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result goodsDetail(@PathVariable("id") String id, HttpServletRequest request){
		return apiGoodsService.goodsDetail(NumberUtils.toInt(id), request);
	}

    @RequestMapping("/goods-info/{gId}")
    @ResponseBody
    @Powers(PowerConsts.NOLOGINPOWER)
    public Map goodsKindeditorContent(@PathVariable("gId") String gId){
        Goods goods = new Goods();
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            goods.setgId(NumberUtils.toInt(gId));
            map = new HashMap<String, Object>();
            map.put("code", Result.OK);
            map.put("data", goodsService.getKindeditorContent(goods));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            map.put("code", Result.ERROR);
            map.put("data", "请传入合法的ID");
        } catch (IOException e) {
            e.printStackTrace();
            map.put("code", Result.ERROR);
            map.put("data", "读取文件信息失败:未找到相应文件");
        }
        return map;
    }
}
