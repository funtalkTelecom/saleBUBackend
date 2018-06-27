package com.hrtx.web.service;

import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.Messager;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.AuctionDepositMapper;
import com.hrtx.web.mapper.AuctionMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.pojo.Auction;
import com.hrtx.web.pojo.AuctionDeposit;
import com.hrtx.web.pojo.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

@Service
public class AuctionDepositService {

	@Autowired private AuctionDepositMapper auctionDepositMapper;
	@Autowired private AuctionMapper auctionMapper;
	@Autowired private ConsumerMapper consumerMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	public List<Map> findAuctionDepositSumEPSaleGoodsByNumId(Long numId) {
		return auctionDepositMapper.findAuctionDepositSumEPSaleGoodsByNumId(numId);
	}

	public List<Map> findAuctionDepositListByNumId(Long numId) {
		return auctionDepositMapper.findAuctionDepositListByNumId(numId);
	}

	/*
	  获取当前用户已支付保证金记录
	 */
    public List<Map> findAuctionDepositListByNumId(AuctionDeposit auctionDeposit){
        auctionDeposit.setStatus(2);
        auctionDeposit.setConsumerId(apiSessionUtil.getConsumer().getId());
        return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndStatus(auctionDeposit);
    }

	/*
      获取当前用户已支付保证金记录
     */
	public List<Map> findAuctionDepositListByNumIdAndGId(AuctionDeposit auctionDeposit){
		auctionDeposit.setStatus(2);
		auctionDeposit.setConsumerId(apiSessionUtil.getConsumer().getId());
		//return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndStatus(auctionDeposit);
		return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndStatusAndGId(auctionDeposit);
	}

	/*
	   通过OrderId获取保证金记录
	 */
	public Map  findAuctionDepositListByOrderId(Long orderId)
	{
		Map map=new HashMap();
	    List<Map> auctionList=auctionMapper.findAuctionListByOrderId(orderId);
	    Long numId=0L;//numId;
		Long gId=0L;//gId
		double deposit=0.00;//保证金
	    if(auctionList.size()>0)
		{
			numId=Long.valueOf(auctionList.get(0).get("numId").toString());
			gId=Long.valueOf(auctionList.get(0).get("gId").toString());
			AuctionDeposit auctionDeposit=new AuctionDeposit();
			auctionDeposit.setStatus(2);
			auctionDeposit.setgId(gId);
			auctionDeposit.setNumId(numId);
			auctionDeposit.setConsumerId(apiSessionUtil.getConsumer().getId());
			//return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndStatus(auctionDeposit);
			List<Map> auctionDepositList=auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndStatusAndGId(auctionDeposit);
			if(auctionDepositList.size()>0)
			{
				deposit=Double.valueOf(auctionDepositList.get(0).get("price").toString());
			}
		}
		map.put("deposit",deposit);
		return   map;
	}

	public void auctionDepositEdit(AuctionDeposit auctionDeposit) {
		List<AuctionDeposit> list = new ArrayList<AuctionDeposit>();
		auctionDeposit.setConsumerId(apiSessionUtil.getConsumer().getId());
		//auctionDeposit.setAddIp(apiSessionUtil.get);
		auctionDeposit.setId(auctionDeposit.getGeneralId());
		auctionDeposit.setAddDate(new Date());
		list.add(auctionDeposit);
		auctionDepositMapper.insertBatch(list);
	}

	public List<Map> findAuctionDepositListConsumerByNumId(Long numId) {
		return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerId(numId,apiSessionUtil.getConsumer().getId());
	}

	public List<Map> findAuctionDepositListConsumerByNumIdAndGId(Long numId,Long gId) {
		return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndGId(numId,apiSessionUtil.getConsumer().getId(),gId);
	}

