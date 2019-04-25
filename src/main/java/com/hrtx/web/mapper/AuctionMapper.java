package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Auction;
import com.hrtx.web.pojo.Num;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.beans.Transient;
import java.util.List;
import java.util.Map;

@Component
public interface AuctionMapper extends Mapper<Auction>,BaseMapper<Auction>{

    List<Map> findAuctionSumEPSaleGoodsByNumId(@Param("numId") Integer numId);

    List<Map> findAuctionSumEPSaleGoodsByNumIdAndGId(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionSumEPSaleGoodsByGId(@Param("gId") Integer gId);

    List<Map> findAuctionListByNumId(@Param("numId") Integer numId);

    List<Map> findAuctionListByNumIdAndGId(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionListByGId(@Param("gId") Integer gId);

    List<Map> findAuctionListByNumIdAndGId2(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionListByGId2(@Param("gId") Integer gId);

    List<Map> findAuctionListByNumIdAndConsumerIdAndGId(@Param("numId") Integer numId,@Param("consumerId") Integer consumerId,@Param("gId") Integer gId);

    List<Map> findAuctionListByConsumerIdAndGId(@Param("consumerId") Integer consumerId,@Param("gId") Integer gId);

    List<Map> findAuctionOrderListByConsumerId(Auction auction);

    List<Map>  findAuctionListByOrderId(@Param("orderId") Integer orderId);

    Page<Object> queryPageNumList(Num num, @Param("consumerId") Integer consumerId, @Param("status") String status);

    List<Map>  findAuctionListByNumIdAndPrice(@Param("numId") Integer numId,@Param("price") double price);

    List<Map>  findAuctionListByNumIdAndPrice2(@Param("numId") Integer numId,@Param("price") double price);

    List<Map> findAuctionListDepositByNumId(@Param("numId") Integer numId);

    List<Map> findAuctionListDepositByNumId2(@Param("numId") Integer numId);

    List<Map> findAuctionListDepositByNumIdAndGId(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionListDepositByNumIdAndGId2(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map> findAuctionGoodsByNumId(@Param("numId") Integer numId);

    List<Map> findCustomersByNumIdAndGId(@Param("numId") Integer numId,@Param("gId") Integer gId);

    List<Map>  findAuctionByNumIdAndStatus(Auction auction);

    List<Map>  findAuctionByNumIdAndStatusAndGId(Auction auction);

    void insertBatch(@Param("auctionList") List<Auction> list);

   // void auctionEditStatusById(@Param("status") int  status,@Param("id") Integer id);

    void auctionEditStatusById(Auction auction);

    void auctionEditStatusById2(Auction auction);

    void auctionEditOrderIDByNumId(Auction auction);

    void auctionEditOrderIDByNumIdAndSkuId(Auction auction);

    void auctionEditOrderIDByNumIdAndSkuIdAndGId(Auction auction);

    void auctionEditOrderIDByGId(Auction auction);

    List<Map> freezeOneNum(@Param("numId") Integer numId);
}
