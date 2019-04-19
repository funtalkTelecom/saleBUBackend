package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.PromotionPlan;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface PromotionPlanMapper extends Mapper<PromotionPlan>,BaseMapper<PromotionPlan> {

    /**
     * 查找所有时间冲突的推广
     * @param queryConflict
     * @return
     */
    public Page<PromotionPlan> queryConflictList(@Param("param")PromotionPlan queryConflict);
}