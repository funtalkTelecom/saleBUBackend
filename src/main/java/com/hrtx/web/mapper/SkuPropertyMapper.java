package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.SkuProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SkuPropertyMapper extends Mapper<SkuProperty>,BaseMapper<SkuProperty>{
    void deleteSkuPropertyBySkuid(SkuProperty skuProperty);

    void insertBatch(@Param("skuPropertyList") List<SkuProperty> skuPropertyList);

    List<SkuProperty> findSkuPropertyByGid(@Param("gId") Long gId);

    void deleteSkuPropertyByGid(SkuProperty skuProperty);

    List findSkuPropertyBySkuid(@Param("skuid") Long skuid);

    List findSkuPropertyBySkuidForOrder(@Param("skuid") Long skuid);
}
