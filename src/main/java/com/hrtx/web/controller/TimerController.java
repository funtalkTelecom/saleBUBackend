package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
/**
 * 定时器处理类
 */
public class TimerController extends BaseReturn{
	public static final Logger log = LoggerFactory.getLogger(TimerController.class);

	@Autowired private GoodsService goodsService;
	@Autowired private NumService numService;
	@Autowired private ApiOrderService apiOrderService;
	@Autowired private EPSaleNoticeService epSaleNoticeService;
	@Autowired private EPSaleService epSaleService;
	@Autowired private OrderService orderService;
	@Autowired private LyCrmService lyCrmService;
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
	public Result second(HttpServletRequest request){
		// /timer/second?task=goods-expire
		String task=request.getParameter("task");
		int date_offset= NumberUtils.toInt(request.getParameter("date_offset"));
		String req_ip=SessionUtil.getUserIp();
		log.info(String.format("准备执行调用定时器，请求任务[%s],请求IP[%s]，限定ip[%s]",task,req_ip,SystemParam.get("timer_limit_ip")));
		//INSERT INTO `tb_system`(`id`, `key_id`, `key_value`, `remark`, `is_audit`) VALUES (60, 'timer_limit_ip', '127.0.0.1', '定时器调用ip限定', '1')
		if(!SystemParam.contains("timer_limit_ip",req_ip))return new Result(Result.ERROR,"非指定请求IP");
		if(StringUtils.equals(task,"goods-expire")){//商品过期下架，平台有过期自动隐藏，所以不紧急 可23点执行一次
			goodsService.goodsTimer();
		}else if(StringUtils.equals(task,"order-expire-cancle")){//订单到期取消	可每分钟执行一次
			orderService.TowHoursCancelOrderStatusTimer();
		}else if(StringUtils.equals(task,"freeze-num")){//解冻客服号码	可每分钟执行一次
			this.numService.unFreezeSystem();
		}else if(StringUtils.equals(task,"order-sign")){//订单签收定时器	可23点执行一次
			this.apiOrderService.signOrderSystem();
		}else if(StringUtils.equals(task,"ep-order-expire-cancle")){//订单到期取消	可每分钟执行一次
			orderService.LastPayTimeCancelOrderStatusTimer();
		}else if(StringUtils.equals(task,"ep-active-notice")){//TODO 竞拍活动开始通知定时器
			this.epSaleNoticeService.startEPSaleListSystemNotice();
		}else if(StringUtils.equals(task,"ep-create-order")){//TODO 竞拍创建订单，需要处理的及时，外部隔期调用视乎也不合适，应该是由外部指定时间调用
			this.epSaleService.epsaleOrder();
		}else if(StringUtils.equals(task,"ly-card")){//上传开卡文件 每日晚上23点执行
			this.lyCrmService.createAgentCardFile();
		}else if(StringUtils.equals(task,"ly-card-result")){//解析开卡结果  每日早上7点执行
			this.lyCrmService.praseOpenCardFileResult();
		}else if(StringUtils.equals(task,"ly-download-num")){//下载号码资源	每日早上7点执行
			this.lyCrmService.praseLyData(date_offset);
		}else if(StringUtils.equals(task,"ly-upload-num")){//上传iccid资源 每日早上2点执行
			this.lyCrmService.uploadLyIccidData();
		}else if(StringUtils.equals(task,"ly-synch-num-price-agent")){//
			this.lyCrmService.paySynchNumPriceAgentData();
		}else if(StringUtils.equals(task,"base-to-num")){//
			int sellerId= NumberUtils.toInt(request.getParameter("sellerId"));
			if(sellerId == 0) return new Result(Result.ERROR,"请传入虚商id");
			this.lyCrmService.synchBaseToNum(sellerId);
		}
		return new Result(Result.OK,task+"在"+Utils.getCurrentDate("yyyy-MM-dd HH:mm:ss") +"执行成功");
	}
}
