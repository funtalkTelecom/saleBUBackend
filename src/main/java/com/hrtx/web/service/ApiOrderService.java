package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.common.express.ExpressUtils;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import net.sf.json.JSONArray;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.lang.System;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


@Service
public class ApiOrderService {
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private GoodsMapper goodsMapper;
	@Autowired private SkuMapper skuMapper;
	@Autowired private SkuPropertyMapper skuPropertyMapper;
	@Autowired private OrderMapper orderMapper;
	@Autowired private NumberMapper numberMapper;
	@Autowired private OrderItemMapper orderItemMapper;
	@Autowired private DeliveryAddressMapper deliveryAddressMapper;
	@Autowired private AuctionMapper auctionMapper;
	@Autowired private RedisUtil redisUtil;
	@Autowired private FileMapper fileMapper;
	@Autowired FundOrderService fundOrderService;
	@Autowired EPSaleService ePSaleService;
	@Autowired private NumPriceMapper numPriceMapper;
	@Autowired private AgentMapper agentMapper;
	@Autowired private CityMapper cityMapper;
	@Autowired private MealMapper mealMapper;
	@Autowired private NumService numService;
	@Autowired private AgentService agentService;
	@Autowired private ApiOrderService apiOrderService;
	@Autowired private LyCrmService lyCrmService;
	@Autowired private DictMapper dictMapper;
	@Autowired private ShareService shareService;

	public  List<Map> findOrderListByNumId(Integer numId)
	{
		return  orderMapper.findOrderListByNumIdAndConsumerId(numId,this.apiSessionUtil.getConsumer().getId());
	}

	public Result signOrder(Order order,HttpServletRequest request){
		Order order2=orderMapper.selectByPrimaryKey(order.getOrderId());
		if(order2==null)return new Result(Result.ERROR, "订单不存在");
		Consumer consumer=this.apiSessionUtil.getConsumer();
		if(!consumer.getId().equals(order2.getConsumer()))return new Result(Result.ERROR, "您无权签收他人订单");
		if(order2.getStatus()!=Constants.ORDER_STATUS_5.getIntKey())return new Result(Result.ERROR, "当前订单无法签收");
		return this.signOrder(order2.getOrderId(),1);
	}
	private Result signOrder(int order_id,int sign_type){
		Order order=new Order();
		order.setOrderId(order_id);
		order.setStatus(Constants.ORDER_STATUS_6.getIntKey());
		order.setSignDate(new Date());//签收时间
		order.setSignType(sign_type);//签收方式1用户自动签收2系统
		orderMapper.signByOrderid(order);
		shareService.orderSettle(order_id);
		return new Result(Result.OK, "签收成功");
	}
	/*
	 订单 已发货待签收>7天
	 */
//	@Scheduled(fixedRate=3000)update by zjc 2018.12.20  统一关闭应用定时器，由外部调用
	public void signOrderSystem() {
		if(!"true".equals(SystemParam.get("exe_timer"))) return;
		log.info("开始执行.....已发货待签收的订单>7天时,系统自动签收......定时器");
		List<Map> list=this.orderMapper.findOrderSignList();
		if(list.isEmpty()){
			log.info(String.format("暂无已发货未签收的订单"));return;
		}
		for(Map map :list){
			this.signOrder(NumberUtils.toInt(ObjectUtils.toString(map.get("orderId"))),2);
		}
	}
//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 提交竞拍单
	 * @param goods_id		竞拍商品
	 * @param sku_id		竞拍商品
	 * @param num_id		竞拍号码
	 * @param ep_price		竞拍价格
	 * @param address_id	收获地址
	 * @param isPack 是否打包  尚未实现
	 * @param conment	备注 (选填)
	 * @return 返回result code=200成功；=888订单已生成，仓储异常；=500失败
	 */
	public Result submitEpOrderEP(Integer goods_id,Integer sku_id,Integer num_id,double ep_price,Integer address_id,int isPack,String conment){
		Consumer user = apiSessionUtil.getConsumer();
		Result adresult=this.checkAddress(address_id,user.getId());
		if(adresult.getCode()!=Result.OK)return adresult;
		DeliveryAddress address=(DeliveryAddress)adresult.getData();
		String shippingMenthodId="";
		int mead_id=-1;
		Result result= this.submitOrder(Constants.ORDER_TYPE_3.getIntKey(),sku_id,num_id,1,ep_price,user,address,shippingMenthodId,mead_id,conment,"","",null);

		if(result.getCode()!=Result.OK)return result;
		Auction action=new Auction();
		action.setgId(goods_id);
		action.setNumId(num_id);
		action.setSkuId(sku_id);
		action.setErISPack(Integer.valueOf(isPack));
		action.setOrderId(NumberUtils.toInt(ObjectUtils.toString(result.getData())));
		ePSaleService.auctionEditOrderID(action);

		return result;
	}

	/**
	 * 提交 白卡或普号 单
	 * 	skuid,数量,地址
	 * @param sku_id		商品
	 * @param order_amount	订购数量
	 * @param address_id	收获地址
	 * @param conment		备注（选填）
	 * @return 返回result code=200成功；=888订单已生成，仓储异常；=500失败
	 */
	public Result submitGoodsOrder(Integer sku_id,int order_amount,Integer address_id,String conment){
		Consumer user = apiSessionUtil.getConsumer();
		Result adresult=this.checkAddress(address_id,user.getId());
		if(adresult.getCode()!=Result.OK)return adresult;
		DeliveryAddress address=(DeliveryAddress)adresult.getData();
		String shippingMenthodId="";
		int mead_id=-1;
		return this.submitOrder(Constants.ORDER_TYPE_1.getIntKey(),sku_id,0,order_amount,0d,user,address,shippingMenthodId,mead_id,conment,"","",null);
	}

	/**
	 * 提交 普靓或超靓常规 单
	 * skuid, numid, addrid, payType, mealid
	 * @param sku_id	商品信息
	 * @param num_id	订购号码
	 * @param mead_id	配对套餐
	 * @param address_id	收获地址
	 * @param conment		备注(选填)
     * @param share_id	分享的编码
	 * @return 返回result code=200成功；=888订单已生成，仓储异常；=500失败
	 */
	public Result submitNumOrder(Integer sku_id,Integer num_id,Integer mead_id,Integer address_id,String conment,int share_id){
		Consumer user = apiSessionUtil.getConsumer();
		Result adresult=this.checkAddress(address_id,user.getId());
		if(adresult.getCode()!=Result.OK)return adresult;
		DeliveryAddress address=(DeliveryAddress)adresult.getData();
		String shippingMenthodId="";

		Map<String,Object> order_ext_param=new HashMap<>();
		Result curr_agent=this.agentService.queryCurrAgent();
		if(curr_agent.getCode()!=Result.OK)return curr_agent;
		Agent agent=(Agent)curr_agent.getData();
		Number number=this.numberMapper.selectByPrimaryKey(num_id);
		if(number==null)return new Result(Result.OTHER,"抱歉，尚未找到您提交的号码");
		Result result = numService.findBossNum(number.getCityId(),agent.getId(),number.getSellerId());
		if(result.getCode() != Result.OK)return result;
		order_ext_param.put("bossNum",String.valueOf(result.getData()));
        order_ext_param.put("shareId",share_id);
		return this.submitOrder(Constants.ORDER_TYPE_2.getIntKey(),sku_id,num_id,1,0d,user,address,shippingMenthodId,mead_id,conment,"","",order_ext_param);
	}

