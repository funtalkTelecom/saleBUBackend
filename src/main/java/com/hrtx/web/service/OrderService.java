package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.NoRepeat;
import com.hrtx.dto.Result;
import com.hrtx.global.CommonMap;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.StorageApiCallUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.mapper.OrderItemMapper;
import com.hrtx.web.mapper.OrderMapper;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.pojo.OrderItem;
import com.hrtx.web.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class OrderService extends BaseService {
    @Autowired private OrderMapper orderMapper;
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
    @NoRepeat
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
                .put("receiver_company", order.getConsumerName()).put("remark",order.getConment()).put("callback_url",SystemParam.get("domain-full")+"/order/deliver-order-callback")
                .put("commodities", commodities).put("order_id",order.getOrderId()).getData();
        Result result = StorageApiCallUtil.storageApiCall(param, "HK0004");
        if(result.getCode() != Result.OK) return result;
        StorageInterfaceResponse storageInterfaceResponse = StorageInterfaceResponse.create(String.valueOf(result.getData()), SystemParam.get("key"));
        if("00000".equals(storageInterfaceResponse.getCode())) {
            order.setStatus(3);
            order.setNoticeShipmentDate(new Date());
            orderMapper.updateByPrimaryKey(order);
        }else {
            return new Result(Result.ERROR, "发货失败["+storageInterfaceResponse.getDesc()+"]");
        }
        return new Result(Result.OK, "发货成功");
    }

    /**
     * 订单支付成功
     * @param orderId
     * @return
     */
    @NoRepeat
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

