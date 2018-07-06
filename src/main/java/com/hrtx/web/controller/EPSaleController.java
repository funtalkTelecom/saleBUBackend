package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.service.*;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.hrtx.global.Utils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

@RestController
//@RequestMapping("/api")
public class EPSaleController extends BaseReturn{

	@Resource
	private CityService cityService;
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EPSaleService epSaleService;
	@Autowired
	private FileService fileService;
	@Autowired
	private AuctionService auctionService;
	@Autowired
	private AuctionDepositService auctionDepositService;
	@Autowired
	private GoodsService goodsService;
	@Autowired
	private ConsumerService consumerService;
	@Autowired
	private FundOrderService fundOrderService;
	@Autowired
	private ApiSessionUtil apiSessionUtil;

	@RequestMapping("/epSale/epSale-query")
	@Powers({PowerConsts.EPSALEMOUDULE})
	public ModelAndView epSaleQuery(EPSale epSale){
		return new ModelAndView("admin/epSale/epSale-query");
	}

	@RequestMapping("/epSale/epSale-list")
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_QUEYR})
	public Result listEPSale(EPSale epSale){
		return epSaleService.pageEPSale(epSale);
	}

	/**
	 * 查询竟拍活动
	 * 限关联有上架商品的竟拍活动
	 * @param epSale
	 * @return
	 */
	@GetMapping("/api/epSales")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result findEPSaleList(EPSale epSale){
		return epSaleService.findEPSaleList();
	}

	/**
	 * 查询竟拍活动
	 * 供商品上架时选竟拍活动进行调用
	 * @param epSale
	 * @return
	 */
	@GetMapping("/epSales")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result findEPSaleList2(EPSale epSale){
		return epSaleService.findEPSaleList2();
	}


	/**
	 * 查询竟拍活动的商品列表
	 * 未过期
	 * @param
	 * @return
	 */
	@GetMapping("/api/epSaleGoodss/{ePSaleId}")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Map findEPSaleGoodss(@PathVariable("ePSaleId") String ePSaleId){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map> epSaleMap=epSaleService.findEPSaleByEPSaleId(Long.valueOf(ePSaleId));
		if(epSaleMap.size()>0)
        {
            List<Map> goodsList=epSaleService.findEPSaleGoodsListByEPSaleId(Long.valueOf(ePSaleId));
            if(goodsList.size()>0)
            {
                epSaleMap.get(0).put("goodsList",goodsList);
            }else
            {
                epSaleMap.get(0).put("goodsList","");
            }
        }
		map.put("code", Result.OK);
		map.put("data", epSaleMap);
		return map;
	}

	private static Object cjLock = new Object();
	/*
	   竟拍商品出价
	   *限 每次gId与numId组合
	   *
	 */
	@PostMapping("/api/epSaleGoodsAuciton")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void goodsAuciton(Auction auction, HttpServletRequest request) {
		Goods goods=goodsService.findGoodsById(auction.getgId());//上架商品信息
		Date endTime=null;//结束时间
		Date currentTime=new Date();//当前时间
        int goodsAuctionCount=0;//最近10次数出价记录的次数
		List<Map> numList=epSaleService.findNumById(auction.getNumId());
		if(numList.size()>0)
		{
			try {
				endTime=Utils.stringToDate(String.valueOf(numList.get(0).get("end_time")),"yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if(currentTime.compareTo(endTime)>0)
			{
				returnResult(new Result(500, "竟拍已经结束"));
			}
		}
		else
		{
			returnResult(new Result(500, "该商品不存在"));
		}
		double startPrice=0L;
		Map numMap=epSaleService.findEPSaleNumInfoByNumId(auction.getNumId());
		if(numMap!=null&&numMap.size()>0)
		{
			startPrice=Double.valueOf(numMap.get("skuTobPrice").toString());
		}
		//最近10次数出价记录
		List<Map> goodsAuctionList=auctionService.findAuctionListByNumIdAndGId3(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
        goodsAuctionCount=goodsAuctionList.size();
		int priceCount=0;//出价次数
		double priceUp=Double.valueOf(goods.getgPriceUp());//每次加价
		int mulPrices=0;//每次加价倍数
		int loopTime=Integer.valueOf(goods.getgLoopTime());//轮咨时间分钟
		double deposit=Double.valueOf(goods.getgDeposit());//保证金
		double beforePrice=0.00;//前一次出价记录
		double subPrice=0.00;//当前出价与前一次出价相差
		Long  autionId=0L;//前一次出价记录Id
		Long consumerId=0L;//前一次出价记录用户Id
		boolean isDeposit=false;//是否支付保证金
		Long auctionDepositId=0L;//保证金Id
		DecimalFormat df=new DecimalFormat("######0.00");
        DecimalFormat df2=new DecimalFormat("######0");
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
				 returnResult(new Result(603, "当前加价不符规则;加价不能低于起拍价"+startPrice));
			 }
		}
		if(subPrice>0)
		{
			subPrice=Double.valueOf(df.format(subPrice));//保留两位小数
			if(Utils.judgeTwoDecimal(subPrice)||Utils.judgeTwoDecimal(priceUp))//若含小数点，转整数进行求%
            {
                subPrice=subPrice*100;
                subPrice=Double.valueOf(df.format(subPrice));//去小数
                priceUp=priceUp*100;
                priceUp=Double.valueOf(df.format(priceUp));//去小数
            }
			if((goodsAuctionCount>0)&&(subPrice%priceUp>0)) {//比较前后次出价差是===>否按每次加价的倍数,若是第一次出价记录则不要进行比较
				if(goodsAuctionCount>0)
				{
					returnResult(new Result(602, "当前加价不符规则;加价应按" + priceUp + "的倍数进行加价"));
				}
			}else
			{
				List<Map> auctionDepositConsumerList=auctionDepositService.findAuctionDepositListConsumerByNumIdAndGId(auction.getNumId(),auction.getgId());//当前用户保证金已支付成功 状态：2成功
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
					List<Map> auctionListStatus2=auctionService.findAuctionListByNumIdAndGId2(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
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
					Auction auctonNew=new Auction();
					synchronized (cjLock) {
						//出价后的最新10次出价记录
						List<Map> goodsAuctionListNew=auctionService.findAuctionListByNumIdAndGId3(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
						if(goodsAuctionListNew.size()>0)
						{
							newPrice=Double.valueOf(goodsAuctionListNew.get(0).get("price").toString());//最新出价记录
							newAutionId=Long.valueOf(goodsAuctionListNew.get(0).get("id").toString());//最新出价记录Id
							newConsumerId=Long.valueOf(goodsAuctionListNew.get(0).get("consumerId").toString());//最新出价记录用户Id
							//1、大于之前的最近出价记录，则最新出价记录状态：4 落败,当前出价记录状态：2成功
							if(auction.getPrice()>newPrice) {
								//auctonNew  状态：4 落败
								auctonNew.setId(newAutionId);
								auctonNew.setStatus(4);//最新出价记录   状态：4 落败
								auctionService.auctionEditStatusById2(auctonNew);//通知用户
								//auction  状态：2成功
								auction.setStatus(2);
								auctionService.auctionEdit(auction);//出价记录 状态：2成功
								//****************短信通信auctonNew****************已落败
								Consumer consumer=new Consumer();
								consumer.setId(newConsumerId);
								consumer=consumerService.getConsumerById(consumer);
								Messager.send(consumer.getPhone(),"你的出价记录低于新的出价记录，已落败");
							}//2、出现同价的成功出价记录，则当前出价记录状态：3失败
							else if(auction.getPrice()==newPrice)//出现同价的成功出价记录
							{
								//auction  状态：3失败
								auction.setStatus(3);
								auctionService.auctionEdit(auction);//出价记录 状态：2成功
							}else {
								//auction  状态：4落败
								auction.setStatus(4);
								auctionService.auctionEdit(auction);//出价记录 状态：2成功
								//****************短信通信auctonNew****************已落败
								Consumer consumer=new Consumer();
								consumer.setId(this.apiSessionUtil.getConsumer().getId());
								consumer=consumerService.getConsumerById(consumer);
								Messager.send(consumer.getPhone(),"你的出价记录低于新的出价记录，已落败");
							}
						}

					}
					epSaleService.epsaleDelayed(auction.getNumId());
					//**************当前出价记录是处于（结束时间-轮询时间）与结束时间 之间************************************************
				/*	if(epSaleService.isLoopTime(addDate,loopTime,auction.getNumId())) //处于（结束时间-轮询时间）与结束时间 之间;则延长结束时间= 结束时间+loopTime;
					{
						//***************************则延长结束时间= 结束时间+loopTime*********************
						epSaleService.numLoopEdit(auction.getNumId(),loopTime);
					}*/
					//出价后的最近10次出价记录
					List<Map> goodsAuctionListAfter=auctionService.findAuctionListByNumIdAndGId(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
					//String goodsAuctionListStr="";
					Map goodsAuctionMap=new HashMap();
					List<Map> epSaleGoodsAuctionPriceInfo=auctionService.findAuctionSumEPSaleGoodsByNumIdAndGId(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
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
					returnResult(new Result(200, goodsAuctionMap));

				}else //当前用户保证金未支付成功    预先生成出价记录 状态：1初始     保证金记录状态：1 初始
				{
					//*******************先测
					//*******************先检测是否有auction.status=1状态的记录
					List<Map> auctionListStatus1=auctionService.findAuctionListByNumIdAndGId2(Long.valueOf(auction.getNumId()),Long.valueOf(auction.getgId()));
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
					 returnResult(res);
				}
			}
		}else
		{
			returnResult(new Result(601, "当前加价不符规则;加价应超过前一次出价"));
		}
	}

	@GetMapping("/api/epSaleAuctionOrders")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Map findAuctionOrders(){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapData= new HashMap<String, Object>();
		List<Map> auctionOrderList=auctionService.findAuctionOrderList();//竟拍订单
		if(auctionOrderList!=null&&auctionOrderList.size()>0)
		{
			mapData.put("auctionOrderList",auctionOrderList);
		}else
		{
			mapData.put("auctionOrderList","");
		}
		map.put("code", Result.OK);
		map.put("data",mapData);
		return map;
	}

	@GetMapping("/api/epSaleAuctionDeposits")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Map findAuctionDepositList(HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapData= new HashMap<String, Object>();
		//List<Map> auctionDepositList=auctionDepositService.findAuctionDepositList();//我的保证金列表
		PageInfo<Object> pm=(PageInfo<Object>)auctionDepositService.queryPageDepositList(request).getData();
		List<Object> auctionDepositList=(List<Object>)pm.getList();//我的保证金列表
		String urlImg="";//首图
		Long numId=0L;
		Long gId=0L;
		Map mapDeposit=new HashMap();
		if(auctionDepositList!=null&&auctionDepositList.size()>0)
		{
			for(Object object:auctionDepositList)
			{
				mapDeposit=(Map)object;
				numId=Long.valueOf((mapDeposit.get("numId").toString()));
				gId=Long.valueOf((mapDeposit.get("gId").toString()));
				List<Map> imgList=epSaleService.findEPSaleGoodsImgByNumIdAndGId(numId,gId);
				if(imgList!=null&&imgList.size()>0)
				{
					urlImg =SystemParam.get("domain-full")+imgList.get(0).get("gImg").toString();
					mapDeposit.put("gImg",urlImg);
				}else
				{
					mapDeposit.put("gImg","");
				}
			}
			mapData.put("auctionDepositList",pm);
		}else
		{
			mapData.put("auctionDepositList","");
		}
		map.put("code", Result.OK);
		map.put("data",mapData);
		return map;
	}

	/**
	 * 查询竟拍活动的商品的最近10条出价记录
	 * 最近出价记录10条
	 * 详情
	 * @param
	 * @return
	 */
	@GetMapping("/api/epSaleAuctions/{numId}/{gId}")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Map findAuctions(@PathVariable("numId") String numId,@PathVariable("gId") String gId){
		Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> mapData= new HashMap<String, Object>();
        int priceCount=0;//出价次数
		//最近10次出价记录
		List<Map> goodsAuctionList=auctionService.findAuctionListByNumIdAndGId(Long.valueOf(numId),Long.valueOf(gId));
		//出价次数
		List<Map> epSaleGoodsAuctionPriceInfo=auctionService.findAuctionSumEPSaleGoodsByNumIdAndGId(Long.valueOf(numId),Long.valueOf(gId));
		if(epSaleGoodsAuctionPriceInfo!=null&&epSaleGoodsAuctionPriceInfo.size()>0) {
			priceCount = NumberUtils.toInt(String.valueOf(epSaleGoodsAuctionPriceInfo.get(0).get("priceCount")));
		}
		if(goodsAuctionList!=null&&goodsAuctionList.size()>0)
		{
            mapData.put("goodsAuctionList",goodsAuctionList);
			mapData.put("priceCount",priceCount);
			mapData.put("serviceTime",java.lang.System.currentTimeMillis());;
		}else
		{
            mapData.put("goodsAuctionList","");
			mapData.put("priceCount","");
			mapData.put("serviceTime",java.lang.System.currentTimeMillis());;
		}
		//当前用户是否支付保证金
		AuctionDeposit auctionDeposit=new AuctionDeposit();
		auctionDeposit.setNumId(Long.valueOf(numId));
		auctionDeposit.setgId(Long.valueOf(gId));
		List<Map> goodsAuctionDepositList=auctionDepositService.findAuctionDepositListByNumIdAndGId(auctionDeposit);
		if(goodsAuctionDepositList!=null&&goodsAuctionDepositList.size()>0)
		{
			mapData.put("idDeposit","1");
		}else
		{
			mapData.put("idDeposit","0");
		}
		map.put("code", Result.OK);
		map.put("data",mapData);
		return map;
	}

	/**
	 * 查询竟拍活动的商品信息
	 * 详情
	 * numId与gId
	 * @param
	 * @return
	 */
	@GetMapping("/api/epSaleGoods/{numId}/{gId}")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Map findEPSaleGoods(@PathVariable("numId") String numId,@PathVariable("gId") String gId){
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> mapData= new HashMap<String, Object>();
		List<Map> goodsList=epSaleService.findEPSaleGoodsByGoodsId(Long.valueOf(numId),Long.valueOf(gId));
		if(goodsList.size()>0){
			Map<String, Object> goodsMap= goodsList.get(0);
			//String gId=String.valueOf(goodsMap.get("gId"));
			//Long numId=NumberUtils.toLong(String.valueOf(goodsMap.get("numId")));
			List<File> fileList = new ArrayList<File>();
			List<Map> imgList= new ArrayList<Map>();
			Map<String, Object> imgMap= new HashMap<String, Object>();
			//图片集
			fileList = fileService.findFilesByRefid(gId);
			//fileList = fileService.findFilesByRefid("1003545391213314048");
			if (fileList != null && fileList.size() > 0) {
				for (File file : fileList) {
					String gImgUrl=SystemParam.get("domain-full") + "get-img/goodsPics/" +gId+"/"+ file.getFileName();
					imgMap.put("seq",file.getSeq());
					imgMap.put("gImg",gImgUrl);
					imgList.add(imgMap);
				}
				goodsList.get(0).put("gImgList",imgList);
			}else
			{
				goodsList.get(0).put("gImgList","");
			}
            //当前价 出价次数
			List<Map> epSaleGoodsAuctionPriceInfo=auctionService.findAuctionSumEPSaleGoodsByNumIdAndGId(Long.valueOf(numId),Long.valueOf(gId));
 			if(epSaleGoodsAuctionPriceInfo!=null&&epSaleGoodsAuctionPriceInfo.size()>0)
			{
				goodsList.get(0).put("priceCount",NumberUtils.toInt(String.valueOf(epSaleGoodsAuctionPriceInfo.get(0).get("priceCount"))));
				goodsList.get(0).put("currentPrice",String.valueOf(epSaleGoodsAuctionPriceInfo.get(0).get("currentPrice")));
			}else
			{
				goodsList.get(0).put("priceCount",0);
				goodsList.get(0).put("currentPrice","");
			}
			//最近10次出价记录
			List<Map> goodsAuctionList=auctionService.findAuctionListByNumIdAndGId(Long.valueOf(numId),Long.valueOf(gId));
			if(goodsAuctionList!=null&&goodsAuctionList.size()>0)
			{
				goodsList.get(0).put("goodsAuctionList",goodsAuctionList);
			}else
			{
				goodsList.get(0).put("goodsAuctionList","");
			}
			AuctionDeposit auctionDeposit=new AuctionDeposit();
			auctionDeposit.setNumId(Long.valueOf(numId));
			auctionDeposit.setgId(Long.valueOf(gId));
			List<Map> goodsAuctionDepositList=auctionDepositService.findAuctionDepositListByNumIdAndGId(auctionDeposit);
			if(goodsAuctionDepositList!=null&&goodsAuctionDepositList.size()>0)
			{
				goodsList.get(0).put("idDeposit","1");
			}else
			{
				goodsList.get(0).put("idDeposit","0");
			}

			//goodsList.get(0).remove("numId");
			goodsList.get(0).put("serviceTime",java.lang.System.currentTimeMillis());
			map.put("data", goodsList.get(0));
		}else
		{
			mapData.put("gImgList","");
			mapData.put("goodsAuctionList","");
			mapData.put("idDeposit","0");
			mapData.put("priceCount",0);
			mapData.put("currentPrice","");
			mapData.put("serviceTime",java.lang.System.currentTimeMillis());
			map.put("data",mapData);
		}
		map.put("code", Result.OK);

		return map;
	}

	/*
	   通过竟拍活动id获取竟拍活动信息
	 */
	@RequestMapping("/epSale/epSale-info")
	@ResponseBody
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_QUEYR})
	public Map epSaleInfo(EPSale epSale, HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		//Object list=cityService.queryByPidList(0);
/*		deliveryAddress.setId(new Long(5000));*/
		epSale=epSaleService.finEPSaleById(epSale.getId());
		int isSale=0;//是否有上架的商品
		List<Map> isSaleList=epSaleService.findIsSaleListByEPSaleId(epSale.getId());
		if(isSaleList!=null&&isSaleList.size()>0)
		{
			isSale=1;
		}
		map.put("code", Result.OK);
		map.put("data", epSale);
		map.put("epSalePics", fileService.findFilesByRefid(epSale.getId().toString()));
		map.put("isSale", String.valueOf(isSale));
		request.setAttribute("bean", epSale);
		//request.setAttribute("province", list);
		return map;
	}

	/*
	   竟拍活动信息修改
	 */
	@PostMapping("/epSale/epSale-edit")
	@ResponseBody
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_EDIT})
	public void epSaleEdit(EPSale epSale, @RequestParam(name = "file",required = false) MultipartFile[] files, HttpServletRequest request){
            returnResult(epSaleService.epSaleEdit(epSale,request, files));
	}

	/*
       竟拍活动信息删除
    */
	@RequestMapping("/epSale/epSale-delete")
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_DELETE})
	public void epSaleDelete(EPSale epSale){
		returnResult(epSaleService.epSaleDelete(epSale));
	}
}
