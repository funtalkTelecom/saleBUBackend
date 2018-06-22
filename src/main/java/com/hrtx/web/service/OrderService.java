package com.hrtx.web.service;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.CommonMap;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.StorageApiCallUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.OrderItemMapper;
import com.hrtx.web.mapper.OrderMapper;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.pojo.OrderItem;
import com.hrtx.web.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.abel533.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

	@Autowired
	private OrderMapper orderMapper;
    @Autowired private OrderItemMapper orderItemMapper;

	public Result pageOrder(Order order) {
		User user = SessionUtil.getUser();
		if(user==null) return new Result(Result.ERROR, "未获取到用户");
		PageHelper.startPage(order.getPageNum(),order.getLimit());
		Page<Object> ob=this.orderMapper.queryPageList(order, user.getId());
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

	public Order findOrderById(Long id) {
		Order order = orderMapper.findOrderInfo(id);
		return order;
	}


    /**
     * 发货
     * @param orderId
     * @return
     */
    public Result payDeliverOrder(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null) return new Result(Result.ERROR, "订单不存在");
        if(order.getIsDel() == 1 || order.getStatus() != 2) return new Result(Result.ERROR, "订单状态异常");
        List commodities = new ArrayList();
        Example example = new Example(OrderItem.class);
        example.createCriteria().andEqualTo("orderId", order.getOrderId());
        List<OrderItem> items = orderItemMapper.selectByExample(example);
        if(items.size() == 0) return new Result(Result.ERROR, "订单下未找到产品");
        for (OrderItem orderItem:items) {
            commodities.add(CommonMap.create("item_id",orderItem.getItemId()).put("companystock_id", orderItem.getCompanystockId()).put("quantity", orderItem.getQuantity()).getData());
        }
        Map param = CommonMap.create("receiver", order.getPersonName()).put("phone",order.getPersonTel()).put("address",order.getAddress())
                .put("receiver_company", order.getConsumerName()).put("remark",order.getConment()).put("callback_url",SystemParam.get("domain-full")+"/deliver-order-callback")
                .put("commodities", commodities).put("order_id",order.getOrderId()).getData();
        order.setStatus(3);
        orderMapper.updateByPrimaryKey(order);
        Result result = StorageApiCallUtil.storageApiCall(param, "HK0004");
        return result;
    }

    /**
     * 订单支付成功
     * @param orderId
     * @return
     */
    public Result payOrderSuccess(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null) return new Result(Result.ERROR, "订单不存在");
        if(order.getIsDel() == 1 || order.getStatus() != 1) return new Result(Result.ERROR, "订单状态异常");
        order.setPayDate(new Date());
        order.setStatus(2);
        orderMapper.updateByPrimaryKey(order);
        return new Result(Result.OK, "success");
    }
}

