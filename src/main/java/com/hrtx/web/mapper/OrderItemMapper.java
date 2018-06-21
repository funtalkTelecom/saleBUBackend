package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface OrderItemMapper extends Mapper<OrderItem>,BaseMapper<OrderItem>{
    void insertBatch(@Param("orderItemList") List<OrderItem> orderItems);

    Page<Object> queryPageListDetail(OrderItem orderItem);
}
