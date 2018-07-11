package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface OrderItemMapper extends Mapper<OrderItem>,BaseMapper<OrderItem>{
    void insertBatch(@Param("orderItemList") List<OrderItem> orderItems);

    Page<Object> queryPageListDetail(OrderItem orderItem);

    List queryPageListDetailForConsumer(OrderItem orderItem);

    /**
     * 查询订单下的号码
     * @param orderId
     * @return
     */
    List<Map> queryOrderNums(@Param("orderId") Long orderId);

    /**
     * 更新套餐
     * @param orderId
     * @param mealId
     */
    void updateMeal(@Param("orderId") Long orderId, @Param("mealId") Long mealId);
}