	/*
	  保证金支付
	  可供支付接口调用操作
	  ****status true 成功 false失败
	  若true成功
	   1、保证金支付成功状态status:1 调整为status:2
	   2、若有对应出价记录（status:1 状态）
	     1）、有大于本次出价的价格的新记录，则本次出价记录  status:1 调整status:4 落败
	          注：新记录 status in(2,4)
	     2）、有与本次出价的价格相同的新记录，则本次出价记录  status:1 调整status:3 落败
	          注：新记录 status in(2,4)
	     3）、本次出价出价状态status:1 调整status:2 成功
	 */
	public void newAuctionDepositPay(Long Id,boolean status,String payDate) {
		AuctionDeposit auctionDeposit=new AuctionDeposit();
		List<Map> auctionDepositList=auctionDepositMapper.findAuctionDepositById(Id);
				//auctionDepositMapper.findAuctionDepositListById(Id);
		Long consumerId=0L;
		Long numId=0L;
		Long gId=0L;
		if(auctionDepositList.size()>0)
		{
			consumerId=Long.valueOf(auctionDepositList.get(0).get("consumer_id").toString());
			numId=Long.valueOf(auctionDepositList.get(0).get("num_id").toString());
			gId=Long.valueOf(auctionDepositList.get(0).get("g_id").toString());
		}
		auctionDeposit.setConsumerId(consumerId);
		auctionDeposit.setNumId(numId);
		auctionDeposit.setId(Id);
		auctionDeposit.setgId(gId);
		if(status)
		{
            Date payDate1=new Date();
            try {
             payDate1=Utils.stringToDate(payDate,"yyyyMMddHHmmss");
            } catch (ParseException e) {
                e.printStackTrace();
            }
			auctionDeposit.setStatus(2);
			auctionDeposit.setPayDate(payDate1);
            //**********************************同步
            //AuctionDeposit.setPaySnn(paySnn);
		}else
		{
			auctionDeposit.setStatus(1);
		}
		auctionDepositMapper.auctionDepositSatusEdit(auctionDeposit);
		if(status)
		{
			//****************保证金支付成功************************
			Boolean isUpdateStatus=false;

			double price=0.00;//出价价格
			double beforePrice=0.00;//前一次出价记录
			Long autionId=0L;
			Long  beforeAutionId=0L;//前一次出价记录Id
			Long beforeConsumerId=0L;//前一次出价记录用户Id
			//auction.status=1记录状态调整
			Auction auction=new Auction();
			Auction auctonBef=new Auction();
            //auction.status=1记录状态的记录
			List<Map> auctionList =auctionMapper.findAuctionListByNumIdAndConsumerIdAndGId(auctionDeposit.getNumId(),auctionDeposit.getConsumerId(),auctionDeposit.getgId());
			if(auctionList.size()>0)
			{
				autionId=Long.valueOf(auctionList.get(0).get("id").toString());
				price=Double.valueOf(auctionList.get(0).get("price").toString());
				auction.setConfirmDate(auctionDeposit.getPayDate());
				auction.setId(autionId);
				//最近10次数出价记录
				List<Map> goodsAuctionList=auctionMapper.findAuctionListByNumIdAndGId(auctionDeposit.getNumId(),auctionDeposit.getgId());
                if(goodsAuctionList.size()>0)
				{
					beforePrice=Double.valueOf(goodsAuctionList.get(0).get("price").toString());//前一次出价记录
					beforeAutionId=Long.valueOf(goodsAuctionList.get(0).get("id").toString());//前一次出价记录Id
					beforeConsumerId=Long.valueOf(goodsAuctionList.get(0).get("consumerId").toString());//前一次出价记录用户Id
					//1、大于之前的最近出价记录，则前一次出价记录状态：4 落败,当前出价记录状态：2成功
					if(price>beforePrice)
					{
						//auctonBef  状态：4 落败
						auctonBef.setId(beforeAutionId);
						auctonBef.setStatus(4);//前一次出价记录   状态：4 落败
						auctionMapper.auctionEditStatusById2(auctonBef);//通知用户
						Consumer beforeConsumer=new Consumer();//前一次出价记录用户
						beforeConsumer.setId(beforeConsumerId);
						beforeConsumer = consumerMapper.selectOne(beforeConsumer);
						Messager.send(beforeConsumer.getPhone(),"你的出价记录低于新的出价记录，已落败");
						//auction  状态：2成功
						auction.setStatus(2);
						auctionMapper.auctionEditStatusById(auction);
					}
					//2、出现同价的成功出价记录，则当前出价记录状态：3失败
					else if(price==beforePrice)//出现同价的成功出价记录
					{
						//auction  状态：3失败
						auction.setStatus(3);
						auctionMapper.auctionEditStatusById(auction);
					}
					//3、低于之前的最近出价记录,则当前出价记录状态：4 落败
					else
					{
						//auction  状态：4落败
						auction.setStatus(4);
						auctionMapper.auctionEditStatusById(auction);
						Consumer consumer=new Consumer();//当前出价记录用户
						consumer.setId(beforeConsumerId);
						consumer = consumerMapper.selectOne(consumer);
						Messager.send(consumer.getPhone(),"你的出价记录低于新的出价记录，已落败");
					}
				}else
				{
					//4、当前出价记录为第一次出价记录 状态：2成功
					auction.setStatus(2);
					auctionMapper.auctionEditStatusById(auction);
				}

				/*Auction auction=new Auction();
				autionId=Long.valueOf(auctionList.get(0).get("id").toString());
				price=Double.valueOf(auctionList.get(0).get("price").toString());
				auction.setConfirmDate(auctionDeposit.getPayDate());
				auction.setId(autionId);
				//1、若有新的相同的出价记录 status=2,4，该出价记录autionId状态为status3失败
				List<Map> auctionListPrice=auctionMapper.findAuctionListByNumIdAndPrice(auctionDeposit.getNumId(),price);
                if(auctionListPrice.size()>0)
				{
					auction.setStatus(3);
					auctionMapper.auctionEditStatusById(auction);
					isUpdateStatus=true;
				}
				//2、若有新的更高的出价记录 status=2,4，该出价记录autionId状态为status 4 落败
				List<Map> auctionListPrice2=auctionMapper.findAuctionListByNumIdAndPrice2(auctionDeposit.getNumId(),price);
                if(auctionListPrice2.size()>0)
				{
					auction.setStatus(4);
					auctionMapper.auctionEditStatusById(auction);
					isUpdateStatus=true;
				}
				//该出价记录autionId状态为status 2 成功
				if(!isUpdateStatus)
				{
					auction.setStatus(2);
					auctionMapper.auctionEditStatusById(auction);
				}*/
			}
		}
	}

	/*
	  保证金退款
	  status true 成功 false失败
	 */
	public void auctionDepositRefund(Long Id,boolean status) {
		AuctionDeposit AuctionDeposit=new AuctionDeposit();
		AuctionDeposit.setId(Id);
		if(status)
		{
			AuctionDeposit.setStatus(3);
		}else
		{
			AuctionDeposit.setStatus(1);
		}
		auctionDepositMapper.auctionDepositSatusEdit(AuctionDeposit);
	}
}
