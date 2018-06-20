package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.EPSale;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface EPSaleMapper extends Mapper<EPSale>,BaseMapper<EPSale>{

    Page<Object> queryPageList(@Param("param") EPSale epSale);

    EPSale findEPSaleById(@Param("id") Long id);

    List<Map> findNumById(@Param("id") Long id);

    void numLoopEdit(@Param("endTime")String endTime, @Param("id") Long numId);

    List<Map> findEPSaleListByUserId(@Param("addUserId") Long addUserId);

    List<Map> findEPSaleGoodsListByEPSaleId(Long epSaleId);

    List<Map> findEPSaleGoodsByGoodsId(Long goodsId);

    List<Map> findEPSaleGoods();

    List<Map> findEPSaleGoodsByGoodsId2(Long goodsId);

    List<Map> findEPSaleList();

    List<Map>  findEPSaleByEPSaleId(Long epSaleId);

    List<Map>  findEPSalePriceCountByEPSaleId(Long epSaleId);

    int checkEPSaleKeyIdIsExist(EPSale epSale);

    void epSaleEdit(EPSale epSale);

    void epSaleDelete(EPSale epSale);

    void insertBatch(@Param("epSaleList") List<EPSale> list);
}
