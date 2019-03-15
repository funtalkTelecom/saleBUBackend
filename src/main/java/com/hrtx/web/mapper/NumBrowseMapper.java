package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.NumBrowse;
import org.apache.ibatis.annotations.Param;

public interface NumBrowseMapper extends Mapper<NumBrowse>,BaseMapper<NumBrowse> {
    public int updateOpenCount(@Param("consumer_id") int consumer_id,@Param("num_id") int num_id);

}