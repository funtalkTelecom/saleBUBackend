package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.Messager;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Auction;
import com.hrtx.web.pojo.AuctionDeposit;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api")
public class BoundController extends BaseReturn{

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

	@PostMapping("/api/epSaleGoodsAuciton")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void goodsAuciton(Auction auction, HttpServletRequest request) {
		Goods goods=goodsService.findGoodsById(auction.getgId());//上架商品信息
		double startPrice=0L;
		Map numMap=epSaleService.findEPSaleNumInfoByNumId(auction.getNumId());
		if(numMap!=null&&numMap.size()>0)
		{
			startPrice=Double.valueOf(numMap.get("skuTobPrice").toString());
		}
		//最近10次数出价记录
		List<Map> goodsAuctionList=auctionService.findAuctionListByNumId(Long.valueOf(auction.getNumId()));
		double priceUp=Double.valueOf(goods.getgPriceUp());//每次加价
		int loopTime=Integer.valueOf(goods.getgLoopTime());//轮咨时间分钟
		double deposit=Double.valueOf(goods.getgDeposit());//保证金
		double beforePrice=0.00;//前一次出价记录
		double subPrice=0.00;//当前出价与前一次出价相差
		Long  autionId=0L;//前一次出价记录Id
		Long consumerId=0L;//前一次出价记录用户Id
		boolean isDeposit=false;//是否支付保证金
		Long auctionDepositId=0L;//保证金Id
		if(goodsAuctionList.size()>0)//最近10次数出价记录
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
			if(subPrice%priceUp>0) {
				returnResult(new Result(602, "当前加价不符规则;加价应按" + priceUp + "的倍数进行加价"));
			}else
			{
				List<Map> auctionDepositConsumerList=auctionDepositService.findAuctionDepositListConsumerByNumId(auction.getNumId());//当前用户保证金已支付成功 状态：2成功
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
				}else
				{
					AuctionDeposit auctionDeposit=new AuctionDeposit();
					auctionDeposit.setStatus(1);
					auctionDeposit.setNum(auction.getNum());
					auctionDeposit.setNumId(auction.getNumId());
					auctionDeposit.setSkuId(auction.getSkuId());
					auctionDeposit.setAmt(Double.valueOf(goods.getgDeposit()));//保证金记录  状态：1初始
					auctionDepositService.auctionDepositEdit(auctionDeposit);
					auctionDepositId=auctionDeposit.getId();
					isDeposit=false;
				}
				if(isDeposit)//当前用户保证金已支付成功 状态：2成功
				{
					//yyyymmddhhiiss 可以用来测试auctionDepositPay接口
					//auctionDepositService.auctionDepositPay(auctionDepositId,true,"20180620112023");
					Date addDate=new Date();
					auction.setStatus(2);
					auction.setAddDate(addDate);
					auction.setConfirmDate(addDate);//status 2 确认时间
					auctionService.auctionEdit(auction);//出价记录 状态：2成功
					//**************当前出价记录是处于（结束时间-轮询时间）与结束时间 之间************************************************
					if(epSaleService.isLoopTime(addDate,loopTime,auction.getNumId())) //处于（结束时间-轮询时间）与结束时间 之间;则延长结束时间= 结束时间+loopTime;
					{
						//***************************则延长结束时间= 结束时间+loopTime*********************
						epSaleService.numLoopEdit(auction.getNumId(),loopTime);
					}
					//***************************前一次出价记录******状态4 落败******并通知*****************
					if(autionId>0)
					{
						Auction auctonBef=new Auction();
						auctonBef.setId(autionId);
						auctonBef.setStatus(4);//前一次出价记录   状态：4 落败
						auctionService.auctionEditStatusById2(auctonBef);//通知用户
						Consumer consumer=new Consumer();
						consumer.setId(consumerId);
						consumer=consumerService.getConsumerById(consumer);
						//最近10次出价记录
						List<Map> goodsAuctionList2=auctionService.findAuctionListByNumId(Long.valueOf(auction.getNumId()));
						String goodsAuctionListStr="";
						Map goodsAuctionMap=new HashMap();
						if(goodsAuctionList!=null&&goodsAuctionList.size()>0)
						{
							goodsAuctionMap.put("goodsAuctionList",goodsAuctionList);
							goodsAuctionListStr="goodsAuctionList:"+goodsAuctionList;
						}else
						{
							goodsAuctionMap.put("goodsAuctionList","");
							goodsAuctionListStr="goodsAuctionList:"+"";
						}

						returnResult(new Result(200, goodsAuctionMap));

						Messager.send(consumer.getPhone(),"你的出价记录低于新的出价记录，已落败");
					}
				}else //当前用户保证金未支付成功 status:2    出价记录 状态：1初始   保证金记录  状态：1 初始
				{
					auction.setStatus(1);
					auctionService.auctionEdit(auction);//出价记录 状态：1初始
					String orderNameStr="商品"+auction.getgName()+",保证金支付,额度:"+deposit;
					Result res=fundOrderService.payPinganWxxDeposit(com.hrtx.global.Utils.doubleToInt(deposit),orderNameStr,auctionDepositId.toString());
					//returnResult(new Result(600, "保证金未支付"+res.getData().toString()));
					 returnResult(res);
				}
			}
		}else
		{
			returnResult(new Result(601, "当前加价不符规则;加价应超过前一次出价"));
		}
	}

}