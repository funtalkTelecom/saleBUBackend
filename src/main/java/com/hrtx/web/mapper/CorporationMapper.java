package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Corporation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CorporationMapper extends Mapper<Corporation>,BaseMapper<Corporation>{
    Corporation findCorporationInfo(@Param("corpId") Long id);
}