	/**
	 * 客服下单
	 * @param num_id	订购号码
	 * @param mead_id	配对套餐
	 * @param addr_name	收件人
	 * @param addr_phone	收件人电话
	 * @param addr	收件地址
	 * @param conment	备注(选填)
	 * @param thirdOrder 第三方订单号(选填)
	 * @param bossNum	BOOS工号(选填)
	 * @param phoneConsumer	客户（使用此号码的人）姓名(选填)
	 * @param phoneConsumerIdType	客户证件类型(选填) 目前只支持身份证
	 * @param phoneConsumerIdNum	客户证件号码(选填)
	 * @return 返回result code=200成功；=888订单已生成，仓储异常；=500失败
	 */
	public Result submitCustomOrder(Integer num_id,Integer mead_id,String addr_name,String addr_phone,String addr,String conment,String thirdOrder,String bossNum,String phoneConsumer,String phoneConsumerIdType,String phoneConsumerIdNum){
		Number number=this.numberMapper.selectByPrimaryKey(num_id);
		if(number==null)return new Result(Result.OTHER,"抱歉，尚未找到您提交的号码");
		Integer sku_id=number.getSkuId();
		User u = SessionUtil.getUser();
		Consumer user =apiSessionUtil.getConsumer();
		if(u==null&&user==null)return new Result(Result.OTHER,"您尚未登陆，请登陆后再提交订单");
		if(user==null){
			user = new Consumer();
			user.setId(u.getId());
			user.setName(u.getName());
			user.setIsAgent(0);
		}
		DeliveryAddress address=new DeliveryAddress();
		address.setId(-1);
		address.setPersonName(addr_name);
		address.setPersonTel(addr_phone);
		address.setAddress(addr);
		String shippingMenthodId="";
		Map<String,Object> order_ext_param=new HashMap<>();
		order_ext_param.put("thirdOrder",thirdOrder);
		order_ext_param.put("bossNum",bossNum);
		order_ext_param.put("phoneConsumer",phoneConsumer);
		order_ext_param.put("phoneConsumerIdType",phoneConsumerIdType);
		order_ext_param.put("phoneConsumerIdNum",phoneConsumerIdNum);
		Result result= this.submitOrder(Constants.ORDER_TYPE_4.getIntKey(),sku_id,num_id,1,0d,user,address,shippingMenthodId,mead_id,conment,"","",order_ext_param);
		return result;
    }

	/**
	 * @param order_type
	 * @param sku_id
	 * @param num_id
	 * @param order_amount
	 * @param ep_price
	 * @param user
	 * @param address
	 * @param shippingMenthodId
	 * @param mead_id
	 * @param conment
	 * @param user_agent
	 * @param req_id
	 * @param order_ext_param
	 * @return   返回result code=200成功；=888订单已生成，仓储异常；=500失败
	 */
	public Result submitOrder(int order_type,Integer sku_id,Integer num_id,int order_amount,double ep_price,Consumer user,DeliveryAddress address,String shippingMenthodId,Integer mead_id,String conment,String user_agent,String req_id,Map<String,Object> order_ext_param){
		Result result=this.apiOrderService.newCreateOrder(order_type,sku_id,num_id,order_amount,ep_price,user,address,shippingMenthodId,mead_id,conment,user_agent,req_id,order_ext_param);
		if(result.getCode()!=Result.OK)return result;
		Integer order_id=NumberUtils.toInt(ObjectUtils.toString(result.getData()));
		Result result1 = this.apiOrderService.payPushOrderToStorage(order_id);
		if(result1.getCode()==Result.OK)return result;//若成功,返回原生成订单的数据(主要含了订单号)
		log.info("冻结仓储库存失败，5分钟后再次调用");
		new Thread(){
			//TODO 到时调整到统一队列中
			public void run() {
				try{
					Thread.sleep(5*60*1000);
				}catch (Exception e){
					log.error("",e);
				}
				apiOrderService.payPushOrderToStorage(order_id);
			}
		}.start();
		return new Result(Result.OTHER,result1.getData());
	}

	/**
	 * 订单创建后需要冻结仓储的库存
	 * @param order_id
	 * @return
	 */
	public Result payPushOrderToStorage(Integer order_id){
		Order order=this.orderMapper.selectByPrimaryKey(order_id);
		if(order==null)return new Result(Result.ERROR, "抱歉，您提交的订单存在错误");
		if(!ArrayUtils.contains(new int[]{Constants.ORDER_STATUS_20.getIntKey(),Constants.ORDER_STATUS_0.getIntKey()},order.getStatus()))return new Result(Result.ERROR, "当前订单状态并非可冻结情况");
		order.setStatus(Constants.ORDER_STATUS_20.getIntKey());
		boolean ordinary_num=(order.getOrderType()==Constants.ORDER_TYPE_2.getIntKey()&&StringUtils.equals(order.getSkuGoodsType(),"3"));//普靓
        if(ordinary_num){
            log.info(String.format("订单[%s]是普靓订单不需要发货，直接进入等待支付状态",order.getOrderId()));
            order.setStatus(Constants.ORDER_STATUS_1.getIntKey());
        }
		this.orderMapper.updateByPrimaryKey(order);
        if(ordinary_num)return new Result(Result.OK,"不需要发货");
		Example example = new Example(OrderItem.class);
		example.createCriteria().andEqualTo("orderId",order.getOrderId());
		List<OrderItem> orderItems=this.orderItemMapper.selectByExample(example);

		Map iparam = new HashMap();
		List items = new ArrayList();
		iparam.put("order_id",order.getOrderId());
		for (OrderItem i : orderItems) {
			if(i.getIsShipment()==0) continue;
			Map<String,Object> item = new HashMap<String,Object>();
			item.put("item_id", i.getItemId());
			item.put("companystock_id", i.getCompanystockId());
			item.put("quantity", i.getQuantity());
			items.add(item);
		}
		iparam.put("commodities",items);

		log.info("准备调用存储，冻结订单库存");
		Result res = StorageApiCallUtil.storageApiCall(iparam, "HK0003");
		log.info(String.format("仓储库存返回结果[%s]",ObjectUtils.toString(res.getData())));
		if(!StringUtils.equals("200",String.valueOf(res.getCode())))return new Result(Result.ERROR, "库存冻结接口失败");
		StorageInterfaceResponse sir = StorageInterfaceResponse.create(ObjectUtils.toString(res.getData()), SystemParam.get("key"));
		if(!StringUtils.equals("00000",String.valueOf(sir.getCode())))return new Result(Result.ERROR, "库存冻结由于["+sir.getDesc()+"]失败");
		order.setStatus(Constants.ORDER_STATUS_1.getIntKey());
        this.orderMapper.updateByPrimaryKey(order);
		if(order.getOrderType()==Constants.ORDER_TYPE_4.getIntKey()){//客服单  code=other虽订单已提交，但存储未冻结，存储成功后进入待审核
           log.info(String.format("订单[%s]为客服单，此类订单生成后若存储库存冻结成功，不进入支付环节，进入待审核",order.getOrderId()));
            order.setStatus(Constants.ORDER_STATUS_21.getIntKey());
            this.orderMapper.updateByPrimaryKey(order);
        }
        this.orderMapper.updateByPrimaryKey(order);
		return new Result(Result.OK,"库存推送成功");
	}
	public void test() {
		String aa=Utils.randomNoByDateTime();
		Sku sku=this.skuMapper.selectByPrimaryKey(501);
		log.info(aa+" 测试 "+sku.getSkuNum());
		int a=this.skuMapper.updateSkuNumWithDataNum(2,501);
		log.info(aa+" 测试 "+a);
	}

