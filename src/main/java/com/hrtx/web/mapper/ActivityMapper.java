package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.github.pagehelper.Page;
import com.hrtx.web.pojo.Activity;
import com.hrtx.web.pojo.ActivityItem;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.pojo.EPSale;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public interface ActivityMapper extends Mapper<Activity>,BaseMapper<Activity>{


    void insertActivity(Activity activity);

    Page<Object> queryPageList(@Param("param") Activity activity);

    Activity findActivityById(@Param("id") Integer id);


    void activityUnsale(Activity activity);

    /**
     * 查询过期的活动
     * @return
     */
    List<Activity> findActivityIsFaile();

}
