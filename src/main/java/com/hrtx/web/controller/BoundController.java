package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Iccid;
import com.hrtx.web.pojo.Meal;
import com.hrtx.web.pojo.Num;
import com.hrtx.web.service.BoundService;
import com.hrtx.web.service.MealService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
//@RequestMapping("/api")
public class BoundController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MealService MealService;
	@Autowired
	private BoundService boundService;
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

		numId=num.getId();
        iccidStr=num.getIccid();
		mealMid=num.getMealMid();

		Num number=boundService.findNumById(numId);
        Iccid iccid=boundService.findIccidByIccid(iccidStr);
        Meal meal =MealService.findMealById(mealMid);
        if(number==null) returnResult(new Result(Result.ERROR,"号码记录为空，请核对！"));
        if(iccid==null) returnResult(new Result(Result.ERROR,"iccid记录为空，请核对！"));
        if(meal==null) returnResult(new Result(Result.ERROR,"套餐记录为空，请核对！"));
        numBuyerId=number.getBuyerId();
        numSectionNo=number.getSectionNo();
        numStatus=number.getStatus();
        iccidSectionNo=iccid.getSections();
        iccidConsumerId=iccid.getConsumerId();
        iccidStatus=iccid.getDealStatus();
        iccidStr=iccid.getIccid();
        if(!(numBuyerId.toString().equals(iccidConsumerId.toString())))//是否同一用户
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"该号码与iccid属于不同的用户，请核对！"));
        }
        if(!(numSectionNo.trim().equals(iccidSectionNo.trim())))//是否同一号段
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"该号码号段与iccid号段不一致，请核对！"));
        }
        if(numStatus!=4)
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"请待配卡的号码进行绑定，请核对！"));
        }
        if(!iccidStatus.equals("1"))//受理状态(1待绑定
        {
            isEdit=false;
            returnResult(new Result(Result.ERROR,"请待绑定的iccid进行绑定，请核对！"));
        }
        if(isEdit)
        {
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
     */
    @GetMapping("/api/numBoundList/{status}")
    @Powers(PowerConsts.NOPOWER)
    @ResponseBody
    public Result numBoundList(@PathVariable("status") String status,HttpServletRequest request){
        return boundService.numBoundList(status,request);
    }
}
