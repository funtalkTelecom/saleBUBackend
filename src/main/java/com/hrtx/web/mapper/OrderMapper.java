package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface OrderMapper extends Mapper<Order>,BaseMapper<Order>{
    void insertBatch(@Param("orderList") List<Order> orderList);

    void deleteByOrderid(@Param("orderid") Long orderId);

    void signByOrderid(Order order);

    List<Map> findOrderSignList();

    List<Map> findOrderListByNumIdAndConsumerId(@Param("numId") Long numId,@Param("consumerId") Long consumerId);

    Order findOrderInfo(@Param("orderid") Long id);

    Page<Object> getOrderByConsumer(@Param("order") Order order, @Param("st") String st);

    Page<Object> queryPageList(@Param("param") Order order, @Param("sellerId") Long sellerId);
}
