package com.hrtx.web.service;

import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.Messager;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.mapper.EPSaleNoticeMapper;
import com.hrtx.web.mapper.GoodsMapper;
import com.hrtx.web.mapper.NumMapper;
import com.hrtx.web.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class EPSaleNoticeService {

    public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private EPSaleNoticeMapper ePSaleNoticeMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
    @Autowired
    private NumMapper numMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private ConsumerMapper consumerMapper;
    public List<Map> findEPSaleNoticeListBydConsumerId()
	{
		return ePSaleNoticeMapper.findEPSaleNoticeListbyConsumerId(apiSessionUtil.getConsumer().getId());
	}


    /*
     竟拍活动开始时系统自动提醒定时器
     */
    @Scheduled(fixedRate=1000)
    public void startEPSaleListSystemNotice() {
       // if("true".equals("true")) return;
        if(!"true".equals(SystemParam.get("exe_timer"))) return;
        log.info("开始执行.....竟拍活动开始,系统自动短信提醒......定时器");
        List<Map> list=this.ePSaleNoticeMapper.findStartEPSaleList();//竟拍活动开始
        if(list.isEmpty()){
            log.info(String.format("暂无竟拍活动开始........"));return;
        }
        Long epSaleId=0L;//竟拍活动id
        String goodsNoticePhone="";//短信通知手机号
        String epSaleTitle="";//竟拍活动标题
        Order order=new Order();
        if(list.size()>0)
        {
            for(Map map :list)
            {
                epSaleId=Long.valueOf(map.get("epSaleId").toString());
                epSaleTitle=map.get("title").toString();
                List<Map> ePSaleNoticeList=this.ePSaleNoticeMapper.findEPSaleNoticeListByEPSaleId(epSaleId);
                if(!ePSaleNoticeList.isEmpty()&&ePSaleNoticeList.size()>0)
                {
                    for(Map map2 :ePSaleNoticeList)
                    {
                        goodsNoticePhone=String.valueOf(map2.get("phone").toString());
                        Messager.send(goodsNoticePhone,"竟拍活动"+epSaleTitle+"开始");
                        log.info(String.format("........竟拍活动"+epSaleTitle+"开始;短信提醒,用户:"+goodsNoticePhone));
                    }
                }else
                {
                    log.info(String.format("该竟拍活动开始,无用户须系统自动短信提醒........"));return;
                }
                log.info("竟拍活动开始,系统自动短信提醒,竟拍活动Id:"+epSaleId);
             /*   orderId=Long.valueOf(map.get("orderId").toString());
                order.setOrderId(orderId);
                order.setStatus(6);
                order.setSignDate(new Date());//签收时间
                order.setSignType(2);//签收方式1用户自动签收2系统
                orderMapper.signByOrderid(order);*/

            }
        }
    }

	/*
	 当前用户提醒列表
	 */
    public Result findEPSaleNoticeList()
    {
        List<Map> list=ePSaleNoticeMapper.findEPSaleNoticeListbyConsumerId(apiSessionUtil.getConsumer().getId());
        Long numId=0L;
        Long gId=0L;
        int numStatus=0;//状态 号码记录
        String urlImg="";
        int gStatus=0;//0 未上架，1上架，2 失效 tb_num.stauts<>2
        String gIsSale="";//是否上架1是0否
        for(Map map:list)
        {
            urlImg=SystemParam.get("domain-full") +map.get("gImg").toString();
            map.put("gImg",urlImg);
            numId=Long.valueOf(map.get("numId").toString());
            gId=Long.valueOf(map.get("gId").toString());
            if(gId>0)
            {
                Goods goods = goodsMapper.findGoodsInfo(gId);
                if(goods!=null&&StringUtils.isNotBlank(goods.getgIsSale()))
                {
                    gIsSale=goods.getgIsSale();
                    if(gIsSale.equals("0"))
                    {
                        map.put("gStatus",0);//0 未上架
                    }else if(gIsSale.equals("1"))
                    {
                        map.put("gStatus",1);//1上架
                    }
                }
            }
            if(numId>0)
            {
                Num number=numMapper.selectByPrimaryKey(numId);
                numStatus=NumberUtils.toInt(String.valueOf(number.getStatus()),0);
                if(numStatus!=2)
                {
                    gStatus=2;//非 2销售中为失效
                    map.put("gStatus",gStatus);
                }
            }
        }
        return new Result(Result.OK,list);
    }

    /*
      获取consumerId的设置提醒列表
     */
    public List<Map> findEPSaleNoticeListByGIdAndConsumerId(Long gId,Long consumerId) {
        return ePSaleNoticeMapper.findEPSaleNoticeListByGIdAndConsumerId(gId,consumerId);
    }

	public Result epSaleEdit(EPSaleNotice ePSaleNotice, HttpServletRequest request) {
            ePSaleNotice.setAddIp(SessionUtil.getUserIp());
            ePSaleNotice.setConsumerId(apiSessionUtil.getConsumer().getId());
            List<Map> ePSaleNoticeList=ePSaleNoticeMapper.findEPSaleNoticeListByEPSaleIdAndConsumerId (ePSaleNotice.getEpSaleId(),this.apiSessionUtil.getConsumer().getId());
            if (!ePSaleNoticeList.isEmpty()&&ePSaleNoticeList.size()>0) {
                ePSaleNotice.setId(Long.valueOf(ePSaleNoticeList.get(0).get("id").toString()));
                ePSaleNotice.setUpdateDate(new Date());
                ePSaleNoticeMapper.ePSaleNoticeEdit(ePSaleNotice);
            } else {
                List<EPSaleNotice> list = new ArrayList<EPSaleNotice>();
                ePSaleNotice.setId(ePSaleNotice.getGeneralId());
                ePSaleNotice.setAddDate(new Date());
                ePSaleNotice.setUpdateDate(new Date());
                list.add(ePSaleNotice);
                ePSaleNoticeMapper.insertBatch(list);
                Consumer consumer=new Consumer();
                if(!StringUtils.isNotBlank(apiSessionUtil.getConsumer().getPhone()))//若用户phone为空,则回填
                {
                    consumer.setId(this.apiSessionUtil.getConsumer().getId());
                    consumer.setPhone(ePSaleNotice.getPhone());
                    consumerMapper.insertPhoneToConsumer(consumer);
                }
            }
			return new Result(Result.OK, "提交成功");
	}

}
