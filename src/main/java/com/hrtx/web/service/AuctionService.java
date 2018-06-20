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
	public List<Map> findAuctionSumEPSaleGoodsByNumId(Long numId) {
		return auctionMapper.findAuctionSumEPSaleGoodsByNumId(numId);
	}

	public List<Map> findAuctionListByNumId(Long numId) {
		return auctionMapper.findAuctionListByNumId(numId);
	}

	public List<Map> findAuctionGoodsByNumId(Long numId) {
		return auctionMapper.findAuctionGoodsByNumId(numId);
	}

	public void auctionEdit(Auction auction) {
			List<Auction> list = new ArrayList<Auction>();
			auction.setConsumerId(apiSessionUtil.getConsumer().getId());
			auction.setId(auction.getGeneralId());
		    auction.setAddDate(new Date());
			list.add(auction);
		   auctionMapper.insertBatch(list);
	}

	public void auctionEditStatusById(Auction auction) {
		auctionMapper.auctionEditStatusById(auction.getStatus(),auction.getId());
	}
}
