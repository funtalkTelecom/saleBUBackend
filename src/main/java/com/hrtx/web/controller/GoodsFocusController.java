package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.GoodsFocus;
import com.hrtx.web.service.GoodsFocusService;
import com.hrtx.web.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
//@RequestMapping("/api")
public class GoodsFocusController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private GoodsFocusService goodsFocusService;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	private static Object cjLock = new Object();
	@Autowired
	private GoodsService goodsService;

	/*
	  当前用户对应该商品收藏记录
	 */
	@GetMapping("/api/goodsFocus/{numId}/{gId}/{erIsPack}")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result findGoodsFocus(@PathVariable("numId") String numId, @PathVariable("gId") String gId,@PathVariable("erIsPack") Integer erIsPack) {
		Goods goods=goodsService.findGoodsById(Long.valueOf(gId));//上架商品信息gActive
		if(erIsPack!=Integer.valueOf(goods.getgIsPack()))
		{
			return new Result(Result.ERROR,"是否打包传参不符，请核对");
		}
		if(erIsPack==0)//商品是否打包 erIsPack
		{
			return new Result(Result.OK,goodsFocusService.findGoodsFocusListByNumIdAndGId(Long.valueOf(numId),Long.valueOf(gId)));
		}else if(erIsPack==1)
		{
			return new Result(Result.OK,goodsFocusService.findGoodsFocusListByGId(Long.valueOf(gId)));
		}
		return new Result(Result.ERROR,"记录为空");
	}

	/*
	  添加商品收藏
	  GoodsFocus.isDel;//是否收藏  是0否1
	 */
	@PostMapping("/api/goodsFocus")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void goodsFocusEdit(GoodsFocus goodsFocus, HttpServletRequest request) {
		returnResult(goodsFocusService.goodsFocusEdit(goodsFocus, request));
	}

	/*
	我的商品收藏列表
	 */
	@GetMapping("/api/goodsFocuss")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result listGoodsFocus(){
		return goodsFocusService.findGoodsFocusList();
	}
}
