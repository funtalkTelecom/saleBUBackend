package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Channel;
import com.hrtx.web.pojo.NumFreeze;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface ChannelMapper extends BaseMapper<Channel>,Mapper<Channel> {
//    Long queryFreeze(@Param("numId") long numId);
}