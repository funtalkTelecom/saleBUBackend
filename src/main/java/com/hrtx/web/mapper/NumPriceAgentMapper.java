package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.NumPriceAgent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface NumPriceAgentMapper extends Mapper<NumPriceAgent>,BaseMapper<NumPriceAgent> {
    List<Integer> queryNumPriceAgentSkus();

    List<Integer> queryGoodSkus();

    List listNum(@Param("StrNums") String StrNums,@Param("agentId") Integer agentId,@Param("sellerId") Integer sellerId);

    void updateNumPriceAgent(NumPriceAgent numPriceAgent);

}