package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SessionUtil;
import com.hrtx.web.mapper.AccountMapper;
import com.hrtx.web.mapper.AgentMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.ConsumerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AgentService {

	@Autowired SessionUtil sessionUtil;
	@Autowired private AgentMapper agentMapper;
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private ConsumerMapper consumerMapper;

	public Result SaveOrUpdateAgent(Long id,String commpayName,String person,String phone,long province,long city,long district,
									String address,String tradingImg){
		Consumer consumer= this.apiSessionUtil.getConsumer();
		long consumerId = consumer.getId();
//		String a = "1006420771322462208";
//		long consumerId = Long.valueOf(a);
		Agent agent = new Agent();
		agent.setId(id);
		Agent agg = agentMapper.selectOne(agent);
		Agent ag = new Agent(id,commpayName,person,phone,province,city,district,address,tradingImg,1,consumerId, new Date(),0,1);
		if(agg==null){  //添加
			Agent param = new Agent();
			param.setAddConsumerId(consumerId);
			Agent ap = agentMapper.selectOne(param);
			if(ap !=null) return new Result(Result.ERROR, "已申请注册过代理商或已经绑定了乐语账号");
			ag.setId(agent.getGeneralId());
			agentMapper.insert(ag);
		}else {  //修改
			agentMapper.updateAgent(ag);
		}
		return new Result(Result.OK, "提交成功");
	}


	public Result SaveAgentLeyu(String loginName,String pwd){
//		Consumer consumer= this.apiSessionUtil.getConsumer();
//		long consumerId = consumer.getId();
		String a = "1006420771322462208";
		long consumerId = Long.valueOf(a);
		Agent param = new Agent();
		param.setLoginName(loginName);
		param.setPwd(pwd);
		param.setStatus(1);
		param.setType(2);    //乐语导入
		Agent agent =  agentMapper.selectOne(param);
		if(agent==null) return new Result(Result.ERROR, "绑定乐语的账号或密码不对");

		agent.setAddConsumerId(consumerId);
		Agent params = new Agent();
		params.setLoginName(loginName);
		params.setPwd(pwd);
		params.setStatus(1);
		params.setType(2);    //乐语导入
		params.setAddConsumerId(consumerId);
		Agent ag = agentMapper.selectOne(params);
		if(ag!=null) return new Result(Result.ERROR, "已经绑定乐语的账号");
		agentMapper.updateAgentStatus(ag);
		return new Result(Result.OK, "绑定成功");
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


//	public  List<Map> findAgentById(Long id) {
//		return  agentMapper.findAgentById(id);
//	}

	public Agent findAgentById(Long id) {
		Agent agent = agentMapper.findAgentById(id);
		return agent;
	}

	public Result checkAgent(Agent agent){
		Agent param = new Agent();
		param.setId(agent.getId());
		param.setAddConsumerId(agent.getAddConsumerId());
		param.setStatus(1);
		param.setType(1);  //自注册
		Agent aa =agentMapper.selectOne(param);
		if(aa ==null) return new Result(Result.ERROR, "当前状态不能审核，请确认！");
		aa.setStatus(agent.getStatus());
		aa.setCheckRemark(agent.getCheckRemark());
		if(agent.getStatus()==2){
			Consumer consparam = new Consumer();
			consparam.setId(aa.getAddConsumerId());
			consparam.setStatus(1);
			Consumer  consumer = consumerMapper.selectOne(consparam);
			if(consumer ==null ) return new Result(Result.ERROR, "审核失败，当前客商不能升级为代理商");
			consumer.setIsAgent(2);
			consumer.setCommpayName(aa.getCommpayName());
			consumer.setName(aa.getPerson());
			consumer.setPhone(aa.getPhone());
			consumer.setAgentProvince(aa.getProvince());
			consumer.setAgentCity(aa.getCity());
			consumer.setAgentDistrict(aa.getDistrict());
			consumer.setAgentAddress(aa.getAddress());
			consumer.setTradingImg(aa.getTradingImg());
			consumerMapper.insertAgentToConsumer(consumer);
		}
		agentMapper.updateAgentStatus(aa);
		return new Result(Result.OK, "提交成功");
	}
}
