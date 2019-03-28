package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.System;
import java.math.BigDecimal;
import java.util.*;

@Service
public class ShareService {
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private PromotionPlanMapper promotionPlanMapper;
	@Autowired private PromotionPlanNumMapper promotionPlanNumMapper;
	@Autowired private UserMapper userMapper;
	@Autowired private ConsumerMapper consumerMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private ShareMapper shareMapper;
	@Autowired private NumBrowseMapper numBrownseMapper;
	@Autowired private AgentService agentService;
	@Autowired private NumPriceMapper numPriceMapper;
	@Autowired private ApiOrderService apiOrderService;
	@Autowired private NumMapper numMapper;
	@Autowired private ImageService imageService;
	@Autowired private OrderSettleMapper orderSettleMapper;
	@Autowired private OrderMapper orderMapper;
	@Autowired private OrderItemMapper orderItemMapper;
	@Autowired private FundOrderService fundOrderService;

	/**
	 * 添加合伙人信息
	 */
	public Result addInfo(String name,String phone,String idcard,String idcard_face,String idcard_back){
		Consumer consumer=apiSessionUtil.getConsumer();
		Consumer bean=consumerMapper.selectByPrimaryKey(consumer.getId());
		boolean is_new=bean.getIsPartner()==null;
		bean.setName(name);
		bean.setPhone(phone);
		bean.setIdcard(idcard);
		if(!StringUtils.isEmpty(idcard_back))bean.setIdcardBack(idcard_back);
		if(!StringUtils.isEmpty(idcard_face))bean.setIdcardFace(idcard_face);
		bean.setIsPartner(Constants.CONSUMER_ISPARTNER_1.getIntKey());
		bean.setPartnerCheck(Constants.CONSUMER_PARTNERCHECK_0.getIntKey());
		this.consumerMapper.updateByPrimaryKey(bean);
		apiSessionUtil.saveOrUpdate(apiSessionUtil.getTokenStr(),bean);
		if(is_new)return new Result(Result.OK,"您的资料已提交，等待运营人员审核，现在您可以开始推广了");
		else return new Result(Result.OK,"您的资料已提交，等待运营人员审核");
	}

	/**
	 * 查询合伙人信息
	 */
	public Result findInfo(){
		String req_path=SessionUtil.getRequestPath(SessionUtil.getRequest());
		Map<String,String> _map=new HashMap<>();
		Consumer consumer=apiSessionUtil.getConsumer();
		Consumer bean=consumerMapper.selectByPrimaryKey(consumer.getId());
		_map.put("is_partner",ObjectUtils.toString(bean.getIsPartner(),"0"));
		_map.put("partner_check",ObjectUtils.toString(bean.getPartnerCheck(),"0"));
		_map.put("name",bean.getName());
		_map.put("phone",bean.getPhone());
		_map.put("idcard",bean.getIdcard());
		_map.put("idcard_face",req_path+"get-img/"+Constants.UPLOAD_PATH_IDCARD.getStringKey()+"/1000/"+bean.getIdcardFace());//格式为:idcard/1000/CN9031409047001.jpg
		_map.put("idcard_back",req_path+"get-img/"+Constants.UPLOAD_PATH_IDCARD.getStringKey()+"/1000/"+bean.getIdcardBack());
		//个人信息和统计数据是否需要分开？ TODO 以下待实现
		List<Object> list=this.shareMapper.countConsumerShare(consumer.getId());
		int share_count=0,browse_count=0;
		if(list.size()>0){
			Map map=(Map)list.get(0);
			share_count=NumberUtils.toInt(ObjectUtils.toString(map.get("share_num")));
			browse_count=NumberUtils.toInt(ObjectUtils.toString(map.get("browse_num")));
		}
		_map.put("share_count",share_count+"");//推广个数
		_map.put("share_browse",browse_count+"");//浏览量
		Double sale_count=0d,sale_price=0d,wait_settle=0d,has_settle=0d,all_settle=0d,balance=0d;
		list=this.orderSettleMapper.countConsumerSettle(consumer.getId());
		if(list.size()>0){
			Map map=(Map)list.get(0);
			sale_count=NumberUtils.toDouble(ObjectUtils.toString(map.get("sale_count")));
			sale_price=NumberUtils.toDouble(ObjectUtils.toString(map.get("sale_price")));
			wait_settle=NumberUtils.toDouble(ObjectUtils.toString(map.get("wait_settle")));
			has_settle=NumberUtils.toDouble(ObjectUtils.toString(map.get("has_settle")));
			all_settle=NumberUtils.toDouble(ObjectUtils.toString(map.get("all_settle")));
		}
		_map.put("sale_count",sale_count.intValue()+"");//销售量
		_map.put("sale_price",Utils.formatFloatNumber(sale_price));//销售额
		_map.put("wait_balance",Utils.formatFloatNumber(wait_settle));//待结算金额
		_map.put("has_balance",Utils.formatFloatNumber(has_settle));//已结算金额
		_map.put("all_income",Utils.formatFloatNumber(all_settle));//总收益

		Result result=this.fundOrderService.payHrPayAccount(consumer.getId()+"");
		if(result.getCode()==Result.OK){
			Map map=(Map)result.getData();
			balance=NumberUtils.toDouble(ObjectUtils.toString(map.get("balance")));
		}
		_map.put("balance",Utils.formatFloatNumber(balance));//可用余额
		return new Result(Result.OK,_map);
	}

