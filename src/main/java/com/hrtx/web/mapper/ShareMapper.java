package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Share;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShareMapper extends Mapper<Share>,BaseMapper<Share> {

    List<Share> findNumShare(@Param("consumer_id") Integer consumer_id,@Param("num_id") Integer num_id);


}