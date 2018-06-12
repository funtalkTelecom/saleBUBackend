package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
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
public class ApiOrderController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	@Autowired
	private GoodsMapper goodsMapper;
	@Autowired
	private SkuMapper skuMapper;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;

	@PostMapping("/order")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result createOrder(HttpServletRequest request){
		Order order = new Order();
		ConsumerLog user = apiSessionUtil.getUserClient();
		//获取传入的商品ID
		String goodsId = request.getParameter("goodsId");
		goodsId = goodsId==null?"":goodsId;
		String[] goodsIds = goodsId.split(",");

		if(!goodsId.equals("") && goodsIds.length>0){
			//获取商品列表
//			List goodsList = goodsMapper.getListByGid(goodsId);
//			if (goodsList != null && goodsList.size() > 0) {
//
//				//判断商品类型:
//				switch (){
//
//				}
//			}

		}else{
			return new Result(Result.PARAM, "未获取到参数");
		}

		return new Result(Result.OK, order.getOrderId());
	}
}
