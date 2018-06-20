package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Auction;
import com.hrtx.web.pojo.AuctionDeposit;
import com.hrtx.web.pojo.EPSale;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface AuctionDepositMapper extends Mapper<AuctionDeposit>,BaseMapper<AuctionDeposit>{

    List<Map> findAuctionDepositSumEPSaleGoodsByNumId(@Param("numId") Long numId);

    List<Map> findAuctionDepositListByNumId(@Param("numId") Long numId);

    List<Map> findAuctionDepositById(@Param("Id") Long Id);

    List<Map> findAuctionDepositListByNumIdAndConsumerId(@Param("numId") Long numId,@Param("consumerId") Long consumerId);

    void insertBatch(@Param("auctionDepositList") List<AuctionDeposit> list);

    void auctionDepositEdit(AuctionDeposit auctionDeposit);

    void auctionDepositSatusEdit(AuctionDeposit auctionDeposit);
}
