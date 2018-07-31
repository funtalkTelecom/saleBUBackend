package com.hrtx.web.controller;

import com.hrtx.config.annotation.NoRepeat;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.FundOrder;
import com.hrtx.web.pojo.Groups;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.pojo.User;
import com.hrtx.web.service.FundOrderService;
import com.hrtx.web.service.OrderService;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class FundOrderController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private FundOrderService fundOrderService;
	@Autowired private OrderService orderService;

    @PostMapping("/pay-order")
    @Powers({PowerConsts.NOPOWER})
    public Result payOrder(String orderId, String payMenthodId){
        return orderService.payOrder(NumberUtils.toLong(orderId), payMenthodId);
    }

    @PostMapping("/pay-balance")
    @Powers({PowerConsts.NOPOWER})
    public Result payBalance(@Validated(value = {Groups.FundOrderPayOrder.class}) Order order, BindingResult result, Long mealId){
        if(result.hasErrors()) return new Result(Result.ERROR, this.getErrors(result.getFieldErrors()));
        return orderService.payBalance(order, mealId);
    }

	@RequestMapping("/pingan-pay-result")
	@Powers({PowerConsts.NOLOGINPOWER})
	public void payResult(HttpServletRequest request){
		Map<String,String> params = this.getParamMap(request);
        Result result = fundOrderService.pinganPayResult(params);
        if(result.getCode() == Result.OK) {
            renderHtml("notify_success");
        }
	}

	@RequestMapping("/yzffq-pay-result")
	@Powers({PowerConsts.NOLOGINPOWER})
	public void yzffqPayResult(HttpServletRequest request){
		Map<String,String> params = this.getParamMap(request);
        Result result = fundOrderService.yzffqPayResult(params);
        if(result.getCode() == Result.OK) {
            renderHtml("notify_success");
        }
	}

	@RequestMapping("/yzffq-pay-result-jump")
	@Powers({PowerConsts.NOLOGINPOWER})
	public ModelAndView yzffqPayResultJump(HttpServletRequest request){
        return new ModelAndView("admin/yzffq-jump");
	}
}
