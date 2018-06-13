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

	/**
	 * 根据商品id创建订单
	 * @param request
	 * @param type 1:普靓和超靓:skuid, numid, 地址id, 支付方式, 套餐id
	 *             2:白卡和普号:skuid,数量,地址
	 * @return
	 */
	@PostMapping("/order")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result createOrder(HttpServletRequest request){
		Order order = new Order();
		String type = request.getParameter("type");

		try {
			switch (type){
//				type:1  普通靓号和超级靓号    skuid, numid, 地址id, 支付方式, 套餐id
				case "1":


//				type:2  白卡和普号		skuid,数量,地址
				case "2":

			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Result.ERROR, "未获取到参数");
		}

		return new Result(Result.OK, order.getOrderId());
	}
}
