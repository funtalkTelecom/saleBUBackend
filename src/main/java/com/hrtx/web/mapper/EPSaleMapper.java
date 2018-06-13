package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.EPSale;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EPSaleMapper extends Mapper<EPSale>,BaseMapper<EPSale>{

    Page<Object> queryPageList(@Param("param") EPSale epSale);

    EPSale findEPSaleById(@Param("id") Long id);

    List<Map> findEPSaleListByUserId(@Param("addUserId") Long addUserId);

    List<Map> findEPSaleGoodsListByEPSaleId(Long epSaleId);

    List<Map> findEPSaleList();

    List<Map>  findEPSaleByEPSaleId(Long epSaleId);

    int checkEPSaleKeyIdIsExist(EPSale epSale);

    void epSaleEdit(EPSale epSale);

    void epSaleDelete(EPSale epSale);

    void insertBatch(@Param("epSaleList") List<EPSale> list);
}
