package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Sku;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SkuMapper extends Mapper<Sku>,BaseMapper<Sku>{
    void deleteSkuByGid(Sku sku);

    void insertBatch(@Param("skuList") List<Sku> skuList);

    List<Sku> findSkuInfo(@Param("gId") Long gId);
}
