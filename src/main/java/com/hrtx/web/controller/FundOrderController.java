package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.pojo.FundOrder;
import com.hrtx.web.service.DictService;
import com.hrtx.web.service.FundOrderService;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class FundOrderController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired private FundOrderService fundOrderService;

	@PostMapping("/pay-order")
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result payOrder(@Validated FundOrder fundOrder, BindingResult result){
        if(result.hasErrors()) return new Result(Result.ERROR, this.getErrors(result.getFieldErrors()));
//		Result result = fundOrderService.payAddOrder(busi_type, amt, payer, order_name, sourceId);
        fundOrder.setPayer("oKvRM5YKZpcp1nIFcyBgApnC-bLk");
		return fundOrderService.payAddOrder(fundOrder.getBusi(), fundOrder.getAmt(), fundOrder.getPayer(), fundOrder.getOrder_name(), fundOrder.getSourceId());
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



}
