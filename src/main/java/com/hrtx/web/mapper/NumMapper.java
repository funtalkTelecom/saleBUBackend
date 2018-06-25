package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Num;
import org.springframework.stereotype.Repository;

@Repository
public interface NumMapper extends BaseMapper<Num>,Mapper<Num> {

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
}