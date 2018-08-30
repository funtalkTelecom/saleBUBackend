package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.GoodsFocus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GoodsFocusMapper extends Mapper<GoodsFocus>,BaseMapper<GoodsFocus>{

    List<Map> findGoodsFocusListBydConsumerId(@Param("consumerId") Long consumerId);

    List<Map> finGoodsFocusListByGIdAndNumIdAndConsumerId(@Param("gId") Long gId,@Param("numId") Long numId,@Param("consumerId") Long consumerId);

    List<Map> finGoodsFocusListByGIdAndConsumerId(@Param("gId") Long gId,@Param("consumerId") Long consumerId);

    List<Map> findGoodsFocusListByConsumerId2(@Param("consumerId") Long consumerId);

    void goodsFocusEdit(GoodsFocus goodsFocus);

    void insertBatch(@Param("goodsFocusList") List<GoodsFocus> list);

}