	///////////////////////////////////////////////////////
	/**
	 * 查看推广记录
	 */
	public PageInfo queryShareList(int start,int limit){
		Consumer consumer=apiSessionUtil.getConsumer();
		PageHelper.startPage(start,limit);
		Page<Object> page=this.shareMapper.queryShareList(consumer.getId());
		for(int i=0;i<page.size();i++){
			Map map=(Map)page.get(i);
			Map<String,String> _map_num=findNumPromotionInfo(NumberUtils.toInt(ObjectUtils.toString(map.get("share_num_id"))));
			map.putAll(_map_num);
		}
		PageInfo<Object> pm = new PageInfo<Object>(page);
		return pm;
	}
	/**
	 * 生成推广卡片
	 */
	public Result shareCard(){
		return new Result(Result.OK,"");
	}
	/**
	 * 创建订单结算数据(只生成数据不实际结算)
	 * 需要结算的群体有
	 * 	1.推广人佣金
	 * 	2.服务费
	 * 	3.交易费
	 * 	4.发展人员
	 * 	5.市场人员
	 *
	 */
	public Result createOrderSettle(int order_id){
		int share_settle_user=-1;
		Order order=orderMapper.selectByPrimaryKey(order_id);
		double order_price=order.getTotal();
		int[] ok_order_status=new int[]{Constants.ORDER_STATUS_2.getIntKey(),Constants.ORDER_STATUS_3.getIntKey()};
		if(!ArrayUtils.contains(ok_order_status,order.getStatus()))return new Result(Result.ERROR,"订单状态非可结算状态");
		if(order.getShareId()!=null&&order.getShareId()!=0){
			Share share=this.shareMapper.selectByPrimaryKey(order.getShareId());
			share_settle_user=share.getConsumerId();
		}

		Example example = new Example(OrderItem.class);
		example.createCriteria().andEqualTo("orderId",order.getOrderId()).andEqualTo("isShipment",Constants.ORDERITEM_SHIPMENT_0.getIntKey());
		List<OrderItem> _list=this.orderItemMapper.selectByExample(example);
		int fee_type=-1;
		int num_id=-1;
		for(OrderItem orderItem:_list){//推广人佣金   按循环则规则上运行按号码收费，不按循环只能按订单收费
			num_id=orderItem.getNumId();//考虑当前的模式下，一个订单只会有一个号码
			fee_type=Constants.PROMOTION_PLAN_FEETYPE_1.getIntKey();//结算用户
			double order_item_price=orderItem.getPrice()*orderItem.getQuantity();
			initSettleFee(order_id,num_id,fee_type,order_item_price,share_settle_user,false,0d,0,0d);
		}
		//技术服务费
		fee_type=Constants.PROMOTION_PLAN_FEETYPE_2.getIntKey();//结算用户
		initSettleFee(order_id,num_id,fee_type,order_price,fee_type,true,NumberUtils.toDouble(SystemParam.get("settle_tech_fee"),2),1,NumberUtils.toDouble(SystemParam.get("settle_tech_fee_limit"),50));
		//交易费
		fee_type=Constants.PROMOTION_PLAN_FEETYPE_3.getIntKey();//结算用户
		initSettleFee(order_id,num_id,fee_type,order_price,fee_type,true,NumberUtils.toDouble(SystemParam.get("settle_pay_fee"),0.8),0,0d);
		//发展人员
		fee_type=Constants.PROMOTION_PLAN_FEETYPE_4.getIntKey();//结算用户
		initSettleFee(order_id,num_id,fee_type,order_price,fee_type,true,NumberUtils.toDouble(SystemParam.get("settle_expand_fee"),0.05),1,NumberUtils.toDouble(SystemParam.get("settle_expand_fee_limit"),500));
		//市场人员
		fee_type=Constants.PROMOTION_PLAN_FEETYPE_5.getIntKey();//结算用户
		initSettleFee(order_id,num_id,fee_type,order_price,fee_type,true,NumberUtils.toDouble(SystemParam.get("settle_market_fee"),5),1,NumberUtils.toDouble(SystemParam.get("settle_market_fee_limit"),500));

		return new Result(Result.OK,"结算创建成功");
	}

