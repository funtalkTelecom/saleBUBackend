package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.global.*;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
	    log.info("进入创建订单");
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
//		Consumer u = new Consumer();
//		u.setId(1L);
//		u.setName("周元强");
//		u.setCity("396");
//		u.setIsAgent(2);//设置为一级代理商
//		String token=TokenGenerator.generateValue();
//		apiSessionUtil.saveOrUpdate(token,u);

//		Consumer user = apiSessionUtil.getConsumer();
        log.info("获取用户信息");
		Consumer user = (Consumer) redisUtil.get("egt-kh:api:"+request.getParameter(ApiSessionUtil.JESSION_ID_NAME));
		if(user==null) return new Result(Result.ERROR, "未获取到用户,请重试");
		try {
		    log.info("获取订单类型");
			type = request.getParameter("type");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Result.ERROR, "未获取到参数");
		}

		try {
		    int storagen = 0;
		    if("1".equals(type)) {
                log.info("进入订单类型1");
//						出货	  出货
//				type:1  白卡1或普号2		skuid,数量,地址
                String skuid = null, addrid;
                int numcount;Map number = null;
                List<Number> nlist = null;
                try {
                        log.info("获取传入参数");
                        skuid = request.getParameter("skuid");
                        addrid = request.getParameter("addrid");//普通靓号可不填
                        numcount = request.getParameter("numcount")==null?-1:Integer.parseInt(request.getParameter("numcount"));
                        storagen=numcount;

                        if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
                        if (addrid == null || "".equals(addrid)) return new Result(Result.ERROR, "收货地址不能为空");
                        if (numcount == -1) return new Result(Result.ERROR, "数量不能为空");
                        if (user.getIsAgent() != 2) return new Result(Result.ERROR, "您不是一级代理商,无法提交");
                        //冻结号码
                        if (!LockUtils.tryLock(skuid)) return new Result(Result.ERROR, "请稍后再试!");
                        try {
                            //获取sku列表
                            log.info("获取sku信息");
                            List skulist = skuMapper.getSkuListBySkuids("'" + skuid.replaceAll(",", "','") + "'");
                            if(skulist==null || skulist.size()<=0) return new Result(Result.ERROR, "未找到相关商品,请刷新后再试");
                            for (int i = 0; i < skulist.size(); i++) {
                                OrderItem orderItem = new OrderItem();
                                Map sku = (Map) skulist.get(i);
                                List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(skuid));

                                orderItem.setItemId(orderItem.getGeneralId());
                                orderItem.setOrderId(order.getOrderId());

                                //获取商品
                                log.info("获取商品信息");
                                goods = goodsMapper.findGoodsInfoBySkuid(skuid);
                                log.info("验证一级代理商");
                                if(user.getIsAgent() != 2){
                                    return new Result(Result.ERROR, "您不是一级代理商,无法提交普通靓号订单");
                                }
                                //判断商品地市和代理商地市
                                log.info("判断商品地市和代理商地市");
                                if(user.getCity()==null || !goods.getgSaleCity().contains(user.getCity())) {
                                    return new Result(Result.ERROR, "不属于您的地市,无法操作");
                                }
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
                                //修改sku数量
                                Sku nowSku = skuMapper.getSkuBySkuid(Long.parseLong((String) sku.get("skuId")));
                                nowSku.setSkuNum(Integer.parseInt((String.valueOf(sku.get("skuNum"))))-numcount);
                                skuMapper.updateSkuNum(nowSku);

                                log.info("冻结号码,添加号码item");
                                //是普号,根据数量冻结号码,添加号码item
                                if("2".equals(sku.get("skuGoodsType"))){
                                    //获取可冻结的号码,判断库存是否充足
                                    nlist = numberMapper.getListBySkuidAndStatus(skuid, "'2'", numcount);
                                    if(nlist.size()!=numcount){//获取的数量和购买不等
                                        return new Result(Result.ERROR, "库存不足,请重试");
                                    }
                                    //冻结号码,往orderItem写数据
                                    freezeNumByIds(nlist, "3");
                                    for (Number n : nlist) {
                                        orderItem = new OrderItem();

                                        orderItem.setItemId(orderItem.getGeneralId());
                                        orderItem.setOrderId(order.getOrderId());

                                        orderItem.setGoodsId(goods.getgId());
                                        orderItem.setSkuId(Long.parseLong(skuid));
                                        orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
                                        orderItem.setNumId(n.getId());
                                        orderItem.setNum(n.getNumResource());
                                        orderItem.setIsShipment(0);//号码item无需发货
                                        orderItem.setSellerId(Long.parseLong((String) sku.get("gSellerId")));
                                        orderItem.setSellerName((String) sku.get("gSellerName"));
                                        orderItem.setShipmentApi("egt");
                                        orderItem.setCompanystockId(Long.parseLong((String) sku.get("skuRepoGoods")));
                                        orderItem.setQuantity(1);
                                        twobPrice = Double.parseDouble((String) sku.get("skuTobPrice"));
                                        orderItem.setPrice(0);
                                        orderItem.setTotal(0);
//                                orderItem.setMealId(Long.parseLong(mealid));
                                        sub_total += orderItem.getTotal();

                                        orderItems.add(orderItem);
                                    }
                                }
                            }
                            log.info("设置订单信息");
                            //设置订单
                            order.setConsumer(user.getId());
                            order.setConsumerName(user.getName());
                            order.setStatus(1);//设置成待付款
                            order.setReqUserAgent(request.getHeader("user-agent"));
                            order.setReqIp(SessionUtil.getUserIp());
                            order.setAddDate(new Date());
                            order.setOrderType(1);

                            log.info("设置收货信息");
                            //获取收货地址信息
                            DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(Long.parseLong(addrid));
                            order.setAddressId(deliveryAddress.getId());
                            order.setPersonName(deliveryAddress.getPersonName());
                            order.setPersonTel(deliveryAddress.getPersonTel());
                            order.setAddress(deliveryAddress.getAddress());

                            log.info("计算金额");
                            order.setCommission(commission);
                            order.setShippingTotal(shipping_total);
                            order.setSubTotal(sub_total);
                            //子项小计打折之后减去运费
                            total = sub_total * commission - shipping_total;
                            order.setTotal(total);

                            orderList.add(order);
                        } finally {
                            LockUtils.unLock(skuid);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        //清除已生成的订单
                        deleteOrder(orderList);
                        log.info("解冻号码");
                        //解冻号码,把冻结之前的状态还原
                        freezeNumByIds(nlist, "2");
                        return new Result(Result.ERROR, "获取数据异常");
                    }


            }else if("2".equals(type)) {
                    log.info("进入订单类型2");
                    storagen = 1;
//						不出货		出货
//				type:2  普通靓号3或超级靓号4    skuid, numid, addrid, payType, mealid
                    String skuid, numid = null, addrid, payType, mealid;
                    Map number = null;
                    try {
                        log.info("获取传入参数");
                        skuid = request.getParameter("skuid");
                        numid = request.getParameter("numid");
                        addrid = request.getParameter("addrid");//普通靓号可不填
                        payType = request.getParameter("payMethod");
                        mealid = request.getParameter("mealid") == null ? "" : request.getParameter("mealid");

                        if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
                        if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");

                        log.info("获取号码信息");
                        //获取号码
                        number = numberMapper.getNumInfoById(numid);
                        //冻结号码
                        if (!LockUtils.tryLock(numid)) return new Result(Result.ERROR, "请稍后再试!");
                        try {
                            log.info("验证号码是否可下单");
                            //验证号码是否可下单,2:销售中
                            if (number == null || !"2".equals(String.valueOf(number.get("status"))))
                                return new Result(Result.ERROR, "号码已被购买!");
                            log.info("冻结号码");
                            freezeNum(numid, "3");
                            log.info("获取sku信息");
                            //获取sku列表
                            List skulist = skuMapper.getSkuListBySkuids("'" + skuid.replaceAll(",", "','") + "'");
                            if(skulist==null || skulist.size()<=0) return new Result(Result.ERROR, "未找到相关商品,请刷新后再试");
                            for (int i = 0; i < skulist.size(); i++) {
                                OrderItem orderItem = new OrderItem();
                                Map sku = (Map) skulist.get(i);

                                log.info("获取商品信息");
                                //获取商品
                                goods = goodsMapper.findGoodsInfoBySkuid(skuid);
                                //普通靓号,需要一级代理商
                                if("3".equals(sku.get("skuGoodsType"))){
                                    log.info("普通靓号,验证一级代理商");
                                    if(user.getIsAgent() != 2){
                                        freezeNum(numid, String.valueOf(number.get("status")));
                                        return new Result(Result.ERROR, "您不是一级代理商,无法提交普通靓号订单");
                                    }
                                    log.info("判断商品地市和代理商地市");
                                    //判断商品地市和代理商地市
                                    if(user.getCity()==null || !goods.getgSaleCity().contains(user.getCity())) {
                                        freezeNum(numid, String.valueOf(number.get("status")));
                                        return new Result(Result.ERROR, "不属于您的地市,无法操作");
                                    }
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


                                //超级靓号添加卡的item
                                if("4".equals(sku.get("skuGoodsType"))){
                                    orderItem = new OrderItem();
                                    orderItem.setItemId(orderItem.getGeneralId());
                                    orderItem.setOrderId(order.getOrderId());

                                    orderItem.setGoodsId(goods.getgId());
                                    orderItem.setSkuId(Long.parseLong(skuid));
                                    orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
                                    orderItem.setNumId(Long.parseLong(numid));
                                    orderItem.setNum((String) number.get("numResource"));
                                    orderItem.setIsShipment(1);//卡体发货
                                    orderItem.setSellerId(Long.parseLong((String) sku.get("gSellerId")));
                                    orderItem.setSellerName((String) sku.get("gSellerName"));
                                    orderItem.setShipmentApi("egt");
                                    orderItem.setCompanystockId(Long.parseLong((String) sku.get("skuRepoGoods")));
                                    num = 1;
                                    orderItem.setQuantity(num);
                                    twobPrice = 0;//Double.parseDouble((String) sku.get("skuTobPrice"));
                                    orderItem.setPrice(twobPrice);
                                    orderItem.setTotal(twobPrice * num);
                                    orderItem.setMealId(Long.parseLong(mealid));
                                    sub_total += orderItem.getTotal();

                                    orderItems.add(orderItem);
                                }
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
            else if("3".equals(type)) {//竞拍订单
		        log.info("进入竞拍订单");
                storagen = 1;
                String skuid, numid = null, addrid, price;

                Map number = null;
                try {
                    log.info("获取传入参数");
                    skuid = request.getParameter("skuid");
                    numid = request.getParameter("numid");
                    addrid = request.getParameter("addrid");
                    price = request.getParameter("price");

                    if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
                    if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");
                    if (addrid == null || "".equals(addrid)) return new Result(Result.ERROR, "addrid不能为空");
                    if (price == null || "".equals(price)) return new Result(Result.ERROR, "price不能为空");

                    log.info("获取号码信息");
                    //获取号码
                    number = numberMapper.getNumInfoById(numid);
                    //冻结号码
                    if (!LockUtils.tryLock(numid)) return new Result(Result.ERROR, "请稍后再试!");
                    try {
                        log.info("验证号码是否可下单");
                        //验证号码是否可下单,2:销售中
                        if (number == null || !"2".equals(String.valueOf(number.get("status"))))
                            return new Result(Result.ERROR, "号码已被购买!");
                        log.info("冻结号码");
                        freezeNum(numid, "3");
                        log.info("获取sku信息");
                        //获取sku列表
                        List skulist = skuMapper.getSkuListBySkuids("'" + skuid.replaceAll(",", "','") + "'");
                        if(skulist==null || skulist.size()<=0) return new Result(Result.ERROR, "未找到相关商品,请刷新后再试");
                        for (int i = 0; i < skulist.size(); i++) {
                            OrderItem orderItem = new OrderItem();
                            Map sku = (Map) skulist.get(i);

                            log.info("获取商品信息");
                            //获取商品
                            goods = goodsMapper.findGoodsInfoBySkuid(skuid);
                            List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(skuid));

                            orderItem.setItemId(orderItem.getGeneralId());
                            orderItem.setOrderId(order.getOrderId());
                            orderItem.setGoodsId(goods.getgId());
                            orderItem.setSkuId(Long.parseLong(skuid));
                            orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
                            orderItem.setNumId(Long.parseLong(numid));
                            orderItem.setNum((String) number.get("numResource"));
                            orderItem.setIsShipment(0);
                            orderItem.setSellerId(Long.parseLong((String) sku.get("gSellerId")));
                            orderItem.setSellerName((String) sku.get("gSellerName"));
                            orderItem.setShipmentApi("egt");
                            orderItem.setCompanystockId(Long.parseLong((String) sku.get("skuRepoGoods")));
                            int num = 1;
                            orderItem.setQuantity(num);
                            double twobPrice = Double.parseDouble(price);
                            orderItem.setPrice(twobPrice);
                            orderItem.setTotal(twobPrice * num);
                            sub_total += orderItem.getTotal();

                            orderItems.add(orderItem);


                            //添加卡的item
                            orderItem = new OrderItem();
                            orderItem.setItemId(orderItem.getGeneralId());
                            orderItem.setOrderId(order.getOrderId());

                            orderItem.setGoodsId(goods.getgId());
                            orderItem.setSkuId(Long.parseLong(skuid));
                            orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
                            orderItem.setNumId(Long.parseLong(numid));
                            orderItem.setNum((String) number.get("numResource"));
                            orderItem.setIsShipment(1);//卡体发货
                            orderItem.setSellerId(Long.parseLong((String) sku.get("gSellerId")));
                            orderItem.setSellerName((String) sku.get("gSellerName"));
                            orderItem.setShipmentApi("egt");
                            orderItem.setCompanystockId(Long.parseLong((String) sku.get("skuRepoGoods")));
                            num = 1;
                            orderItem.setQuantity(num);
                            twobPrice = 0;//Double.parseDouble((String) sku.get("skuTobPrice"));
                            orderItem.setPrice(twobPrice);
                            orderItem.setTotal(twobPrice * num);
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
                        order.setOrderType(3);
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
            if (goods.getgStartTime()==null || goods.getgEndTime()==null || !betweenCalendar(new Date(), goods.getgStartTime(), goods.getgEndTime()))
                return new Result(Result.ERROR, "商品不在有效期内");

            log.info("调用仓储接口");
            //调用仓储接口
            //callback SystemParam.get("Storage_domain")+"/deliver-order-callback"
            Map param = new HashMap();
            List items = new ArrayList();
            Long preOrderId = 0L;
            for (OrderItem i : orderItems) {
                if(preOrderId!=i.getOrderId()) {
                    preOrderId = i.getOrderId();
                    param = new HashMap();
                    items = new ArrayList();
                    param.put("order_id", i.getOrderId());
                }

                Map item = new HashMap();
                if(i.getIsShipment()==0) continue;
                item.put("item_id", i.getItemId());
                item.put("companystock_id", i.getCompanystockId());
                item.put("quantity", i.getQuantity());
                items.add(item);

            }
            param.put("commodities", items);
            Result res = HttpUtil.doHttpPost(SystemParam.get("Storage_domain")+"/dispatchRequests.htm",
                    JSONObject.fromObject(new StorageInterfaceRequest(
                            SystemParam.get("merid"),
                            "HK0003",
                            Utils.randomNoByDateTime(),
                            SystemParam.get("key"),
                            param
                    )).toString(),
                    "application/json",
                    "UTF-8");
            if(200!=(res.getCode())){
                return new Result(Result.ERROR, "库存验证失败");
            }else{
                StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
                if("00000".equals(sir.getCode())){
                    //调用仓储接口成功之后写入
                    for (Order o : orderList) {
                        o.setNoticeShipmentDate(new Date());
                    }
                    orderMapper.insertBatch(orderList);
                    orderItemMapper.insertBatch(orderItems);
                }else{
                    return new Result(Result.ERROR, "创建订单异常");
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
        log.info("删除订单");
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

    private void freezeNumByIds(List<Number> nlist , String status) throws  Exception{
        numberMapper.freezeNumByIds(nlist, status);
    }
}
