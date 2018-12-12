package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Channel;
import com.hrtx.web.pojo.Sku;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ChannelMapper extends Mapper<Channel>,BaseMapper<Channel>{

    Map getListbyNum(@Param("num_resource") String num_resource);

}
