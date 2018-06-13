package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Number;
import org.apache.ibatis.annotations.Param;

public interface NumberMapper extends Mapper<Number>,BaseMapper<Number>{
    void updateStatus(Number number);

    int checkNumberIsOk(Number number);

    Page<Object> queryPageListApi(@Param("tags") String tags);

    Number getNumInfoById(@Param("id") String id);
}
