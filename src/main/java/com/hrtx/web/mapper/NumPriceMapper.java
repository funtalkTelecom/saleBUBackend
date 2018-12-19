package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.pojo.NumRule;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface NumPriceMapper extends BaseMapper<NumPrice>,Mapper<NumPrice> {
    long countList(@Param("param") NumPrice numPrice);

    long matchNumPrice();

    void batchUpateFeature(@Param("batch") List<NumRule> batch, @Param("feature") String feature);

    void updateFeature(@Param("feature") String feature);

    Map queryAgentNumprice(@Param("param") NumPrice numPrice);
}