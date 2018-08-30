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

    List<Map> findGoodsByGId(@Param("gId") Long gId);

    void numLoopEdit(@Param("endTime")String endTime, @Param("id") Long id);

    List<Map> findEPSaleListByUserId(@Param("addUserId") Long addUserId);

    List<Map> findEPSaleGoodsListByEPSaleId(Long epSaleId);

    List<Map> findEPSaleGoodsListByEPSaleId2(Long epSaleId);

    List<Map> findEPSaleGoodsListByEPSaleId3(Long epSaleId);

    List<Map> findEPSaleGoodsByGoodsId(Long goodsId);

    List<Map> findEPSaleGoods();

    List<Map> findEPSaleGoods2();

    List<Map> findEPSaleGoodsByGoodsId2(Long goodsId);

    List<Map> findEPSaleGoodsByNumId(Long numId);

    List<Map> findEPSaleGoodsByNumIdAndGId(Long numId,Long gId);

    List<Map> findEPSaleGoodsByGId(Long gId);

    List<Map> findEPSaleGoodsImgByNumIdAndGId(Long numId,Long gId);

    List<Map> findEPSaleGoodsImgByGId(Long gId);

    List<Map> findEPSaleList();

    List<Map> findEPSaleList2();

    List<Map> findEPSaleList3();

    List<Map> findEPSaleList4();

    List<Map>  findEPSaleByEPSaleId(Long epSaleId);

    List<Map>  findEPSaleByEPSaleId2(Long epSaleId);

    List<Map>  findEPSaleByEPSaleId3(Long epSaleId);

    List<Map>  findEPSalePriceCountByEPSaleId(Long epSaleId);

    int checkEPSaleKeyIdIsExist(EPSale epSale);

    void epSaleEdit(EPSale epSale);

    void epSaleDelete(EPSale epSale);

    void insertBatch(@Param("epSaleList") List<EPSale> list);

    //////////////////////////////////////////////////////
    List<Map> queryEndAuctionNum();

    List<Map> queryEndAuctionGoods();

    List<Map> freezeOneRecord();

    List<Map> queryActiveAuction(@Param("num_id") Long num_id,@Param("g_id") Long g_id);

    List<Map> queryActiveAuctionByNumIdAndGId(@Param("num_id") Long num_id,@Param("g_id") Long g_id);

    List<Map> queryActiveAuctionByGId(@Param("g_id") Long g_id);

    List<Map> countAuctions(@Param("num_id") Long num_id,@Param("g_id") Long g_id);

    List<Map> countAuctionsByNumIdAndGId(@Param("num_id") Long num_id,@Param("g_id") Long g_id);

    List<Map> countAuctionsByGId(@Param("g_id") Long g_id);

    List<Map> queryNeedReturn(@Param("num_id") Long num_id,@Param("g_id") Long g_id,@Param("consumer_id") Long consumer_id);

    List<Map> queryNeedReturnByNumIdAndGIdAndComsumerId(@Param("num_id") Long num_id,@Param("g_id") Long g_id,@Param("consumer_id") Long consumer_id);

    List<Map> queryNeedReturnByGIdAndComsumerId(@Param("g_id") Long g_id,@Param("consumer_id") Long consumer_id);

    ///////////////////////////////////
    List<Map> queryNumEndTime(@Param("num_id") Long num_id);

    List<Map> queryGoodsEndTime(@Param("g_id") Long g_id);

    void updateNumDelayed(@Param("num_id") Long num_id,@Param("loop_time") Integer loop_time);

    void updateGoodsDelayed(@Param("g_id") Long g_id,@Param("loop_time") Integer loop_time);
}
