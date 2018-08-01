package com.hrtx.web.controller;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.pojo.OrderItem;
import com.hrtx.web.service.AuctionDepositService;
import com.hrtx.web.service.NumService;
import com.hrtx.web.service.OrderItemService;
import com.hrtx.web.service.OrderService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
	@Autowired
	private AuctionDepositService auctionDepositService;
	@Autowired private NumService numService;

	@RequestMapping("/order-query")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_QUEYR})
	public ModelAndView orderQuery(Order order){
		return new ModelAndView("admin/order/order-query");
	}

	@RequestMapping("/order-list")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_QUEYR})
	public Result listOrder(Order order, HttpServletRequest request){
		order.setStatus(Integer.parseInt(StringUtils.isBlank(request.getParameter("qstatus"))?"-1":request.getParameter("qstatus")));
		return orderService.pageOrder(order);
	}

	@RequestMapping("/order-receipt")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_RECEIPT})
	public Result receipt(Order order, HttpServletRequest request){
		return orderService.payReceipt(order, request);
	}

	@RequestMapping("/order-payDeliver")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_RECEIPT})
	public Result payDeliver(Order order, HttpServletRequest request){
		return orderService.payDeliver(order, request);
	}

	@RequestMapping("/order-bindCard")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_BINDCARD})
	public Result payBindCard(Order order, HttpServletRequest request){
		return orderService.bindCard(order/*, request*/);
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

	@RequestMapping("/order-deposit")
	@ResponseBody
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_QUEYR})
	public Map orderDeposit(Order order){
		Map m = auctionDepositService.findAuctionDepositListByOrderId(order.getOrderId());
		return m;
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

	/***
	 * 取消订单回调地址
	 * @return
	 */
	@RequestMapping("/cancel-order-callback")
	@Powers({PowerConsts.NOLOGINPOWER})
	public String CancelOrderCallback(HttpServletRequest request) {
		try {
			String param = this.getParamBody(request);
			log.info("接收到发货回调参数["+param+"]");
			StorageInterfaceRequest storageInterfaceRequest = StorageInterfaceRequest.create(param, SystemParam.get("key"));
			//接收仓库回调，取消订单操作
			Result result = orderService.OrderCallbackStatus(storageInterfaceRequest);
			if (result.getCode() == 200) {
				renderHtml("取消订单成功");
			} else {
				renderHtml("取消订单失败");
			}
			return null;
		}catch (ServiceException e) {
			log.error(e.getMessage(), e);
			return renderHtml(e.getMessage());
		}catch (Exception e){
			log.error("未知异常", e);
			return renderHtml("error");
		}
	}

}
