package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.config.utils.RedisUtil;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.web.controller.BaseReturn;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import net.sf.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

@Service
public class EPSaleService {

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired SessionUtil sessionUtil;
	@Autowired private EPSaleMapper epSaleMapper;
	@Autowired private AuctionMapper auctionMapper;
	@Autowired private AuctionDepositMapper auctionDepositMapper;
	@Autowired private FileMapper fileMapper;
    @Autowired private ConsumerMapper consumerMapper;
	@Autowired private DeliveryAddressMapper deliveryAddressMapper;
	@Autowired private NumberMapper numberMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private FundOrderService fundOrderService;
	@Autowired private ApiOrderService apiOrderService;
	public Result pageEPSale(EPSale epSale) {
		PageHelper.startPage(epSale.getPageNum(),epSale.getLimit());
		Page<Object> ob=this.epSaleMapper.queryPageList(epSale);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Result findEPSaleList() {
		List<Map> list=epSaleMapper.findEPSaleList();
		Long epSaleId=0L;
		int priceCount=0;
		for(Map map:list)
		{
			String urlImg=SystemParam.get("domain-full") +"/"+map.get("epImg").toString();
			map.put("epImg",urlImg);
            epSaleId=Long.valueOf(map.get("id").toString());
            List<Map> priceCountList=epSaleMapper.findEPSalePriceCountByEPSaleId(epSaleId);
            if(priceCountList.size()>0)
            {
                priceCount=Integer.valueOf(priceCountList.get(0).get("priceCount").toString());
				map.put("priceCount",priceCount);
            }else
			{
				priceCount=0;
				map.put("priceCount",priceCount);
			}
		}
		return new Result(Result.OK, list);
	}

	public List<Map> findEPSaleByEPSaleId(Long ePSaleId) {
		List<Map> list=epSaleMapper.findEPSaleByEPSaleId(ePSaleId);
		for(Map map:list)
		{
			String urlImg=SystemParam.get("domain-full") +"/"+map.get("epImg").toString();
			map.put("epImg",urlImg);
		}
		return list;
	}

	public List<Map> findEPSaleGoodsListByEPSaleId(Long ePSaleId) {
		List<Map> list=epSaleMapper.findEPSaleGoodsListByEPSaleId(ePSaleId);
		for(Map map:list)
		{
			String urlImg=SystemParam.get("domain-full") +"/"+map.get("gImg").toString();
			map.put("gImg",urlImg);
		}
		return list;
	}

	public List<Map> findEPSaleGoodsByGoodsId(Long numId) {
		return epSaleMapper.findEPSaleGoodsByNumId(numId);
	}

    public List<Map> findEPSaleGoodsByGoodsId(Long numId,Long gId) {
        return epSaleMapper.findEPSaleGoodsByNumIdAndGId(numId,gId);
    }

	public Map findEPSaleNumInfoByNumId(Long numId) {
		return  numberMapper.getNumInfoById(numId.toString());
	}

	public EPSale finEPSaleById(Long id) {
		EPSale epSale = epSaleMapper.findEPSaleById(id);
		return epSale;
	}

   /*
   *
   * 竟拍的号码是否结束
   * "0 0/5 14,18 * * ?" 在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
   *
	*/
	//@Scheduled(cron = "0 3 18 * * ?")
   @Scheduled(fixedRate=10000000)
	public void checkEPsaleNum() {
		List<Map> list=epSaleMapper.findEPSaleGoods2();//已出价的Num列表
		String endTimeStr="";//结束时间
		String currentTimeStr="";//当前时间
		Long numId=0L;//条码ID
        Long gId=0L;//gId
	    Long skuId=0L;//skuId
	    Long addrid=0L;//最后成功出价成功=》默认地此
	    String num="";//出价的号码
        double successAutionPrice=0.00;//最后成功出价记录价格
		int startNum=0;//起拍人数
		boolean isEPSaleValid=false;//是否竟拍成功
	    boolean isStartNum=false;//起拍人数是否足够
	    int priceCumsumerCount=0;//出价人数
	    long successConsumerId=0L;//最后成功出价记录 用户ID
		double depositPrice=0.00;//保证金

		if(list.size()>0)
		{
			for (Map map:list)
			{
				log.info(String.format("numId[%s]",map.get("numId")));
				endTimeStr=map.get("endTime").toString();
				numId=Long.valueOf(map.get("numId").toString());
                gId=Long.valueOf(map.get("gId").toString());
				num=map.get("num").toString();
				startNum=Integer.valueOf(map.get("startNum").toString());
				depositPrice=Double.valueOf(map.get("depositPrice").toString());
				currentTimeStr=Utils.dateToString(new Date(),"yyyy-MM-dd HH:mm:ss");

				//********************************************************************************************************************
				//***********************************当前时间>=结束时间*******竟拍人数>=起拍人数符合规则******************************
				if(Utils.compareDate(currentTimeStr,endTimeStr)>=0)//当前时间>=结束时间
				{
					List<Map> auctionCustomers=auctionMapper.findCustomersByNumIdAndGId(numId,gId);//竟拍人数
					if(auctionCustomers.size()>0)
					{
						priceCumsumerCount=auctionCustomers.size();//竟拍人数
						if(priceCumsumerCount>=startNum)//竟拍人数>=起拍人数符合规则
						{
							isStartNum=true;
							Auction auction =new Auction();
							auction.setNumId(numId);
							auction.setStatus(2);
							auction.setgId(gId);
							List<Map> successAution=auctionMapper.findAuctionByNumIdAndStatusAndGId(auction);//最后成功出价记录 status:2
							if(successAution.size()>0)
							{
								successConsumerId=Long.valueOf(successAution.get(0).get("consumerId").toString());//最后成功出价记录 用户ID
								numId=Long.valueOf(successAution.get(0).get("numId").toString());//条码ID
								skuId=Long.valueOf(successAution.get(0).get("skuId").toString());//skuId
								successAutionPrice=Double.valueOf(successAution.get(0).get("price").toString());//最后成功出价记录价格
								isEPSaleValid=true;
							}
						}
					}
				}

				//********************************************************************************************************************
				//***********************************有效竟拍转订单********************************************************************
				//isEPSaleValid=true;
				if(isEPSaleValid)//有效竟拍，最新一条出价转订单，其他用户退回保证金
				{
                    Consumer user=this.consumerMapper.findConsumerById(successConsumerId);
					List<Map> deliveryAddressDefault=deliveryAddressMapper.findDeliveryAddressDefaultByUserId(successConsumerId);
					/*if(deliveryAddressDefault.size()>0)
					{
						addrid=Long.valueOf(deliveryAddressDefault.get(0).get("id").toString());
					}*/
                    Map mapOrder=new HashMap();
                    mapOrder.put("type","3");//竟拍订单
					mapOrder.put("user",user);//竟拍成功用户
					mapOrder.put("skuid",skuId);//竟拍成功skuId
					mapOrder.put("numid",numId);//竟拍成功numId
					mapOrder.put("addrid",addrid);//竟拍成功addrid
					mapOrder.put("price",successAutionPrice);//竟拍成功price
					if(apiOrderService.createOrder(null,mapOrder).getCode()!=200)
					{
						//************************通知管理员，竟拍出价成功转订单失败********************//
						Messager.send(SystemParam.get("system_phone"),"竟拍结束;出价成功转订单失败;竟拍号:"+num);
					}
				}

				//****************************************************************************************************************
                //******************************保证金退回并通知****************开始***********************************************
				//****************isEPSaleValid是否竟拍成功***********************************************************************
				//****************是****状态4 落败者 保证金退回并通知**********否****状态4,2 落败者成功者 保证金全部退回并通知****
				Long consumerId=0L;
				Long depositId=0L;
				String consumerPhone="";
				AuctionDeposit auctionDeposit=new AuctionDeposit();
				List<Map> auctionList=new ArrayList<Map>();
				if(isEPSaleValid)
				{
					auctionList=auctionMapper.findAuctionListDepositByNumIdAndGId(numId,gId);//退回落败者的保证金
				}else
				{
					auctionList=auctionMapper.findAuctionListDepositByNumIdAndGId2(numId,gId);//退回所有出价者的保证金
				}
				if(auctionList.size()>0)
				{
					for(Map map2:auctionList)
					{
						consumerId=Long.valueOf(map2.get("consumerId").toString());
						consumerPhone=map2.get("consumerPhone").toString();
						auctionDeposit.setConsumerId(consumerId);
						auctionDeposit.setNumId(numId);
						//*************************************出价记录status=4,status<>2 用户保证金
						List<Map> depositList =auctionDepositMapper.findAuctionDepositListByNumIdAndConsumerIdAndGId(numId,consumerId,gId);
						if(depositList.size()>0)
						{
							depositId=Long.valueOf(depositList.get(0).get("id").toString());
							if(depositId>0)
							{
								auctionDeposit.setId(depositId);
								if(fundOrderService.payDepositRefund(auctionDeposit.getId().toString(),"保证金").getCode()==200)
								{
									auctionDeposit.setStatus(3);//status 3 已退款
									auctionDepositMapper.auctionDepositEdit(auctionDeposit);
									if(!isStartNum)//起拍人数是否足够
									{
										Messager.send(consumerPhone,"竟拍结束;竟拍失败,出价人数少于起拍人类;竟拍号:"+num);
									}
									Messager.send(consumerPhone,"竟拍结束;你的出价落败,保证金已退回,金额："+depositPrice+"竟拍号:"+num);
								}else
								{
									//************************通知管理员，保证金退回失败********************//
									Messager.send(SystemParam.get("system_phone"),"竟拍结束;保证金退回失败,金额："+depositPrice+"竟拍号:"+num);
								}
							}
						}
					}
				}
				//************************************************************************************************************
				//******************************保证金退回并通知*******************结束***************************************
				//************************************************************************************************************
			}
		}
	}

	public List<Map> findNumById(Long id) {
		return epSaleMapper.findNumById(id);
	}

	public void numLoopEdit(Long numId,int loopTime) {
		List<Map> numList=findNumById(numId);
		Date endTime=new Date();//结束时间
		if(numList.size()>0) {
			try {
				endTime=Utils.stringToDate(numList.get(0).get("end_time").toString(),"yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		String endTimeStr=Utils.getDate2(loopTime,endTime,"yyyy-MM-dd HH:mm:ss");
		epSaleMapper.numLoopEdit(endTimeStr,numId);
	}

	public boolean isLoopTime(Date addTime,int loopTime,Long numId)
	{
		boolean isLoop=false;
		List<Map> numList=findNumById(numId);
		if(numList.size()>0)
		{
			Date endTime=new Date();//结束时间
			String endTimeStr="";
			Date strtTime=new Date();//开始时间
			Date currentTime=new Date();//当前时间
			endTimeStr=numList.get(0).get("end_time").toString();
			try {
				endTime=Utils.stringToDate(endTimeStr,"yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			endTimeStr=Utils.dateToString(endTime,"yyyy-MM-dd HH:mm:ss");
			String startTimeStr=Utils.getDate2(-loopTime,endTime,"yyyy-MM-dd HH:mm:ss");
			String currentTimeStr=Utils.dateToString(addTime,"yyyy-MM-dd HH:mm:ss");
			/*try {
				strtTime=Utils.date(startTimeStr,"yyyy-MM-dd HH:mm:ss");
			} catch (ParseException e) {
				e.printStackTrace();
			}*/

			if(Utils.compareDate(startTimeStr,currentTimeStr)<0&&Utils.compareDate(currentTimeStr,endTimeStr)<0)//addTime 处于 （结束时间-轮询时间）与结束时间 之间
			{
				return  true;
			}
		}
		return false;

	}

	public Result goodsAuciton(Auction auciton, HttpServletRequest request) {
		return new Result(Result.OK, "提交成功");
	}

	public Result epSaleEdit(EPSale epSale, HttpServletRequest request, MultipartFile[] files) {
		epSale.setAddUserId(SessionUtil.getUserId());
		epSale.setIsShow(0);
		if (epSale.getId() != null && epSale.getId() > 0) {
			epSale.setUpdateDate(new Date());
			epSaleMapper.epSaleEdit(epSale);
		} else {
			List<EPSale> list = new ArrayList<EPSale>();
			epSale.setId(epSale.getGeneralId());
			epSale.setCreateDate(new Date());
			epSale.setUpdateDate(new Date());
			list.add(epSale);
			epSaleMapper.insertBatch(list);
		}

		//图片保存
		Result result = null;
		String picSeqs = request.getParameter("picSeqs")==null?"":request.getParameter("picSeqs");
		String delPicSeqs = request.getParameter("delPicSeqs")==null?"":request.getParameter("delPicSeqs");
		if(!picSeqs.equals("")){
			fileMapper.deleteFilesByRefid(epSale.getId().toString(), picSeqs.equals("")?"":picSeqs.substring(0, picSeqs.length()-1));
		}
		if(!delPicSeqs.equals("")){
			fileMapper.deleteFilesByRefid(epSale.getId().toString(), delPicSeqs.equals("")?"":delPicSeqs.substring(0, delPicSeqs.length()-1));
		}
		if(files!=null && files.length>0){
			try {
				List<File> fileList = new ArrayList<File>();
				for (int i=0; i<files.length; i++) {
					MultipartFile file = files[i];
					File f = new File();
					f.setFileId(f.getGeneralId());
					f.setFileGroup("epSalePic");
					//String kk=SystemParam.get("epSalePics");
					result = BaseReturn.uploadFile(SystemParam.get("epSalePics")+epSale.getId()+"\\", "jpg,png,gif", file, false, false);
					f.setFileName(((Map)result.getData()).get("sourceServerFileName").toString());
					f.setRefId(epSale.getId());
					f.setSeq(Integer.parseInt(picSeqs.replaceAll("\"","").split(",")[i]));
					fileList.add(f);
				}
				if(fileList!=null && fileList.size()>0) {
					fileMapper.insertBatch(fileList);
				}
			} catch (Exception e) {
				e.printStackTrace();
				if(result==null) result = new Result(Result.ERROR, "保存图片异常");
				return result;
			}
		}
		return new Result(Result.OK, "提交成功");
	}

    public Result epSaleDelete(EPSale epSale) {
		epSaleMapper.epSaleDelete(epSale);
        return new Result(Result.OK, "删除成功");
    }
}
