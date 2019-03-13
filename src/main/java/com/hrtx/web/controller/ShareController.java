package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.Utils;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.PromotionPlan;
import com.hrtx.web.service.AccountService;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.ShareService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.*;

@RestController
public class ShareController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private ShareService shareService;
	/**
	 * 添加合伙人信息
	 */
	@PostMapping("/api/partner/user-info")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result addInfo(){
		return new Result(Result.OK,"");
	}

	/**
	 * 查询合伙人信息
	 */
	@GetMapping("/api/partner/user-info")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result findInfo(){
		return new Result(Result.OK,"");
	}

	/**
	 * 修改合伙人信息
	 */
	@PutMapping("/api/partner/user-info")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result editInfo(){
		return new Result(Result.OK,"");
	}

	////////////////////////////////////////////////////////
	/**
	 * 查看合伙人收益
	 */
	@GetMapping("/api/partner/income")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result findIncome(){
		return new Result(Result.OK,"");
	}
	/**
	 * 查看推广记录
	 */
	@GetMapping("/api/partner/share")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result queryShareList(){
		return new Result(Result.OK,"");
	}
	/**
	 * 生成推广卡片
	 */
	@PostMapping("/api/partner/share-card")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result shareCard(){
		return new Result(Result.OK,"");
	}
	/**
	 * 生成分享地址
	 */
	@PostMapping("/api/partner/share-url")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result shareUrl(){
		return new Result(Result.OK,"");
	}
	/**
	 * 分享浏览记录统计
	 */
	@GetMapping("/api/partner/share-browse-count")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result shareBrowseCount(){
		return new Result(Result.OK,"");
	}
	/**
	 * 分享浏览记录
	 */
	@GetMapping("/api/partner/share-browse")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result shareBrowse(){
		return new Result(Result.OK,"");
	}

	///////////////////////
	/**
	 * 余额查看
	 */
	@GetMapping("/api/partner/finance-balance")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result financeBalance(){
		return new Result(Result.OK,"");
	}
	/**
	 * 结算汇总
	 */
	@GetMapping("/api/partner/finance-count")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result financeCount(){
		return new Result(Result.OK,"");
	}
	/**
	 * 收支明细
	 */
	@GetMapping("/api/partner/finance-list")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result financeList(){
		return new Result(Result.OK,"");
	}
	/**
	 * 收支明细
	 */
	@GetMapping("/api/partner/finance-withdraw-progresss")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result financeWithdrawProgresss(){
		return new Result(Result.OK,"");
	}

	/**
	 * 推广计划
	 */
	@GetMapping("/partner/promotion-plan-index")
	@Powers({PowerConsts.PROMOTIONPLAN_QUERY})
	public ModelAndView promotionIndex(){
		return new ModelAndView("admin/goods/num-share");
	}
	/**
	 * 推广计划
	 */
	@PostMapping("/partner/promotion-plan-list")
	@Powers({PowerConsts.PROMOTIONPLAN_EDIT})
	public Result promotionList(HttpServletRequest request){
		int start=NumberUtils.toInt(request.getParameter("start"),1);
		int limit=NumberUtils.toInt(request.getParameter("limit"),15);
		int status=NumberUtils.toInt(request.getParameter("status"),-1);
		String num=request.getParameter("num");
		return shareService.promotionPlanPage(status,num,start,limit);
	}
	/**
	 * 推广计划-佣金添加
	 */
	@PostMapping("/partner/promotion-plan")
	@Powers({PowerConsts.PROMOTIONPLAN_EDIT})
	public Result promotionAdd(HttpServletRequest request){
		int id=NumberUtils.toInt(request.getParameter("id"),0);
		int operation=NumberUtils.toInt(request.getParameter("operation"));
		int promotion=NumberUtils.toInt(request.getParameter("promotion"),-1);
		int award_way=NumberUtils.toInt(request.getParameter("awardWay"),-1);
		double award=NumberUtils.toDouble(request.getParameter("award"),0d);
		int is_limit=NumberUtils.toInt(request.getParameter("isLimit"),-1);
		double limit_award=NumberUtils.toInt(request.getParameter("limitAward"),-1);
		double begin_price=NumberUtils.toDouble(request.getParameter("beginPrice"),0d);
		double end_price=NumberUtils.toDouble(request.getParameter("endPrice"),0d);
		String begin_date=request.getParameter("beginDate");
		String end_date=request.getParameter("endDate");
		if(StringUtils.isEmpty(begin_date)||StringUtils.isEmpty(end_date))return new Result(Result.ERROR,"请选择推广的生效时间");
		Date _begin_date= null;
		Date _end_date= null;
		try {
			_begin_date = Utils.stringToDate(begin_date,"yyyy-MM-dd HH:mm:ss");
			_end_date = Utils.stringToDate(end_date,"yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			return new Result(Result.ERROR,"时间格式错误");
		}
		String[] nums=StringUtils.split(request.getParameter("nums"),"\r\n");
		for(String num:nums){
			if(promotion==Constants.PROMOTION_PLAN_PROMOTION_2.getIntKey() && num.length()!=11)return new Result(Result.ERROR,"号码格式错误，只能等于11位");
		}
		PromotionPlan bean=new PromotionPlan(promotion,award_way,award,is_limit,limit_award,begin_price,end_price,_begin_date,_end_date);
		bean.setId(id);
		Result result=this.shareService.promotionPlanAdd(operation,bean,nums);
		return result;
	}
	/**
	 * 推广计划-查询
	 */
	@GetMapping("/partner/promotion-plan")
	@Powers({PowerConsts.PROMOTIONPLAN_EDIT})
	public Result promotionPlan(HttpServletRequest request){
		int id=NumberUtils.toInt(request.getParameter("id"),0);
		return this.shareService.findPromotionPlan(id);
	}
	/**
	 * 推广计划-失效
	 */
	@PostMapping("/partner/promotion-plan-expire")
	@Powers({PowerConsts.PROMOTIONPLAN_EDIT})
	public Result promotionPlanExpire(HttpServletRequest request){
		int id=NumberUtils.toInt(request.getParameter("id"),0);
		return this.shareService.promotionPlanExpire(id);
	}
}
