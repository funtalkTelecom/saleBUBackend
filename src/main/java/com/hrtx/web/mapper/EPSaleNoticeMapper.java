package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.EPSaleNotice;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface EPSaleNoticeMapper extends Mapper<EPSaleNotice>,BaseMapper<EPSaleNotice>{

    List<Map> findEPSaleNoticeListBydConsumerId(@Param("consumerId") Long consumerId);

    List<Map> findEPSaleNoticeListByEPSaleIdAndConsumerId(@Param("epSaleId") Long epSaleId, @Param("consumerId") Long consumerId);

    List<Map> findEPSaleNoticeListByEPSaleIdAndConsumerId2(@Param("epSaleId") Long epSaleId, @Param("consumerId") Long consumerId);

    List<Map> findEPSaleNoticeListByGIdAndConsumerId(@Param("gId") Long gId , @Param("consumerId") Long consumerId);

    void ePSaleNoticeEdit(EPSaleNotice ePSaleNotice);

    void insertBatch(@Param("ePSaleNoticeList") List<EPSaleNotice> list);

}
