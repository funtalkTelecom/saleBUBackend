package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.Messager;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.AuctionDepositMapper;
import com.hrtx.web.mapper.AuctionMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.pojo.Auction;
import com.hrtx.web.pojo.AuctionDeposit;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.websocket.WebSocketServer;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hrtx.web.mapper.EPSaleNoticeMapper;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

@Service
public class AuctionDepositService {
	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private AuctionService auctionService;
	@Autowired private EPSaleService epSaleService;
	@Autowired private GoodsService goodsService;
	@Autowired private AuctionDepositMapper auctionDepositMapper;
	@Autowired private AuctionMapper auctionMapper;
	@Autowired private ConsumerMapper consumerMapper;
	@Autowired private EPSaleNoticeMapper ePSaleNoticeMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
	public List<Map> findAuctionDepositSumEPSaleGoodsByNumId(Integer numId) {
		return auctionDepositMapper.findAuctionDepositSumEPSaleGoodsByNumId(numId);
	}

	public List<Map> findAuctionDepositListByNumId(Integer numId) {
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
      获取当前用户已支付保证金记录
     */
	public List<Map> findAuctionDepositListByGId(AuctionDeposit auctionDeposit){
		auctionDeposit.setStatus(2);
		auctionDeposit.setConsumerId(apiSessionUtil.getConsumer().getId());
		//return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndStatus(auctionDeposit);
		return auctionDepositMapper.findAuctionDepositListByConsumerIdAndStatusAndGId(auctionDeposit);
	}

	/*
	   通过OrderId获取保证金记录
	 */
	public Map  findAuctionDepositListByOrderId(Integer orderId)
	{
		Map map=new HashMap();
	    List<Map> auctionList=auctionMapper.findAuctionListByOrderId(orderId);
		List<Map> auctionDepositList=null;
		Integer numId=0;//numId;
		Integer gId=0;//gId
		double deposit=0.00;//保证金
	    if(auctionList.size()>0)
		{
			numId=NumberUtils.toInt(auctionList.get(0).get("numId").toString());
			gId=NumberUtils.toInt(auctionList.get(0).get("gId").toString());
			AuctionDeposit auctionDeposit=new AuctionDeposit();
			auctionDeposit.setStatus(2);
			auctionDeposit.setgId(gId);
			auctionDeposit.setNumId(numId);
            if(apiSessionUtil.getConsumer()==null)//非api调用,则不关联ConsumerId的记录
			{
				auctionDepositList=auctionDepositMapper.findAuctionDepositListByNumIdAndStatusAndGId(auctionDeposit);
			}else
			{
				auctionDeposit.setConsumerId(apiSessionUtil.getConsumer().getId());
				auctionDepositList=auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndStatusAndGId(auctionDeposit);
			}
			if(auctionDepositList!=null&&auctionDepositList.size()>0)
			{
				deposit=Double.valueOf(auctionDepositList.get(0).get("amt").toString());
			}
		}
		map.put("deposit",deposit);
		return   map;
	}

	/*
	  通过NumId、StatusA、GId的返回对应的保证金列表
	 */
	public List<Map>  findAuctionDepositListByNumIdAndStatusAndGId(AuctionDeposit auctionDeposit)
	{
		List<Map> auctionDepositList=auctionDepositMapper.findAuctionDepositListByNumIdAndStatusAndGId(auctionDeposit);
		return auctionDepositList;
	}

	/*
	  返回当前用户保证金列表
	 */
	public List<Map>  findAuctionDepositList()
	{
		return  auctionDepositMapper.findAuctionDepositListByConsumerId(apiSessionUtil.getConsumer().getId());
	}

	/*
	  返回当前用户保证金列表
	 */
	public Result  queryPageDepositList( HttpServletRequest request)
	{
		AuctionDeposit auctionDeposit=new AuctionDeposit();
		PageInfo<Object> pm = null;
		Result result = null;
		try {
			int pageNum=request.getParameter("pageNum") == null ? 1 : Integer.parseInt(request.getParameter("pageNum"));
			int limit=request.getParameter("limit") == null ? 15 : Integer.parseInt(request.getParameter("limit"));
			auctionDeposit.setPageNum(pageNum);
			//auctionDeposit.setStart(request.getParameter("start") == null ? 0 : Integer.parseInt(request.getParameter("start")));
			auctionDeposit.setLimit(limit);
			auctionDeposit.setStart(limit*(pageNum-1));
			//auctionDeposit.setLimit(auctionDeposit.getLimit());
			PageHelper.startPage(pageNum,limit);
			//Page<Object> ob=this.auctionDepositMapper.queryPageDepositListByConsumerId(auctionDeposit,apiSessionUtil.getConsumer().getId());
			Page<Object> ob=this.auctionDepositMapper.queryPageDepositListByConsumerId2(auctionDeposit,apiSessionUtil.getConsumer().getId());
			pm = new PageInfo<Object>(ob);
			result = new Result(Result.OK, pm);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			result = new Result(Result.ERROR, pm);
		}
		return result;
	}

	public void auctionDepositEdit(AuctionDeposit auctionDeposit) {
		List<AuctionDeposit> list = new ArrayList<AuctionDeposit>();
		auctionDeposit.setConsumerId(apiSessionUtil.getConsumer().getId());
		//auctionDeposit.setAddIp(apiSessionUtil.get);
		//auctionDeposit.setId(auctionDepositMapper.getId());
		auctionDeposit.setAddDate(new Date());
		list.add(auctionDeposit);
		auctionDepositMapper.insertBatch(list);
	}

	public List<Map> findAuctionDepositListConsumerByNumId(Integer numId) {
		return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerId(numId,apiSessionUtil.getConsumer().getId());
	}

	public List<Map> findAuctionDepositListConsumerByNumIdAndGId(Integer numId,Integer gId) {
		return auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndGId(numId,apiSessionUtil.getConsumer().getId(),gId);
	}

	public List<Map> findAuctionDepositListConsumerByGId(Integer gId) {
		return auctionDepositMapper.findAuctionDepositListByConsumerIdAndGId(apiSessionUtil.getConsumer().getId(),gId);
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
	public void newAuctionDepositPay(Integer Id,boolean status,String payDate) {

		AuctionDeposit auctionDeposit=new AuctionDeposit();
		List<Map> auctionDepositList=auctionDepositMapper.findAuctionDepositById(Id);
				//auctionDepositMapper.findAuctionDepositListById(Id);
		Integer consumerId=0;
		Integer numId=0;
		Integer gId=0;
		Integer epSaleId=0;//竞拍活动Id
		Integer erIsPack=0;//商品是否打包
		int loopTime=0;//轮咨时间分钟
		if(auctionDepositList.size()>0)
		{
			consumerId=NumberUtils.toInt(auctionDepositList.get(0).get("consumer_id").toString());
			numId=NumberUtils.toInt(auctionDepositList.get(0).get("num_id").toString());
			gId=NumberUtils.toInt(auctionDepositList.get(0).get("g_id").toString());
			Goods goods=goodsService.findGoodsById(gId);//上架商品信息
			loopTime=Integer.valueOf(goods.getgLoopTime());
			epSaleId=goods.getgActive();
			erIsPack=NumberUtils.toInt(goods.getgIsPack());
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
			//****************auction.status=1记录状态的记录状态调整************************
			double price=0.00;//出价价格
			double beforePrice=0.00;//前一次出价记录
			Integer autionId=0;
			Integer  beforeAutionId=0;//前一次出价记录Id
			Integer beforeConsumerId=0;//前一次出价记录用户Id
			String ePSaleNoticePhone="";//短信通知手机号
			//auction.status=1记录状态调整
			Auction auction=new Auction();
			Auction auctonBef=new Auction();
			//***********************************
			//***************************加锁记录
			//***********************************
			auctionMapper.freezeOneNum(numId);
			//auction.status=1记录状态的记录
			List<Map> auctionList=new ArrayList<Map>();
			if(erIsPack==0)//商品是否打包 erIsPack
			{
				auctionList =auctionMapper.findAuctionListByNumIdAndConsumerIdAndGId(auctionDeposit.getNumId(),auctionDeposit.getConsumerId(),auctionDeposit.getgId());
			}else if(erIsPack==1)
			{
				auctionList =auctionMapper.findAuctionListByConsumerIdAndGId(auctionDeposit.getConsumerId(),auctionDeposit.getgId());
			}
			if(auctionList.size()>0)
			{
				autionId=Integer.parseInt(auctionList.get(0).get("id").toString());
				price=Double.valueOf(auctionList.get(0).get("price").toString());
				auction.setConfirmDate(auctionDeposit.getPayDate());
				auction.setId(autionId);
				//最近10次数出价记录
				List<Map> goodsAuctionList=new ArrayList<Map>();
				if(erIsPack==0)//商品是否打包 erIsPack
				{
					goodsAuctionList=auctionMapper.findAuctionListByNumIdAndGId(auctionDeposit.getNumId(),auctionDeposit.getgId());

				}else if(erIsPack==1)
				{
					goodsAuctionList=auctionMapper.findAuctionListByGId(auctionDeposit.getgId());
				}
				if(goodsAuctionList.size()>0)
				{
					beforePrice=Double.valueOf(goodsAuctionList.get(0).get("price").toString());//前一次出价记录
					beforeAutionId=NumberUtils.toInt(goodsAuctionList.get(0).get("id").toString());//前一次出价记录Id
					beforeConsumerId=Integer.parseInt(goodsAuctionList.get(0).get("consumerId").toString());//前一次出价记录用户Id
					//auctionMapper.freezeOneNum(numId);//锁行记录
					//1、大于之前的最近出价记录，则前一次出价记录状态：4 落败,当前出价记录状态：2成功
					if(price>beforePrice)
					{
						//auctonBef  状态：4 落败
						auctonBef.setId(beforeAutionId);
						auctonBef.setStatus(4);//前一次出价记录   状态：4 落败
						auctionMapper.auctionEditStatusById2(auctonBef);//通知用户
						//****************短信通知提醒auctonNew****************已落败
						List<Map> ePSaleNoticeList=ePSaleNoticeMapper.findEPSaleNoticeListByEPSaleIdAndConsumerId2(epSaleId,beforeConsumerId);
						if(!ePSaleNoticeList.isEmpty()&&ePSaleNoticeList.size()>0)
						{
							ePSaleNoticePhone=String.valueOf(ePSaleNoticeList.get(0).get("phone").toString());
							Messager.send(ePSaleNoticePhone,"你的出价记录低于新的出价记录，已落败");
						}
						//auction  状态：2成功
						auction.setStatus(2);
						//****************保证金支付成功*****当前出价记录状态：2成功*******************
						auctionDepositMapper.auctionDepositSatusEdit(auctionDeposit);
						auctionMapper.auctionEditStatusById(auction);
						//epSaleService.epsaleDelayed(numId);
						epSaleService.epsaleDelayed(numId,gId,erIsPack);
						/*if(epSaleService.isLoopTime(auction.getConfirmDate(),loopTime,numId)) //处于（结束时间-轮询时间）与结束时间 之间;则延长结束时间= 结束时间+loopTime;
						{
							//***************************则延长结束时间= 结束时间+loopTime*********************
							epSaleService.numLoopEdit(numId,loopTime);
						}*/
						//****************保证金支付成功*******当前出价记录状态：2成功*****************
					}
					//2、出现同价的成功出价记录，则当前出价记录状态：3失败
					else if(price==beforePrice)//出现同价的成功出价记录
					{
						//auction  状态：3失败
						//auction.setStatus(3);//调整为状态：4落败
						auction.setStatus(4);
						//****************保证金支付成功*****当前出价记录状态：3失败*******************
						auctionDepositMapper.auctionDepositSatusEdit(auctionDeposit);
						auctionMapper.auctionEditStatusById(auction);
						//****************保证金支付成功*******当前出价记录状态：3失败*****************
					}
					//3、低于之前的最近出价记录,则当前出价记录状态：4 落败
					else
					{
						//auction  状态：4落败
						auction.setStatus(4);
						//****************保证金支付成功*****当前出价记录状态：4落败*******************
						auctionDepositMapper.auctionDepositSatusEdit(auctionDeposit);
						auctionMapper.auctionEditStatusById(auction);
						//****************保证金支付成功*******当前出价记录状态：4落败*****************
						//****************短信通知提醒auctonNew****************已落败
						List<Map> ePSaleNoticeList=ePSaleNoticeMapper.findEPSaleNoticeListByEPSaleIdAndConsumerId2(epSaleId,consumerId);
						if(!ePSaleNoticeList.isEmpty()&&ePSaleNoticeList.size()>0)
						{
							ePSaleNoticePhone=String.valueOf(ePSaleNoticeList.get(0).get("phone").toString());
							Messager.send(ePSaleNoticePhone,"你的出价记录低于新的出价记录，已落败");
						}
					}
				}else
				{
					//4、当前出价记录为第一次出价记录 状态：2成功
					auction.setStatus(2);
					//****************保证金支付成功*****当前出价记录状态：2成功*******************
					auctionDepositMapper.auctionDepositSatusEdit(auctionDeposit);
					auctionMapper.auctionEditStatusById(auction);
					epSaleService.epsaleDelayed(numId);
					//****************保证金支付成功*******当前出价记录状态：2成功*****************
				/*	if(epSaleService.isLoopTime(auction.getConfirmDate(),loopTime,numId)) //处于（结束时间-轮询时间）与结束时间 之间;则延长结束时间= 结束时间+loopTime;
					{
						//***************************则延长结束时间= 结束时间+loopTime*********************
						epSaleService.numLoopEdit(numId,loopTime);
					}*/
				}
				try {
					log.info("出价成功**************广播信息***********************************");
					WebSocketServer.sendInfo(String.valueOf(auctionDeposit.getNumId()),String.valueOf(auctionDeposit.getgId()),erIsPack);
					log.info("出价成功**************广播信息***********************************");
					//log.info("广播信息"+msg);
					//log.info("保证金支付成功，广播信息,最近10次出价记录，状态：2支付成功保证金列表");
				}catch (IOException e)
				{
					log.info(String.format("出价成功，广播信息异常【[%s]",e.getMessage())+"】");
				}
		}else
		{
			auctionDeposit.setStatus(1);
			auctionDepositMapper.auctionDepositSatusEdit(auctionDeposit);
			log.info("保证金支付失败");
		}

		/*if(status)
		{*/


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
		/*	}*/
		}
	}

	/*
	  保证金退款
	  status true 成功 false失败
	 */
	public void auctionDepositRefund(Integer Id,boolean status) {
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
