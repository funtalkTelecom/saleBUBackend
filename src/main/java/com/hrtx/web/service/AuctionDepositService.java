package com.hrtx.web.service;

import com.hrtx.global.ApiSessionUtil;
import com.hrtx.web.mapper.AuctionDepositMapper;
import com.hrtx.web.pojo.AuctionDeposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AuctionDepositService {

	@Autowired private AuctionDepositMapper auctionDepositMapper;
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

	public List<Map> findAuctionDepositListConsumerByNumId(Long numId,int status) {
		return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndStatus(numId,apiSessionUtil.getConsumer().getId(),status);
	}
}