	/**
	 *
	 * @param order_type	订单类型
	 * @param sku_id		商品编码
	 * @param num_id		订购的号码编码
	 * @param order_amount	订购数量(若订购的是号码，则数量将会被强制设置为1)
	 * @param ep_price		竞拍价格(order_type=3时有效)
	 * @param user			订购用户
	 * @param address		发货地址
	 * @param shippingMenthodId	快递方法
	 * @param meal_id			套餐id
	 * @param conment			备注
	 * @param user_agent		订购用户请求信息
	 * @param req_ip			订购用户请求IP
	 * @param order_ext_param	订单额外字段
	 * @return 返回result code=200成功；=500失败
	 * 可能会主动抛出ServiceException，message是具体抛出的原因
	 */
	public Result newCreateOrder(int order_type,Integer sku_id,Integer num_id,int order_amount,double ep_price,Consumer user,DeliveryAddress address,String shippingMenthodId,Integer meal_id,String conment,String user_agent,String req_ip,Map<String,Object> order_ext_param){
		/*int order_amount=2;
		String order_type="2";//订单类型
		Long sku_id=1073428441224708096l;
		Long num_id=1073423228233318403l;
		Long address_id=1002389937389043713l;
		Long mead_id=1002389937389043713l;
		double ep_price=2d;
		String user_agent="";
		String req_ip="";
		String shippingMenthodId="";
		String conment="";*/
		/*Consumer user = apiSessionUtil.getConsumer();
		DeliveryAddress address = deliveryAddressMapper.findDeliveryAddressByIdForOrder(address_id);
		if(address==null)return new Result(Result.ERROR, "抱歉，您选择的地址不存在");
		if(!StringUtils.equals(String.valueOf(address.getAddUserId()),String.valueOf(user.getId())))return new Result(Result.ERROR, "抱歉，您选择的地址不存在");
*/
		String shippingMenthod="";

		Sku sku=this.skuMapper.selectByPrimaryKey(sku_id);
		if(sku==null) return new Result(Result.ERROR, "抱歉，尚未找到您所需商品");
		if(sku.getSkuNum()<=0) return new Result(Result.ERROR, "抱歉，您购买的商品已售罄");
		if(sku.getSkuNum()<order_amount) return new Result(Result.ERROR, "抱歉，您购买的商品库存不足，请减少下单量再试");

		boolean low_num=order_type==Constants.ORDER_TYPE_1.getIntKey()/*StringUtils.equals(order_type,"1")*/;//白卡或普号
		boolean ordinary_num=(order_type==Constants.ORDER_TYPE_2.getIntKey()/*StringUtils.equals(order_type,"2")*/&&StringUtils.equals(sku.getSkuGoodsType(),"3"));//普靓
		boolean ep_num=order_type==Constants.ORDER_TYPE_3.getIntKey()/*StringUtils.equals(order_type,"3")*/;//竞拍
		int[] super_order_type=new int[]{Constants.ORDER_TYPE_2.getIntKey(),Constants.ORDER_TYPE_4.getIntKey()};//客服、超靓
		boolean super_num=(ArrayUtils.contains(super_order_type,order_type)&&StringUtils.equals(sku.getSkuGoodsType(),"4"));//超靓
		boolean is_agent=user.getIsAgent() == 2;
		String agent_city=String.valueOf(user.getAgentCity());

		Goods goods=this.goodsMapper.selectByPrimaryKey(sku.getgId());
		if(goods.getStatus()==null||goods.getStatus()!=1)return new Result(Result.ERROR,"抱歉，您购买的商品并非在售中");
		if(System.currentTimeMillis()<goods.getgStartTime().getTime())return new Result(Result.ERROR,"抱歉，您购买的商品尚未开始销售");
		//竞拍不考虑过期
		if(System.currentTimeMillis()>goods.getgEndTime().getTime()&&!ep_num)return new Result(Result.ERROR,"抱歉，您购买的商品已过期");

		double commission=0d;
		double shippingTotal=0d;
		double subTotal=0d;
		List<OrderItem> itemNums=new ArrayList<>();
		Order order=this.createOrderBean(order_type,sku.getSkuGoodsType(),user,user_agent,req_ip,address,shippingMenthodId,shippingMenthod,commission,shippingTotal,subTotal,conment);
		try {
			if(order_ext_param!=null){
				log.info("存储订单额外参数信息");
				Iterator<String> iterator=order_ext_param.keySet().iterator();
				while (iterator.hasNext()){
					String key=iterator.next();
					BeanUtils.copyProperty(order,key,order_ext_param.get(key));
				}
			}
		}catch (Exception e){
			log.error("存储订单额外参数信息异常",e);
		}

		Meal meal= mealMapper.selectByPrimaryKey(meal_id);
		if(meal==null && meal_id>0)return new Result(Result.ERROR, "抱歉，尚未找到您提交的套餐");

		if(ordinary_num||super_num||ep_num)order_amount=1;//此类情况只会出库一个

		if(low_num||ordinary_num){
			if(!is_agent) return new Result(Result.ERROR, "抱歉，您权限不足，无法订购当前商品");
			//goods.getgSaleCity()的格式为:,地市ID,地市ID,
			//普号或白卡时检验商品上架的地市是否与代理商地市一致
			if(low_num && !StringUtils.contains(goods.getgSaleCity(),agent_city))  return new Result(Result.ERROR, "抱歉，您权限不足，无法订购此地市商品");
			//普靓时检验号码的地市是否与代理商地市一致
			if(ordinary_num){
				Number number=this.numberMapper.selectByPrimaryKey(num_id);
				if(!StringUtils.equals(String.valueOf(number.getCityId()),agent_city))return new Result(Result.ERROR, "抱歉，您权限不足，无法订购此地市商品");
				Result result=this.checkNumCondition(number,sku_id);
				if(result.getCode()!=Result.OK)return result;

				OrderItem itemNum=this.createOrderItemBean(order.getOrderId(),0,meal_id,goods,sku,number,1,sku.getSkuTobPrice());
				itemNums.add(itemNum);
				return this.saveOrderInfo(sku,order_amount,order,null,itemNums);
			}
			//购买白卡时只需要发相应数量的白卡即可
			if(low_num&&StringUtils.equals(sku.getSkuGoodsType(),"1")){
				OrderItem itemIccid=this.createOrderItemBean(order.getOrderId(),0,meal_id,goods,sku,null,order_amount,sku.getSkuTobPrice());
				return this.saveOrderInfo(sku,order_amount,order,itemIccid,itemNums);
			}

			//购买普号时需要获取一批相应数量的号码
			if(low_num&&StringUtils.equals(sku.getSkuGoodsType(),"2")){
				Example example = new Example(Number.class);
				example.createCriteria().andEqualTo("status",2)
						.andEqualTo("isFreeze",0)
						.andEqualTo("skuId", sku.getSkuId());
				List<Number> number_list=this.numberMapper.selectByExample(example);
				if(number_list.size()<order_amount)return new Result(Result.ERROR, "抱歉，号码库存不足，无法订购");
				OrderItem itemIccid=this.createOrderItemBean(order.getOrderId(),0,meal_id,goods,sku,null,order_amount,sku.getSkuTobPrice());
				for (int i=0;i<order_amount;i++){
					Number number=number_list.get(i);
					OrderItem itemNum=this.createOrderItemBean(order.getOrderId(),itemIccid.getItemId(),meal_id,goods,sku,number,1,0d);
					itemNums.add(itemNum);
				}
				return this.saveOrderInfo(sku,order_amount,order,itemIccid,itemNums);
			}
		}

		if(super_num||ep_num){
			Number number=this.numberMapper.selectByPrimaryKey(num_id);
			Result result=this.checkNumCondition(number,sku_id);
			if(result.getCode()!=Result.OK)return result;
			double num_price=ep_num?ep_price:sku.getSkuTobPrice();
			if(super_num/*order.getOrderType()==2&&StringUtils.equals(order.getSkuGoodsType(),"4")*/){
				Result curr_price=findNumSalePrice(number.getId());
				if(curr_price.getCode()!=Result.OK)return curr_price;
				num_price=NumberUtils.toDouble(ObjectUtils.toString(curr_price.getData()));
				/*2019.3.14update
				Result curr_agent=this.agentService.queryCurrAgent();
				if(curr_agent.getCode()!=Result.OK)return curr_agent;
				Agent agent=(Agent)curr_agent.getData();
				NumPrice numPrice=new NumPrice();
				numPrice.setAgentId(agent.getId());
				numPrice.setNumId(number.getId());
				List aplist=this.numPriceMapper.queryList(numPrice);
				if(aplist.size()==0||aplist.size()>1)return new Result(Result.ERROR, "抱歉，号码价格错误，无法订购");
				Map numPrice1=(Map)aplist.get(0);
//				num_price=NumberUtils.toDouble(ObjectUtils.toString(numPrice1.get("price")));// numPrice1.getPrice().doubleValue();
				Object price_range=numPrice1.get("price_range");
				if(price_range==null)return new Result(Result.ERROR, "抱歉，号码价格错误，无法订购");
				num_price=NumberUtils.toDouble(ObjectUtils.toString(price_range));//2019.1.24增加秒杀功能，秒杀时取秒杀价格*/
			}

			OrderItem itemIccid=this.createOrderItemBean(order.getOrderId(),0,meal_id,goods,sku,null,1,0d);

			OrderItem itemNum=this.createOrderItemBean(order.getOrderId(),0,meal_id/*itemIccid.getItemId()*/,goods,sku,number,1,num_price);
			itemNums.add(itemNum);
			return this.saveOrderInfo(sku,order_amount,order,itemIccid,itemNums);
		}
//		throw new ServiceException("未知订单类型");
		return new Result(Result.ERROR, "未知订单类型");
	}

