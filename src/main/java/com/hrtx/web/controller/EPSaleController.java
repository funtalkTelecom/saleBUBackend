package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.FileMapper;
import com.hrtx.web.pojo.Auction;
import com.hrtx.web.pojo.AuctionDeposit;
import com.hrtx.web.pojo.EPSale;
import com.hrtx.web.pojo.File;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.service.*;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
	 * 查询未过期的竟拍活动
	 * @param epSale
	 * @return
	 */
	@GetMapping("/api/epSales")
	@Powers({PowerConsts.NOLOGINPOWER})
	@ResponseBody
	public Result findEPSaleList(EPSale epSale){
		return epSaleService.findEPSaleList();
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
		List<Map> goodsList=epSaleService.findEPSaleGoodsListByEPSaleId(Long.valueOf(ePSaleId));
		epSaleMap.get(0).put("goodsList",goodsList);
		map.put("code", Result.OK);
		map.put("data", epSaleMap);
		return map;
	}

	@PostMapping("/api/epSaleGoodsAuciton")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void goodsAuciton(Auction auction, HttpServletRequest request) {
		Goods goods=goodsService.findGoodsById(auction.getgId());//上架商品信息
		//最近10次数出价记录
		List<Map> goodsAuctionList=auctionService.findAuctionListByNumId(Long.valueOf(auction.getNumId()));
		double priceUp=Double.valueOf(goods.getgPriceUp());//每次加价
		int loopTime=Integer.valueOf(goods.getgLoopTime());//轮咨时间分钟
		double beforePrice=0.00;//前一次出价记录
		double subPrice=0.00;//当前出价与前一次出价相差
		Long  autionId=0L;//前一次出价记录Id
		boolean isDeposit=false;//是否支付保证金
		if(goodsAuctionList.size()>0)
		{
			 beforePrice=Double.valueOf(goodsAuctionList.get(0).get("price").toString());//前一次出价记录
			 autionId=Long.valueOf(goodsAuctionList.get(0).get("id").toString());//前一次出价记录Id
			 subPrice=auction.getPrice()-beforePrice;
		}else
		{
			 subPrice=auction.getPrice();
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
					if(auctionDepositConsumerList.get(0).get("status").toString().equals("2"))
					{
						isDeposit=true;
					}else
					{
						isDeposit=false;
					}

				}else
				{
					isDeposit=false;
				}
				if(isDeposit)//当前用户保证金已支付成功 状态：2成功
				{
					auction.setStatus(2);
					auction.setAddDate(new Date());
					auctionService.auctionEdit(auction);//出价记录 状态：2成功
					if(autionId>0)
					{
						Auction auctonBef=new Auction();
						auctonBef.setId(autionId);
						auctonBef.setStatus(4);//前一次出价记录   状态：4 落败
						auctionService.auctionEditStatusById(auctonBef);//通知用户
					}
				}else //当前用户保证金未支付成功 status:2    出价记录 状态：1初始   保证金记录  状态：1 初始
				{
					auction.setStatus(1);
					auctionService.auctionEdit(auction);//出价记录 状态：1初始
					AuctionDeposit auctionDeposit=new AuctionDeposit();
					auctionDeposit.setStatus(1);
					auctionDeposit.setNum(auction.getNum());
					auctionDeposit.setNumId(auction.getNumId());
					auctionDeposit.setSkuId(auction.getSkuId());
					auctionDeposit.setAmt(Double.valueOf(goods.getgDeposit()));//保证金记录  状态：1初始
					auctionDepositService.auctionDepositEdit(auctionDeposit);
					returnResult(new Result(600, "保证金未支付"));
				}
			}
		}else
		{
			returnResult(new Result(601, "当前加价不符规则;加价应超过前一次出价"));
		}
	}

	/**
	 * 查询竟拍活动的商品信息
	 * 详情
	 * @param
	 * @return
	 */
	@GetMapping("/api/epSaleGoods/{goodsId}")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Map findEPSaleGoods(@PathVariable("goodsId") String goodsId){
		Map<String, Object> map = new HashMap<String, Object>();
		List<Map> goodsList=epSaleService.findEPSaleGoodsByGoodsId(Long.valueOf(goodsId));
		if(goodsList.size()>0){
			Map<String, Object> goodsMap= goodsList.get(0);
			String gId=String.valueOf(goodsMap.get("gId"));
			Long numId=NumberUtils.toLong(String.valueOf(goodsMap.get("numId")));
			List<File> fileList = new ArrayList<File>();
			List<Map> imgList= new ArrayList<Map>();
			Map<String, Object> imgMap= new HashMap<String, Object>();
			//图片集
			fileList = fileService.findFilesByRefid(gId);
			//fileList = fileService.findFilesByRefid("1003545391213314048");
			if (fileList != null && fileList.size() > 0) {
				for (File file : fileList) {
					String gImgUrl=SystemParam.get("domain-full") + "/get-img"+SystemParam.get("goodsPics") +goodsId+"/"+ file.getFileName();
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
			List<Map> epSaleGoodsAuctionPriceInfo=auctionService.findAuctionSumEPSaleGoodsByNumId(Long.valueOf(numId));
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
			List<Map> goodsAuctionList=auctionService.findAuctionListByNumId(Long.valueOf(numId));
			if(goodsAuctionList!=null&&goodsAuctionList.size()>0)
			{
				goodsList.get(0).put("goodsAuctionList",goodsAuctionList);
			}else
			{
				goodsList.get(0).put("goodsAuctionList","");
			}

			List<Map> goodsAuctionDepositList=auctionDepositService.findAuctionDepositListByNumId(Long.valueOf(numId));
			if(goodsAuctionDepositList!=null&&goodsAuctionDepositList.size()>0)
			{
				goodsList.get(0).put("idDeposit","1");
			}else
			{
				goodsList.get(0).put("idDeposit","0");
			}

			//goodsList.get(0).remove("numId");
		}else
		{
			goodsList.get(0).put("gImgList","");
			goodsList.get(0).put("goodsAuctionList","");
			goodsList.get(0).put("idDeposit","0");
			goodsList.get(0).put("priceCount",0);
			goodsList.get(0).put("currentPrice","");
		}
		map.put("code", Result.OK);
		map.put("data", goodsList.get(0));
		return map;
	}

	@RequestMapping("/epSale/epSale-info")
	@ResponseBody
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_QUEYR})
	public Map epSaleInfo(EPSale epSale, HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		//Object list=cityService.queryByPidList(0);
/*		deliveryAddress.setId(new Long(5000));*/
		epSale=epSaleService.finEPSaleById(epSale.getId());
		map.put("code", Result.OK);
		map.put("data", epSale);
		map.put("epSalePics", fileService.findFilesByRefid(epSale.getId().toString()));
		request.setAttribute("bean", epSale);
		//request.setAttribute("province", list);
		return map;
	}

	@PostMapping("/epSale/epSale-edit")
	@ResponseBody
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_EDIT})
	public void epSaleEdit(EPSale epSale, @RequestParam(name = "file",required = false) MultipartFile[] files, HttpServletRequest request){
            returnResult(epSaleService.epSaleEdit(epSale,request, files));
	}

	@RequestMapping("/epSale/epSale-delete")
	@Powers({PowerConsts.EPSALEMOUDULE_COMMON_DELETE})
	public void epSaleDelete(EPSale epSale){
		returnResult(epSaleService.epSaleDelete(epSale));
	}
}
