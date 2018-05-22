package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.City;
import com.hrtx.web.pojo.Meal;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface CityMapper extends Mapper<Meal>,BaseMapper<Meal>{
    List<City> queryByPidList(@Param("pid") int pid);

    City findCityByName(@Param("name") String name);

    List<HashMap> findCityByNameFromThird(@Param("name") String name);
}
