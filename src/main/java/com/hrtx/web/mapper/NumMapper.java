package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Num;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface NumMapper extends BaseMapper<Num>,Mapper<Num> {

    /**
     * 查询本次需要插入的号码
     * @return
     */
    List<Map> queryActiveNum();
    /**
     * 添加可用号码
     */
    void insertAcitveNum();

    /**
     * 更新失效号码
     */
    void updateLoseNum();

    /**
     * 绑定号码
     */
    void updateBoundNum(Num num);

    /**
     * 批量更新为待配卡状态
     * @param nums
     */
    int batchUpdateDpk(@Param("buyerId") Long buyerId, @Param("buyer") String buyer, @Param("list") List<Map> nums);

    /*
     5,6,7,9已绑定
     */
    Page<Object> queryPageNumList(Num   num, @Param("consumerId") Long consumerId, @Param("status") String status);

    /*
      sku_goods_type=3  status4 未绑定
     */
    Page<Object> queryPageNumList2(Num   num, @Param("consumerId") Long consumerId, @Param("status") String status);

    /**
     * 查找所有待受理号码
     * @return
     */
    List<Map> queryDslNum();

    /**
     * 批量更新为受理中
     * @param snums
     * @return
     */
    int batchUpdateSlz(@Param("fileName") String fileName, @Param("list") List<Long> snums);

    /***
     * 更加skuid，查询数量
     * @return
     */
    Map queryNumCountByskuid(@Param("skuid") long skuid,@Param("status") String status);


    List<Map> findNumFreezeList();
}