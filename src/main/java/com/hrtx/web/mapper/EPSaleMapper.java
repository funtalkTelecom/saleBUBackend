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

    List<Map> findNumById(@Param("id") Long id);

    void numLoopEdit(@Param("endTime")String endTime, @Param("id") Long id);

    List<Map> findEPSaleListByUserId(@Param("addUserId") Long addUserId);

    List<Map> findEPSaleGoodsListByEPSaleId(Long epSaleId);

    List<Map> findEPSaleGoodsByGoodsId(Long goodsId);

    List<Map> findEPSaleGoods();

    List<Map> findEPSaleGoods2();

    List<Map> findEPSaleGoodsByGoodsId2(Long goodsId);

    List<Map> findEPSaleGoodsByNumId(Long numId);

    List<Map> findEPSaleGoodsByNumIdAndGId(Long numId,Long gId);

    List<Map> findEPSaleList();

    List<Map>  findEPSaleByEPSaleId(Long epSaleId);

    List<Map>  findEPSalePriceCountByEPSaleId(Long epSaleId);

    int checkEPSaleKeyIdIsExist(EPSale epSale);

    void epSaleEdit(EPSale epSale);

    void epSaleDelete(EPSale epSale);

    void insertBatch(@Param("epSaleList") List<EPSale> list);

    //////////////////////////////////////////////////////
    List<Map> queryEndAuction();
    List<Map> freezeOneRecord();
    List<Map> queryActiveAuction(@Param("num_id") Long num_id,@Param("g_id") Long g_id);
    List<Map> countAuctions(@Param("num_id") Long num_id,@Param("g_id") Long g_id);
    List<Map> queryNeedReturn(@Param("num_id") Long num_id,@Param("g_id") Long g_id,@Param("consumer_id") Long consumer_id);
}
