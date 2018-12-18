package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.EPSaleNotice;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface EPSaleNoticeMapper extends Mapper<EPSaleNotice>,BaseMapper<EPSaleNotice>{

    List<Map> findEPSaleNoticeListbyConsumerId(@Param("consumerId") Integer consumerId);

    List<Map> findEPSaleNoticeListByEPSaleId(@Param("epSaleId") Integer epSaleId);

    List<Map> findStartEPSaleList();

    List<Map> findEPSaleNoticeListByEPSaleIdAndConsumerId(@Param("epSaleId") Integer epSaleId, @Param("consumerId") Integer consumerId);

    List<Map> findEPSaleNoticeListByEPSaleIdAndConsumerId2(@Param("epSaleId") Integer epSaleId, @Param("consumerId") Integer consumerId);

    List<Map> findEPSaleNoticeListByGIdAndConsumerId(@Param("gId") Integer gId , @Param("consumerId") Integer consumerId);

    void ePSaleNoticeEdit(EPSaleNotice ePSaleNotice);

    void insertBatch(@Param("ePSaleNoticeList") List<EPSaleNotice> list);

}
