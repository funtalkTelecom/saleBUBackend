package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.service.ApiOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiOrderController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
    ApiOrderService apiOrderService;


    @PostMapping("/order")
//    @Powers(PowerConsts.NOPOWER)
    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result createOrder(HttpServletRequest request){
        return apiOrderService.createOrder(request, null);
    }

    @PostMapping("/orderSign")
    @Powers(PowerConsts.NOPOWER)
//    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result signOrder(Order order,HttpServletRequest request){
        return apiOrderService.signOrder(order,request);
    }

    @GetMapping("/order")
    @Powers(PowerConsts.NOPOWER)
//    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result getOrderByConsumer(HttpServletRequest request){
        return apiOrderService.getOrderByConsumer(request);
    }

    @GetMapping("/order/{id}")
//    @Powers(PowerConsts.NOPOWER)
    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result getOrderAndItemsByOrderId(HttpServletRequest request, @PathVariable("id") String id){
        return apiOrderService.getOrderAndItemsByOrderId(request, id);
    }
}
