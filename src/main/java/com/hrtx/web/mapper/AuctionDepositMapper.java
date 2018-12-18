package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.AuctionDeposit;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuctionDepositMapper extends Mapper<AuctionDeposit>,BaseMapper<AuctionDeposit>{

    List<Map> findAuctionDepositSumEPSaleGoodsByNumId(@Param("numId") Long numId);

    List<Map> findAuctionDepositListByNumId(@Param("numId") Long numId);

    List<Map> findAuctionDepositById(@Param("Id") Integer Id);

    List<Map> findAuctionDepositListByNumIdAndConsumerId(@Param("numId") Integer numId,@Param("consumerId") Integer consumerId);

    List<Map> findAuctionDepositListByNumIdAndConsumerIdAndGId(@Param("numId") Integer numId,@Param("consumerId") Integer consumerId,@Param("gId") Integer gId);

    List<Map> findAuctionDepositListByConsumerIdAndGId(@Param("consumerId") Integer consumerId,@Param("gId") Integer gId);

    List<Map> findAuctionDepositListByConsumerId(@Param("consumerId") Integer consumerId);

    Page<Object> queryPageDepositListByConsumerId(@Param("auctionDeposit") AuctionDeposit auctionDeposit,@Param("consumerId") Long consumerId);

    Page<Object> queryPageDepositListByConsumerId2(@Param("auctionDeposit") AuctionDeposit auctionDeposit,@Param("consumerId") Integer consumerId);
    /*
        通过NumId、ConsumerId、Status查询对应AuctionDepositList
     */
    List<Map> findAuctionDepositListByNumIdAndConsumerIdAndStatus(AuctionDeposit auctionDeposit);

    List<Map> findAuctionDepositListByNumIdAndConsumerIdAndStatusAndGId(AuctionDeposit auctionDeposit);

    List<Map> findAuctionDepositListByConsumerIdAndStatusAndGId(AuctionDeposit auctionDeposit);

    List<Map> findAuctionDepositListByNumIdAndStatusAndGId(AuctionDeposit auctionDeposit);

    List<Map> findAuctionDepositListByStatusAndGId(AuctionDeposit auctionDeposit);

    void insertBatch(@Param("auctionDepositList") List<AuctionDeposit> list);

    void auctionDepositEdit(AuctionDeposit auctionDeposit);

    void auctionDepositSatusEdit(AuctionDeposit auctionDeposit);
}
