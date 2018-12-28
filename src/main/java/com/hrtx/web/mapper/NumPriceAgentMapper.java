package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.NumPriceAgent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface NumPriceAgentMapper extends Mapper<NumPriceAgent>,BaseMapper<NumPriceAgent> {
    List<Integer> queryNumPriceAgentSkus();

    List<Integer> queryGoodSkus();

    int updateNumPriceAgentStatus();

    int insertNumPriceAgent(@Param("skus") List<Integer> moreGoodSkus);

    int deleteNumPriceAgent(@Param("skus") List<Integer> moreNumPriceAgentSkus);

    int deleteCompleteNumPriceAgent();

    int updateNumPriceAgentBasePrice();

    int updateNumPriceAgentAgentPrice();

    int updateNumPriceAgentStatusByNumId(@Param("numId") int numId);
}