	public Result findNumSalePrice(Integer num_id){
		Result curr_agent=this.agentService.queryCurrAgent();
		if(curr_agent.getCode()!=Result.OK)return curr_agent;
		Agent agent=(Agent)curr_agent.getData();
		NumPrice numPrice=new NumPrice();
		numPrice.setAgentId(agent.getId());
		numPrice.setNumId(num_id);
		List aplist=this.numPriceMapper.queryList(numPrice);
		if(aplist.size()==0||aplist.size()>1)return new Result(Result.ERROR, "抱歉，号码价格错误，无法订购");
		Map numPrice1=(Map)aplist.get(0);
		Object price_range=numPrice1.get("price_range");
		if(price_range==null)return new Result(Result.ERROR, "抱歉，号码价格错误，无法订购");
		Double num_price=NumberUtils.toDouble(ObjectUtils.toString(price_range));//2019.1.24增加秒杀功能，秒杀时取秒杀价格
		return new Result(Result.OK,num_price);
	}

	private Result saveOrderInfo(Sku sku,int order_amount,Order order,OrderItem itemIccid,List<OrderItem> itemNums){

		/*order.setOrderId(order.getGeneralId());
		if(itemIccid!=null)itemIccid.setItemId(itemIccid.getGeneralId());
		for (int i=0;itemNums!=null&&i<itemNums.size();i++){
			itemNums.get(i).setItemId(itemNums.get(i).getGeneralId());
		}*/
		//类似for update，在最后更新时刻获取库内数据进行更新,降低锁的范围
		int update_num=this.skuMapper.updateSkuNumWithDataNum(order_amount,sku.getSkuId());
		if(update_num==0)return new Result(Result.ERROR,"抱歉，您购买的商品库存不足");
		if(update_num>1)throw new ServiceException("数据异常，无法下单");//数据错误，回滚当前事务
		this.orderMapper.insert(order);
		double subTotal=0d;
		if(itemIccid!=null){
			itemIccid.setOrderId(order.getOrderId());
			this.orderItemMapper.insert(itemIccid);
			subTotal=Utils.sum(subTotal,itemIccid.getTotal());
		}
		for (int i=0;itemNums!=null&&i<itemNums.size();i++){
			OrderItem itemNum=itemNums.get(i);
			itemNum.setOrderId(order.getOrderId());
			if(itemIccid!=null)itemNum.setpItemId(itemIccid.getItemId());
			this.orderItemMapper.insert(itemNum);
			subTotal=Utils.sum(subTotal,itemNum.getTotal());
			//类似for update，在最后更新时刻获取库内数据进行更新,降低锁的范围
			update_num=this.numberMapper.updateNumStatusWithData(Constants.NUM_STATUS_2.getIntKey(),Constants.NUM_STATUS_3.getIntKey(),itemNum.getNumId());
			if(update_num==0)throw new ServiceException("抱歉，您选择的商品已被订购");//数据错误，回滚当前事务
			if(update_num>1)throw new ServiceException("数据异常，无法下单");//数据错误，回滚当前事务
			long _start=System.currentTimeMillis();
			lyCrmService.synchNumPriceAgentStatus(itemNum.getNumId());
			log.info(String.format("剔除号码销售耗时[%s]",(System.currentTimeMillis()-_start)));
		}
		order.setSubTotal(subTotal);
		order.setTotal(Order.calculateTotal(order));
		this.orderMapper.updateByPrimaryKey(order);
//		if(true)throw new ServiceException("测试数据异常，订单被回滚");
		return new Result(Result.OK, order.getOrderId());
	}
	private Result checkAddress(Integer address_id,Integer consumer_id){
		DeliveryAddress address = deliveryAddressMapper.findDeliveryAddressByIdForOrder(address_id);
		if(address==null)return new Result(Result.ERROR, "抱歉，您选择的地址不存在");
		if(!StringUtils.equals(String.valueOf(address.getAddUserId()),String.valueOf(consumer_id)))return new Result(Result.ERROR, "抱歉，您选择的地址不存在");
		return new Result(Result.OK,address);
	}


