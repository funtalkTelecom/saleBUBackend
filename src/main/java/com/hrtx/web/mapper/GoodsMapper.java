package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Goods;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface GoodsMapper extends Mapper<Goods>,BaseMapper<Goods>{
    Goods findGoodsInfo(@Param("id") Integer id);

    void goodsEdit(Goods goods);

    void goodsDelete(Goods goods);

    void insertBatch(@Param("goodsList") List<Goods> list);

    Page<Object> queryPageListApi(Goods goods, @Param("gSaleCityArr") String[] gSaleCityArr);

    void goodsUnsale(Goods goods);

    Goods findGoodsInfoBySkuid(@Param("skuid") Integer skuid);

    Page<Object> queryPageSkuListApi(Goods goods, @Param("gSaleCity") String split);

    int checkGnameIsExist(Goods goods);

    /***
     * 查询过期的上架商品
     * @return
     */
    List<Goods> findGoodsIsSale();

    List findNumStatus(@Param("num") String num,@Param("skuid") String skuid);

    void updateNumStatus(@Param("num_id") String num_id,@Param("skuid") String skuid,@Param("num") String num);

    /**
     * 更新goods.status
     * @param goods
     */
    void updateGoodStatus(Goods goods);

    List findSkuNumCount(@Param("sellerId") Integer sellerId);

    List isPutAwayGoodsCorpList();
}
