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
    int batchInsertTemp(@Param("list") List allImeis, @Param("orderId") Integer orderId);

    void iccidEditStatus(Iccid iccid);

    /**
     * 插入未找到的仓库回调iccid
     * @param consumer
     * @return
     */
    int batchInsertNoFund(@Param("consumer") Integer consumer, @Param("orderId") Integer orderId);

    List queryNoFund(@Param("consumer") Integer consumer, @Param("orderId") Integer orderId);

    /**
     * 更新已找到的的iccid  客户订单归属
     * @param consumer
     */
    int batchUpdate(@Param("consumer") Integer consumer, @Param("orderId") Integer orderId);

    /**
     * 删除回调串码
     * @param orderId
     */
    void deleteTempByBatchNum(@Param("orderId") Integer orderId);

    /**
     * 分组查询回调串码
     * @param orderId
     * @return
     */
    List<Map> queryTempItemsByBatchNum(@Param("orderId") Integer orderId);

    /**
     * 回调串码与明细号码匹配
     * @param item_id
     * @return
     */
    List matchOrderItem(@Param("item_id") Integer item_id);

    /**
     * 从临时表插入到正式表（iccid）
     * @param orderId
     * @param selllerId
     */
    void insertFromTemp(@Param("orderId") Integer orderId, @Param("sellerId") Integer selllerId);
}