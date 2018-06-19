package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Agent;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AgentMapper extends Mapper<Agent>,BaseMapper<Agent>{
    void updateAgent(Agent agent);

     List<Map> findAgentListByConsumerId(@Param("ConsumerId") Long ConsumerId);

//    List<Map> findAgentById(@Param("id") Long id);

    Agent findAgentById(@Param("id") Long id);

    void updateAgentStatus(Agent agent);

    void updateAgentStatusToLeyu(Agent agent);

}