	private Result checkNumCondition(Number number,int sku_id){
		if(number==null)return new Result(Result.ERROR, "抱歉，您购买的号码并非在售中");
		if(!StringUtils.equals(String.valueOf(number.getStatus()),"2"))return new Result(Result.ERROR, "抱歉，您购买的号码并非在售中");
		if(!Integer.valueOf(sku_id).equals(number.getSkuId()) )return new Result(Result.ERROR, "抱歉，您购买的号码存在异常");
		if(number.getIsFreeze()!=null&&number.getIsFreeze()==0)return new Result(Result.OK,null);
		Integer fuser = numService.queryFreeze(number.getId());//判断是否冻结
        if(fuser != null && !fuser.equals(SessionUtil.getUserId())) return new Result(Result.ERROR, "抱歉，您购买的号码已冻结");
		return new Result(Result.OK,null);
	}
	private OrderItem createOrderItemBean(Integer orderId,Integer parent_item_id,Integer mead_id,Goods goods,Sku sku,Number number,int quantity,double goods_price){
		Integer num_id=number==null?null:number.getId();
		String num=number==null?null:number.getNumResource();
		int isShipment=number==null?1:0;//是否需要发货 1=发货
		List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(sku.getSkuId());
		String skuProperty=JSONArray.fromObject(skuPropertyList).toString();//商品属性
		Integer skuRepoGoods=NumberUtils.toInt(sku.getSkuRepoGoods());/*可能存在问题*/
		OrderItem bean = new OrderItem(orderId,goods.getgId(),sku.getSkuId(),skuProperty,num_id,num,
				isShipment,goods.getgSellerId(),goods.getgSellerName(),"egt",skuRepoGoods,
				quantity,goods_price,Utils.mul(quantity,goods_price),parent_item_id);
		bean.setMealId(mead_id);
		return bean;
	}

	private Order createOrderBean(int orderType,String skuGoodsType,Consumer user,String user_agent,String req_ip,DeliveryAddress address,String shippingMenthodId,String shippingMenthod,double commission, double shippingTotal, double subTotal,String conment){
		int status=Constants.ORDER_STATUS_0.getIntKey();
		String address1=address.getAddress();
		if(address.getDistrictId()>0){
			City city=cityMapper.selectByPrimaryKey(address.getDistrictId());
			address1=city.getFullName()+address1;
		}
		Order order=new Order(user.getId(),user.getName(),status,StringUtils.substring(user_agent,0,300),req_ip,
				orderType,shippingMenthodId,shippingMenthod,address.getId(),address.getPersonName(),address.getPersonTel(),
				address1, commission, shippingTotal, subTotal,conment,skuGoodsType);
		return order;
	}
//////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 查询订单快递路由
	 * @param order_id
	 * @return
	 */
	public Result queryExpressInfo(Integer order_id){
		Consumer user =apiSessionUtil.getConsumer();
		if(user==null)return new Result(Result.ERROR,"请登录后查询");
		Order order=orderMapper.selectByPrimaryKey(order_id);
		if(order==null)return new Result(Result.ERROR,"订单数据错误");
		String expressId=order.getExpressId();
		String expressNo=order.getExpressNumber();
		String express_phone=StringUtils.substring(order.getPersonTel(),String.valueOf(order.getPersonTel()).length()-4);//收件人或寄件人手机号后四位
		if(!order.getConsumer().equals(user.getId()))return new Result(Result.ERROR,"无法查询他人订单信息");
		if(StringUtils.isEmpty(expressId)&&StringUtils.isEmpty(expressNo))return new Result(Result.ERROR,"订单尚未发货");
		Example example = new Example(Dict.class);
		example.createCriteria().andEqualTo("keyGroup","express-type").andEqualTo("keyId",expressId);
		List<Dict> dict_list=this.dictMapper.selectByExample(example);
		if(dict_list.size()<=0)return new Result(Result.ERROR,"物流代码未配置，暂时无法查询");
		Dict dict=dict_list.get(0);
		String expressType=dict.getNote();
		if(StringUtils.equals(expressType,"SFEXPRESS"))expressNo+=":"+express_phone;
		com.hrtx.common.dto.Result expressStr = ExpressUtils.queryExpressInfo(expressNo, expressType);
		if(expressStr.getCode()==com.hrtx.common.dto.Result.OK)return new Result(Result.OK,expressStr.getData());
		return new Result(Result.ERROR,expressStr.getDesc());
	}

//////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 根据商品id创建订单
	 * @param :普靓和超靓:skuid, numid, 地址id, 支付方式, 套餐id
	 *             2:白卡和普号:skuid,数量,地址
	 * @return
	 */
	public Result createOrder(Map<String, String> param, Map oparam){
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
//		order.setOrderId(order.getGeneralId());
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		List<Order> orderList = new ArrayList<Order>();
		String type = null;
		String skuGoodsType = null;


//		log.info("模拟登陆");
//		Consumer u = new Consumer();
//		u.setId(1014426510456520704L);
//		u.setName("詹德海");
//		u.setCity("158");
//		u.setIsAgent(2);//设置为一级代理商
//		String token=request.getParameter("__sessid");
//		apiSessionUtil.saveOrUpdate(token,u);
//		Consumer user = apiSessionUtil.getConsumer();
		//模拟登陆end
		int agentId = 0;
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
				type = param.get("type");
				user = apiSessionUtil.getConsumer();
				User u = SessionUtil.getUser();
				if(user == null && u != null) {
					user = new Consumer();
					user.setId(u.getId());
					user.setName(u.getName());
					user.setIsAgent(0);
				}
			}
			List _list = agentMapper.findConsumenrIdCount(user.getId());

			if(_list.size()>0){
				Map _map = (Map) _list.get(0);
				agentId =NumberUtils.toInt(String.valueOf(_map.get("id"))) ;
			}else{
				agentId = NumberUtils.toInt(SystemParam.get("default_agent"));  //默认代理商id
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
					skuid = param.get("skuid") ;
					addrid = param.get("addrid");//普通靓号可不填
					numcount = param.get("numcount")==null?-1:Integer.parseInt(param.get("numcount"));
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
						List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Integer.parseInt(skuid));

//						orderItem.setItemId(orderItem.getGeneralId());
						orderItem.setOrderId(order.getOrderId());

						//获取商品
						log.info("获取商品信息");
						goods = goodsMapper.findGoodsInfoBySkuid(Integer.parseInt(skuid));
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
						orderItem.setSkuId(Integer.parseInt(skuid));
						orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
//                                orderItem.setNumId(Long.parseLong(numid));
//                                orderItem.setNum(String.valueOf( number.get("numResource"));
						orderItem.setIsShipment(1);//都需要发货
						orderItem.setSellerId(Integer.parseInt(String.valueOf( sku.get("gSellerId"))));
						orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
						orderItem.setShipmentApi("egt");
						orderItem.setCompanystockId(NumberUtils.toInt(String.valueOf( sku.get("skuRepoGoods"))));
						orderItem.setQuantity(numcount);
						double twobPrice = Double.parseDouble(String.valueOf(sku.get("skuTobPrice")));
						orderItem.setPrice(twobPrice);
						orderItem.setTotal(twobPrice * numcount);
//                                orderItem.setMealId(Long.parseLong(mealid));
						sub_total += orderItem.getTotal();

