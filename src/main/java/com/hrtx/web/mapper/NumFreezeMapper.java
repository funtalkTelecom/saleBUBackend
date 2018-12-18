package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.NumFreeze;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface NumFreezeMapper extends BaseMapper<NumFreeze>,Mapper<NumFreeze> {
    Integer queryFreeze(@Param("numId") Integer numId);
}