package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.OrderSettle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderSettleMapper  extends Mapper<OrderSettle>,BaseMapper<OrderSettle> {
    /**
     * 统计分享的情况
     * @param consumer_id
     * @return
     */
    public List<Object> countConsumerSettle(@Param("consumer_id") int consumer_id);
}