	/**
	 * 订单正式结算(进行确认支付，费用、佣金等结算)
	 * @param order_id	订单号
	 * @return
	 */
	public Result orderSettle(int order_id){
		Result result=fundOrderService.payHrPayOrderSign(order_id+"");
		log.info(String.format("订单签收结果[%s],[%s]",result.getCode(),result.getData()));
		Example example = new Example(OrderSettle.class);
		example.createCriteria().andEqualTo("orderId",order_id).andEqualTo("status",Constants.ORDERSETTLE_STATUS_1.getIntKey());
		List<OrderSettle> _list=this.orderSettleMapper.selectByExample(example);
		if(_list.isEmpty()) return new Result(Result.ERROR,"暂无需要结算的费用");
		List<Map> payeeList= new ArrayList<>();
		for(OrderSettle orderSettle:_list){
			Map map=new HashMap();
			map.put("payee",orderSettle.getSettleUser());
			map.put("item_amt",orderSettle.getSettleAmt().doubleValue());
			map.put("item_id",orderSettle.getId());
			payeeList.add(map);
		}
		OrderSettle settle=new OrderSettle();
		settle.setStatus(Constants.ORDERSETTLE_STATUS_2.getIntKey());
		settle.setSettleDate(new Date());
		result=this.fundOrderService.payHrOrderSettle(order_id,payeeList);
		log.info(String.format("订单结算结果[%s],[%s]",result.getCode(),result.getData()));
		if(result.getCode()==Result.OK)this.orderSettleMapper.updateByExampleSelective(settle,example);
		return result;
	}

	private Result initSettleFee(int order_id,int num_id,int fee_type,double order_price,int settle_user,boolean emptyAndInit,double init_award,int init_limit,double init_limit_award){
        if(settle_user==-1)return new Result(Result.OK,"结算用户不存在");
        Example example = new Example(OrderSettle.class);
        example.createCriteria().andEqualTo("orderId",order_id).andEqualTo("feeType",fee_type).andIn("status",Arrays.asList(new Object[]{Constants.ORDERSETTLE_STATUS_1.getIntKey(),Constants.ORDERSETTLE_STATUS_2.getIntKey()}));
        List<OrderSettle> _list=this.orderSettleMapper.selectByExample(example);
        if(!_list.isEmpty())return new Result(Result.OK,"该订单类型已经结算");
		Map<String,String> _map=findNumPromotionInfo(fee_type,num_id,order_price);
		if(emptyAndInit&&StringUtils.equals(_map.get("is_pp"),"0")){
			Num num=numMapper.selectByPrimaryKey(num_id);
			initSettlePromotionPlan(num.getSellerId(),fee_type,init_award,init_limit,init_limit_award);
			_map=findNumPromotionInfo(fee_type,num_id,order_price);
		}
		OrderSettle orderSettle=new OrderSettle(order_id,fee_type,settle_user,NumberUtils.toDouble(_map.get("income")),Constants.ORDERSETTLE_STATUS_1.getIntKey());
        this.orderSettleMapper.insert(orderSettle);
        log.info(String.format("订单[%s]结算[%s]给用户[%s]费用[%s]",order_id,fee_type,settle_user,_map.get("income")));
		return new Result(Result.OK,"结算创建成功");
	}
	private PromotionPlan initSettlePromotionPlan(int corp_id,int feeType,Double award,int isLimit,Double limit_award){
		Calendar calendar=Calendar.getInstance();
		calendar.set(2019,1,1,0,0,0);
		Date beginDate=calendar.getTime();
		calendar.set(2119,1,1,0,0,0);//百年大计
		Date endDate=calendar.getTime();
		PromotionPlan newBean=new PromotionPlan(Constants.PROMOTION_PLAN_PROMOTION_0.getIntKey(),Constants.PROMOTION_PLAN_AWARDWAY_2.getIntKey(),award,isLimit,limit_award,0d,0d,beginDate,endDate);
		newBean.setAddUser(1);
		newBean.setCorpId(corp_id);
		newBean.setStatus(Constants.PROMOTION_PLAN_STATUS_2.getIntKey());
		newBean.setFeeType(feeType);
		this.promotionPlanMapper.insert(newBean);
		return newBean;
	}

