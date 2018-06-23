package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Iccid;

public interface IccidMapper extends Mapper<Iccid>,BaseMapper<Iccid>{
    void iccidEditStatus(Iccid iccid);
}