package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.Number;
import java.lang.System;
import java.util.*;

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
	private SkuPropertyMapper skuPropertyMapper;
	@Autowired
	private OrderMapper orderMapper;
	@Autowired
	private NumberMapper numberMapper;
	@Autowired
	private OrderItemMapper orderItemMapper;
	@Autowired
	private DeliveryAddressMapper deliveryAddressMapper;
	@Autowired
	private RedisUtil redisUtil;

	/**
	 * 根据商品id创建订单
	 * @param request
	 * @param type 1:普靓和超靓:skuid, numid, 地址id, 支付方式, 套餐id
	 *             2:白卡和普号:skuid,数量,地址
	 * @return
	 */
	@PostMapping("/order")
//    @Powers(PowerConsts.NOPOWER)
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result createOrder(HttpServletRequest request){
		//子项小计
		double sub_total = 0;
		//合计
		double total = 0;
		//折扣
		double commission = 1;
		//运费
		double shipping_total = 0;

		Order order = new Order();
		order.setOrderId(order.getGeneralId());
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		List<Order> orderList = new ArrayList<Order>();
		String type = null;

		//模拟登陆
		Consumer u = new Consumer();
		u.setId(1l);
		u.setName("周元强");
		u.setIsAgent(2);//设置为一级代理商
		String token=TokenGenerator.generateValue();
		apiSessionUtil.saveOrUpdate(token,u);

//		Consumer user = apiSessionUtil.getConsumer();
		Consumer user = (Consumer) redisUtil.get("egt-kh:api:"+token);
		try {
			type = request.getParameter("type");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Result.ERROR, "未获取到参数");
		}

		try {
			switch (type){
//				type:1  白卡1或普号2		skuid,数量,地址
                case "1":

//						不出货		出货
//				type:2  普通靓号3或超级靓号4    skuid, numid, addrid, payType, mealid
				case "2":
					String skuid, numid = null, addrid, payType, mealid;
					Map number = null;
					try {
						skuid = request.getParameter("skuid");
						numid = request.getParameter("numid");
						addrid = request.getParameter("addrid");//普通靓号可不填
						payType = request.getParameter("pauMethod");
						mealid = request.getParameter("mealid")==null?"":request.getParameter("mealid");

						//获取号码
						number = numberMapper.getNumInfoById(numid);
						//冻结号码
						if(!LockUtils.tryLock(numid)) return new Result(Result.ERROR, "请稍后再试!");
						try {
							//验证号码是否可下单,2:销售中
							if(number==null || !"2".equals(String.valueOf(number.get("status")))) return new Result(Result.ERROR, "号码已被购买!");
							freezeNum(numid, "3");
						} finally {
							LockUtils.unLock(numid);
						}
						//获取sku列表
						List skulist = skuMapper.getSkuListBySkuids("'"+ skuid.replaceAll(",", "','") +"'");
						for (int i = 0; i < skulist.size(); i++) {
                            OrderItem orderItem = new OrderItem();
							Map sku = (Map) skulist.get(i);
                            List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(skuid));

							orderItem.setItemId(orderItem.getGeneralId());
							orderItem.setOrderId(order.getOrderId());

							//获取商品
							Goods goods = goodsMapper.findGoodsInfoBySkuid(skuid);
							orderItem.setGoodsId(goods.getgId());
							orderItem.setSkuId(Long.parseLong(skuid));
							orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
							orderItem.setNumId(Long.parseLong(numid));
							orderItem.setNum((String) number.get("numResource"));
							orderItem.setIsShipment("3".equals(sku.get("skuGoodsType"))?0:1);//普通靓号不发货
							orderItem.setSellerId(Long.parseLong((String) sku.get("gSellerId")));
							orderItem.setSellerName((String) sku.get("gSellerName"));
							orderItem.setShipmentApi("egt");
							orderItem.setCompanystockId(Long.parseLong((String) sku.get("skuRepoGoods")));
							int num = 1;
							orderItem.setQuantity(num);
							double twobPrice = Double.parseDouble((String) sku.get("skuTobPrice"));
							orderItem.setPrice(twobPrice);
							orderItem.setTotal(twobPrice*num);
							orderItem.setMealId(Long.parseLong(mealid));
							sub_total += orderItem.getTotal();

                            orderItems.add(orderItem);
						}
						//设置订单
						order.setConsumer(user.getId());
						order.setConsumerName(user.getName());
						order.setStatus(1);//设置成待付款
						order.setReqUserAgent(request.getHeader("user-agent"));
						order.setReqIp(SessionUtil.getUserIp());
						order.setAddDate(new Date());
						order.setOrderType(2);
						if(addrid==null) order.setAddressId(null);
						else{
							//获取收货地址信息
							DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(Long.parseLong(addrid));
							order.setAddressId(deliveryAddress.getId());
							order.setPersonName(deliveryAddress.getPersonName());
							order.setPersonTel(deliveryAddress.getPersonTel());
							order.setAddress(deliveryAddress.getAddress());
						}
						order.setCommission(commission);
						order.setShippingTotal(shipping_total);
						order.setSubTotal(sub_total);
						//子项小计打折之后减去运费
						total = sub_total*commission-shipping_total;
						order.setTotal(total);


						//调用仓储接口成功之后写入
						order.setNoticeShipmentDate(new Date());

						orderList.add(order);
						orderMapper.insertBatch(orderList);
						orderItemMapper.insertBatch(orderItems);
					} catch (Exception e) {
						e.printStackTrace();
						//清除已生成的订单
						deleteOrder(orderList);
						//解冻号码,把冻结之前的状态还原
						freezeNum(numid, String.valueOf(number.get("status")));
						return new Result(Result.ERROR, "获取数据异常");
					}

			}
		} catch (Exception e) {
			e.printStackTrace();
			//清除已生成的订单
			deleteOrder(orderList);
			return new Result(Result.ERROR, "创建订单异常");
		}

		return new Result(Result.OK, order.getOrderId());
	}

	private void deleteOrder(List<Order> orderList) {
		for (Order o : orderList) {
			orderMapper.deleteByOrderid(o.getOrderId());
		}
	}

	/**
	 *
	 * @param numid
	 * @param status 1在库、2销售中、3冻结(下单未付款)、4待配卡(已付款 针对2C或电销无需购买卡时、代理商买号而未指定白卡时)、5待受理(代理商已提交或仓库已发货，待提交乐语BOSS)、6已受理(乐语BOSS处理成功)、7受理失败(BOSS受理失败，需要人介入解决)、8已失效(乐语BOSS提示号码已非可用)
	 */
	private void freezeNum(String numid, String status) {
		numberMapper.freezeNum(numid, status);
	}
}
