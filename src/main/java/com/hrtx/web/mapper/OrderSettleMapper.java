package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.OrderSettle;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
@Component
public interface OrderSettleMapper  extends Mapper<OrderSettle>,BaseMapper<OrderSettle> {
    /**
     * 统计分享的情况
     * @param consumer_id
     * @return
     */
    public List<Object> countConsumerSettle(@Param("consumer_id") int consumer_id);

    /**
     * 统计结算用户结算月份签收的订单量
     * @param fee_type
     * @param settle_user
     * @param settle_month
     * @return
     */
    public List<Map> settleUserMonthSignCount(@Param("fee_type") int fee_type, @Param("settle_user") int settle_user, @Param("settle_month") String settle_month);

    /**
     * 查询某个月份的所有结算数据
     * @param settle_month
     * @return
     */
    public List<Map> queryMonthSettle(@Param("fee_type") int fee_type,@Param("settle_month") String settle_month,@Param("settle_user") int settle_user,@Param("limit_count") int limit_count);

    /**
     * 查询用户推广订单
     * @param consumer_id
     * @return
     */
    public Page<Object> queryOrderSettle(@Param("consumer_id") int consumer_id);
}