package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper extends Mapper<Order>,BaseMapper<Order>{
    void insertBatch(@Param("orderList") List<Order> orderList);

    void deleteByOrderid(@Param("orderid") Long orderId);

    Order findOrderInfo(@Param("orderid") Long id);
}
