package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.LockUtils;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.service.ApiOrderService;
import com.hrtx.web.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ApiOrderController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
    ApiOrderService apiOrderService;
	@Autowired
    UserService userService;


    @PostMapping("/order")
    @Powers(PowerConsts.NOPOWER)
//    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result createOrder(HttpServletRequest request){
        String type =  request.getParameter("type");
        String skuid = request.getParameter("skuid");
        String numid = request.getParameter("numid");
        if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
        if("1".equals(type)) {
            //冻结号码
            if (!LockUtils.tryLock(skuid)) return new Result(Result.ERROR, "请稍后再试!");
            try {
                return apiOrderService.createOrder(request, null);
            }finally {
                LockUtils.unLock(skuid);
            }
        }else {
            //冻结号码
            if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");
            if (!LockUtils.tryLock(numid)) return new Result(Result.ERROR, "请稍后再试!");
            try {
                return apiOrderService.createOrder(request, null);
            }finally {
                LockUtils.unLock(numid);
            }
        }
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

    @PostMapping("/cancel-order")
//  @Powers(PowerConsts.NOPOWER)
    @Powers(PowerConsts.NOLOGINPOWER)
    public Result CancelOrder(@RequestParam("orderId") String orderId,@RequestParam("reason") String reason){
        return apiOrderService.CancelOrderAllCase(orderId,reason);
    }

    }
