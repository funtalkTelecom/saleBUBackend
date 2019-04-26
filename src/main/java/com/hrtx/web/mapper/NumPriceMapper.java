package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Num;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.pojo.NumRule;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public interface NumPriceMapper extends BaseMapper<NumPrice>,Mapper<NumPrice> {
    long countList(@Param("param") NumPrice numPrice);

    List<Map> queryNewestNumPrice(@Param("corpId") Integer corpId);

    long matchNumPriceByBatch(@Param("batch") List<Map> batch);

    long matchNumPrice(@Param("corpId") Integer corpId);

    long matchNumPriceBySku(@Param("corpId") Integer corpId, @Param("skuId") Integer skuId);

    void batchUpateFeature(@Param("batch") List<NumRule> batch, @Param("feature") String feature);

    void updateFeature(@Param("feature") String feature);

    Map queryAgentNumprice(@Param("param") NumPrice numPrice);

    Integer checkNumpriceCount(@Param("param") NumPrice numPrice,@Param("resource")String resource );

    void batchUpdataIsDel(@Param("batch") Set batch, @Param("param") NumPrice numPrice);

    void insertBatchbyAgentId(@Param("batch") Set batch, @Param("param") NumPrice numPrice);

    int freezeNum(@Param("param")Num num1);

    int updateNumPriceAgentStatus();

    int insertNumPriceAgent(@Param("skus") List<Integer> moreGoodSkus);

    int deleteNumPriceAgent(@Param("skus") List<Integer> moreNumPriceAgentSkus);

    int deleteCompleteNumPriceAgent();

    int updateNumPriceAgentBasePrice();

    int updateNumPriceAgentAgentPrice();

    int updateNumPriceAgentStatusByNumId(@Param("numId") int numId);
}