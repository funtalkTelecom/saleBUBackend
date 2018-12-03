package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.NumRule;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface NumRuleMapper extends Mapper<NumRule>,BaseMapper<NumRule>{
    List<NumRule> getNumRuleByNum(@Param("num") String num);

    void insertBatch(@Param("numRuleList") List insertList);

    void deleteByNums(@Param("numbers") String numbers);

    void batchInsert(@Param("list") List<NumRule> batch);
}
