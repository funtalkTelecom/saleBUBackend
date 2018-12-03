package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.City;
import com.hrtx.web.pojo.Meal;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public interface CityMapper extends Mapper<City>,BaseMapper<City>{
    List<City> queryByPidList(@Param("pid") int pid);

    City findCityByName(@Param("name") String name);

    List<HashMap> findCityByNameFromThird(@Param("name") String name);

    List queryByThird(@Param("third")  String third);

    List queryByPidListForZtree(@Param("pid") int pid, @Param("isopen") String isopen);

    List<Map> findCityByGrade(@Param("grade") int grade);
}
