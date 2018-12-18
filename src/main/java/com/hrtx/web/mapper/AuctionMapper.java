package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Auction;
import com.hrtx.web.pojo.Num;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuctionMapper extends Mapper<Auction>,BaseMapper<Auction>{

    List<Map> findAuctionSumEPSaleGoodsByNumId(@Param("numId") Long numId);

    List<Map> findAuctionSumEPSaleGoodsByNumIdAndGId(@Param("numId") Long numId,@Param("gId") Long gId);

    List<Map> findAuctionSumEPSaleGoodsByGId(@Param("gId") Long gId);

    List<Map> findAuctionListByNumId(@Param("numId") Long numId);

    List<Map> findAuctionListByNumIdAndGId(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionListByGId(@Param("gId") Integer gId);

    List<Map> findAuctionListByNumIdAndGId2(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionListByGId2(@Param("gId") Long gId);

    List<Map> findAuctionListByNumIdAndConsumerIdAndGId(@Param("numId") Integer numId,@Param("consumerId") Integer consumerId,@Param("gId") Integer gId);

    List<Map> findAuctionListByConsumerIdAndGId(@Param("consumerId") Integer consumerId,@Param("gId") Integer gId);

    List<Map> findAuctionOrderListByConsumerId(Auction auction);

    List<Map>  findAuctionListByOrderId(@Param("orderId") Long orderId);

    Page<Object> queryPageNumList(Num num, @Param("consumerId") Long consumerId, @Param("status") String status);

    List<Map>  findAuctionListByNumIdAndPrice(@Param("numId") Long numId,@Param("price") double price);

    List<Map>  findAuctionListByNumIdAndPrice2(@Param("numId") Long numId,@Param("price") double price);

    List<Map> findAuctionListDepositByNumId(@Param("numId") Long numId);

    List<Map> findAuctionListDepositByNumId2(@Param("numId") Long numId);

    List<Map> findAuctionListDepositByNumIdAndGId(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionListDepositByNumIdAndGId2(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionGoodsByNumId(@Param("numId") Long numId);

    List<Map> findCustomersByNumIdAndGId(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map>  findAuctionByNumIdAndStatus(Auction auction);

    List<Map>  findAuctionByNumIdAndStatusAndGId(Auction auction);

    void insertBatch(@Param("auctionList") List<Auction> list);

   // void auctionEditStatusById(@Param("status") int  status,@Param("id") Long id);

    void auctionEditStatusById(Auction auction);

    void auctionEditStatusById2(Auction auction);

    void auctionEditOrderIDByNumId(Auction auction);

    void auctionEditOrderIDByNumIdAndSkuId(Auction auction);

    void auctionEditOrderIDByNumIdAndSkuIdAndGId(Auction auction);

    void auctionEditOrderIDByGId(Auction auction);

    List<Map> freezeOneNum(@Param("numId") Integer numId);
}
