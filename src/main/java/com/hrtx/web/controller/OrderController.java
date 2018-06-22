package com.hrtx.web.controller;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.pojo.OrderItem;
import com.hrtx.web.service.OrderItemService;
import com.hrtx.web.service.OrderService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	public Result receipt(Order order){
		return orderService.pageOrder(order);
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
	public void deliverOrderCallback(HttpServletRequest request){
		try{
			String param = this.getParamBody(request);
			log.info("接收到发货回调参数["+param+"]");
			StorageInterfaceRequest storageInterfaceRequest = StorageInterfaceRequest.create(param, SystemParam.get("key"));
//			Result result = orderService.
		}catch (ServiceException e) {
			log.error(e.getMessage(), e);
			renderHtml(e.getMessage());
		}catch (Exception e) {
			long err_no=System.currentTimeMillis();
			log.error("系统未知异常"+err_no, e);
			renderHtml("系统未知异常"+err_no);
		}
	}
}
