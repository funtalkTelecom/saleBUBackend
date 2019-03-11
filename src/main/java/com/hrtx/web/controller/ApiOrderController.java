package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.LockUtils;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.service.ApiOrderService;
import com.hrtx.web.service.UserService;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiOrderController extends BaseReturn {

    public final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ApiOrderService apiOrderService;
    @Autowired
    UserService userService;

    @PostMapping("/order")
    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result createOrder(HttpServletRequest request) {
        Integer skuid = NumberUtils.toInt(request.getParameter("skuid"));
        Integer addrid = NumberUtils.toInt(request.getParameter("addrid"));
        String type =request.getParameter("type");
        if ( skuid==0 || addrid==0 ) return new Result(Result.ERROR, "参数有误");
        if ("1".equals(type)) {//白卡1或普号2
            Integer numcount = NumberUtils.toInt(request.getParameter("numcount"));
            if (numcount ==0) return new Result(Result.ERROR, "请填写购买数量");
            return apiOrderService.submitGoodsOrder(skuid,numcount,addrid,"");
        } else {  //普通靓号3或超级靓号4
            Integer numid = NumberUtils.toInt(request.getParameter("numid"));
            Integer mealid = Integer.valueOf(request.getParameter("mealid"));
            if ( numid==0 || mealid==0) return new Result(Result.ERROR, "参数有误");
            return apiOrderService.submitNumOrder(skuid, numid, mealid, addrid, "");
        }
    }
    @GetMapping("/order-express")
    @Powers(PowerConsts.NOLOGINPOWER)
    @ResponseBody
    public Result orderExpress(HttpServletRequest request) {
        Integer order_id = NumberUtils.toInt(request.getParameter("order_id"));
        Result result=this.apiOrderService.queryExpressInfo(order_id);
        return result;
    }
//    @PostMapping("/order")
////    @Powers(PowerConsts.NOPOWER)
//    @Powers(PowerConsts.NOLOGINPOWER)
//    @ResponseBody
//    public Result createOrder(HttpServletRequest request){
//        String type =  request.getParameter("type");
//        String skuid = request.getParameter("skuid");
//        String numid = request.getParameter("numid");
////        if (skuid == null || "".equals(skuid)) return new Result(Result.ERROR, "skuid不能为空");
//        Map<String, String> param = new HashMap<>();
//        param.put("user-agent", request.getHeader("user-agent"));
//        param.put("type", type);
//        param.put("skuid", skuid);
//        param.put("numid", numid);
//        param.put("addrid", request.getParameter("addrid"));
//        param.put("numcount", request.getParameter("numcount"));
//        param.put("mealid", request.getParameter("mealid"));
//        param.put("bossNum", request.getParameter("bossNum"));
//        param.put("phoneConsumer", request.getParameter("phoneConsumer"));
//        param.put("phoneConsumerIdType", request.getParameter("phoneConsumerIdType"));
//        param.put("phoneConsumerIdNum", request.getParameter("phoneConsumerIdNum"));
//        param.put("channel", "3");
//        if("1".equals(type)) {
//            //冻结号码
//            if (!LockUtils.tryLock(skuid)) return new Result(Result.ERROR, "请稍后再试!");
//            try {
//                return apiOrderService.createOrder(param, null);
//            }finally {
//                LockUtils.unLock(skuid);
//            }
//        }else {
//            //冻结号码
////            if (numid == null || "".equals(numid)) return new Result(Result.ERROR, "numid不能为空");
//            if (!LockUtils.tryLock(numid)) return new Result(Result.ERROR, "请稍后再试!");
//            try {
//                return apiOrderService.createOrder(param, null);
//            }finally {
//                LockUtils.unLock(numid);
//            }
//        }
//    }

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

    @GetMapping("/cancel-order")
//  @Powers(PowerConsts.NOPOWER)
    @Powers(PowerConsts.NOLOGINPOWER)
    public Result CancelOrder(@RequestParam("orderId") String orderId,@RequestParam("reason") String reason){
        return apiOrderService.CancelOrderAllCase(orderId,reason);
    }

    }