	private Map<String,String> findNumPromotionInfo(int free_type,int num_id,Double num_price){
		Map<String,String> _map=new HashMap<>();
		PromotionPlan ppbean=null;
		Double ppfee=null;
		String valid_date=null;
		Result result=this.findNumPromotionPlan(free_type,num_id);
		if(result.getCode()==Result.OK){
			ppbean=(PromotionPlan)result.getData();
			valid_date=String.valueOf(ppbean.getEndDate().getTime());
			if(ppbean.getAwardWay()==Constants.PROMOTION_PLAN_AWARDWAY_1.getIntKey())ppfee=ppbean.getAward().doubleValue();
			if(ppbean.getAwardWay()==Constants.PROMOTION_PLAN_AWARDWAY_2.getIntKey()){
				ppfee=Arith.div(Arith.mul(num_price,ppbean.getAward().doubleValue()),100);
				//检查是否有设置上限
				if(ppbean.getIsLimit()==1)ppfee=ppfee>ppbean.getLimitAward().doubleValue()?ppbean.getLimitAward().doubleValue():ppfee;
			}
		}
		_map.put("is_pp",ppbean==null?"0":"1");//是否进行推广1是0否
		_map.put("income",ppfee==null?"0":Utils.formatFloatNumber(ppfee));//预期收益
		_map.put("valid_date",valid_date==null?"":valid_date);//有效期至
		_map.put("sale_price",Utils.formatFloatNumber(num_price));//号码当前售价
		return _map;
	}

	private Map<String,String> findNumPromotionInfo(Integer num_id){
		Result curr_price = apiOrderService.findNumSalePrice(num_id);
		Double num_price=0d;
		if(curr_price.getCode()==Result.OK)num_price=NumberUtils.toDouble(ObjectUtils.toString(curr_price.getData()));
		Map<String,String> _map=findNumPromotionInfo(Constants.PROMOTION_PLAN_FEETYPE_1.getIntKey(),num_id,num_price);
		return _map;
	}
	/**
	 * 生成分享地址
	 */
	public Result shareUrl(Integer num_id){
		String req_path=SessionUtil.getRequestPath(SessionUtil.getRequest());
		Num num=numMapper.selectByPrimaryKey(num_id);
		if(num==null)return new Result(Result.ERROR,"号码不存在");
		Consumer consumer=apiSessionUtil.getConsumer();
		List<Share> _share_list=this.shareMapper.findNumShare(consumer.getId(),num_id);
		Share bean=null;
		if(_share_list.size()<=0){
			String share_image="";
			String share_url="";
			try{
				Result result=imageService.createShareLinkFile(num.getNumResource(),num.getCityName(),num.getNetType(),num.getTeleType(),String.valueOf(num.getLowConsume()));
				share_image=String.valueOf(result.getData());
			}catch (IOException e){
				log.error("分享图片创建失败",e);
				return new Result(Result.ERROR,"分享图片创建失败");
			}
			bean=new Share(consumer.getId(),Constants.SHARE_SOURCE_4.getIntKey(),num.getNumResource(),num_id,share_image,share_url);
			this.shareMapper.insert(bean);
		}else{
			bean=_share_list.get(0);
		}
		Map<String,String> _map=new HashMap<>();
		Map<String,String> _map_num=findNumPromotionInfo(num_id);
		_map.put("num_id",num.getId()+"");//号码编码
		_map.put("num_resource",num.getNumResource());//号码
		_map.put("city_name",num.getCityName());//地市
		_map.put("net_type",num.getNetType());//网络制式
		_map.put("tele_type",num.getTeleType());//运营商
		_map.put("low_consume",num.getLowConsume()+"");//最低消费
		_map.put("share_id",bean.getId()+"");//推广编号
		_map.put("share_image",req_path+"get-img/"+Constants.UPLOAD_PATH_SHARE.getStringKey()+"/"+StringUtils.defaultIfEmpty(bean.getShareImage(),""));//推广图片
		_map.put("share_url", req_path+StringUtils.defaultIfEmpty(bean.getShareUrl(),""));//推广URL地址
		_map.putAll(_map_num);
		return new Result(Result.OK,_map);
	}

