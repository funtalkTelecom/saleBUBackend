package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Iccid;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface IccidMapper extends Mapper<Iccid>,BaseMapper<Iccid>{
    /**
     * 插入临时表
     * @param allImeis
     */
    int batchInsertTemp(@Param("list")List allImeis);
    void iccidEditStatus(Iccid iccid);
}