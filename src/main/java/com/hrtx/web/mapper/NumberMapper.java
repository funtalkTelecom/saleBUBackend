package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Number;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NumberMapper extends Mapper<Number>,BaseMapper<Number>{
    void updateStatus(Number number);

    int checkNumberIsOk(Number number);

    Page<Object> queryPageListApi(@Param("tags") String tags);

    Map getNumInfoById(@Param("id") String id);

    void freezeNum(@Param("id") String numid, @Param("status") String status);

    List<Number> getListBySkuid(@Param("skuid")String skuid);

    void freezeNumByIds(@Param("numberList") List<Number> nlist, @Param("status") String status);

    List<Number> getListBySkuidAndStatus(@Param("skuid") String skuid, @Param("status") String status, @Param("numcount") int numcount);

    Page<Object> queryPageListApiForNumber3(@Param("skuGoodsType") String skuGoodsType,@Param("agentCity") Long agentCity);
}
