package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.Messager;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.*;
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
	private BoundService boundService;
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

	@PostMapping("/api/boundNumIccid")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void boundNumIccid(Num num, HttpServletRequest request) {

		Long numId=0L;
		Long iccidId=0L;
		
	}

}
