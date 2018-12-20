package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.service.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/timer")
public class TimerController extends BaseReturn{
	public static final Logger log = LoggerFactory.getLogger(TimerController.class);

	@Autowired private GoodsService goodsService;
	@Autowired private NumService numService;
	@Autowired private ApiOrderService apiOrderService;
	@Autowired private EPSaleNoticeService epSaleNoticeService;
	@Autowired private EPSaleService epSaleService;
	@Autowired private OrderService orderService;
	/**
	 * unFreezeSystem 定时器
	 *
	 * 	public void signOrderSystem()
	 * 	public void startEPSaleListSystemNotice()
	 * 	public void epsaleOrder()
	 *
	 * 	GoodsService.goodsTimer()  //判断是否过期的上架商品
	 * OrderService.TowHoursCancelOrderStatusTimer()//待付款订单120分钟后，系统自动取消，除竞拍订单
	 * OrderService.LastPayTimeCancelOrderStatusTimer() //系统自动取消竞拍订单
	 */
	@GetMapping("/second")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result accountDelete(HttpServletRequest request){
		// /timer/second?task=goods-expire
		String task=request.getParameter("task");
		String req_ip=SessionUtil.getUserIp();
		if(!SystemParam.contains("timer-ip",req_ip))return new Result(Result.OK,"非指定请求IP");
		if(StringUtils.equals(task,"goods-expire")){//商品过期下架，平台有过期自动隐藏，所以不紧急 可每日12、23点执行
			goodsService.goodsTimer();
		}else if(StringUtils.equals(task,"order-expire-cancle")){//订单到期取消	可每分钟执行一次
			orderService.TowHoursCancelOrderStatusTimer();
		}else if(StringUtils.equals(task,"freeze-num")){//解冻客服号码	可每分钟执行一次
			this.numService.unFreezeSystem();
		}else if(StringUtils.equals(task,"order-sign")){//订单签收定时器	可每日12、23点执行
			this.apiOrderService.signOrderSystem();
		}else if(StringUtils.equals(task,"ep-order-expire-cancle")){//订单到期取消	可每分钟执行一次
			orderService.LastPayTimeCancelOrderStatusTimer();
		}else if(StringUtils.equals(task,"ep-active-notice")){//竞拍活动开始通知定时器
			this.epSaleNoticeService.startEPSaleListSystemNotice();
		}else if(StringUtils.equals(task,"ep-create-order")){//竞拍创建订单，需要处理的及时，外部隔期调用视乎也不合适，应该是由外部指定时间调用
			this.epSaleService.epsaleOrder();
		}
		return new Result(Result.OK,"执行成功");
	}
}