						pOrderItem = orderItem;
						orderItems.add(orderItem);
						//修改sku数量
						Sku nowSku = skuMapper.getSkuBySkuid(Integer.parseInt(String.valueOf( sku.get("skuId"))));
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

//								orderItem.setItemId(orderItem.getGeneralId());
								orderItem.setOrderId(order.getOrderId());

								orderItem.setGoodsId(goods.getgId());
								orderItem.setSkuId(Integer.parseInt(skuid));
								orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
								orderItem.setNumId(n.getId());
								orderItem.setNum(n.getNumResource());
								orderItem.setIsShipment(0);//号码item无需发货
								orderItem.setSellerId(Integer.parseInt(String.valueOf( sku.get("gSellerId"))));
								orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
								orderItem.setShipmentApi("egt");
								orderItem.setCompanystockId(NumberUtils.toInt(String.valueOf( sku.get("skuRepoGoods"))));
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
					order.setReqUserAgent(param.get("user-agent"));
					order.setReqIp(SessionUtil.getUserIp());
					order.setAddDate(new Date());
					order.setOrderType(1);

					log.info("设置收货信息");
					//获取收货地址信息
					DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(Integer.valueOf(addrid));
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
				String skuid, numid = null, addrid, payType, mealid,channel;
				Map numberPrice = null;
				try {
					log.info("获取传入参数");
					skuid = param.get("skuid");
					numid = param.get("numid");
					addrid = param.get("addrid") == null ? "" : param.get("addrid");//普通靓号可不填
					payType = param.get("payMethod") == null ? "" : param.get("payMethod");
					mealid = param.get("mealid") == null ? "" : param.get("mealid");
					channel = param.get("channel") == null ? "" : param.get("channel");

					if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
					if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");

					log.info("获取号码信息");
					//获取号码
//					number = numberMapper.getNumInfoById(numid);
					NumPrice numPrice = new NumPrice();
					numPrice.setNumId(NumberUtils.toInt(numid));
					numPrice.setChannel(Integer.parseInt(channel));
					numPrice.setAgentId(agentId);
					List nps = numPriceMapper.queryList(numPrice);//numPriceMapper.queryPageList(numPrice);
					numberPrice = nps.size() != 1 ? null : (Map) nps.get(0);
					//冻结号码
					log.info("验证号码是否可下单");
					//验证号码是否可下单,2:销售中
					if (numberPrice == null || !"2".equals(String.valueOf(numberPrice.get("status"))) || "1".equals(String.valueOf(numberPrice.get("is_freeze"))))
						return new Result(Result.ERROR, "号码已被购买或冻结!");
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
						goods = goodsMapper.findGoodsInfoBySkuid(Integer.parseInt(skuid));
						//普通靓号,需要一级代理商
						if("3".equals(sku.get("skuGoodsType"))){
							log.info("普通靓号,验证一级代理商");
							if(user.getIsAgent() != 2){
								freezeNum(numid, String.valueOf(numberPrice.get("status")),false);
								return new Result(Result.ERROR, "您不是一级代理商,无法提交普通靓号订单");
							}
							log.info("判断商品地市和代理商地市");
							//判断商品地市和代理商地市

							if(!StringUtils.equals(numberPrice.get("city_code")+"",user.getAgentCity()+"")) {
								freezeNum(numid, String.valueOf(numberPrice.get("status")),false);
								return new Result(Result.ERROR, "不属于您的地市,无法操作");
							}
							/*if(user.getAgentCity()==null || !goods.getgSaleCity().contains(String.valueOf(user.getAgentCity()))) {
								freezeNum(numid, String.valueOf(number.get("status")));
								return new Result(Result.ERROR, "不属于您的地市,无法操作");
							}*/
						}
						List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Integer.parseInt(skuid));


						int num = 1;
						double twobPrice = 0;//Double.parseDouble(String.valueOf( sku.get("skuTobPrice"));
						//修改sku数量
						Sku nowSku = skuMapper.getSkuBySkuid(Integer.parseInt(String.valueOf( sku.get("skuId"))));
						nowSku.setSkuNum(Integer.parseInt((String.valueOf(sku.get("skuNum"))))-num);
						skuMapper.updateSkuNum(nowSku);
						//超级靓号添加卡的item
						if("4".equals(sku.get("skuGoodsType"))){
							log.info("超级靓号添加卡体item");
							orderItem = new OrderItem();
//							orderItem.setItemId(orderItem.getGeneralId());
							orderItem.setOrderId(order.getOrderId());

							orderItem.setGoodsId(goods.getgId());
							orderItem.setSkuId(Integer.parseInt(skuid));
							orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
							orderItem.setNumId(Integer.parseInt(numid));
							orderItem.setNum(String.valueOf( numberPrice.get("resource")));
							orderItem.setIsShipment(1);//卡体发货
							orderItem.setSellerId(Integer.parseInt(String.valueOf( sku.get("gSellerId"))));
							orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
							orderItem.setShipmentApi("egt");
							orderItem.setCompanystockId(NumberUtils.toInt(String.valueOf( sku.get("skuRepoGoods"))));
							orderItem.setQuantity(num);
							orderItem.setPrice(twobPrice);
							orderItem.setTotal(twobPrice * num);
							if(!StringUtils.isBlank(mealid)) orderItem.setMealId(NumberUtils.toInt(mealid));
							sub_total += orderItem.getTotal();

							pOrderItem = orderItem;
							orderItems.add(orderItem);
						}

						skuGoodsType = String.valueOf(sku.get("skuGoodsType"));
						log.info("添加号码item");
						//号码item
						orderItem = new OrderItem();
						orderItem.setpItemId(pOrderItem.getItemId());
//						orderItem.setItemId(orderItem.getGeneralId());
						orderItem.setOrderId(order.getOrderId());
						orderItem.setGoodsId(goods.getgId());
						orderItem.setSkuId(Integer.parseInt(skuid));
						orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
						orderItem.setNumId(Integer.parseInt(numid));
						orderItem.setNum(String.valueOf( numberPrice.get("resource")));
						orderItem.setIsShipment(0);//普通靓号不发货
						orderItem.setSellerId(Integer.parseInt(String.valueOf( sku.get("gSellerId"))));
						orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
						orderItem.setShipmentApi("egt");
						orderItem.setCompanystockId(NumberUtils.toInt(String.valueOf( sku.get("skuRepoGoods"))));
						num = 1;
						orderItem.setQuantity(num);
						twobPrice = Double.parseDouble(String.valueOf( numberPrice.get("price")));
						orderItem.setPrice(twobPrice);
						orderItem.setTotal(twobPrice * num);
						if(!StringUtils.isBlank(mealid)) orderItem.setMealId(NumberUtils.toInt(mealid));
						sub_total += orderItem.getTotal();

