package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Channel;
import com.hrtx.web.pojo.NumberPrice;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface NumberPriceMapper extends Mapper<NumberPrice>,BaseMapper<NumberPrice>{

    void insertBatch(@Param("numberPriceList") List<NumberPrice> numberPriceList);

    void updateNumberPrice(@Param("skuId") Long skuId);

    void insertListNumPrice(@Param("skuId") Long skuId,@Param("basePrice") double basePrice,@Param("addCorpId") Long addCorpId);

}
