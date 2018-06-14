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
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
		Goods goods = new Goods();
		order.setOrderId(order.getGeneralId());
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		List<Order> orderList = new ArrayList<Order>();
		String type = null;

		//模拟登陆
		Consumer u = new Consumer();
		u.setId(1L);
		u.setName("周元强");
		u.setCity("396");
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
		    if("1".equals(type)) {
//						出货	  出货
//				type:1  白卡1或普号2		skuid,数量,地址
                    String skuid = null, addrid;
                    int numcount;
                    Map number = null;
                    try {
                        skuid = request.getParameter("skuid");
                        addrid = request.getParameter("addrid");//普通靓号可不填
                        numcount = request.getParameter("numcount")==null?-1:Integer.parseInt(request.getParameter("numcount"));

                        if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
                        if (addrid == null || "".equals(addrid)) return new Result(Result.ERROR, "收货地址不能为空");
                        if (numcount == -1) return new Result(Result.ERROR, "数量不能为空");
                        if (user.getIsAgent() != 2) return new Result(Result.ERROR, "您不是一级代理商,无法提交");
                        //冻结号码
                        if (!LockUtils.tryLock(skuid)) return new Result(Result.ERROR, "请稍后再试!");
                        try {
                            //获取sku列表
                            List skulist = skuMapper.getSkuListBySkuids("'" + skuid.replaceAll(",", "','") + "'");
                            for (int i = 0; i < skulist.size(); i++) {
                                OrderItem orderItem = new OrderItem();
                                Map sku = (Map) skulist.get(i);
                                List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(skuid));

                                orderItem.setItemId(orderItem.getGeneralId());
                                orderItem.setOrderId(order.getOrderId());

                                //获取商品
                                goods = goodsMapper.findGoodsInfoBySkuid(skuid);
                                orderItem.setGoodsId(goods.getgId());
                                orderItem.setSkuId(Long.parseLong(skuid));
                                orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
//                                orderItem.setNumId(Long.parseLong(numid));
//                                orderItem.setNum((String) number.get("numResource"));
                                orderItem.setIsShipment(1);//都需要发货
                                orderItem.setSellerId(Long.parseLong((String) sku.get("gSellerId")));
                                orderItem.setSellerName((String) sku.get("gSellerName"));
                                orderItem.setShipmentApi("egt");
                                orderItem.setCompanystockId(Long.parseLong((String) sku.get("skuRepoGoods")));
                                orderItem.setQuantity(numcount);
                                double twobPrice = Double.parseDouble((String) sku.get("skuTobPrice"));
                                orderItem.setPrice(twobPrice);
                                orderItem.setTotal(twobPrice * numcount);
//                                orderItem.setMealId(Long.parseLong(mealid));
                                sub_total += orderItem.getTotal();

                                orderItems.add(orderItem);

                                //是普号,添加号码item
                                if("2".equals(sku.get("skuGoodsType"))){

                                }
                            }
                            //设置订单
                            order.setConsumer(user.getId());
                            order.setConsumerName(user.getName());
                            order.setStatus(1);//设置成待付款
                            order.setReqUserAgent(request.getHeader("user-agent"));
                            order.setReqIp(SessionUtil.getUserIp());
                            order.setAddDate(new Date());
                            order.setOrderType(1);

                            //获取收货地址信息
                            DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(Long.parseLong(addrid));
                            order.setAddressId(deliveryAddress.getId());
                            order.setPersonName(deliveryAddress.getPersonName());
                            order.setPersonTel(deliveryAddress.getPersonTel());
                            order.setAddress(deliveryAddress.getAddress());

                            order.setCommission(commission);
                            order.setShippingTotal(shipping_total);
                            order.setSubTotal(sub_total);
                            //子项小计打折之后减去运费
                            total = sub_total * commission - shipping_total;
                            order.setTotal(total);


                            //调用仓储接口成功之后写入
                            order.setNoticeShipmentDate(new Date());

                            orderList.add(order);
                        } finally {
                            LockUtils.unLock(skuid);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        //清除已生成的订单
                        deleteOrder(orderList);
                        //解冻号码,把冻结之前的状态还原
                        freezeNumBySkuid(skuid, "2");
                        return new Result(Result.ERROR, "获取数据异常");
                    }


            }else if("2".equals(type)) {
//						不出货		出货
//				type:2  普通靓号3或超级靓号4    skuid, numid, addrid, payType, mealid
                    String skuid, numid = null, addrid, payType, mealid;
                    Map number = null;
                    try {
                        skuid = request.getParameter("skuid");
                        numid = request.getParameter("numid");
                        addrid = request.getParameter("addrid");//普通靓号可不填
                        payType = request.getParameter("pauMethod");
                        mealid = request.getParameter("mealid") == null ? "" : request.getParameter("mealid");

                        if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
                        if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");

                        //获取号码
                        number = numberMapper.getNumInfoById(numid);
                        //冻结号码
                        if (!LockUtils.tryLock(numid)) return new Result(Result.ERROR, "请稍后再试!");
                        try {
                            //验证号码是否可下单,2:销售中
                            if (number == null || !"2".equals(String.valueOf(number.get("status"))))
                                return new Result(Result.ERROR, "号码已被购买!");
                            freezeNum(numid, "3");
                            //获取sku列表
                            List skulist = skuMapper.getSkuListBySkuids("'" + skuid.replaceAll(",", "','") + "'");
                            for (int i = 0; i < skulist.size(); i++) {
                                OrderItem orderItem = new OrderItem();
                                Map sku = (Map) skulist.get(i);
                                //获取商品
                                goods = goodsMapper.findGoodsInfoBySkuid(skuid);
                                //普通靓号,需要一级代理商
                                if("3".equals(sku.get("skuGoodsType")) && user.getIsAgent() != 2){
                                    freezeNum(numid, String.valueOf(number.get("status")));
                                    return new Result(Result.ERROR, "您不是一级代理商,无法提交普通靓号订单");
                                }
                                //判断商品地市和代理商地市
                                else if(user.getCity()==null || !goods.getgSaleCity().contains(user.getCity())) {
                                    freezeNum(numid, String.valueOf(number.get("status")));
                                    return new Result(Result.ERROR, "不属于您的地市,无法操作");
                                }
                                List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(skuid));

                                orderItem.setItemId(orderItem.getGeneralId());
                                orderItem.setOrderId(order.getOrderId());

                                orderItem.setGoodsId(goods.getgId());
                                orderItem.setSkuId(Long.parseLong(skuid));
                                orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
                                orderItem.setNumId(Long.parseLong(numid));
                                orderItem.setNum((String) number.get("numResource"));
                                orderItem.setIsShipment("3".equals(sku.get("skuGoodsType")) ? 0 : 1);//普通靓号不发货
                                orderItem.setSellerId(Long.parseLong((String) sku.get("gSellerId")));
                                orderItem.setSellerName((String) sku.get("gSellerName"));
                                orderItem.setShipmentApi("egt");
                                orderItem.setCompanystockId(Long.parseLong((String) sku.get("skuRepoGoods")));
                                int num = 1;
                                orderItem.setQuantity(num);
                                double twobPrice = Double.parseDouble((String) sku.get("skuTobPrice"));
                                orderItem.setPrice(twobPrice);
                                orderItem.setTotal(twobPrice * num);
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
                            if (addrid == null) order.setAddressId(null);
                            else {
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
                            total = sub_total * commission - shipping_total;
                            order.setTotal(total);


                            //调用仓储接口成功之后写入
                            order.setNoticeShipmentDate(new Date());

                            orderList.add(order);
                        } finally {
                            LockUtils.unLock(numid);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //清除已生成的订单
                        deleteOrder(orderList);
                        //解冻号码,把冻结之前的状态还原
                        freezeNum(numid, String.valueOf(number.get("status")));
                        return new Result(Result.ERROR, "获取数据异常");
                    }
            }
            //判断商品是否在有效期内
            if (!betweenCalendar(new Date(), goods.getgStartTime(), goods.getgEndTime()))
                return new Result(Result.ERROR, "不在有效期内");
            orderMapper.insertBatch(orderList);
            orderItemMapper.insertBatch(orderItems);
		} catch (Exception e) {
			e.printStackTrace();
			//清除已生成的订单
			deleteOrder(orderList);
			return new Result(Result.ERROR, "创建订单异常");
		}

		return new Result(Result.OK, order.getOrderId());
	}

    /**
     * 字符串转换成日期
     * @param str
     * @return date
     */
    public static Date StrToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 判断time是否在from，to之内
     *
     * @param time 指定日期
     * @param from 开始日期
     * @param to   结束日期
     * @return
     */
    private static boolean betweenCalendar(Date time, Date from, Date to) {
        Calendar date = Calendar.getInstance();
        date.setTime(time);

        Calendar after = Calendar.getInstance();
        after.setTime(from);

        Calendar before = Calendar.getInstance();
        before.setTime(to);

        if (date.after(after) && date.before(before)) {
            return true;
        } else {
            return false;
        }
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

    private void freezeNumBySkuid(String skuid, String status) {
//        numberMapper.freezeNumBySkuid(skuid, status);
    }
}
