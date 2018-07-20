package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Iccid;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.pojo.Num;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.service.ApiOrderService;
import com.hrtx.web.service.BoundService;
import com.hrtx.web.service.MealService;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
//@RequestMapping("/api")
public class BoundController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MealService MealService;
	@Autowired
	private BoundService boundService;
    @Autowired
    ApiOrderService apiOrderService;
	@Autowired
	private ApiSessionUtil apiSessionUtil;

	@PostMapping("/api/boundNum")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public void  boundNumIccid(Num num, HttpServletRequest request) {
	    Boolean isEdit=true;
		Long numId=0L;//Id  号码记录
        Long numBuyerId=0L;//buyer_id 用户Id  号码记录
        String numSectionNo="";//号段 号码记录
        int numStatus=0;//状态 号码记录
		String iccids="";//iccid  iccid记录 iccid
        String iccidStr="";// iccid  iccid记录
        String iccidSectionNo="";//号段  iccid记录
        Long iccidConsumerId=0L;//用户Id  iccid记录
        String iccidStatus="";//状态 iccid记录
		Long mealMid=0L;//mdelMid 套餐记录
        int orderStatus=0;//订单状态
        Long orderId=0L;//订单Id
        Order order=new Order();//签收订单
		numId=num.getId();
        iccidStr=num.getIccid();
		mealMid=num.getMealMid();
		//sku商品类型=3 普靓
        List<Map> orderList=apiOrderService.findOrderListByNumId(numId);
        if(orderList!=null&&orderList.size()>0)
        {
            orderStatus= NumberUtils.toInt(String.valueOf(orderList.get(0).get("orderStatus")),0) ;
            orderId=NumberUtils.toLong(String.valueOf(orderList.get(0).get("orderId")),0L);
        }else
        {
            returnResult(new Result(Result.ERROR,"号码对应的订单记录为空，请核对！"));
        }
		Num number=boundService.findNumById(numId);
        Iccid iccid=boundService.findIccidByIccid(iccidStr);
        Meal meal =MealService.findMealById(mealMid);
        if(number==null) returnResult(new Result(Result.ERROR,"号码记录为空，请核对！"));
        if(iccid==null) returnResult(new Result(Result.ERROR,"你所填写的iccid不存在，请重新填写！"));
        if(meal==null) returnResult(new Result(Result.ERROR,"套餐记录为空，请核对！"));
        numBuyerId=NumberUtils.toLong(String.valueOf(number.getBuyerId()),0L);
        if(StringUtils.isNotBlank(number.getSectionNo()))
        {
            numSectionNo=number.getSectionNo();
        }
        numStatus=NumberUtils.toInt(String.valueOf(number.getStatus()),0);
        if(StringUtils.isNotBlank(iccid.getSections()))
        {
            iccidSectionNo=iccid.getSections();
        }
        iccidConsumerId=NumberUtils.toLong(String.valueOf(iccid.getConsumerId()),0L);
        if(StringUtils.isNotBlank(iccid.getDealStatus()))
        {
            iccidStatus=iccid.getDealStatus();
        }
        if(StringUtils.isNotBlank(iccid.getIccid()))
        {
            iccidStr=iccid.getIccid();
        }
        if(orderStatus!=4)//4待配卡；5待签收(仓储物流已取件)；6完成
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"你所绑的号码对应的订单状态不是待配卡状态，请核对！"));
        }
        if(!(String.valueOf(numBuyerId).equals(String.valueOf(iccidConsumerId))))//是否同一用户
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"该号码与iccid属于不同的用户，请核对！"));
        }
        if(!(numSectionNo.trim().equals(iccidSectionNo.trim())))//是否同一号段
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"该号码所属号段与所填iccid的号段不一致，请重新填定！"));
        }
        if(numStatus!=4)
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"请待配卡的号码进行绑定，请核对！"));
        }
        if(iccidStatus.equals("2"))//受理状态(1待绑定 2已绑定
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"你所填写的iccid已被绑定，请重新填写！"));
        }
        if(!iccidStatus.equals("1"))//受理状态(1待绑定
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"请待绑定的iccid进行绑定，请核对！"));
        }
        if(isEdit)
        {
            order.setOrderId(orderId);
            order.setStatus(6);
            order.setSignDate(new Date());//签收时间
            order.setSignType(2);//签收方式1用户自动签收2系统
            boundService.orderSign(order);//4待配卡,6完成
            num.setIccidId(iccid.getId());
            num.setIccid(iccidStr);
            num.setStatus(5);//4待配卡(已付款 针对2C或电销无需购买卡时、代理商买号而未指定白卡时)、5待受理(代理商已提交或仓库已发货，待提交乐语BOSS)
            Iccid iccidEdit=new Iccid();
            boundService.bindNum(num);
            iccidEdit.setId(iccid.getId());
            iccidEdit.setDealStatus("2");//受理状态(1待绑定=》2已绑定)
            boundService.iccidEditStatus(iccidEdit);
            returnResult(new Result(Result.OK,"该号码绑定成功！"));
        }
	}

    /**
     * 号码列表
     * 查询当前用户
     * status:0 未绑定限 sku商品类型=3 普靓
     */
    @GetMapping("/api/numBoundList/{status}")
    @Powers(PowerConsts.NOPOWER)
    @ResponseBody
    public Result numBoundList(@PathVariable("status") String status,HttpServletRequest request){
        return boundService.numBoundList(status,request);
    }
}
