package com.hrtx.web.mapper;

import com.github.abel533.mapper.Mapper;
import com.hrtx.web.pojo.Agent;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AgentMapper extends Mapper<Agent>,BaseMapper<Agent>{
    void updateAgent(Agent agent);

}