	/**
	 * 号码浏览记录
	 * @param num_id	号码id
	 * @param channel	打开的渠道
	 * @param open_url	打开的地址
	 * @param share_id	分享编码
	 * @return
	 */
	public Result addBrowse(int num_id,String channel,String open_url,int share_id){
		Consumer consumer=apiSessionUtil.getConsumer();
		NumBrowse bean=new NumBrowse(num_id,null,consumer.getId(),channel,open_url,SessionUtil.getUserIp(),Constants.NUMBROWSE_ACTTYPE_1.getIntKey(),share_id);
		return this.addBrowse(bean);
	}
	private Result addBrowse(NumBrowse bean){
		long _start=System.currentTimeMillis();
		Num numbean=this.numMapper.selectByPrimaryKey(bean.getNumId());
		if(numbean!=null)bean.setNum(numbean.getNumResource());
		Share share=this.shareMapper.selectByPrimaryKey(bean.getShareId());
		if(share!=null)bean.setShareConsumerId(share.getConsumerId());
		Example example = new Example(NumBrowse.class);
		example.createCriteria().andEqualTo("numId",bean.getNumId()).andEqualTo("consumerId",bean.getConsumerId()).andEqualTo("shareId",bean.getShareId());
		List<?> _list=this.numBrownseMapper.selectByExample(example);
		if(_list.isEmpty())bean.setShareFirstBrowse(1);
		this.numBrownseMapper.insert(bean);//每次都添加浏览记录
		/////////////////////以下处理分享的问题////////////////////////
		if(numbean!=null)this.numBrownseMapper.updateOpenCount(bean.getConsumerId(),numbean.getId());//只要用户有浏览，则通知所有以往的浏览记录
		log.info(String.format("添加浏览记录并更新浏览次数耗时[%s]ms",(System.currentTimeMillis()-_start)));
		return new Result(Result.OK,"添加成功");
	}
	/**
	 * 分享浏览记录
	 */
	public PageInfo shareBrowse(int start,int limit){
		Consumer consumer=apiSessionUtil.getConsumer();
//		NumBrowse pp=new NumBrowse();
//		pp.setStart(start);
//		pp.setLimit(limit);
//		PageHelper.startPage(pp.startToPageNum(),pp.getLimit());
		PageHelper.startPage(start,limit);
		Page<Object> page=this.numBrownseMapper.queryPartnerNumBrowseList(consumer.getId());
		Map map_act=Constants.contantsToMap("NUMBROWSE_ACTTYPE");
		for(int i=0;i<page.size();i++){
			Map map=(Map)page.get(i);
			map.put("act_type",map_act.get(map.get("act_type")));
		}
		PageInfo<Object> pm = new PageInfo<Object>(page);
		return pm;
	}

	//////////////////////////////////////////////

