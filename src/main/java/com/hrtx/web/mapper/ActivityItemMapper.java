package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Activity;
import com.hrtx.web.pojo.ActivityItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ActivityItemMapper extends Mapper<ActivityItem>,BaseMapper<ActivityItem>{

    void insertBatch(@Param("activityItemsList") List<ActivityItem> activityItemsList);

    List findActivityItemList(@Param("activityId") Integer activityId);

    void updateItem(@Param("activityId") Integer activityId);

    int isActivityNum(@Param("num") String num);

}
