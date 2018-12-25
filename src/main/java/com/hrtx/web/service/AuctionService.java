package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.controller.BaseReturn;
import com.hrtx.web.mapper.AuctionMapper;
import com.hrtx.web.pojo.Auction;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AuctionService {

	@Autowired private AuctionMapper auctionMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;

	public List<Map> findAuctionSumEPSaleGoodsByNumId(Integer numId) {
		return auctionMapper.findAuctionSumEPSaleGoodsByNumId(numId);
	}

	public List<Map> findAuctionSumEPSaleGoodsByNumIdAndGId(Integer numId,Integer gId) {
		return auctionMapper.findAuctionSumEPSaleGoodsByNumIdAndGId(numId,gId);
	}

	public List<Map> findAuctionSumEPSaleGoodsByGId(Integer gId) {
		return auctionMapper.findAuctionSumEPSaleGoodsByGId(gId);
	}

	public List<Map> findAuctionListByNumId(Integer numId) {
		return auctionMapper.findAuctionListByNumId(numId);
	}

	/*
	  status:1状态记录
	 */
    public List<Map> findAuctionListByNumIdAndGId2(Integer numId,Integer gId)
	{
		return auctionMapper.findAuctionListByNumIdAndConsumerIdAndGId(numId,apiSessionUtil.getConsumer().getId(),gId);
	}

	/*
	  status:1状态记录
	 */
	public List<Map> findAuctionListByGId2(Integer gId)
	{
		return auctionMapper.findAuctionListByConsumerIdAndGId(apiSessionUtil.getConsumer().getId(),gId);
	}

	public List<Map> findAuctionListByNumIdAndGId(Integer numId,Integer gId) {
        return auctionMapper.findAuctionListByNumIdAndGId(numId,gId);
    }

	public List<Map> findAuctionListByGId(Integer gId) {
		return auctionMapper.findAuctionListByGId(gId);
	}

	public List<Map> findAuctionListByNumIdAndGId3(Integer numId,Integer gId) {
		return auctionMapper.findAuctionListByNumIdAndGId2(numId,gId);
	}

	public List<Map> findAuctionListByGId3(Integer gId) {
		return auctionMapper.findAuctionListByGId2(gId);
	}

	public List<Map> findAuctionGoodsByNumId(Integer numId) {
		return auctionMapper.findAuctionGoodsByNumId(numId);
	}

	public List<Map> findAuctionOrderList() {
		Auction auction=new Auction();
		auction.setConsumerId(apiSessionUtil.getConsumer().getId());
		return auctionMapper.findAuctionOrderListByConsumerId(auction);
	}

	public void auctionEdit(Auction auction) {
			List<Auction> list = new ArrayList<Auction>();
			auction.setConsumerId(apiSessionUtil.getConsumer().getId());
		    //auction.setId(auctionMapper.getId());
		    //auction.setAddDate(new Date());
			list.add(auction);
		   auctionMapper.insertBatch(list);
	}

	public void auctionEditStatusById(Auction auction) {
		auctionMapper.auctionEditStatusById(auction);
	}

	public void auctionEditStatusById2(Auction auction) {
		auctionMapper.auctionEditStatusById2(auction);
	}

	public List<Map> findCustomersByNumIdAndGId(Integer numId,Integer gId) {
		return auctionMapper.findCustomersByNumIdAndGId(numId,gId);
	}
}
