package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SessionUtil;
import com.hrtx.web.mapper.AccountMapper;
import com.hrtx.web.mapper.AgentMapper;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.ConsumerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AgentService {

	@Autowired SessionUtil sessionUtil;
	@Autowired private AgentMapper agentMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;

	public Result SaveOrUpdateAgent(Long id,String commpayName,String person,String phone,long province,long city,long district,
									String address,String tradingImg){
		Consumer consumer= this.apiSessionUtil.getConsumer();
		long consumerId = consumer.getId();
//		long userid = 10001;
		Agent agent = new Agent();
		Agent ag = new Agent(id,commpayName,person,phone,province,city,district,address,tradingImg,1,consumerId, new Date(),0);
		if(ag.getId()==null || ag.getId()==0){  //添加
			ag.setId(agent.getGeneralId());
			agentMapper.insert(ag);
		}else {  //修改
			agentMapper.updateAgent(ag);
		}
		return new Result(Result.OK, "提交成功");
	}

	public Result findAgentListByaddConsumerId(Long ConsumerId) {
		return new Result(Result.OK,  agentMapper.findAgentListByConsumerId( ConsumerId));
	}

    public Result pageAgent(Agent agent) {
        PageHelper.startPage(agent.getPageNum(),agent.getLimit());
        Page<Object> ob=this.agentMapper.queryPageList(agent);
        PageInfo<Object> pm = new PageInfo<Object>(ob);
        return new Result(Result.OK, pm);
    }
}
