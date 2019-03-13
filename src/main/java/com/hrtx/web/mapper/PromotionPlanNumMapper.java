package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.PromotionPlanNum;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PromotionPlanNumMapper extends Mapper<PromotionPlanNum>,BaseMapper<PromotionPlanNum> {
    public int deletePromotionPlanNum(@Param("promotion_plan_id")Integer promotionPlanNumId);
}