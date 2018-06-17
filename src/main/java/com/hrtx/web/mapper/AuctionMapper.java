package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Auction;
import com.hrtx.web.pojo.DeliveryAddress;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface AuctionMapper extends Mapper<Auction>,BaseMapper<Auction>{

    List<Map> findAuctionSumEPSaleGoodsByNumId(@Param("numId") Long numId);

    List<Map> findAuctionListByNumId(@Param("numId") Long numId);

    void insertBatch(@Param("auctionList") List<Auction> list);

    void auctionEditStatusById(@Param("status") int  status,@Param("id") Long id);
}
