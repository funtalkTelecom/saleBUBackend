package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.AuctionDeposit;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AuctionDepositMapper extends Mapper<AuctionDeposit>,BaseMapper<AuctionDeposit>{

    List<Map> findAuctionDepositSumEPSaleGoodsByNumId(@Param("numId") Long numId);

    List<Map> findAuctionDepositListByNumId(@Param("numId") Long numId);

    List<Map> findAuctionDepositById(@Param("Id") Long Id);

    List<Map> findAuctionDepositListByNumIdAndConsumerId(@Param("numId") Long numId,@Param("consumerId") Long consumerId);

    List<Map> findAuctionDepositListByNumIdAndConsumerIdAndGId(@Param("numId") Long numId,@Param("consumerId") Long consumerId,@Param("gId") Long gId);

    List<Map> findAuctionDepositListByConsumerId(@Param("consumerId") Long consumerId);

    /*
        通过NumId、ConsumerId、Status查询对应AuctionDepositList
     */
    List<Map> findAuctionDepositListByNumIdAndConsumerIdAndStatus(AuctionDeposit auctionDeposit);

    List<Map> findAuctionDepositListByNumIdAndConsumerIdAndStatusAndGId(AuctionDeposit auctionDeposit);

    void insertBatch(@Param("auctionDepositList") List<AuctionDeposit> list);

    void auctionDepositEdit(AuctionDeposit auctionDeposit);

    void auctionDepositSatusEdit(AuctionDeposit auctionDeposit);
}
