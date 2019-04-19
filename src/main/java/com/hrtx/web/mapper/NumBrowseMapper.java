package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.NumBrowse;
import com.hrtx.web.pojo.PromotionPlan;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Map;
@Component
public interface NumBrowseMapper extends Mapper<NumBrowse>,BaseMapper<NumBrowse> {
    public int updateOpenCount(@Param("consumer_id") int consumer_id,@Param("num_id") int num_id);

    /**
     * 查找合伙人推广的用户浏览清单
     * @param      *
     * @return
     */
    public Page<Object> queryPartnerNumBrowseList(@Param("share_consumer_id") int share_consumer_id);

}