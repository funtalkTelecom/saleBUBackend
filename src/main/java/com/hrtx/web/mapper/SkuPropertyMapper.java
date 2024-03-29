package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.SkuProperty;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface SkuPropertyMapper extends Mapper<SkuProperty>,BaseMapper<SkuProperty>{
    void deleteSkuPropertyBySkuid(SkuProperty skuProperty);

    void insertBatch(@Param("skuPropertyList") List<SkuProperty> skuPropertyList);

    List<SkuProperty> findSkuPropertyByGid(@Param("gId") Integer gId);

    void deleteSkuPropertyByGid(SkuProperty skuProperty);

    List findSkuPropertyBySkuid(@Param("skuid") Integer skuid);

    List findSkuPropertyBySkuidForOrder(@Param("skuid") Integer skuid);
}
