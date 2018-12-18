package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.System;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SystemMapper extends Mapper<System>,BaseMapper<System>{
    System findSystemInfo(@Param("id") Integer id);

    int checkSystemKeyIdIsExist(System system);

    void systemEdit(System system);

    void systemDelete(System system);

    void insertBatch(@Param("systemList") List<System> list);

    void systemAudit(System system);

    List<Map> findSystemParam();
}
