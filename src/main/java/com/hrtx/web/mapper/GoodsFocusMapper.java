package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.GoodsFocus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface GoodsFocusMapper extends Mapper<GoodsFocus>,BaseMapper<GoodsFocus>{

    List<Map> findGoodsFocusListBydConsumerId(@Param("consumerId") Integer consumerId);

    List<Map> finGoodsFocusListByGIdAndNumIdAndConsumerId(@Param("gId") Integer gId,@Param("numId") Integer numId,@Param("consumerId") Integer consumerId);

    List<Map> finGoodsFocusListByGIdAndConsumerId(@Param("gId") Integer gId,@Param("consumerId") Integer consumerId);

    List<Map> findGoodsFocusListByConsumerId2(@Param("consumerId") Integer consumerId);

    List<Map> findGoodsFocusListByConsumerId3(@Param("consumerId") Integer consumerId);

    void goodsFocusEdit(GoodsFocus goodsFocus);

    void insertBatch(@Param("goodsFocusList") List<GoodsFocus> list);

}
