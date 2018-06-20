package com.hrtx.web.service;

import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.AuctionDepositMapper;
import com.hrtx.web.mapper.AuctionMapper;
import com.hrtx.web.pojo.AuctionDeposit;
import com.hrtx.web.pojo.Auction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AuctionDepositService {

	@Autowired private AuctionDepositMapper auctionDepositMapper;
	@Autowired private AuctionMapper auctionMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	public List<Map> findAuctionDepositSumEPSaleGoodsByNumId(Long numId) {
		return auctionDepositMapper.findAuctionDepositSumEPSaleGoodsByNumId(numId);
	}

	public List<Map> findAuctionDepositListByNumId(Long numId) {
		return auctionDepositMapper.findAuctionDepositListByNumId(numId);
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

	/*
	  保证金支付
	  status true 成功 false失败
	  若true成功，对应出价记录status:1 状态调整
	 */
	public void auctionDepositPay(Long Id,boolean status,String payDate) {
		AuctionDeposit auctionDeposit=new AuctionDeposit();
		List<Map> auctionDepositList=auctionDepositMapper.findAuctionDepositById(Id);
				//auctionDepositMapper.findAuctionDepositListById(Id);
		Long consumerId=0L;
		Long numId=0L;
		if(auctionDepositList.size()>0)
		{
			consumerId=Long.valueOf(auctionDepositList.get(0).get("consumer_id").toString());
			numId=Long.valueOf(auctionDepositList.get(0).get("num_id").toString());
		}
		auctionDeposit.setConsumerId(consumerId);
		auctionDeposit.setNumId(numId);
		auctionDeposit.setId(Id);
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
			Long autionId=0L;
			double price=0.00;//出价价格
			//auction.status=1记录状态调整
			List<Map> auctionList =auctionMapper.findAuctionListByNumIdAndConsumerId(auctionDeposit.getNumId(),auctionDeposit.getConsumerId());
			if(auctionList.size()>0)
			{
				Auction auction=new Auction();
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
				}
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
