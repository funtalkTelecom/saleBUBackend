package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.controller.BaseReturn;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.websocket.WebSocketServer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.lang.System;

@Service
public class EPSaleService {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired SessionUtil sessionUtil;

	@Value("${auction.timer}")
	private String auction_timer;
	@Autowired private EPSaleMapper epSaleMapper;
	@Autowired private EPSaleNoticeMapper ePSaleNoticeMapper;
	@Autowired private AuctionMapper auctionMapper;
	@Autowired private AuctionDepositMapper auctionDepositMapper;
	@Autowired private FileMapper fileMapper;
    @Autowired private ConsumerMapper consumerMapper;
	@Autowired private DeliveryAddressMapper deliveryAddressMapper;
	@Autowired private NumberMapper numberMapper;
	@Autowired private GoodsMapper goodsMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private FundOrderService fundOrderService;
	@Autowired private ApiOrderService apiOrderService;
	@Autowired private EPSaleService epSaleService;
	@Autowired private AuctionService auctionService;
	@Autowired private AuctionDepositService auctionDepositService;
	@Autowired private GoodsService goodsService;
	public Result pageEPSale(EPSale epSale) {
		PageHelper.startPage(epSale.startToPageNum(),epSale.getLimit());
		Page<Object> ob=this.epSaleMapper.queryPageList(epSale);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Result findEPSaleList() {
		//List<Map> list=epSaleMapper.findEPSaleList();
		//竞拍活动结束时间是以该活动的所有Num中最迟结束时间为主
		List<Map> list=epSaleMapper.findEPSaleList2();
		Long epSaleId=0L;
		int priceCount=0;
		for(Map map:list)
		{
			String urlImg=SystemParam.get("domain-full")+map.get("epImg").toString();
			map.put("epImg",urlImg);
            epSaleId=Long.valueOf(map.get("id").toString());
            //该活动的所有出价总次数
            List<Map> priceCountList=epSaleMapper.findEPSalePriceCountByEPSaleId(epSaleId);
            if(priceCountList.size()>0)
            {
                priceCount=Integer.valueOf(priceCountList.get(0).get("priceCount").toString());
				map.put("priceCount",priceCount);
            }else
			{
				priceCount=0;
				map.put("priceCount",priceCount);
			}
			map.put("serviceTime", java.lang.System.currentTimeMillis());
		}
		return new Result(Result.OK, list);
	}

	public Result findEPSaleList2() {
		//List<Map> list=epSaleMapper.findEPSaleList();
		List<Map> list=epSaleMapper.findEPSaleList3();
		for(Map map:list)
		{
			String urlImg=SystemParam.get("domain-full")+map.get("epImg").toString();
			map.put("epImg",urlImg);
		}
		return new Result(Result.OK, list);
	}

	public List<Map> findEPSaleByEPSaleId(Long ePSaleId) {
		//竞拍活动结束时间是以该活动的所有Num中最迟结束时间为主
		List<Map> list=epSaleMapper.findEPSaleByEPSaleId(ePSaleId);
		for(Map map:list)
		{
			String urlImg=SystemParam.get("domain-full") +map.get("epImg").toString();
			map.put("epImg",urlImg);
            map.put("serviceTime", java.lang.System.currentTimeMillis());
		}
		return list;
	}

	/*
      erSatus 状态 1竟拍前，2竟拍中，3竟拍后
	 */
	public List<Map> findERSatusByEPSaleId(Long ePSaleId) {
		return epSaleMapper.findEPSaleByEPSaleId2(ePSaleId);
	}

	/*
	  该活动是否有对应上架的商品
	 */
	public List<Map> findIsSaleListByEPSaleId(Long ePSaleId) {
		return  epSaleMapper.findEPSaleGoodsListByEPSaleId2(ePSaleId);
	}
    public List<Map> findEPSaleGoodsListByEPSaleId(Long ePSaleId) {
	    //获取竞拍活动ePSaleId的商品列表信息，图片限商品首图
        List<Map> list=epSaleMapper.findEPSaleGoodsListByEPSaleId(ePSaleId);
        Long numId=0L;
        Long gId=0L;
        String urlImg="";
        int priceCount=0;//出价次数
        double currentPrice=0.00;//当前价
        for(Map map:list)
        {
            urlImg=SystemParam.get("domain-full") +map.get("gImg").toString();
            map.put("gImg",urlImg);
            numId=Long.valueOf(map.get("numId").toString());
            gId=Long.valueOf(map.get("gId").toString());
            if(numId>0)
            {  //每个numId,gId的商品的出价次数及当前价
               List<Map> listAuction=auctionMapper.findAuctionSumEPSaleGoodsByNumIdAndGId(numId,gId);
               if(listAuction.size()>0)
               {
                   priceCount=Integer.valueOf(listAuction.get(0).get("priceCount").toString());
                   currentPrice=Double.valueOf(listAuction.get(0).get("currentPrice").toString());
               }
               else
			   {
				   priceCount=0;
				   currentPrice=0.00;
			   }
               map.put("priceCount",priceCount);
               map.put("currentPrice",currentPrice);
            }
        }
        return list;
    }

	public List<Map> findEPSaleGoodsByGoodsId(Long numId) {
		return epSaleMapper.findEPSaleGoodsByNumId(numId);
	}

    public List<Map> findEPSaleGoodsByGoodsId(Long numId,Long gId) {
        return epSaleMapper.findEPSaleGoodsByNumIdAndGId(numId,gId);
    }

    /*
      产品首图 numId与gId
     */
	public List<Map> findEPSaleGoodsImgByNumIdAndGId(Long numId,Long gId) {
		return epSaleMapper.findEPSaleGoodsImgByNumIdAndGId(numId,gId);
	}

	public Map findEPSaleNumInfoByNumId(Long numId) {
		return  numberMapper.getNumInfoById(numId.toString());
	}

	public EPSale finEPSaleById(Long id) {
		EPSale epSale = epSaleMapper.findEPSaleById(id);
		return epSale;
	}

	public Result epsaleDelayed(Long num_id) {
		List<Map> list=this.epSaleMapper.queryNumEndTime(num_id);
		log.info(String.format("检测并更新号码[%s]延时时间,计划更新条数[%s]",num_id,list.size()));
		if(list.isEmpty())return new Result(Result.ERROR,"暂无可出来号码书");
		for (Map map:list) {
			Integer time_status=NumberUtils.toInt(String.valueOf(map.get("time_status")),0);
			Integer loop_time=NumberUtils.toInt(String.valueOf(map.get("loop_time")),0);
			Long q_num_id=NumberUtils.toLong(String.valueOf(map.get("num_id")),0l);
			log.info(String.format("结束时间距离当前时间[%s]s,延时周期[%s]min",time_status,loop_time));
			if(time_status>=0&& time_status<=loop_time*60){
				this.epSaleMapper.updateNumDelayed(q_num_id,loop_time);
			}
		}
		return new Result(Result.OK,"更新成功");
	}

    @Scheduled(fixedRate=1000)
    public void epsaleOrder() {
		if(!StringUtils.equals(auction_timer,"true"))return;
	    this.epSaleMapper.freezeOneRecord();
	    try {
			epSaleService.payEpsaleOrder();
		}catch (Exception e){
	    	log.error("生成竞拍订单&退还保证金异常",e);
		}

    }

	/**
	 * 防止实际业务方法回滚导致此处数据回滚
	 * @param mapOrder
	 * @return
	 */
	public Result newCreateOrder(Map mapOrder) {
		return apiOrderService.createOrder(null,mapOrder);
	}
	public Result newByNumToUnShelve(String num,String skuid) {
		return this.goodsService.ByNumToUnShelve(num,skuid);
	}
	public void payEpsaleOrder() {
		/**
		 * 1、取出所有竞拍的上架单的时间到期未生成订单的号码
		 * 2、取唯一一条有效出价单(价格最高、时间升序)
		 * 3、校验规则 取出价人数是否大于起拍人数  >> 符合 则 2 转单(转单内部更新号码状态=3)  ；否则流拍 & 号码状态=1(在库)
		 * 4、实生成订单的客户不退保证金，其他用户退还保证金
		 * 5、短信通知
		 *
		 */
		List<Map> list=this.epSaleMapper.queryEndAuction();
		if(list.isEmpty()){
			log.info(String.format("暂无上架中的时间到期的竞拍单(号码)"));return;
		}
		for (Map map:list ) {
			//n.id num_id,g.g_id,s.sku_id,n.`status`,n.num_resource,n.g_start_num
			Long sku_id=NumberUtils.toLong(String.valueOf(map.get("sku_id")));
			Long num_id=NumberUtils.toLong(String.valueOf(map.get("num_id")));
			Long g_id=NumberUtils.toLong(String.valueOf(map.get("g_id")));
			Long success_consumer_id=null;
			int g_start_num=NumberUtils.toInt(String.valueOf(map.get("g_start_num")));
			String num_resource=String.valueOf(map.get("num_resource"));
			log.info(String.format("号码[%s]时间已到期，准备生成订单、退还保证金",num_resource));

			List<Map> countAuctions=this.epSaleMapper.countAuctions(num_id,g_id);
			if(countAuctions.size()>=g_start_num){//参与人数足
				List<Map> activeAuction=epSaleMapper.queryActiveAuction(num_id,g_id);
				if(activeAuction.size()>0){
					Map mapa=activeAuction.get(0);//
					//a.sku_id,a.num_id,a.g_id,a.consumer_id,a.price
					Map mapOrder=new HashMap();
					success_consumer_id=NumberUtils.toLong(String.valueOf(mapa.get("consumer_id")));
					Consumer user=this.consumerMapper.findConsumerById(success_consumer_id);
					Double price=NumberUtils.toDouble(String.valueOf(mapa.get("price")));
					mapOrder.put("type","3");//竞拍订单
					mapOrder.put("user",user);//竞拍成功用户
					mapOrder.put("skuid",sku_id);//竞拍成功skuId
					mapOrder.put("numid",num_id);//竞拍成功numId
					mapOrder.put("addrid",0);//竞拍成功addrid
					mapOrder.put("price",price);//竞拍成功price
                    log.info("准备生成订单："+mapOrder);
					Result result=epSaleService.newCreateOrder(mapOrder);
					if(result.getCode()!=200){
						log.error(String.format("竞拍结束;出价成功转订单失败;竞拍号[%s]",num_resource));
						Messager.send(SystemParam.get("system_phone"),"竞拍结束;出价成功转订单失败;竞拍号:"+num_resource);
					}else
					{
						try {
							log.info("出价成功************广播信息*************************************");
							WebSocketServer.sendInfo(String.valueOf(num_id),String.valueOf(g_id));
							log.info("出价成功*************广播信息************************************");
						}catch (IOException e)
						{
							log.info(String.format("出价成功，广播信息异常【[%s]",e.getMessage())+"】");
						}
					}
				}
			}else{//人数不足  流拍
			    //号码状态已变更，但库存未扣减
				/*Number number=numberMapper.selectByPrimaryKey(num_id);
				number.setStatus(1);
				this.numberMapper.updateByPrimaryKey(number);*/
				Result result=epSaleService.newByNumToUnShelve(num_resource,String.valueOf(sku_id));
				log.info(String.format("人数不足，流拍出来结果[%s][%s]",result.getCode(),String.valueOf(result.getData())));
			}
			//if(true) throw  new RuntimeException("测试。。。。。。。。。。。。。。。。。");
			List<Map> needReturn=epSaleMapper.queryNeedReturn(num_id,g_id,success_consumer_id);//success_consumer_id为空时查询该号码所有出价结果
			if(needReturn.isEmpty()){
				log.info(String.format("竞拍单[%s]无可退保证金",num_resource));continue;
			}
			for (Map map_return: needReturn) {
				String id=String.valueOf(map_return.get("id"));
				Long consumer_id=NumberUtils.toLong(String.valueOf(map_return.get("consumer_id")));
				Result result= fundOrderService.payDepositRefund(id,String.format("[%s]%s号码保证金退还",SystemParam.get("system_name"),StringUtils.replace(num_resource,StringUtils.substring(num_resource,3,7),"****")));
				Consumer user=this.consumerMapper.findConsumerById(consumer_id);
				AuctionDeposit auctionDeposit=this.auctionDepositMapper.selectByPrimaryKey(Long.valueOf(id));
				if(result.getCode()==200){
					Messager.send(user.getPhone(),"竞拍结束;保证金退还成功，您参与的竞拍号[%s]由于参与人数不足或者出价金额被赶超");
					auctionDeposit.setStatus(3);//status 3 已退款
					auctionDepositMapper.auctionDepositEdit(auctionDeposit);
				}else{
					Messager.send(SystemParam.get("system_phone"),"竞拍结束;保证金退回失败["+num_resource+"]");
				}
			}
		}
	}

   /*
   *
   * 竞拍的号码是否结束
   * "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
   *
	*/
	//@Scheduled(cron = "0 3 18 * * ?")
//   @Scheduled(fixedRate=-1)
	public void checkEPsaleNum() {
		List<Map> list=epSaleMapper.findEPSaleGoods2();//已出价的Num列表
		String endTimeStr="";//结束时间
		String currentTimeStr="";//当前时间
		Long numId=0L;//条码ID
        Long gId=0L;//gId
	    Long skuId=0L;//skuId
	    Long addrid=0L;//最后成功出价成功=》默认地此
	    String num="";//出价的号码
        double successAutionPrice=0.00;//最后成功出价记录价格
		int startNum=0;//起拍人数
		boolean isEPSaleValid=false;//是否竞拍成功
	    boolean isStartNum=false;//起拍人数是否足够
	    int priceCumsumerCount=0;//出价人数
	    long successConsumerId=0L;//最后成功出价记录 用户ID
		double depositPrice=0.00;//保证金

		if(list.size()>0)
		{
			for (Map map:list)
			{
				log.info(String.format("numId[%s]",map.get("numId")));
				endTimeStr=map.get("endTime").toString();
				numId=Long.valueOf(map.get("numId").toString());
                gId=Long.valueOf(map.get("gId").toString());
				num=map.get("num").toString();
				startNum=Integer.valueOf(map.get("startNum").toString());
				depositPrice=Double.valueOf(map.get("depositPrice").toString());
				currentTimeStr=Utils.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");

				if(Utils.compareDate(currentTimeStr,endTimeStr)<0){
					log.info(String.format("竞拍[%s]暂未结束",map.get("numId")));
					return ;
				}
				//********************************************************************************************************************
				//***********************************当前时间>=结束时间*******竞拍人数>=起拍人数符合规则******************************
				List<Map> auctionCustomers=auctionMapper.findCustomersByNumIdAndGId(numId,gId);//竞拍人数


				if(auctionCustomers.size()>0)
				{
					priceCumsumerCount=auctionCustomers.size();//竞拍人数
					if(priceCumsumerCount>=startNum)//竞拍人数>=起拍人数符合规则
					{
						isStartNum=true;
						Auction auction =new Auction();
						auction.setNumId(numId);
						auction.setStatus(2);
						auction.setgId(gId);
						List<Map> successAution=auctionMapper.findAuctionByNumIdAndStatusAndGId(auction);//最后成功出价记录 status:2
						if(successAution.size()>0)
						{
							successConsumerId=Long.valueOf(successAution.get(0).get("consumerId").toString());//最后成功出价记录 用户ID
							numId=Long.valueOf(successAution.get(0).get("numId").toString());//条码ID
							skuId=Long.valueOf(successAution.get(0).get("skuId").toString());//skuId
							successAutionPrice=Double.valueOf(successAution.get(0).get("price").toString());//最后成功出价记录价格
							isEPSaleValid=true;
						}
					}
				}

				//********************************************************************************************************************
				//***********************************有效竞拍转订单********************************************************************
				//isEPSaleValid=true;
				if(isEPSaleValid)//有效竞拍，最新一条出价转订单，其他用户退回保证金
				{
                    Consumer user=this.consumerMapper.findConsumerById(successConsumerId);
					List<Map> deliveryAddressDefault=deliveryAddressMapper.findDeliveryAddressDefaultByUserId(successConsumerId);
					/*if(deliveryAddressDefault.size()>0)
					{
						addrid=Long.valueOf(deliveryAddressDefault.get(0).get("id").toString());
					}*/
                    Map mapOrder=new HashMap();
                    mapOrder.put("type","3");//竞拍订单
					mapOrder.put("user",user);//竞拍成功用户
					mapOrder.put("skuid",skuId);//竞拍成功skuId
					mapOrder.put("numid",numId);//竞拍成功numId
					mapOrder.put("addrid",addrid);//竞拍成功addrid
					mapOrder.put("price",successAutionPrice);//竞拍成功price
					if(apiOrderService.createOrder(null,mapOrder).getCode()!=200)
					{
						//************************通知管理员，竞拍出价成功转订单失败********************//
						Messager.send(SystemParam.get("system_phone"),"竞拍结束;出价成功转订单失败;竞拍号:"+num);
					}
				}

				//****************************************************************************************************************
                //******************************保证金退回并通知****************开始***********************************************
				//****************isEPSaleValid是否竞拍成功***********************************************************************
				//****************是****状态4 落败者 保证金退回并通知**********否****状态4,2 落败者成功者 保证金全部退回并通知****
				Long consumerId=0L;
				Long depositId=0L;
				String consumerPhone="";
				AuctionDeposit auctionDeposit=new AuctionDeposit();
				List<Map> auctionList=new ArrayList<Map>();
				if(isEPSaleValid)
				{
					auctionList=auctionMapper.findAuctionListDepositByNumIdAndGId(numId,gId);//退回落败者的保证金
				}else
				{
					auctionList=auctionMapper.findAuctionListDepositByNumIdAndGId2(numId,gId);//退回所有出价者的保证金
				}
				if(auctionList.size()>0)
				{
					for(Map map2:auctionList)
					{
						consumerId=Long.valueOf(map2.get("consumerId").toString());
						consumerPhone=map2.get("consumerPhone").toString();
						auctionDeposit.setConsumerId(consumerId);
						auctionDeposit.setNumId(numId);
						//*************************************出价记录status=4,status<>2 用户保证金
						List<Map> depositList =auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndGId(numId,consumerId,gId);
						if(depositList.size()>0)
						{
							depositId=Long.valueOf(depositList.get(0).get("id").toString());
							if(depositId>0)
							{
								auctionDeposit.setId(depositId);
								if(fundOrderService.payDepositRefund(auctionDeposit.getId().toString(),"保证金").getCode()==200)
								{
									auctionDeposit.setStatus(3);//status 3 已退款
									auctionDepositMapper.auctionDepositEdit(auctionDeposit);
									if(!isStartNum)//起拍人数是否足够
									{
										Messager.send(consumerPhone,"竞拍结束;竞拍失败,出价人数少于起拍人类;竞拍号:"+num);
									}
									Messager.send(consumerPhone,"竞拍结束;你的出价落败,保证金已退回,金额："+depositPrice+"竞拍号:"+num);
								}else
								{
									//************************通知管理员，保证金退回失败********************//
									Messager.send(SystemParam.get("system_phone"),"竞拍结束;保证金退回失败,金额："+depositPrice+"竞拍号:"+num);
								}
							}
						}
					}
				}
				//************************************************************************************************************
				//******************************保证金退回并通知*******************结束***************************************
				//************************************************************************************************************
			}
		}
	}

	public List<Map> findNumById(Long id) {
		return epSaleMapper.findNumById(id);
	}

	public void numLoopEdit(Long numId,int loopTime) {
		List<Map> numList=findNumById(numId);
		Date endTime=new Date();//结束时间
		if(numList.size()>0) {
			try {
				endTime=Utils.stringToDate(numList.get(0).get("end_time").toString(),"yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String endTimeStr=Utils.getDate2(loopTime,endTime,"yyyy-MM-dd HH:mm:ss");
		epSaleMapper.numLoopEdit(endTimeStr,numId);
	}

	public boolean isLoopTime(Date addTime,int loopTime,Long numId)
	{
		boolean isLoop=false;
		List<Map> numList=findNumById(numId);
		if(numList.size()>0)
		{
			Date endTime=new Date();//结束时间
			String endTimeStr="";
			Date strtTime=new Date();//开始时间
			Date currentTime=new Date();//当前时间
			endTimeStr=numList.get(0).get("end_time").toString();
			try {
				endTime=Utils.stringToDate(endTimeStr,"yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//strtTime=Utils.getDate2(loopTime,endTime);

			endTimeStr=Utils.dateToString(endTime,"yyyy-MM-dd HH:mm:ss");
			String startTimeStr=Utils.getDate2(-loopTime,endTime,"yyyy-MM-dd HH:mm:ss");
			String currentTimeStr=Utils.dateToString(addTime,"yyyy-MM-dd HH:mm:ss");
			if(Utils.compareDate(startTimeStr,currentTimeStr)<0&&Utils.compareDate(currentTimeStr,endTimeStr)<0)//addTime 处于 （结束时间-轮询时间）与结束时间 之间
			{
				return  true;
			}

		/*	if(strtTime.compareTo(currentTime)<0&&currentTime.compareTo(endTime)<0)//addTime 处于 （结束时间-轮询时间）与结束时间 之间
			{
				return  true;
			}*/
		}
		return false;
	}

	/*
	   竞拍商品出价
	   *限 每次gId与numId组合
	   *
	 */
	private static Object cjLock = new Object();
	public void goodsAuciton(Auction auction, HttpServletRequest request) {
		//***********************************
		//***************************加锁记录
		//***********************************
		log.info("********************************************************************************************");
		log.info("**********************freezeOneNum锁开始*****************************************************");
		auctionMapper.freezeOneNum(auction.getNumId());
		log.info("**********************freezeOneNum锁开始******************************************************");
		log.info("*********************************************************************************************");
		Goods goods=goodsMapper.findGoodsInfo(auction.getgId());//上架商品信息gActive
		Date endTime=null;//结束时间
		Date currentTime=new Date();//当前时间
		int goodsAuctionCount=0;//最近10次数出价记录的次数
		List<Map> numList=epSaleMapper.findNumById(auction.getNumId());
		if(numList.size()>0)
		{
			try {
				endTime=Utils.stringToDate(String.valueOf(numList.get(0).get("end_time")),"yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(currentTime.compareTo(endTime)>0)
			{
				com.hrtx.config.Utils.returnResult(new Result(500, "竞拍已经结束"));
				return;
			}
		}
		else
		{
			com.hrtx.config.Utils.returnResult(new Result(500, "该商品不存在"));
			return;
		}
		double startPrice=0L;
		Map numMap= numberMapper.getNumInfoById(String.valueOf(auction.getNumId()));
		if(numMap!=null&&numMap.size()>0)
		{
			startPrice=Double.valueOf(numMap.get("skuTobPrice").toString());
		}
		//最近10次数出价记录
		List<Map> goodsAuctionList=auctionMapper.findAuctionListByNumIdAndGId2(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
		goodsAuctionCount=goodsAuctionList.size();
		int priceCount=0;//出价次数
		double priceUp=Double.valueOf(goods.getgPriceUp());//每次加价
		double priceUp2=0.00;//priceUp*100
		int mulPrices=0;//每次加价倍数
		int loopTime=Integer.valueOf(goods.getgLoopTime());//轮咨时间分钟
		double deposit=Double.valueOf(goods.getgDeposit());//保证金
		double beforePrice=0.00;//前一次出价记录
		double subPrice=0.00;//当前出价与前一次出价相差
		Long epSaleId=goods.getgActive();//竞拍活动Id
		BigDecimal subPrice2=null;//subPrice*100
		BigDecimal priceUp3=null;//priceUp*100
		Long  autionId=0L;//前一次出价记录Id
		Long consumerId=0L;//前一次出价记录用户Id
		boolean isDeposit=false;//是否支付保证金
		Long auctionDepositId=0L;//保证金Id
		DecimalFormat df=new DecimalFormat("######0.00");
		DecimalFormat df2=new DecimalFormat("######0");
		if(Utils.formatFloatNumber(auction.getPrice()).length()>10)//整数7位.小数2位
		{
			com.hrtx.config.Utils.returnResult(new Result(606, "当前加价额度不能超过9位"));
			return;
		}
		if(goodsAuctionCount>0)//最近10次数出价记录
		{
			beforePrice=Double.valueOf(goodsAuctionList.get(0).get("price").toString());//前一次出价记录
			autionId=Long.valueOf(goodsAuctionList.get(0).get("id").toString());//前一次出价记录Id
			consumerId=Long.valueOf(goodsAuctionList.get(0).get("consumerId").toString());//前一次出价记录用户Id
			subPrice=auction.getPrice()-beforePrice;
		}else
		{
			subPrice=auction.getPrice();
			if(startPrice>subPrice)
			{
				com.hrtx.config.Utils.returnResult(new Result(603, "当前加价不符规则;加价不能低于起拍价"+startPrice));
				return;
			}
		}
		if(subPrice>0)
		{
			subPrice=Double.valueOf(df.format(subPrice));//保留两位小数
			if(Utils.judgeTwoDecimal(subPrice)||Utils.judgeTwoDecimal(priceUp))//若含小数点，转整数进行求%
			{
				subPrice=subPrice*100;
				subPrice=Double.valueOf(df.format(subPrice));//去小数
				subPrice2=new BigDecimal(subPrice);//可以过虑科学计数格式
				priceUp2=priceUp*100;
				priceUp2=Double.valueOf(df.format(priceUp2));//去小数
				priceUp3=new BigDecimal(priceUp2);//可以过虑科学计数格式
			}
			if((goodsAuctionCount>0)&&(subPrice2.divideAndRemainder(priceUp3)[1].compareTo(BigDecimal.ZERO)>0)) {//比较前后次出价差是===>否按每次加价的倍数%,若是第一次出价记录则不要进行比较
				if(goodsAuctionCount>0)
				{
					com.hrtx.config.Utils.returnResult(new Result(602, "当前加价不符规则;加价应按" + priceUp + "的倍数进行加价"));
					return;
				}
			}else
			{
				//当前用户保证金已支付成功 状态：2成功
				List<Map> auctionDepositConsumerList=auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndGId(auction.getNumId(),apiSessionUtil.getConsumer().getId(),auction.getgId());
				if(auctionDepositConsumerList.size()>0)
				{
					auctionDepositId=Long.valueOf(auctionDepositConsumerList.get(0).get("id").toString());
					if(auctionDepositConsumerList.get(0).get("status").toString().equals("2"))
					{
						isDeposit=true;
					}else
					{
						isDeposit=false;
					}
				}else//当前用户无保证金记录，预先生成status:1保证金记录
				{
					AuctionDeposit auctionDeposit=new AuctionDeposit();
					auctionDeposit.setStatus(1);
					auctionDeposit.setgId(auction.getgId());
					auctionDeposit.setNum(auction.getNum());
					auctionDeposit.setNumId(auction.getNumId());
					auctionDeposit.setSkuId(auction.getSkuId());
					auctionDeposit.setAmt(Double.valueOf(goods.getgDeposit()));//保证金记录  状态：1初始
					auctionDeposit.setAddIp(SessionUtil.getUserIp());
					auctionDepositService.auctionDepositEdit(auctionDeposit);
					auctionDepositId=auctionDeposit.getId();
					isDeposit=false;
				}
				if(isDeposit)//当前用户保证金已支付成功 状态：2成功
				{
					//*******************先检测是否有auction.status=1状态的记录
					List<Map> auctionListStatus2=auctionMapper.findAuctionListByNumIdAndConsumerIdAndGId(Long.valueOf(auction.getNumId()),apiSessionUtil.getConsumer().getId(),Long.valueOf(auction.getgId()));
					//yyyymmddhhiiss 可以用来测试auctionDepositPay接口
					//auctionDepositService.auctionDepositPay(auctionDepositId,true,"20180620112023");
					Date addDate=new Date();
					//auction.setStatus(2);
					auction.setAddDate(addDate);
					auction.setConfirmDate(addDate);//status 2 确认时间
					auction.setAddIp(SessionUtil.getUserIp());
					double newPrice=0.00;
					Long newAutionId=0L;
					Long newConsumerId=0L;
					String goodsNoticePhone="";//短信通知手机号
					Auction auctonNew=new Auction();



					//synchronized (cjLock) {
						//出价后的最新10次出价记录
						List<Map> goodsAuctionListNew=auctionMapper.findAuctionListByNumIdAndGId2(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
						if(goodsAuctionListNew.size()>0)
						{
							newPrice=Double.valueOf(goodsAuctionListNew.get(0).get("price").toString());//最新出价记录
							newAutionId=Long.valueOf(goodsAuctionListNew.get(0).get("id").toString());//最新出价记录Id
							newConsumerId=Long.valueOf(goodsAuctionListNew.get(0).get("consumerId").toString());//最新出价记录用户Id
							//1、大于之前的最近出价记录，则最新出价记录状态：4 落败,当前出价记录状态：2成功
							log.info("****************************************************************************");
							log.info("******最新出价**"+newPrice+"*******************************************************");
							log.info("******当前出价**"+auction.getPrice()+"***********************************************");
							log.info("****************************************************************************");
							if(auction.getPrice()>newPrice) {
								//auctonNew  状态：4 落败
								auctonNew.setId(newAutionId);
								auctonNew.setStatus(4);//最新出价记录   状态：4 落败
								auctionService.auctionEditStatusById2(auctonNew);//通知用户
								//auction  状态：2成功
								auction.setStatus(2);
								auctionService.auctionEdit(auction);//出价记录 状态：2成功
								log.info("****************************************************************************");
								log.info("******当前出价**"+auction.getPrice()+"***********************出价成功************************");
								//****************短信通知提醒auctonNew****************已落败
								List<Map> goodsNoticeList=ePSaleNoticeMapper.findEPSaleNoticeListByEPSaleIdAndConsumerId2(epSaleId,newConsumerId);
								if(!goodsNoticeList.isEmpty()&&goodsNoticeList.size()>0)
								{
									goodsNoticePhone=String.valueOf(goodsNoticeList.get(0).get("phone").toString());
									Messager.send(goodsNoticePhone,"你的出价记录低于新的出价记录，已落败");
								}
							}//2、出现同价的成功出价记录，则当前出价记录状态：3失败
							else if(auction.getPrice()==newPrice)//出现同价的成功出价记录
							{
								//auction  状态：3失败
								auction.setStatus(3);
								auctionService.auctionEdit(auction);//出价记录 状态：2成功
								log.info("****************************************************************************");
								log.info("******当前出价**"+auction.getPrice()+"***********************出价失败************************");
							}else {
								//auction  状态：4落败
								auction.setStatus(4);
								auctionService.auctionEdit(auction);//出价记录 状态：2成功
								log.info("****************************************************************************");
								log.info("******当前出价**"+auction.getPrice()+"***********************出价落败************************");
								//****************短信通知提醒auctonNew****************已落败
								List<Map> goodsNoticeList=ePSaleNoticeMapper.findEPSaleNoticeListByEPSaleIdAndConsumerId2(epSaleId,this.apiSessionUtil.getConsumer().getId());
								if(!goodsNoticeList.isEmpty()&&goodsNoticeList.size()>0)
								{
									goodsNoticePhone=String.valueOf(goodsNoticeList.get(0).get("phone").toString());
									Messager.send(goodsNoticePhone,"你的出价记录低于新的出价记录，已落败");
								}
							}
						}

					//}
					epSaleService.epsaleDelayed(auction.getNumId());
					//**************当前出价记录是处于（结束时间-轮询时间）与结束时间 之间************************************************
				/*	if(epSaleService.isLoopTime(addDate,loopTime,auction.getNumId())) //处于（结束时间-轮询时间）与结束时间 之间;则延长结束时间= 结束时间+loopTime;
					{
						//***************************则延长结束时间= 结束时间+loopTime*********************
						epSaleService.numLoopEdit(auction.getNumId(),loopTime);
					}*/
					//出价后的最近10次出价记录
					List<Map> goodsAuctionListAfter=auctionMapper.findAuctionListByNumIdAndGId(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
					//String goodsAuctionListStr="";
					Map goodsAuctionMap=new HashMap();
					List<Map> epSaleGoodsAuctionPriceInfo=auctionMapper.findAuctionSumEPSaleGoodsByNumIdAndGId(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
					if(epSaleGoodsAuctionPriceInfo!=null&&epSaleGoodsAuctionPriceInfo.size()>0) {
						priceCount = NumberUtils.toInt(String.valueOf(epSaleGoodsAuctionPriceInfo.get(0).get("priceCount")));
					}
					if(goodsAuctionListAfter!=null&&goodsAuctionListAfter.size()>0)
					{
						goodsAuctionMap.put("goodsAuctionList",goodsAuctionListAfter);
						goodsAuctionMap.put("priceCount",priceCount);
						goodsAuctionMap.put("serviceTime",java.lang.System.currentTimeMillis());;
						//goodsAuctionListStr="goodsAuctionList:"+goodsAuctionListAfter;
					}else
					{
						goodsAuctionMap.put("goodsAuctionList","");
						goodsAuctionMap.put("priceCount","");
						goodsAuctionMap.put("serviceTime",java.lang.System.currentTimeMillis());;
						//goodsAuctionListStr="goodsAuctionList:"+"";
					}
					goodsAuctionMap.put("idDeposit","1");
					com.hrtx.config.Utils.returnResult(new Result(200, goodsAuctionMap));

					//******************************出价后的向所有WebSocket客户端广播信息
					//String msg = "{\"code\":\"" +  Result.OK + "\", \"data\":" + JSONArray.fromObject(goodsAuctionMap) + "}";
					//*/
					//String msg=this.epSaleService.AuctionAfterInfo(auction.getNumId(),auction.getgId());
					try {
						log.info("出价成功************广播信息*************************************");
						WebSocketServer.sendInfo(String.valueOf(auction.getNumId()),String.valueOf(auction.getgId()));
						log.info("出价成功*************广播信息************************************");
					}catch (IOException e)
					{
						log.info(String.format("出价成功，广播信息异常【[%s]",e.getMessage())+"】");
					}
				}else //当前用户保证金未支付成功    预先生成出价记录 状态：1初始     保证金记录状态：1 初始
				{
					//*******************先测
					//*******************先检测是否有auction.status=1状态的记录
					List<Map> auctionListStatus1=auctionMapper.findAuctionListByNumIdAndConsumerIdAndGId(Long.valueOf(auction.getNumId()),this.apiSessionUtil.getConsumer().getId(),Long.valueOf(auction.getgId()));
					if(auctionListStatus1==null||auctionListStatus1.size()==0)
					{
						auction.setStatus(1);
						auction.setAddIp(SessionUtil.getUserIp());
						auctionService.auctionEdit(auction);//出价记录 状态：1初始
					}
					String orderNameStr=SystemParam.get("system_name")+auction.getNum()+"号码保证金";
					Result res=fundOrderService.payPinganWxxDeposit(com.hrtx.global.Utils.doubleToInt(deposit),orderNameStr,auctionDepositId.toString());
					if(res.getCode()==200)
					{
						res.setCode(604);//"保证金支付中"
					}else
					{
						res.setCode(605);//"保证金支付中异常"
					}
					//returnResult(new Result(600, "保证金未支付"+res.getData().toString()));
					com.hrtx.config.Utils.returnResult(res);
				}
			}
		}else
		{
			com.hrtx.config.Utils.returnResult(new Result(601, "当前加价不符规则;加价应超过前一次出价"));
			return;
		}
		log.info("**********************freezeOneNum锁结束******************************************************");
		log.info("****************************************************************************");
	}

	public Result epSaleEdit(EPSale epSale, HttpServletRequest request, MultipartFile[] files) {
		epSale.setAddUserId(SessionUtil.getUserId());
		epSale.setIsShow(0);
		if (epSale.getId() != null && epSale.getId() > 0) {
			epSale.setUpdateDate(new Date());
			epSaleMapper.epSaleEdit(epSale);
		} else {
			List<EPSale> list = new ArrayList<EPSale>();
			epSale.setId(epSale.getGeneralId());
			epSale.setCreateDate(new Date());
			epSale.setUpdateDate(new Date());
			list.add(epSale);
			epSaleMapper.insertBatch(list);
		}
		//富文本信息获取
		String kindeditorContent = request.getParameter("epRule");
		Utils.kindeditorWriter(kindeditorContent, epSale.getId()+".txt", SystemParam.get("kindedtiorDir"));
		//图片保存1
		Result result = null;
		String picSeqs = request.getParameter("picSeqs")==null?"":request.getParameter("picSeqs");
		String delPicSeqs = request.getParameter("delPicSeqs")==null?"":request.getParameter("delPicSeqs");
		if(!picSeqs.equals("")){
			fileMapper.deleteFilesByRefid(epSale.getId().toString(), picSeqs.equals("")?"":picSeqs.substring(0, picSeqs.length()-1));
		}
		if(!delPicSeqs.equals("")){
			fileMapper.deleteFilesByRefid(epSale.getId().toString(), delPicSeqs.equals("")?"":delPicSeqs.substring(0, delPicSeqs.length()-1));
		}
		if(files!=null && files.length>0){
			try {
				List<File> fileList = new ArrayList<File>();
				for (int i=0; i<files.length; i++) {
					MultipartFile file = files[i];
					File f = new File();
					f.setFileId(f.getGeneralId());
					f.setFileGroup("epSalePics");
					//String kk=SystemParam.get("epSalePics");
					//result = BaseReturn.uploadFile(SystemParam.get("epSalePics")+epSale.getId()+"\\", "jpg,png,gif", file, false, false);
					result = BaseReturn.uploadFile(SystemParam.get("epSalePics")+epSale.getId()+java.io.File.separator, "jpg,png,gif", file, false, false);
					f.setFileName(((Map)result.getData()).get("sourceServerFileName").toString());
					f.setRefId(epSale.getId());
					f.setSeq(Integer.parseInt(picSeqs.replaceAll("\"","").split(",")[i]));
					fileList.add(f);
				}
				if(fileList!=null && fileList.size()>0) {
					fileMapper.insertBatch(fileList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(result==null) result = new Result(Result.ERROR, "保存图片异常");
				return result;
			}
		}
		return new Result(Result.OK, "提交成功");
	}

    public Result epSaleDelete(EPSale epSale) {
		epSaleMapper.epSaleDelete(epSale);
        return new Result(Result.OK, "删除成功");
    }
}
