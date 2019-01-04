package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Agent;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
@Component
public interface AgentMapper extends Mapper<Agent>,BaseMapper<Agent>{
    void updateAgent(Agent agent);

     List<Map> findAgentListByConsumerId(@Param("ConsumerId") Integer ConsumerId);

//    List<Map> findAgentById(@Param("id") Long id);

    Agent findAgentById(@Param("id") Integer id);

    void updateAgentStatus(Agent agent);


    void updateAgentChannel(@Param("ids") Integer ids,@Param("channelId") Integer channelId);

    void updateAgentStatusToLeyu(Agent agent);

    List<Map> findIsLyByConsumerId(@Param("ConsumerId") Integer ConsumerId);

    List findConsumenrIdCount(@Param("ConsumerId") Integer ConsumerId);

    List queryAgentByCName(@Param("param")Agent agent);
}