	/**
	 * 收支明细
	 */
	public Object financeList(int pageNum,int limit){
		Consumer consumer=apiSessionUtil.getConsumer();
//		String account_no=consumer.getId()+"";
		String account_no="2";
		Result result= this.fundOrderService.payHrPayAccountDetail(pageNum,limit,null,account_no,null,null);
		boolean _bool=result.getCode()==Result.OK;
		if(!_bool)return new PageInfo<Object>(null);
		List<Map> _list=new ArrayList<>();
		Map pageInfo=(Map)result.getData();
		List _res_list=null;
		if(pageInfo.containsKey("list"))_res_list=(List)pageInfo.get("list");
		for(int i=0;_res_list!=null&&i<_res_list.size();i++){
			Map map=(Map)_res_list.get(i);
			try {
				map.put("pay_date",Utils.stringToDate(ObjectUtils.toString(map.get("add_date")),"yyyy-MM-dd HH:mm:ss"));
			}catch (Exception e){

			}
		}
		return pageInfo;
	}
	/**
	 * 提现进度
	 */
	public Result financeWithdrawProgresss(){
		return new Result(Result.OK,"");//TODO 待实现
	}
	/**
	 * 提现
	 */
	public Result financeWithdraw(Double amt){
		if(amt<=0.3d)return new Result(Result.OK,"提现金额必须大于等于0.3元");
		Consumer consumer=apiSessionUtil.getConsumer();
		Consumer consumer1=this.consumerMapper.selectByPrimaryKey(consumer.getId());
		if(!(consumer1.getPartnerCheck()!=null&&consumer1.getPartnerCheck()==1))return new Result(Result.OK,"抱歉，您的材料还未审核通过，无法完成提现");
		String account_no=consumer.getId()+"";
		Result result=fundOrderService.payHrPayWithdrawToWx(account_no,amt);
		if(result.getCode()==Result.OK)return new Result(Result.OK,"提现成功");
		else return new Result(Result.ERROR,result.getData());
	}

	//////////////////////////////////////////////

