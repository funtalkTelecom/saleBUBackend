package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.EPSale;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface EPSaleMapper extends Mapper<EPSale>,BaseMapper<EPSale>{

    Page<Object> queryPageList(@Param("param") EPSale epSale);

    EPSale findEPSaleById(@Param("id") Integer id);

    List<Map> findNumById(@Param("id") Integer id);

    List<Map> findGoodsByGId(@Param("gId") Integer gId);

    void numLoopEdit(@Param("endTime")String endTime, @Param("id") Integer id);

    List<Map> findEPSaleListByUserId(@Param("addUserId") Integer addUserId);

    List<Map> findEPSaleGoodsListByEPSaleId(Integer epSaleId);

    List<Map> findEPSaleGoodsListByEPSaleId2(Integer epSaleId);

    List<Map> findEPSaleGoodsListByEPSaleId3(Integer epSaleId);

    List<Map> findEPSaleGoodsByGoodsId(Integer goodsId);

    List<Map> findEPSaleGoods();

    List<Map> findEPSaleGoods2();

    List<Map> findEPSaleGoodsByGoodsId2(Integer goodsId);

    List<Map> findEPSaleGoodsByNumId(Integer numId);

    List<Map> findEPSaleGoodsByNumIdAndGId(Integer numId,Integer gId);

    List<Map> findEPSaleGoodsByGId(Integer gId);

    List<Map> findEPSaleGoodsImgByNumIdAndGId(Integer numId,Integer gId);

    List<Map> findEPSaleGoodsImgByGId(Integer gId);

    List<Map> findEPSaleList();

    List<Map> findEPSaleList2();

    List<Map> findEPSaleList3();

    List<Map> findEPSaleList4();

    List<Map>  findEPSaleByEPSaleId(Integer epSaleId);

    List<Map>  findEPSaleByEPSaleId2(Integer epSaleId);

    List<Map>  findEPSaleByEPSaleId3(Integer epSaleId);

    List<Map>  findEPSalePriceCountByEPSaleId(Integer epSaleId);

    int checkEPSaleKeyIdIsExist(EPSale epSale);

    void epSaleEdit(EPSale epSale);

    void epSaleDelete(EPSale epSale);

    void insertBatch(@Param("epSaleList") List<EPSale> list);

    //////////////////////////////////////////////////////
    List<Map> queryEndAuctionNum();

    List<Map> queryEndAuctionGoods();

    List<Map> freezeOneRecord();

    List<Map> queryActiveAuction(@Param("num_id") Integer num_id,@Param("g_id") Integer g_id);

    List<Map> queryActiveAuctionByNumIdAndGId(@Param("num_id") Integer num_id,@Param("g_id") Integer g_id);

    List<Map> queryActiveAuctionByGId(@Param("g_id") Integer g_id);

    List<Map> countAuctions(@Param("num_id") Integer num_id,@Param("g_id") Integer g_id);

    List<Map> countAuctionsByNumIdAndGId(@Param("num_id") Integer num_id,@Param("g_id") Integer g_id);

    List<Map> countAuctionsByGId(@Param("g_id") Integer g_id);

    List<Map> queryNeedReturn(@Param("num_id") Integer num_id,@Param("g_id") Integer g_id,@Param("consumer_id") Integer consumer_id);

    List<Map> queryNeedReturnByNumIdAndGIdAndComsumerId(@Param("num_id") Integer num_id,@Param("g_id") Integer g_id,@Param("consumer_id") Integer consumer_id);

    List<Map> queryNeedReturnByGIdAndComsumerId(@Param("g_id") Integer g_id,@Param("consumer_id") Integer consumer_id);

    ///////////////////////////////////
    List<Map> queryNumEndTime(@Param("num_id") Integer num_id);

    List<Map> queryGoodsEndTime(@Param("g_id") Integer g_id);

    void updateNumDelayed(@Param("num_id") Integer num_id,@Param("loop_time") Integer loop_time);

    void updateGoodsDelayed(@Param("g_id") Integer g_id,@Param("loop_time") Integer loop_time);
}
