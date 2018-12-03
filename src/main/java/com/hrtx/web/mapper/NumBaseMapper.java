package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.NumBase;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NumBaseMapper extends Mapper<NumBase>,BaseMapper<NumBase> {
    void batchInsert(@Param("list") List<NumBase> batch);
}