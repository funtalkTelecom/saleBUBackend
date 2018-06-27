package com.hrtx.web.controller;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.FundOrder;
import com.hrtx.web.pojo.Groups;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.pojo.OrderItem;
import com.hrtx.web.service.NumService;
import com.hrtx.web.service.OrderItemService;
import com.hrtx.web.service.OrderService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderItemService orderItemService;
	@Autowired private NumService numService;

	@RequestMapping("/order-query")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_QUEYR})
	public ModelAndView orderQuery(Order order){
		return new ModelAndView("admin/order/order-query");
	}

	@RequestMapping("/order-list")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_QUEYR})
	public Result listOrder(Order order){
		return orderService.pageOrder(order);
	}

	@RequestMapping("/order-receipt")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_RECEIPT})
	public Result receipt(Order order, HttpServletRequest request){
		return orderService.receipt(order, request);
	}

	@RequestMapping("/item-list")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_QUEYR})
	public Result listItem(OrderItem orderItem){
		orderItem.setPageNum(1);
		orderItem.setLimit(999999);
		return orderItemService.pageOrderItem(orderItem);
	}

	@RequestMapping("/order-info")
	@ResponseBody
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_QUEYR})
	public Map orderInfo(Order order){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", Result.OK);
		map.put("data", orderService.findOrderById(order.getOrderId()));
		return map;
	}

	@RequestMapping("/deliver-order-callback")
	@Powers({PowerConsts.NOLOGINPOWER})
	public String deliverOrderCallback(HttpServletRequest request){
		try{
			String param = this.getParamBody(request);
			log.info("接收到发货回调参数["+param+"]");
			StorageInterfaceRequest storageInterfaceRequest = StorageInterfaceRequest.create(param, SystemParam.get("key"));
			Result result = orderService.updateDeliverCallbackInfo(storageInterfaceRequest);
			if(result.getCode() == 200) {
                renderHtml("success");
				Order order = (Order) result.getData();
                String goodsType = order.getSkuGoodsType();
                if("1".equals(goodsType) || "2".equals(goodsType) || "4".equals(goodsType)) {//白卡 或 普号 或 超靓
                    try{
                        result = numService.blindNum(order.getOrderId());
                        if(result.getCode() == Result.OK) {
                            orderService.updateDqx(order.getOrderId());
                        }
                    }catch (Exception e){
                        log.error("绑卡异常", e);
                    }
                }
                return null;
			}else {
                return renderHtml(String.valueOf(result.getData()));
            }
		}catch (ServiceException e) {
			log.error(e.getMessage(), e);
			return renderHtml(e.getMessage());
		}catch (Exception e) {
			long err_no=System.currentTimeMillis();
			log.error("系统未知异常"+err_no, e);
			return renderHtml("系统未知异常"+err_no);
		}
	}

}
