package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Goods;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public interface GoodsMapper extends Mapper<Goods>,BaseMapper<Goods>{
    Goods findGoodsInfo(@Param("id") Long id);

    void goodsEdit(Goods goods);

    void goodsDelete(Goods goods);

    void insertBatch(@Param("goodsList") List<Goods> list);

    Page<Object> queryPageListApi(Goods goods, @Param("gSaleCityArr") String[] gSaleCityArr);

    void goodsUnsale(Goods goods);

    Goods findGoodsInfoBySkuid(@Param("skuid") String skuid);

    Page<Object> queryPageSkuListApi(Goods goods, @Param("gSaleCity") String split);

    int checkGnameIsExist(Goods goods);
}