	/**
	 *
	 * @param status
	 * @param num
	 * @param start
	 * @param limit
	 * @return
	 */
	public Result promotionPlanPage(int status,String num,int start,int limit) {
		PromotionPlan pp=new PromotionPlan();
		pp.setStart(start);
		pp.setLimit(limit);
		PageHelper.startPage(pp.startToPageNum(),pp.getLimit());
		pp.setCorpId(SessionUtil.getUser().getCorpId());
		pp.setFeeType(Constants.PROMOTION_PLAN_FEETYPE_1.getIntKey());
		pp.setStatus(status);
		pp.setNum(StringUtils.trim(num));
		Page<Object> ob=promotionPlanMapper.queryPageList(pp);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Result promotionPlanAdd(int operation,PromotionPlan bean,String[] nums) {
		bean.setFeeType(Constants.PROMOTION_PLAN_FEETYPE_1.getIntKey());
		bean.setCorpId(SessionUtil.getUser().getCorpId());
		boolean is_update=bean.getId()!=0;
		if(!is_update){
			bean.setUpdateUser(SessionUtil.getUserId());
			bean.setAddUser(SessionUtil.getUserId());
		}
		if(operation==1){//草稿操作
			bean.setStatus(Constants.PROMOTION_PLAN_STATUS_1.getIntKey());
			return promotionPlanSave(bean,nums);
		}
		bean.setStatus(Constants.PROMOTION_PLAN_STATUS_2.getIntKey());
		/////////////////////////////////
		//promotion 0全部号码1根据号码销售价格段2指定号码;award_way 1固定金额2订单比例;is_limit0无1有
		if(bean.getPromotion()== Constants.PROMOTION_PLAN_PROMOTION_0.getIntKey()){
			nums=new String[0];
			bean.setBeginPrice(BigDecimal.valueOf(0d));
			bean.setEndPrice(BigDecimal.valueOf(0d));
		}else if(bean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_1.getIntKey()){
			nums=new String[0];
			if(bean.getBeginPrice().doubleValue()<0)return new Result(Result.ERROR,"价格范围必须大于等于0元");
			if(bean.getBeginPrice().doubleValue()>bean.getEndPrice().doubleValue())return new Result(Result.ERROR,"截至价格需大于等于开始价格");
		}else if(bean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_2.getIntKey()){
			if(nums.length<=0)return new Result(Result.ERROR,"请输入参与推广的号码");
			bean.setBeginPrice(BigDecimal.valueOf(0d));
			bean.setEndPrice(BigDecimal.valueOf(0d));
		}else{
			return new Result(Result.ERROR,"未知参与方式");
		}
		if(bean.getAward().doubleValue()<=0d)return new Result(Result.ERROR,"奖励金额不能小于等于0");
		if(bean.getAwardWay()==Constants.PROMOTION_PLAN_AWARDWAY_1.getIntKey()){
		}else if(bean.getAwardWay()==Constants.PROMOTION_PLAN_AWARDWAY_2.getIntKey()){
			if(bean.getAward().doubleValue()>100d)return new Result(Result.ERROR,"奖励方案按比例时，比例不能超过100%");
		}else{
			return new Result(Result.ERROR,"未知奖励方案");
		}
		if(bean.getEndDate().getTime()<bean.getBeginDate().getTime())return new Result(Result.ERROR,"截至时间需大于等于开始时间");
		/*
		* 1、全部号码参与时间段不能冲突
		* 2、指定号码的同号码参与时间段不能冲突
		* 3、指定价格段的同价格区间参与时间段不能冲突
		*  区间对比
		*  区间线性效果:a_start	b_start------b_end	a_end
		*  反响思考  a_start>b_end or a_end<b_start 时不冲突，其他情况都冲突
		* */
		Set<String> num_set=new HashSet<>();
		for (String num:nums){
			num_set.add(StringUtils.trim(num));
		}
		if(num_set.size()!=nums.length)return new Result(Result.ERROR,"所提交的号码中存在重复");
		Page<PromotionPlan> page=this.promotionPlanMapper.queryConflictList(bean);//查出时间冲突的推广
		for(PromotionPlan cbean:page){
			if(cbean.getPromotion()!=bean.getPromotion())continue;
			if(cbean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_0.getIntKey())return new Result(Result.ERROR,"您当前提交的和之前提交的推广计划(推广编号:"+cbean.getId()+")存在冲突");
			if(cbean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_1.getIntKey()){//价格
				double a_start=bean.getBeginPrice().doubleValue();
				double a_end=bean.getEndPrice().doubleValue();
				double b_start=cbean.getBeginPrice().doubleValue();
				double b_end=cbean.getEndPrice().doubleValue();
				if(!(a_start>b_end || a_end<b_start))return new Result(Result.ERROR,"您当前提交的和之前提交的推广计划(推广编号:"+cbean.getId()+")存在价格冲突");
			}
			if(cbean.getPromotion()==Constants.PROMOTION_PLAN_PROMOTION_2.getIntKey()){
				Example example = new Example(PromotionPlanNum.class);
				example.createCriteria().andEqualTo("promotionPlanId",cbean.getId()).andEqualTo("isDel",0);
				List<?> _list=this.promotionPlanNumMapper.selectByExample(example);
				Set<String> cnum_set=new HashSet<>();
				cnum_set.addAll(num_set);
				for (int i=0;i<_list.size();i++){
					PromotionPlanNum cbeanNum=(PromotionPlanNum)_list.get(i);
					cnum_set.add(cbeanNum.getNum());
				}
				if(cnum_set.size()!=(num_set.size()+_list.size()))return new Result(Result.ERROR,"您当前提交的和之前提交的推广计划(推广编号:"+cbean.getId()+")存在号码冲突");
			}
		}
		return promotionPlanSave(bean,nums);
	}
	private Result promotionPlanSave(PromotionPlan bean,String[] nums) {
		if(bean.getId()!=0){
			PromotionPlan newBean=this.promotionPlanMapper.selectByPrimaryKey(bean.getId());
			if(newBean==null)return new Result(Result.ERROR,"数据错误");
			if(!SessionUtil.getUser().getCorpId().equals(newBean.getCorpId()))return new Result(Result.ERROR,"您无权修改他人推广计划");
			if(newBean.getStatus()!=Constants.PROMOTION_PLAN_STATUS_1.getIntKey())return new Result(Result.ERROR,"只能对草稿的推广计划进行修改");
			newBean.setPromotion(bean.getPromotion());
			newBean.setAwardWay(bean.getAwardWay());
			newBean.setAward(bean.getAward());
			newBean.setIsLimit(bean.getIsLimit());
			newBean.setLimitAward(bean.getLimitAward());
			newBean.setBeginPrice(bean.getBeginPrice());
			newBean.setEndPrice(bean.getEndPrice());
			newBean.setUpdateUser(SessionUtil.getUserId());
			newBean.setUpdateDate(new Date());
			newBean.setBeginDate(bean.getBeginDate());
			newBean.setEndDate(bean.getEndDate());
			newBean.setStatus(bean.getStatus());
			this.promotionPlanMapper.updateByPrimaryKey(newBean);
		}else{
			this.promotionPlanMapper.insert(bean);
		}
		this.promotionPlanNumMapper.deletePromotionPlanNum(bean.getId());
		for(String num:nums){
			PromotionPlanNum nbean=new PromotionPlanNum(bean.getId(),StringUtils.trim(num));
			promotionPlanNumMapper.insert(nbean);
		}
		return new Result(Result.OK,"保存成功");
	}
	public Result findPromotionPlan(Integer promotion_plan_id){
		PromotionPlan bean=this.promotionPlanMapper.selectByPrimaryKey(promotion_plan_id);
		if(!SessionUtil.getUser().getCorpId().equals(bean.getCorpId()))return new Result(Result.ERROR,"您无权查看他人推广计划");
		Example example = new Example(PromotionPlanNum.class);
		example.createCriteria().andEqualTo("promotionPlanId",promotion_plan_id).andEqualTo("isDel",0);
		List<?> _list=this.promotionPlanNumMapper.selectByExample(example);
		bean.setList((List<Object>)_list);
		User adduser=userMapper.selectByPrimaryKey(bean.getAddUser());
		bean.setAddUserStr(adduser.getName());
		User updateuser=userMapper.selectByPrimaryKey(bean.getUpdateUser());
		bean.setUpdateUserStr(updateuser.getName());
		return new Result(Result.OK,bean);
	}
	public Result promotionPlanExpire(Integer promotion_plan_id){
		PromotionPlan bean=this.promotionPlanMapper.selectByPrimaryKey(promotion_plan_id);
		if(!SessionUtil.getUser().getCorpId().equals(bean.getCorpId()))return new Result(Result.ERROR,"您无权修改他人推广计划");
		if(bean.getStatus()!=Constants.PROMOTION_PLAN_STATUS_2.getIntKey())return new Result(Result.ERROR,"只能对有效的推广计划进行设置");
		bean.setStatus(Constants.PROMOTION_PLAN_STATUS_3.getIntKey());
		bean.setUpdateDate(new Date());
		bean.setUpdateUser(SessionUtil.getUserId());
		this.promotionPlanMapper.updateByPrimaryKey(bean);
		return new Result(Result.OK,"设置成功");
	}

	/**
	 *  获得号码的推广计划
	 *  按优先级进行查找
	 * @param num_id
	 * @return
	 */
	public Result findNumPromotionPlan(int fee_type,Integer num_id){
		//1.按号码查
		Num num=numMapper.selectByPrimaryKey(num_id);
		PromotionPlan ppbean=null;
		ppbean=new PromotionPlan(num.getSellerId(),Constants.PROMOTION_PLAN_PROMOTION_2.getIntKey(),Constants.PROMOTION_PLAN_STATUS_2.getIntKey(),new Date(),null,num.getNumResource());
		ppbean.setFeeType(fee_type);
		List<?> list= this.promotionPlanMapper.queryPageList(ppbean);
		log.info(String.format("查得以号码[编码%s]的推广计划[%s]条",num_id,list.size()));
		if(list.size()>0)return new Result(Result.OK,list.get(0));
		//2.按价格查
		Result curr_price = apiOrderService.findNumSalePrice(num_id);
		Double num_price=-1d;
		if(curr_price.getCode()==Result.OK)num_price=NumberUtils.toDouble(ObjectUtils.toString(curr_price.getData()));
		ppbean=new PromotionPlan(num.getSellerId(),Constants.PROMOTION_PLAN_PROMOTION_1.getIntKey(),Constants.PROMOTION_PLAN_STATUS_2.getIntKey(),new Date(),num_price,null);
		ppbean.setFeeType(fee_type);
		list= this.promotionPlanMapper.queryPageList(ppbean);
		log.info(String.format("查得以号码[编码%s]销售价[%s]的推广计划[%s]条",num_id,curr_price,list.size()));
		if(list.size()>0)return new Result(Result.OK,list.get(0));
		//3.按全号码查
		ppbean=new PromotionPlan(num.getSellerId(),Constants.PROMOTION_PLAN_PROMOTION_0.getIntKey(),Constants.PROMOTION_PLAN_STATUS_2.getIntKey(),new Date(),null,null);
		ppbean.setFeeType(fee_type);
		list= this.promotionPlanMapper.queryPageList(ppbean);
		log.info(String.format("查得全号码[号码编码%s]的推广计划[%s]条",num_id,list.size()));
		if(list.size()>0)return new Result(Result.OK,list.get(0));
		//4.未参与推广
		return new Result(Result.ERROR,"号码未参与推广");
	}
}
