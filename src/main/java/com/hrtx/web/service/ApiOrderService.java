package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import net.sf.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ApiOrderService {
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
	private AuctionMapper auctionMapper;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private FileMapper fileMapper;
	@Autowired
	FundOrderService fundOrderService;

	public  List<Map> findOrderListByNumId(Long numId)
	{
		return  orderMapper.findOrderListByNumIdAndConsumerId(numId,this.apiSessionUtil.getConsumer().getId());
	}

	public Result signOrder(Order order,HttpServletRequest request)
	{
		Long orderId=0L;
		orderId=order.getOrderId();
		int status=0;
		Long consumerId=0L;
		if(orderId>0)
		{
			Order order2=orderMapper.selectByPrimaryKey(order.getOrderId());
			if(order2!=null)
			{
				consumerId=order2.getConsumer();
				if(!(this.apiSessionUtil.getConsumer().getId().toString().equals(consumerId.toString())))
				{
					return new Result(Result.ERROR, "该订单不属于当前用户");
				}
				if(!(order2.getStatus()==5))//5待签收(仓储物流已取件)；6完成
				{
					if(order2.getStatus()==6)
					{
						return new Result(Result.ERROR, "该订单处于完成状态，请选待签收状态的订单");
					}else
					{
						return new Result(Result.ERROR, "该订单不是待签收状态的订单");
					}
				}
			}else
			{
				return new Result(Result.ERROR, "该订单系统不存在");
			}
		}else
		{
			return new Result(Result.ERROR, "该订单系统不存在");
		}
		order.setStatus(6);
		order.setSignDate(new Date());//签收时间
		order.setSignType(1);//签收方式1用户自动签收2系统
		orderMapper.signByOrderid(order);
		return new Result(Result.OK, "提交成功");
	}

	/*
	 订单 已发货待签收>7天
	 */
	@Scheduled(fixedRate=3000)
	public void signOrderSystem() {
		if(!"true".equals(SystemParam.get("exe_timer"))) return;
		log.info("开始执行.....已发货待签收的订单>7天时,系统自动签收......定时器");
		List<Map> list=this.orderMapper.findOrderSignList();
		if(list.isEmpty()){
			log.info(String.format("暂无已发货未签收的订单"));return;
		}
		Long orderId=0L;
		Order order=new Order();
		if(list.size()>0)
		{
			for(Map map :list)
			{
				orderId=Long.valueOf(map.get("orderId").toString());
				order.setOrderId(orderId);
				order.setStatus(6);
				order.setSignDate(new Date());//签收时间
				order.setSignType(2);//签收方式1用户自动签收2系统
				orderMapper.signByOrderid(order);
				log.info("订单已发货未签收>7天系统自动签收,OrderId:"+orderId);
			}
		}
	}

	/**
	 * 根据商品id创建订单
	 * @param request
	 * @param 1:普靓和超靓:skuid, numid, 地址id, 支付方式, 套餐id
	 *             2:白卡和普号:skuid,数量,地址
	 * @return
	 */
	public Result createOrder(HttpServletRequest request, Map oparam){
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
		OrderItem pOrderItem = new OrderItem();
		Goods goods = new Goods();
		order.setOrderId(order.getGeneralId());
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		List<Order> orderList = new ArrayList<Order>();
		String type = null;
		String skuGoodsType = null;


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
		Consumer user =null;
		Auction action=null;//type:3 出价记录
		try {
			log.info("获取订单类型");
			if(oparam!=null) {
				type = String.valueOf(oparam.get("type"));
				user = (Consumer) oparam.get("user");
			}
			else {
				type = request.getParameter("type");
				user = apiSessionUtil.getConsumer();
			}
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return new Result(Result.ERROR, "未获取到参数");
		}

		if(user==null) return new Result(Result.ERROR, "未获取到用户,请重试");
		try {
			int storagen = 0;
			if("1".equals(type)) {
				log.info("进入订单类型1");
//						出货	  出货
//				type:1  白卡1或普号2		skuid,数量,地址
				String skuid = null, addrid;
				int numcount;Map number = null;
				List<com.hrtx.web.pojo.Number> nlist = null;
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
					//获取sku列表
					log.info("获取sku信息");
					List skulist = skuMapper.getSkuListBySkuids("'" + skuid.replaceAll(",", "','") + "'");
					if(skulist==null || skulist.size()<=0) return new Result(Result.ERROR, "未找到相关商品,请刷新后再试");
					for (int i = 0; i < skulist.size(); i++) {
						OrderItem orderItem = new OrderItem();
						Map sku = (Map) skulist.get(i);
						int a = Integer.parseInt((String.valueOf(sku.get("skuNum"))));
						if(a==0) return new Result(Result.ERROR, "已售罄,请稍微再试");
						if(a < numcount) return new Result(Result.ERROR, "库存数不足,请重试");
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
						if(user.getAgentCity()==null || !goods.getgSaleCity().contains(String.valueOf(user.getAgentCity()))) {
							return new Result(Result.ERROR, "不属于您的地市,无法操作");
						}
						skuGoodsType = String.valueOf(sku.get("skuGoodsType"));
						//卡体item
						orderItem.setGoodsId(goods.getgId());
						orderItem.setSkuId(Long.parseLong(skuid));
						orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
//                                orderItem.setNumId(Long.parseLong(numid));
//                                orderItem.setNum(String.valueOf( number.get("numResource"));
						orderItem.setIsShipment(1);//都需要发货
						orderItem.setSellerId(Long.parseLong(String.valueOf( sku.get("gSellerId"))));
						orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
						orderItem.setShipmentApi("egt");
						orderItem.setCompanystockId(Long.parseLong(String.valueOf( sku.get("skuRepoGoods"))));
						orderItem.setQuantity(numcount);
						double twobPrice = Double.parseDouble(String.valueOf(sku.get("skuTobPrice")));
						orderItem.setPrice(twobPrice);
						orderItem.setTotal(twobPrice * numcount);
//                                orderItem.setMealId(Long.parseLong(mealid));
						sub_total += orderItem.getTotal();

						pOrderItem = orderItem;
						orderItems.add(orderItem);
						//修改sku数量
						Sku nowSku = skuMapper.getSkuBySkuid(Long.parseLong(String.valueOf( sku.get("skuId"))));
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
							for (com.hrtx.web.pojo.Number n : nlist) {
								orderItem = new OrderItem();
								orderItem.setpItemId(pOrderItem.getItemId());

								orderItem.setItemId(orderItem.getGeneralId());
								orderItem.setOrderId(order.getOrderId());

								orderItem.setGoodsId(goods.getgId());
								orderItem.setSkuId(Long.parseLong(skuid));
								orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
								orderItem.setNumId(n.getId());
								orderItem.setNum(n.getNumResource());
								orderItem.setIsShipment(0);//号码item无需发货
								orderItem.setSellerId(Long.parseLong(String.valueOf( sku.get("gSellerId"))));
								orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
								orderItem.setShipmentApi("egt");
								orderItem.setCompanystockId(Long.parseLong(String.valueOf( sku.get("skuRepoGoods"))));
								orderItem.setQuantity(1);
								twobPrice = Double.parseDouble(String.valueOf(sku.get("skuTobPrice")));
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
					order.setSkuGoodsType(skuGoodsType);
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
				} catch (Exception e) {
					e.printStackTrace();
					//清除已生成的订单
					deleteOrder(orderList);
					log.info("解冻号码");
					//解冻号码,把冻结之前的状态还原
					freezeNumByIds(nlist, "2");
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return new Result(Result.ERROR, "获取数据异常");
				}


			}
			else if("2".equals(type)) {
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
					addrid = request.getParameter("addrid") == null ? "" : request.getParameter("addrid");//普通靓号可不填
					payType = request.getParameter("payMethod") == null ? "" : request.getParameter("payMethod");
					mealid = request.getParameter("mealid") == null ? "" : request.getParameter("mealid");

					if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
					if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");

					log.info("获取号码信息");
					//获取号码
					number = numberMapper.getNumInfoById(numid);
					//冻结号码
					log.info("验证号码是否可下单");
					//验证号码是否可下单,2:销售中
					if (number == null || !"2".equals(String.valueOf(number.get("status"))))
						return new Result(Result.ERROR, "号码已被购买!");
					log.info("冻结号码");
					freezeNum(numid, "3",false);
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
								freezeNum(numid, String.valueOf(number.get("status")),false);
								return new Result(Result.ERROR, "您不是一级代理商,无法提交普通靓号订单");
							}
							log.info("判断商品地市和代理商地市");
							//判断商品地市和代理商地市

							if(!StringUtils.equals(number.get("cityId")+"",user.getAgentCity()+"")) {
								freezeNum(numid, String.valueOf(number.get("status")),false);
								return new Result(Result.ERROR, "不属于您的地市,无法操作");
							}
							/*if(user.getAgentCity()==null || !goods.getgSaleCity().contains(String.valueOf(user.getAgentCity()))) {
								freezeNum(numid, String.valueOf(number.get("status")));
								return new Result(Result.ERROR, "不属于您的地市,无法操作");
							}*/
						}
						List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(skuid));


						int num = 1;
						double twobPrice = 0;//Double.parseDouble(String.valueOf( sku.get("skuTobPrice"));
						//修改sku数量
						Sku nowSku = skuMapper.getSkuBySkuid(Long.parseLong(String.valueOf( sku.get("skuId"))));
						nowSku.setSkuNum(Integer.parseInt((String.valueOf(sku.get("skuNum"))))-num);
						skuMapper.updateSkuNum(nowSku);
						//超级靓号添加卡的item
						if("4".equals(sku.get("skuGoodsType"))){
							log.info("超级靓号添加卡体item");
							orderItem = new OrderItem();
							orderItem.setItemId(orderItem.getGeneralId());
							orderItem.setOrderId(order.getOrderId());

							orderItem.setGoodsId(goods.getgId());
							orderItem.setSkuId(Long.parseLong(skuid));
							orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
							orderItem.setNumId(Long.parseLong(numid));
							orderItem.setNum(String.valueOf( number.get("numResource")));
							orderItem.setIsShipment(1);//卡体发货
							orderItem.setSellerId(Long.parseLong(String.valueOf( sku.get("gSellerId"))));
							orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
							orderItem.setShipmentApi("egt");
							orderItem.setCompanystockId(Long.parseLong(String.valueOf( sku.get("skuRepoGoods"))));
							orderItem.setQuantity(num);
							orderItem.setPrice(twobPrice);
							orderItem.setTotal(twobPrice * num);
							if(!StringUtils.isBlank(mealid)) orderItem.setMealId(Long.parseLong(mealid));
							sub_total += orderItem.getTotal();

							pOrderItem = orderItem;
							orderItems.add(orderItem);
						}

						skuGoodsType = String.valueOf(sku.get("skuGoodsType"));
						log.info("添加号码item");
						//号码item
						orderItem = new OrderItem();
						orderItem.setpItemId(pOrderItem.getItemId());
						orderItem.setItemId(orderItem.getGeneralId());
						orderItem.setOrderId(order.getOrderId());
						orderItem.setGoodsId(goods.getgId());
						orderItem.setSkuId(Long.parseLong(skuid));
						orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
						orderItem.setNumId(Long.parseLong(numid));
						orderItem.setNum(String.valueOf( number.get("numResource")));
						orderItem.setIsShipment(0);//普通靓号不发货
						orderItem.setSellerId(Long.parseLong(String.valueOf( sku.get("gSellerId"))));
						orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
						orderItem.setShipmentApi("egt");
						orderItem.setCompanystockId(Long.parseLong(String.valueOf( sku.get("skuRepoGoods"))));
						num = 1;
						orderItem.setQuantity(num);
						twobPrice = Double.parseDouble(String.valueOf(sku.get("skuTobPrice")));
						orderItem.setPrice(twobPrice);
						orderItem.setTotal(twobPrice * num);
						if(!StringUtils.isBlank(mealid)) orderItem.setMealId(Long.parseLong(mealid));
						sub_total += orderItem.getTotal();

						orderItems.add(orderItem);
					}

					log.info("设置订单信息");
					//设置订单
					order.setSkuGoodsType(skuGoodsType);
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
				} catch (Exception e) {
					e.printStackTrace();
					//清除已生成的订单
					deleteOrder(orderList);
					//解冻号码,把冻结之前的状态还原
					freezeNum(numid, String.valueOf(number.get("status")),false);
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
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
					if(oparam!=null){
						skuid = String.valueOf(oparam.get("skuid"));
						numid = String.valueOf(oparam.get("numid"));
						addrid = String.valueOf(oparam.get("addrid"));
						price = String.valueOf(oparam.get("price"));
					}else {
						skuid = request.getParameter("skuid");
						numid = request.getParameter("numid");
						addrid = request.getParameter("addrid");
						price = request.getParameter("price");
					}
					action=new Auction();
					action.setNumId(Long.valueOf(numid));
					action.setSkuId(Long.valueOf(skuid));
					if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
					if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");
					//if (addrid == null || "".equals(addrid)) return new Result(Result.ERROR, "addrid不能为空");
					if (price == null || "".equals(price)) return new Result(Result.ERROR, "price不能为空");
					log.info("获取号码信息");
					//获取号码
					number = numberMapper.getNumInfoById(numid);
					//冻结号码
					log.info("验证号码是否可下单");
					//验证号码是否可下单,2:销售中
					if (number == null || !"2".equals(String.valueOf(number.get("status"))))
						return new Result(Result.ERROR, "号码已被购买!");
					log.info("冻结号码");
					freezeNum(numid, "3",false);
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

						skuGoodsType = String.valueOf(sku.get("skuGoodsType"));

						int num = 1;
						double twobPrice = Double.parseDouble(price);
						//修改sku数量
						Sku nowSku = skuMapper.getSkuBySkuid(Long.parseLong(String.valueOf( sku.get("skuId"))));
						nowSku.setSkuNum(Integer.parseInt((String.valueOf(sku.get("skuNum"))))-num);
						skuMapper.updateSkuNum(nowSku);
						log.info("添加卡体item");
						//添加卡的item
						orderItem = new OrderItem();
						orderItem.setItemId(orderItem.getGeneralId());
						orderItem.setOrderId(order.getOrderId());

						orderItem.setGoodsId(goods.getgId());
						orderItem.setSkuId(Long.parseLong(skuid));
						orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
						orderItem.setNumId(Long.parseLong(numid));
						orderItem.setNum(String.valueOf( number.get("numResource")));
						orderItem.setIsShipment(1);//卡体发货
						orderItem.setSellerId(Long.parseLong(String.valueOf( sku.get("gSellerId"))));
						orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
						orderItem.setShipmentApi("egt");
						orderItem.setCompanystockId(Long.parseLong(String.valueOf( sku.get("skuRepoGoods"))));
						num = 1;
						orderItem.setQuantity(num);
						twobPrice = 0;//Double.parseDouble(String.valueOf( sku.get("skuTobPrice"));
						orderItem.setPrice(twobPrice);
						orderItem.setTotal(twobPrice * num);
						sub_total += orderItem.getTotal();

						pOrderItem=orderItem;
						orderItems.add(orderItem);

						log.info("添加号码item");
						//号码item
						orderItem = new OrderItem();
						orderItem.setItemId(orderItem.getGeneralId());
						orderItem.setpItemId(pOrderItem.getItemId());
						orderItem.setOrderId(order.getOrderId());
						orderItem.setGoodsId(goods.getgId());
						orderItem.setSkuId(Long.parseLong(skuid));
						orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
						orderItem.setNumId(Long.parseLong(numid));
						orderItem.setNum(String.valueOf( number.get("numResource")));
						orderItem.setIsShipment(0);
						orderItem.setSellerId(Long.parseLong(String.valueOf( sku.get("gSellerId"))));
						orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
						orderItem.setShipmentApi("egt");
						orderItem.setCompanystockId(Long.parseLong(String.valueOf( sku.get("skuRepoGoods"))));
						num = 1;
						orderItem.setQuantity(num);
						twobPrice = Double.parseDouble(price);
						orderItem.setPrice(twobPrice);
						orderItem.setTotal(twobPrice * num);
						sub_total += orderItem.getTotal();

						orderItems.add(orderItem);
					}

					log.info("设置订单信息");
					//设置订单
					order.setSkuGoodsType(skuGoodsType);
					order.setConsumer(user.getId());
					order.setConsumerName(user.getName());
					order.setStatus(1);//设置成待付款
					//order.setReqUserAgent(request.getHeader("user-agent"));
					order.setReqIp(SessionUtil.getUserIp());
					order.setAddDate(new Date());
					order.setOrderType(3);
					if (addrid == null) order.setAddressId(null);
					else {
						//获取收货地址信息
						DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(Long.parseLong(addrid));
						if(deliveryAddress!=null)
						{
							order.setAddressId(deliveryAddress.getId());
							order.setPersonName(deliveryAddress.getPersonName());
							order.setPersonTel(deliveryAddress.getPersonTel());
							order.setAddress(deliveryAddress.getAddress());
						}
					}
					order.setCommission(commission);
					order.setShippingTotal(shipping_total);
					order.setSubTotal(sub_total);
					//子项小计打折之后减去运费
					total = sub_total * commission - shipping_total;
					order.setTotal(total);
					orderList.add(order);
				} catch (Exception e) {
					e.printStackTrace();
					//清除已生成的订单
					deleteOrder(orderList);
					//解冻号码,把冻结之前的状态还原
					freezeNum(numid, String.valueOf(number.get("status")),false);
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return new Result(Result.ERROR, "获取数据异常");
				}
			}
			if(!type.equals("3"))//竞拍type3,不需要判断商品有效期
			{
				log.info("判断商品有效期");
				//判断商品是否在有效期内
				if (goods.getgStartTime()==null || goods.getgEndTime()==null || !betweenCalendar(new Date(), goods.getgStartTime(), goods.getgEndTime())) {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return new Result(Result.ERROR, "商品不在有效期内");
				}
			}

			log.info("调用仓储接口前封装参数");
			Map param = new HashMap();
			List items = new ArrayList();
			Long preOrderId = 0L;
			for (OrderItem i : orderItems) {
				if(i.getIsShipment()==0) continue;
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
			//先写入表,再调用仓储
			try{
				orderMapper.insertBatch(orderList);
				orderItemMapper.insertBatch(orderItems);
				if (type.equals("3"))//竞拍订单生成，对应订单Id回填到 出价记录(aution.status=2)的orderId字段
				{
					action.setOrderId(preOrderId);
					auctionMapper.auctionEditOrderIDByNumIdAndSkuId(action);
				}
			}catch(Exception e){
			    //写入数据异常,回滚
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return new Result(Result.ERROR, "写入订单数据异常");
			}
			//要发货的item,调用仓储接口
			if(items!=null && items.size()>0) {
				log.info("调用仓储接口");
				Result res = StorageApiCallUtil.storageApiCall(param, "HK0003");
				if (200 != (res.getCode())) {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return new Result(Result.ERROR, "库存验证失败");
				} else {
					StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
					if (!"00000".equals(sir.getCode())) {
						TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
						return new Result(Result.ERROR, "库存验证失败\n"+sir.getDesc());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			//解冻号码
			for (OrderItem i : orderItems) {
				freezeNum(i.getNumId().toString(), "2",false);
			}
			//清除已生成的订单
			deleteOrder(orderList);
			if(type.equals("3"))//竞拍订单生成，对应订单Id回填到 出价记录(aution.status=2)的orderId字段
			{
				action.setOrderId(0L);
				auctionMapper.auctionEditOrderIDByNumIdAndSkuId(action);
			}
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return new Result(Result.ERROR, "创建订单异常");
		}

		return new Result(Result.OK, order.getOrderId().toString());
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
	public void freezeNum(String numid, String status,boolean isUpdateSukid) {
		numberMapper.freezeNum(numid, status,isUpdateSukid);
	}

	private void freezeNumByIds(List<com.hrtx.web.pojo.Number> nlist , String status) throws  Exception{
		numberMapper.freezeNumByIds(nlist, status);
	}

	public Result getOrderByConsumer(HttpServletRequest request) {
		Consumer consumer = apiSessionUtil.getConsumer();
		//模拟登陆
//		consumer = new Consumer();
//		consumer.setId(1L);
		if(consumer==null) return new Result(Result.ERROR, "未获取到用户");
		PageInfo<Object> pm = null;
		Order order = new Order();
		try{
			int pageNum = request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum"));
			int limit = request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit"));
			int status = request.getParameter("status")==null?0: Integer.parseInt(request.getParameter("status"));
			String st = "";
//			order.setStart(limit*(pageNum-1));
			order.setLimit(limit);
			order.setPageNum(pageNum);
			order.setConsumer(consumer.getId());
			if(status==0){
				st = "1,2,3,4,5,6,7,11,12,13,14";
			}else if(status==1){
				st = "'1'";
			}else if(status==2){
				st = "'2','3'";
			}else if(status==3){
				st = "'4','5'";
			}else if(status==4){
				st = "'6'";
			}

			PageHelper.startPage(order.getPageNum(),order.getLimit());
			Page<Object> ob=orderMapper.getOrderByConsumer(order, st);
			if(ob!=null && ob.size()>0){
				for(int i=0; i<ob.size(); i++){
					Map g = (Map) ob.get(i);
					g.put("fileName", SystemParam.get("domain-full") + "/get-img"+SystemParam.get("goodsPics") +g.get("gId")+"/"+ g.get("fileName"));
					//获取sku的属性,追加到名称中
//					List prolist = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Long.parseLong(String.valueOf(g.get("skuId"))));
//					if(prolist!=null && prolist.size()>0){
//						StringBuffer pro = new StringBuffer();
//						for(int j=0; j<prolist.size(); j++){
//							Map p = (Map) prolist.get(j);
//							pro.append(p.get("keyValue")+" ");
//						}
//						g.put("gName", g.get("gName") + " (" + pro.substring(0, pro.length()-1) + ")");
//					}
				}
			}
			pm = new PageInfo<Object>(ob);
		}catch(Exception e){
			return new Result(Result.ERROR, "获取列表异常");
		}

		return new Result(Result.OK, pm);
	}

	public Result getOrderAndItemsByOrderId(HttpServletRequest request, String id) {
		Map o = new HashMap();
		try{
			Order order = orderMapper.findOrderInfo(Long.parseLong(id));
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(Long.parseLong(id));
			List list = orderItemMapper.queryPageListDetailForConsumer(orderItem);
			List<File> fileList = new ArrayList<File>();
			Goods goods = new Goods();
			if(list!=null && list.size()>0) {
				goods = goodsMapper.findGoodsInfo(Long.parseLong(String.valueOf(((Map) list.get(0)).get("goodsId"))));

				fileList = fileMapper.findFilesByRefid(String.valueOf(goods.getgId()));
				String picUrl = "";
				if (fileList != null && fileList.size() > 0) {
					for (File file : fileList) {
						file.setFileName(SystemParam.get("domain-full") + "/get-img"+SystemParam.get("goodsPics") +goods.getgId()+"/"+ file.getFileName());
						picUrl = file.getFileName();
						break;
					}
				}
				for (int i = 0; i < list.size(); i++) {
					Map m = (Map) list.get(i);
					m.put("picUrl", picUrl);
					//获取sku的属性,追加到名称中
					JSONArray prolist = JSONArray.fromObject(m.get("skuProperty"));
					if(prolist!=null && prolist.size()>0){
						StringBuffer pro = new StringBuffer();
						for(int j=0; j<prolist.size(); j++){
							Map p = (Map) prolist.get(j);
							pro.append(p.get("keyValue")+" ");
						}
						String n = goods.getgName() + " (" + pro.substring(0, pro.length() - 1) + ")";
						m.put("itemName", pro.length()>0?n:n.substring(0, n.length()-3));
					}
				}
			}

			DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(order.getAddressId());
			order.setAddress(getFullAddress(deliveryAddress));

			o.put("order", order);
			o.put("goods", goods);
			o.put("orderItem", list);
		}catch(Exception e){
			return new Result(Result.ERROR, "获取异常");
		}

		return new Result(Result.OK, o);
	}

	private String getFullAddress(DeliveryAddress deliveryAddress) {
	    if(deliveryAddress == null) return "";
		StringBuffer addr = new StringBuffer();
		addr.append(deliveryAddress.getProvinceName());
		addr.append(deliveryAddress.getCityName());
		addr.append(deliveryAddress.getDistrictName());
		addr.append(deliveryAddress.getAddress());

		return addr.toString();
	}

	/***
	 * 小程序端取消订单
	 * @param orderIds
	 * @param reason
	 * @return
	 */
	public Result CancelOrderAllCase(String orderIds,String reason){
		Consumer consumer= this.apiSessionUtil.getConsumer();
		long consumerId = consumer.getId();
//		String a = "1014426510456520704";
//		long consumerId = Long.valueOf(a);
		Example example = new Example(Order.class);
		Long orderId =Long.parseLong(orderIds);
		example.createCriteria().andEqualTo("consumer",consumerId).andEqualTo("orderId", orderId);
		List<Order> orders=orderMapper.selectByExample(example);
		if(orders.size()==0) return new Result(Result.ERROR, "该订单不存在");
		return this.CancelOrder(orderIds,reason);
	}


	public Result CancelOrder(String orderIds,String reason){
		Long orderId =Long.parseLong(orderIds);
		Order order = orderMapper.findOrderInfo(orderId);
		if(order.getSkuGoodsType().equals("3")){  //普靓没有冻结库存，不调用仓库接口
			log.info("更新订单状态为7:已取消");
			int status =7;
			CancelOrderStatus(orderId,status,reason);
			Result ispay =fundOrderService.queryPayOrderInfo(String.valueOf(orderId));
			if(ispay.getCode()==200){  //已支付
				if(ispay.getData().equals("1")){//线上支付
					CancelOrderStatus(orderId,12,""); //退款中
					Result payR = fundOrderService.payOrderRefund(String.valueOf(orderId),reason);
					if(payR.getCode()==200){  //退款成功
						orderType(orderId);
					}else { //退款失败
						CancelOrderStatus(orderId,13,""); //退款失败

					}
				}else {//线下支付
					CancelOrderStatus(orderId,14,""); //待财务退款
				}
			}else {//未支付
				//上架涉及的表，数量，状态
				orderType(orderId);
			}
		}else {
			log.info("调用仓储取消订单接口前封装参数");
			Map param = new HashMap();
			String callbackUrl =SystemParam.get("domain-full")+"/order/cancel-order-callback";
			param.put("order_id",orderId);
			param.put("callback_url",callbackUrl);
			param.put("reason",reason);

			log.info("调用仓储接口");
			Result res = StorageApiCallUtil.storageApiCall(param, "HK0005");
			Map maps =  MapJsonUtils.parseJSON2Map(res.getData().toString());
			String code =maps.get("code").toString();
			String desc =maps.get("desc").toString();
			if("10000".equals(code)){
				log.info("业务受理成功,等待回调");
				log.info("更新订单状态为11:待仓库撤销");
				int status =11;
				CancelOrderStatus(orderId,status,reason);
			}else if ("00000".equals(code)){
				log.info("成功");
				log.info("更新订单状态为7:已取消");
				int status =7;
				CancelOrderStatus(orderId,status,reason);
				Result ispay =fundOrderService.queryPayOrderInfo(String.valueOf(orderId));
				if(ispay.getCode()==200){  //已支付
					if(ispay.getData().equals("1")){ //线上支付
						CancelOrderStatus(orderId,12,""); //退款中
						Result payR = fundOrderService.payOrderRefund(String.valueOf(orderId),reason);
						if(payR.getCode()==Result.OK){  //退款成功
							orderType(orderId);
						}else { //退款失败
							CancelOrderStatus(orderId,13,""); //退款失败
						}
					}else {//线下支付
						CancelOrderStatus(orderId,14,""); //待财务退款
					}
				}else {//未支付
					//上架涉及的表，数量，状态
					orderType(orderId);
				}
			}else { //异常或者超时
				return new Result(Result.ERROR, "超时或者异常，请稍后再试");
			}
		}
		return new Result(Result.OK, "取消成功");
	}


	/**
	 * 根据orderId，更新订单状态
	 * @param orderId
	 * @param status
	 * @param reason
	 * @return
	 */
	public Result CancelOrderStatus(Long orderId,int status,String  reason){
		orderMapper.CancelOrderStatus(orderId,status,reason);
		return new Result(Result.OK, "更新订单状态成功");
	}

	public Result orderType(Long orderId){
		Order order = orderMapper.findOrderInfo(orderId);
		if(order.getOrderType()==1 && order.getSkuGoodsType().equals("1")){
			//白卡
			updateGoogsT(orderId,1);
		}else if(order.getOrderType()==3 &&  order.getSkuGoodsType().equals("4")){ //竞拍
			List list = orderMapper.getOrderItmeList(orderId,0);
			if(list==null || list.size()<=0) return new Result(Result.ERROR, "未找到订单相应的信息");
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
				String num_id =String.valueOf( map.get("num_id"));
				String num =String.valueOf( map.get("num"));
				Long skuId =Long.parseLong(String.valueOf( map.get("sku_id")));
				String sku_Id =String.valueOf( map.get("sku_id"));
				int quantity = Integer.parseInt(String.valueOf(map.get("quantity")));
				log.info("号码还原在库");
//				freezeNum(num_id, "1",true);
				goodsMapper.updateNumStatus(num_id,sku_Id,num);
			}
		}else {
			//普号，普靓，超靓
			updateGoogsT(orderId,0);
		}
		return new Result(Result.OK, "更新订单状态成功");
	}
	//isShipment==1 白卡，2 其他
	public Result updateGoogsT(Long orderId ,int isShipment){
		List itemList = orderMapper.getOrderItmeCount(orderId,isShipment);
		for(int i = 0; i < itemList.size(); i++){
			Map cmap = (Map) itemList.get(i);
			int quantity = Integer.parseInt(String.valueOf(cmap.get("quantity")));
			Long skuId =Long.parseLong(String.valueOf( cmap.get("sku_id")));
			Long goods_id =Long.parseLong(String.valueOf( cmap.get("goods_id")));
			//判断是否上架
			Goods gPram = new Goods();
			gPram.setgIsSale("1");  //上架
			gPram.setgId(goods_id);
			Goods goods = goodsMapper.selectOne(gPram);
			if(goods !=null){
				//有效上架时间内
				List bklist = orderMapper.getOrderItmeList(orderId,isShipment);
				if(bklist==null || bklist.size()<=0) return new Result(Result.ERROR, "未找到订单相应的信息");
				if(betweenCalendar(new Date(), goods.getgStartTime(), goods.getgEndTime())) {
					for (int b = 0; b < bklist.size(); b++) {
						Map map = (Map) bklist.get(b);
						Long skuIds =Long.parseLong(String.valueOf( map.get("sku_id")));
						int quantitys =Integer.parseInt(String.valueOf( map.get("quantity")));
						String num_id =String.valueOf( map.get("num_id"));
						Sku nowSku = skuMapper.getSkuBySkuid(skuIds);
						nowSku.setSkuNum(Integer.parseInt((String.valueOf(nowSku.getSkuNum())))+quantitys);//修改sku数量
						skuMapper.updateSkuNum(nowSku);
						if(isShipment==0){
							log.info("号码还原销售中");
							freezeNum(num_id, "2",false);
						}

					}
					//调用仓储接口
					Map param = new HashMap();
					//获取目前sku信息
					Sku nowSku = skuMapper.getSkuBySkuid(skuId);
					if(!nowSku.getSkuGoodsType().equals("3")){
						param.put("supply_id", nowSku.getSkuId());//供货单编码(sku_id)
						Result res;
						//再冻结新库存
						param.put("type", "1");//处理类型1上架；2下架
						param.put("quantity", quantity);//数量
						param.put("companystock_id", nowSku.getSkuRepoGoods());//库存编码(skuRepoGoods)
						if(!"0".equals(param.get("quantity").toString())) {
							res = StorageApiCallUtil.storageApiCall(param, "HK0002");
							if(res.getCode()!=200){
								TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
								return new Result(Result.ERROR, "库存验证失败");
							}else {
								StorageInterfaceResponse sir = StorageInterfaceResponse.create(res.getData().toString(), SystemParam.get("key"));
								if (!"00000".equals(sir.getCode())) {
									TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
									return new Result(Result.ERROR, "冻结库存失败\n"+sir.getDesc());
								}
							}
						}
					}
				}else {
					if(isShipment==0){
						for (int b = 0; b < bklist.size(); b++) {
							Map map = (Map) bklist.get(b);
							String num_id =String.valueOf( map.get("num_id"));
							log.info("号码还原在库");
							freezeNum(num_id, "1",true);
						}
					}
				}
			}else {  //下架
				if(isShipment==0){
					List list = orderMapper.getOrderItmeList(orderId,isShipment);
					if(list==null || list.size()<=0) return new Result(Result.ERROR, "未找到订单相应的信息");
					for (int j = 0; j < list.size(); j++) {
						Map map = (Map) list.get(j);
						String num_id =String.valueOf( map.get("num_id"));
						log.info("号码还原在库");
						freezeNum(num_id, "1",true);
					}
				}
			}
		}
		return new Result(Result.OK, "更新成功");
	}
}
