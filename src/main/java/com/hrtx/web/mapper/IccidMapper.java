package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Iccid;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface IccidMapper extends Mapper<Iccid>,BaseMapper<Iccid>{
    /**
     * 插入回调串码
     * @param allImeis
     */
    int batchInsertTemp(@Param("list") List allImeis, @Param("orderId") Long orderId);

    void iccidEditStatus(Iccid iccid);

    /**
     * 插入未找到的仓库回调iccid
     * @param consumer
     * @return
     */
    int batchInsertNoFund(@Param("consumer") Long consumer, @Param("orderId") Long orderId);

    /**
     * 更新已找到的的iccid  客户订单归属
     * @param consumer
     */
    int batchUpdate(@Param("consumer") Long consumer, @Param("orderId") Long orderId);

    /**
     * 删除回调串码
     * @param orderId
     */
    void deleteTempByBatchNum(@Param("orderId") Long orderId);

    /**
     * 分组查询回调串码
     * @param orderId
     * @return
     */
    List<Map> queryTempItemsByBatchNum(@Param("orderId") Long orderId);

    /**
     * 回调串码与明细号码匹配
     * @param item_id
     * @return
     */
    List matchOrderItem(@Param("item_id") long item_id);
}