						orderItems.add(orderItem);
					}

					log.info("设置订单信息");
					//设置订单
					order.setSkuGoodsType(skuGoodsType);
					order.setConsumer(user.getId());
					order.setConsumerName(user.getName());
					order.setStatus(1);//设置成待付款
					order.setReqUserAgent(param.get("user-agent"));
					order.setReqIp(SessionUtil.getUserIp());
					order.setAddDate(new Date());
					order.setOrderType(2);

					order.setConment(param.get("conment"));
					order.setThirdOrder(param.get("thirdOrder"));
					order.setBossNum(param.get("bossNum"));
					order.setPhoneConsumer(param.get("phoneConsumer"));
					order.setPhoneConsumerIdType(param.get("phoneConsumerIdType"));
					order.setPhoneConsumerIdNum(param.get("phoneConsumerIdNum"));

					if (StringUtils.isBlank(addrid)) {
						order.setAddressId(null);
						order.setPersonName(param.get("personName"));
						order.setPersonTel(param.get("personTel"));
						order.setAddress(param.get("address"));
					}
					else {
						//获取收货地址信息
						DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(Integer.valueOf(addrid));
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
					freezeNum(numid, String.valueOf(numberPrice.get("status")),false);
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return new Result(Result.ERROR, "获取数据异常");
				}
			}
			else if("3".equals(type)) {
				//竞拍订单
				log.info("进入竞拍订单");
				storagen = 1;
				String skuid, numid = null, addrid, price,isPack,goodsid;
				Map number = null;
				log.info("获取传入参数");
				if(oparam!=null){
					skuid = String.valueOf(oparam.get("skuid"));
					numid = String.valueOf(oparam.get("numid"));
					addrid = String.valueOf(oparam.get("addrid"));
					price =  String.valueOf(oparam.get("price"));
					isPack = String.valueOf(oparam.get("gispack"));
					goodsid = String.valueOf(oparam.get("gid"));
				}else {
					skuid = param.get("skuid");
					numid = param.get("numid");
					addrid = param.get("addrid");
					price = param.get("price");
					isPack = param.get("gispack");
					goodsid = param.get("gid");
				}
				try {
					action=new Auction();
					action.setgId(Integer.valueOf(goodsid));
					action.setNumId(Integer.valueOf(numid));
					action.setSkuId(Integer.valueOf(skuid));
					action.setErISPack(Integer.valueOf(isPack));
					if (price == null || "".equals(price)) return new Result(Result.ERROR, "price不能为空");
					Double prices = Double.parseDouble(price);
					//更新订单明细表信息
					Result s = this.isPackfun(goodsid,isPack,order,skuid,numid,prices,number);
					if(s.getCode()==Result.OK){
						Map maps = (Map) s.getData();
						List<OrderItem> ois = (List<OrderItem>) maps.get("orderItems");
						sub_total =Double.parseDouble(String.valueOf(maps.get("sub_total")));
						orderItems.addAll(ois);
					}
					log.info("设置订单信息");
					//设置订单
					order.setSkuGoodsType("4");
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
						DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(Integer.valueOf(addrid));
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
					if("1".equals(isPack)){
						//子项小计打折之后减去运费
						total = prices * commission - shipping_total;
					}else{
						//子项小计打折之后减去运费
						total = sub_total * commission - shipping_total;
					}
					order.setTotal(total);
					orderList.add(order);
				} catch (Exception e) {
					e.printStackTrace();
					//清除已生成的订单
					deleteOrder(orderList);
					//解冻号码,把冻结之前的状态还原
//					freezeNum(numid, String.valueOf(number.get("status")),false);
					for (OrderItem i : orderItems) {
						freezeNum(i.getNumId().toString(), String.valueOf(number.get("status")),false);
					}
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
			Map iparam = new HashMap();
			List items = new ArrayList();
			Integer preOrderId =0;
			for (OrderItem i : orderItems) {
				if(i.getIsShipment()==0) continue;
				if(preOrderId!=i.getOrderId()) {
					preOrderId = i.getOrderId();
					iparam = new HashMap();
					items = new ArrayList();
					iparam.put("order_id", i.getOrderId());
				}

				Map item = new HashMap();
				if(i.getIsShipment()==0) continue;
				item.put("item_id", i.getItemId());
				item.put("companystock_id", i.getCompanystockId());
				item.put("quantity", i.getQuantity());
				items.add(item);

			}

			iparam.put("commodities", items);
			//先写入表,再调用仓储
			try{
				orderMapper.insertBatch(orderList);
				orderItemMapper.insertBatch(orderItems);
				if (type.equals("3"))//竞拍订单生成，对应订单Id回填到 出价记录(aution.status=2)的orderId字段
				{
					action.setOrderId(preOrderId);
					ePSaleService.auctionEditOrderID(action);
				}
			}catch(Exception e){
				if(type.equals("3"))//竞拍订单生成，对应订单Id回填到 出价记录(aution.status=2)的orderId字段
				{
					action.setOrderId(0);//异常处理
					ePSaleService.auctionEditOrderID(action);
				}
			    //写入数据异常,回滚
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return new Result(Result.ERROR, "写入订单数据异常");
			}
			//要发货的item,调用仓储接口
			if(items!=null && items.size()>0) {
				log.info("调用仓储接口");
				Result res = StorageApiCallUtil.storageApiCall(iparam, "HK0003");
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
			    action.setOrderId(0);//异常处理
				ePSaleService.auctionEditOrderID(action);
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
			Order order = orderMapper.findOrderInfo(NumberUtils.toInt(id));
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderId(Integer.valueOf(id));
			List list = orderItemMapper.queryPageListDetailForConsumer(orderItem);
			List<File> fileList = new ArrayList<File>();
			Goods goods = new Goods();
			if(list!=null && list.size()>0) {
				goods = goodsMapper.findGoodsInfo(Integer.parseInt(String.valueOf(((Map) list.get(0)).get("goodsId"))));
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

			List mealList = new ArrayList();
			mealList = mealMapper.getMeal(String.valueOf(((Map) list.get(0)).get("mealId")));
			if(mealList == null && mealList.size()==0){
				mealList = new ArrayList();
			}
			DeliveryAddress deliveryAddress = deliveryAddressMapper.findDeliveryAddressByIdForOrder(order.getAddressId());
			order.setAddress(getFullAddress(deliveryAddress));

			o.put("order", order);
			o.put("goods", goods);
			o.put("orderItem", list);
			o.put("mealList", mealList);
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
		int orderId =NumberUtils.toInt(orderIds);
		Order order = orderMapper.findOrderInfo(orderId);
		if(order.getSkuGoodsType().equals("3")){  //普靓没有冻结库存，不调用仓库接口
			log.info("更新订单状态为7:已取消");
			int status =7;
			CancelOrderStatus(orderId,status,reason);
			Result ispay =fundOrderService.queryPayOrderInfo(String.valueOf(orderId));
			if(ispay.getCode()==Result.OK){  //已支付
				if(ispay.getData().equals("1")){//线上支付
					CancelOrderStatus(orderId,Constants.ORDER_STATUS_12.getIntKey(),""); //退款中
					Result payR = fundOrderService.payOrderRefund(String.valueOf(orderId),reason);
					if(payR.getCode()==200){  //退款成功
						orderType(orderId);
					}else { //退款失败
						CancelOrderStatus(orderId,Constants.ORDER_STATUS_13.getIntKey(),""); //退款失败

					}
				}else {//线下支付
					CancelOrderStatus(orderId,Constants.ORDER_STATUS_14.getIntKey(),""); //待财务退款
				}
			}else if(ispay.getCode()==Result.ERROR) {//未支付
				//上架涉及的表，数量，状态
				orderType(orderId);
			}else{//未知结果
				CancelOrderStatus(orderId,Constants.ORDER_STATUS_12.getIntKey(),""); //退款中
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
						CancelOrderStatus(orderId,Constants.ORDER_STATUS_12.getIntKey(),""); //退款中
						Result payR = fundOrderService.payOrderRefund(String.valueOf(orderId),reason);
						if(payR.getCode()==Result.OK){  //退款成功
							orderType(orderId);
						}else { //退款失败
							CancelOrderStatus(orderId,Constants.ORDER_STATUS_13.getIntKey(),""); //退款失败
						}
					}else {//线下支付
						CancelOrderStatus(orderId,Constants.ORDER_STATUS_14.getIntKey(),""); //待财务退款
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
	public Result CancelOrderStatus(Integer orderId,int status,String  reason){
		orderMapper.CancelOrderStatus(orderId,status,reason);
		return new Result(Result.OK, "更新订单状态成功");
	}

	public Result orderType(Integer orderId){
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
	public Result updateGoogsT(Integer orderId ,int isShipment){
		List itemList = orderMapper.getOrderItmeCount(orderId,isShipment);
		for(int i = 0; i < itemList.size(); i++){
			Map cmap = (Map) itemList.get(i);
			int quantity = Integer.parseInt(String.valueOf(cmap.get("quantity")));
			Integer skuId =Integer.valueOf(String.valueOf( cmap.get("sku_id")));
			Integer goods_id =Integer.valueOf(String.valueOf( cmap.get("goods_id")));
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
						Integer skuIds =Integer.parseInt(String.valueOf( map.get("sku_id")));
						int quantitys =Integer.parseInt(String.valueOf( map.get("quantity")));
						String num_id =String.valueOf( map.get("num_id"));
						Sku nowSku = skuMapper.getSkuBySkuid(Integer.valueOf(skuIds));
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
						param.put("supply_id", orderId);//供货单编码(sku_id)
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

	public Result isPackfun(String goodsid,String isPack,Order order ,String skuid,String numid,Double price,Map number) {
		double sub_total=0;
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		Map pram = new HashMap();
		List listN = numberMapper.queryGoodsNumberList(goodsid);
		if("1".equals(isPack)){
			//打包
			if(listN==null || listN.size()<=0) return new Result(Result.ERROR, "未找到对应的号码信息");
			String count = String.valueOf(listN.size());
			Double prices = Utils.div(price,Double.parseDouble(count),2) ;

			for (int i = 0; i < listN.size(); i++) {
				Map map = (Map) listN.get(i);
				skuid = String.valueOf(map.get("skuid"));
				numid = String.valueOf(map.get("numid"));
				Result s=setOrderItem(order,skuid,numid,prices,number);
				if(s.getCode()==Result.OK){
					Map maps = (Map) s.getData();
					List<OrderItem> ois = (List<OrderItem>) maps.get("orderItems");
					sub_total =Double.parseDouble(String.valueOf(maps.get("sub_total")));
					orderItems.addAll(ois);
				}
			}
		}else {
			if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
			if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");
			Result s=setOrderItem(order,skuid,numid,price,number);
			if(s.getCode()==Result.OK){
				Map maps = (Map) s.getData();
				List<OrderItem> ois = (List<OrderItem>) maps.get("orderItems");
				sub_total =Double.parseDouble(String.valueOf(maps.get("sub_total")));
				orderItems.addAll(ois);
			}
		}
		pram.put("orderItems",orderItems);
		pram.put("sub_total",sub_total);
		return  new Result(Result.OK,pram);
	}

	public Result setOrderItem(Order order,String skuid,String numid,Double price,Map number) {
		double sub_total=0;
		List<OrderItem> orderItems = new ArrayList<OrderItem>();
		Map pram = new HashMap();
		try {
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
			Goods goods = new Goods();
			OrderItem pOrderItem = new OrderItem();

			String skuGoodsType=null;
			log.info("获取sku信息");
			//获取sku列表
			List skulist = skuMapper.getSkuListBySkuids("'" + skuid.replaceAll(",", "','") + "'");
			if(skulist==null || skulist.size()<=0) return new Result(Result.ERROR, "未找到相关商品,请刷新后再试");
			for (int i = 0; i < skulist.size(); i++) {
				OrderItem orderItem = new OrderItem();
				Map sku = (Map) skulist.get(i);
				log.info("获取商品信息");
				//获取商品
				goods = goodsMapper.findGoodsInfoBySkuid(Integer.parseInt(skuid));
				List skuPropertyList = skuPropertyMapper.findSkuPropertyBySkuidForOrder(Integer.parseInt(skuid));
				skuGoodsType = String.valueOf(sku.get("skuGoodsType"));
				int num = 1;
				double twobPrice =price;
				//修改sku数量
				Sku nowSku = skuMapper.getSkuBySkuid(Integer.parseInt(String.valueOf( sku.get("skuId"))));
				nowSku.setSkuNum(Integer.parseInt((String.valueOf(sku.get("skuNum"))))-num);
				skuMapper.updateSkuNum(nowSku);
				log.info("添加卡体item");
				//添加卡的item
				orderItem = new OrderItem();
//				orderItem.setItemId(orderItem.getGeneralId());
				orderItem.setOrderId(order.getOrderId());
				orderItem.setGoodsId(goods.getgId());
				orderItem.setSkuId(Integer.parseInt(skuid));
				orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
				orderItem.setNumId(Integer.parseInt(numid));
				orderItem.setNum(String.valueOf( number.get("numResource")));
				orderItem.setIsShipment(1);//卡体发货
				orderItem.setSellerId(Integer.parseInt(String.valueOf( sku.get("gSellerId"))));
				orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
				orderItem.setShipmentApi("egt");
				orderItem.setCompanystockId(NumberUtils.toInt(String.valueOf( sku.get("skuRepoGoods"))));
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
//				orderItem.setItemId(orderItem.getGeneralId());
				orderItem.setpItemId(pOrderItem.getItemId());
				orderItem.setOrderId(order.getOrderId());
				orderItem.setGoodsId(goods.getgId());
				orderItem.setSkuId(Integer.parseInt(skuid));
				orderItem.setSkuProperty(JSONArray.fromObject(skuPropertyList).toString());
				orderItem.setNumId(Integer.parseInt(numid));
				orderItem.setNum(String.valueOf( number.get("numResource")));
				orderItem.setIsShipment(0);
				orderItem.setSellerId(Integer.parseInt(String.valueOf( sku.get("gSellerId"))));
				orderItem.setSellerName(String.valueOf( sku.get("gSellerName")));
				orderItem.setShipmentApi("egt");
				orderItem.setCompanystockId(NumberUtils.toInt(String.valueOf( sku.get("skuRepoGoods"))));
				num = 1;
				orderItem.setQuantity(num);
				twobPrice = price;
				orderItem.setPrice(twobPrice);
				orderItem.setTotal(twobPrice * num);
				sub_total += orderItem.getTotal();
				orderItems.add(orderItem);
			}
			pram.put("orderItems",orderItems);
			pram.put("sub_total",sub_total);
		}catch (Exception e){
			e.printStackTrace();
			//解冻号码,把冻结之前的状态还原
			freezeNum(numid, String.valueOf(number.get("status")),false);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			return new Result(Result.ERROR, "获取数据异常");
		}
		return new Result(Result.OK,pram);
	}
}
