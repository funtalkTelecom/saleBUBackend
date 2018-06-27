package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Auction;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuctionMapper extends Mapper<Auction>,BaseMapper<Auction>{

    List<Map> findAuctionSumEPSaleGoodsByNumId(@Param("numId") Long numId);

    List<Map> findAuctionSumEPSaleGoodsByNumIdAndGId(@Param("numId") Long numId,@Param("gId") Long gId);

    List<Map> findAuctionListByNumId(@Param("numId") Long numId);

    List<Map> findAuctionListByNumIdAndGId(@Param("numId") Long numId,@Param("gId") Long gId);

    List<Map> findAuctionListByNumIdAndConsumerIdAndGId(@Param("numId") Long numId,@Param("consumerId") Long consumerId,@Param("gId") Long gId);

    List<Map> findAuctionOrderListByConsumerId(Auction auction);

    List<Map>  findAuctionListByOrderId(@Param("orderId") Long orderId);

    List<Map>  findAuctionListByNumIdAndPrice(@Param("numId") Long numId,@Param("price") double price);

    List<Map>  findAuctionListByNumIdAndPrice2(@Param("numId") Long numId,@Param("price") double price);

    List<Map> findAuctionListDepositByNumId(@Param("numId") Long numId);

    List<Map> findAuctionListDepositByNumId2(@Param("numId") Long numId);

    List<Map> findAuctionListDepositByNumIdAndGId(@Param("numId") Long numId,@Param("gId") Long gId);

    List<Map> findAuctionListDepositByNumIdAndGId2(@Param("numId") Long numId,@Param("gId") Long gId);

    List<Map> findAuctionGoodsByNumId(@Param("numId") Long numId);

    List<Map> findCustomersByNumIdAndGId(@Param("numId") Long numId,@Param("gId") Long gId);

    List<Map>  findAuctionByNumIdAndStatus(Auction auction);

    List<Map>  findAuctionByNumIdAndStatusAndGId(Auction auction);

    void insertBatch(@Param("auctionList") List<Auction> list);

   // void auctionEditStatusById(@Param("status") int  status,@Param("id") Long id);

    void auctionEditStatusById(Auction auction);

    void auctionEditStatusById2(Auction auction);

    void auctionEditOrderIDByNumId(Auction auction);
}
