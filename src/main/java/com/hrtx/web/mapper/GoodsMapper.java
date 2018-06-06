package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Goods;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsMapper extends Mapper<Goods>,BaseMapper<Goods>{
    Goods findGoodsInfo(@Param("id") Long id);
    void goodsEdit(Goods goods);

    void goodsDelete(Goods goods);

    void insertBatch(@Param("goodsList") List<Goods> list);
}
