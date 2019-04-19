package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Consumer;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface ConsumerMapper extends Mapper<Consumer>,BaseMapper<Consumer>{

    void insertConsumer (@Param("userid") Integer userid, @Param("nickName") String nickName,
                           @Param("img") String img, @Param("province") String province, @Param("city") String city);

    /***
     * 代理商审核成功，更新Consumer 中的信息
     * @param consumer
     */
    void insertAgentToConsumer(Consumer consumer);

    Consumer findConsumerById(@Param("id") Integer id);

    void insertPhoneToConsumer(Consumer consumer);
}
