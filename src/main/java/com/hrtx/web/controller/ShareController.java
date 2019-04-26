package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.Utils;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.PromotionPlan;
import com.hrtx.web.service.AccountService;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.ConsumerService;
import com.hrtx.web.service.ShareService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.ParseException;
import java.util.*;

@RestController
public class ShareController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private ShareService shareService;


	/**
	 * 查询合伙人信息
	 */
	@GetMapping("/api/partner/user-info")
	@Powers({PowerConsts.NOPOWER})
	public Result findInfo(HttpServletRequest request){
		return this.shareService.partnerInfo();
	}

	////////////////////////////////////////////////////////
	/**
	 * 查看推广记录
	 */
	@GetMapping("/api/partner/share")
	@Powers({PowerConsts.NOPOWER})
	public PageInfo queryShareList(HttpServletRequest request){
		int start=NumberUtils.toInt(request.getParameter("pageNum"));
		int limit=NumberUtils.toInt(request.getParameter("limit"));
		return this.shareService.queryShareList(start,limit);
	}
	/**
	 * 查看推广订单记录
	 */
	@GetMapping("/api/order-settle/list")
	@Powers({PowerConsts.NOPOWER})
	public PageInfo queryOrderSettleList(HttpServletRequest request){
		int start=NumberUtils.toInt(request.getParameter("pageNum"));
		int limit=NumberUtils.toInt(request.getParameter("limit"));
		return this.shareService.queryOrderSettle(start,limit);
	}
	/**
	 * 生成分享地址  由合伙人提交生成分享地址
	 */
	@PostMapping("/api/partner/share-del")
	@Powers({PowerConsts.NOPOWER})
	public Result delShare(HttpServletRequest request){
		String share_id=request.getParameter("share_id");
		return this.shareService.delShare(NumberUtils.toInt(share_id));
	}
	/**
	 * 生成分享地址  由合伙人提交生成分享地址
	 */
	@PostMapping("/api/partner/share-url")
	@Powers({PowerConsts.NOPOWER})
	public Result shareUrl(HttpServletRequest request){
		String num_id=request.getParameter("num_id");
		return this.shareService.shareUrl(NumberUtils.toInt(num_id));
	}
	/**
	 * 生成推广卡片
	 */
	@PostMapping("/api/partner/share-card")
	@Powers({PowerConsts.NOPOWER})
	public Result shareCard(HttpServletRequest request){
		int num_id=NumberUtils.toInt(request.getParameter("num_id"));
		String head_img_file=request.getParameter("head_img");
		String nick_name=request.getParameter("nick_name");
		String promotion_tip=request.getParameter("promotion_tip");
		String share_page=request.getParameter("share_page");
		if(StringUtils.isEmpty(share_page))return new Result(Result.ERROR,"分享的地址不能为空");
		if(StringUtils.isEmpty(promotion_tip))return new Result(Result.ERROR,"说一段分享推荐语吧");
		Result result=this.shareService.shareCard(num_id,head_img_file,nick_name,promotion_tip,share_page);
		return result;
	}

	/**
	 * 分享浏览记录
	 */
	@PostMapping("/api/num-browse")
	@Powers({PowerConsts.NOPOWER})
	public Result addBrowse(HttpServletRequest request){
		String open_url=request.getParameter("open_url");
		int num_id=NumberUtils.toInt(request.getParameter("num_id"));
		int share_id=NumberUtils.toInt(request.getParameter("share_id"));
		String chennel=request.getParameter("chennel");
		return this.shareService.addBrowse(num_id,chennel,open_url,share_id);
	}

	/**
	 * 分享浏览记录
	 */
	@GetMapping("/api/share-browse")
	@Powers({PowerConsts.NOPOWER})
	public PageInfo shareBrowse(HttpServletRequest request){
		int start=NumberUtils.toInt(request.getParameter("pageNum"));
		int limit=NumberUtils.toInt(request.getParameter("limit"));
		return this.shareService.shareBrowse(start,limit);
	}
	/**
	 * 订单结算
	 */
	@GetMapping("/api/order-settle")
	@Powers({PowerConsts.NOPOWER})
	public Result createOrderSettle(HttpServletRequest request){
		int order_id=NumberUtils.toInt(request.getParameter("order_id"));
		int opt=NumberUtils.toInt(request.getParameter("opt"));
		if(opt==1)return this.shareService.createOrderSettle(order_id);
		else return this.shareService.orderSettle(order_id);
	}
	/**
	 * 收支明细
	 */
	@GetMapping("/api/partner/finance-list")
	@Powers({PowerConsts.NOPOWER})
	public Object financeList(HttpServletRequest request){
		int pageNum=NumberUtils.toInt(request.getParameter("pageNum"),1);
		int limit=NumberUtils.toInt(request.getParameter("limit"),15);
		return this.shareService.financeList(pageNum,limit);
	}
	/**
	 * 提现进度
	 */
	@PostMapping("/api/partner/finance-withdraw-progresss")
	@Powers({PowerConsts.NOPOWER})
	public Result financeWithdrawProgresss(){
		return new Result(Result.OK,"");
	}
	/**
	 * 提现
	 */
	@PostMapping("/api/partner/finance-withdraw")
	@Powers({PowerConsts.NOPOWER})
	public Result financeWithdraw(HttpServletRequest request){
		Double amt=NumberUtils.toDouble(request.getParameter("amt"));
		return this.shareService.financeWithdraw(amt);
	}

	//////////////////////////////////////////////////////
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
		int is_limit=NumberUtils.toInt(request.getParameter("isLimit"),0);
		double limit_award=NumberUtils.toDouble(request.getParameter("limitAward"),0);
		if(is_limit==0)limit_award=0d;
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
