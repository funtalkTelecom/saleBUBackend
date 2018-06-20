package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.ConsumerLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface ConsumerLogMapper extends Mapper<ConsumerLog>,BaseMapper<ConsumerLog>{


    void insertConsumerLog(@Param("userid") Long userid, @Param("loginName") String loginName,
                             @Param("livePhone") String livePhone, @Param("nickName") String nickName,
                             @Param("sex") long sex);
}
