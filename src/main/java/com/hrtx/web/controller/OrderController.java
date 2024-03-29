package com.hrtx.web.controller;

import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.global.*;
import com.hrtx.global.Constants;
import com.hrtx.global.LockUtils;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.pojo.OrderItem;
import com.hrtx.web.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
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
	@Autowired private ApiOrderService apiOrderService;

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

	@RequestMapping("/order-push-storage")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_RECEIPT})
	public Result orderPushStorage(HttpServletRequest request){
		String order_id=request.getParameter("orderId");
		Result result=apiOrderService.payPushOrderToStorage(Integer.valueOf(order_id));
		return result;
	}

	/**
	 *线下单退款
	 * @param order
	 * @return
	 */
	@RequestMapping("/order-refund")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_REFUND})
	public Result reFund(Order order, HttpServletRequest request){
		return orderService.reFund(order,request);
	}

	/***
	 * 线上单退款
	 * @param order
	 * @param request
	 * @return
	 */
	@RequestMapping("/order-refund-live")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_REFUND_LIVE})
	public Result reFundLive(Order order, HttpServletRequest request){
		return orderService.reFundLive(order,request);
	}
	/***
	 * 后台取消订单
	 * @return
	 */
	@RequestMapping("/order-cancel")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_CANCEL,PowerConsts.ORDERMOUDULE_COMMON_CANCEL_OUT})
	public Result orderCancel( String orderId,String reason){
		if (!LockUtils.tryLock(orderId)) return new Result(Result.ERROR, "请稍后再试!");
		try {
			Order order  = orderService.findOrderInfo(NumberUtils.toInt(orderId));
			if( (order.getStatus()==Constants.ORDER_STATUS_1.getIntKey()
					|| order.getStatus()==Constants.ORDER_STATUS_2.getIntKey()
					|| order.getStatus()==Constants.ORDER_STATUS_3.getIntKey()
					|| order.getStatus()==Constants.ORDER_STATUS_21.getIntKey()) && SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_CANCEL)) {
				return apiOrderService.CancelOrder(orderId,reason);
				//仓库未发货取消
			} else if((order.getStatus()==Constants.ORDER_STATUS_4.getIntKey() || order.getStatus()==Constants.ORDER_STATUS_5.getIntKey()
					|| order.getStatus()==Constants.ORDER_STATUS_6.getIntKey()) && SessionUtil.hasPower(PowerConsts.ORDERMOUDULE_COMMON_CANCEL_OUT)
					) {  //仓库已发货取消
				return apiOrderService.CancelOrder(orderId,reason);
			}else {
				return new Result(Result.ERROR, "您没有权限取消订单");
			}
		}finally {
			LockUtils.unLock(orderId);
		}
	}

	@RequestMapping("/order-yPayAmt")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_RECEIPT})
	public Result yPayAmt(Order order, HttpServletRequest request){
		return orderService.findFundOrderAmt(order, request);
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

	@RequestMapping("/order-check")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_CHECK})
	public Result payCheck(Order order){
		return orderService.payCheck(order);
	}

	@RequestMapping("/adjust-order")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_ADJUST})
	public Result adjustOrder(Order order){
		return orderService.adjustOrder(order);
	}

	@RequestMapping("/again-order")
	@Powers({PowerConsts.ORDERMOUDULE_COMMON_AGAIN})
	public Result againOrder(Order order){
		int orderId = order.getOrderId() == null ? 0 : order.getOrderId();
		if(!LockUtils.tryLock("again"+orderId)) return new Result(Result.ERROR, "订单正在补发中,请稍后再试！");
		try{
			return apiOrderService.deliverAgainOrder(orderId);
		}finally {
			LockUtils.unLock("again"+orderId);
		}
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
			if(result.getCode() == Result.OK) {
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
				renderHtml("success");
			}
//			else {
//				renderHtml("取消订单失败");
//			}
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
