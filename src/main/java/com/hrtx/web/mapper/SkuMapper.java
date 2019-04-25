package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface SkuMapper extends Mapper<Sku>,BaseMapper<Sku>{

    void deleteSkuByGid(Sku sku);

    void insertBatch(@Param("skuList") List<Sku> skuList);

    List<Sku> findSkuInfo(@Param("gId") Integer gId);

    List getSkuListBySkuids(@Param("skuids") String skuids);

    Sku getSkuBySkuid(@Param("skuid") Integer skuId);

    void updateSkuNum(@Param("skuId") Integer skuId,@Param("quantity") Integer quantity);

    int updateSkuNumDown(@Param("skuId") Integer skuId,@Param("quantity") Integer quantity);

    int updateSkuNumWithDataNum(@Param("order_amount") Integer order_amount,@Param("sku_id") Integer sku_id);

    void updateSku(Sku sku);

    void updateSkuStatus(Sku sku);

    void deleteSkuBySkuids(@Param("delSkus") String delSkus);

    List findNumStatus(@Param("gId") Long gId);

    List queryStatusList(@Param("gId") Integer gId,@Param("statusArry") String statusArry);

    List querySkuList(@Param("statusArry") String statusArry);


}
