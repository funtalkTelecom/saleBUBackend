package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.controller.BaseReturn;
import com.hrtx.web.mapper.AccountMapper;
import com.hrtx.web.mapper.AgentMapper;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.pojo.Account;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.pojo.ConsumerLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

	public Result SaveOrUpdateAgent(Integer id,String commpayName,String person,String phone,long province,long city,long district,
									String address, MultipartFile file){
		Consumer consumer= this.apiSessionUtil.getConsumer();
		Integer consumerId = consumer.getId();
		//图片保存
		String tradingImg="";
		try {
			if(file!=null){
				String path = SystemParam.get("trading_url");
				Result result = BaseReturn.uploadFile(path, "jpg,png,gif,jpeg", file, true, false);
				if(result.getCode()==Result.OK) tradingImg = ((Map)result.getData()).get("sourceServerFileName").toString();
				else return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(Result.ERROR, "图片保存异常,请稍后再试");
		}
//		String a = "1006420771322462210";
//		long consumerId = Long.valueOf(a);
		Agent ag ;
		if(id==null){  //添加
			Agent param = new Agent();
			param.setAddConsumerId(consumerId);
			Agent ap = agentMapper.selectOne(param);
			if(ap !=null) return new Result(Result.ERROR, "已申请注册过代理商或已经绑定了乐语账号");
			ag = new Agent(commpayName,person,phone,province,city,district,address,tradingImg,1,consumerId, new Date(),0,1,3);
			agentMapper.insert(ag);
		}else {  //修改
			ag = new Agent(commpayName,person,phone,province,city,district,address,tradingImg,1,consumerId, new Date(),0,1,3);
			agentMapper.updateAgent(ag);
		}
		return new Result(Result.OK, "提交成功");
	}


	public Result SaveAgentLeyu(String loginName,String pwd){
		Consumer consumer= this.apiSessionUtil.getConsumer();
		Integer consumerId = consumer.getId();
//		String a = "1014426510456520704";
//		long consumerId = Long.valueOf(a);
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
		params.setStatus(2);
		params.setType(2);    //乐语导入
		params.setAddConsumerId(consumerId);
		Agent ag = agentMapper.selectOne(params);
		if(ag!=null) return new Result(Result.ERROR, "已经绑定乐语的账号");

		Consumer consparam = new Consumer();
		consparam.setId(consumerId);
		consparam.setStatus(1);
		consparam.setIsAgent(1);
		Consumer  consumer1 = consumerMapper.selectOne(consparam);
		if(consumer1 ==null ) return new Result(Result.ERROR, "当前客商已是代理商，无法绑定");
		//更新agent 状态2，客商id
		agent.setStatus(2);
		agentMapper.updateAgentStatusToLeyu(agent);
		consumer1.setIsAgent(2);
		consumer1.setCommpayName(agent.getCommpayName());
		consumer1.setName(agent.getPerson());
		consumer1.setPhone(agent.getPhone());
		consumer1.setAgentProvince(agent.getProvince());
		consumer1.setAgentCity(agent.getCity());
		consumer1.setAgentDistrict(agent.getDistrict());
		consumer1.setAgentAddress(agent.getAddress());
		consumer1.setTradingImg(agent.getTradingImg());
		//绑定后，重新存用户信息
		this.apiSessionUtil.saveOrUpdate(apiSessionUtil.getTokenStr(),consumer1);
		consumerMapper.insertAgentToConsumer(consumer1);

		return new Result(Result.OK, "绑定成功");
	}
	public List<Map> findAgentListByaddConsumerId(Integer ConsumerId) {
		return this.agentMapper.findAgentListByConsumerId( ConsumerId);
	}
	public List<Map> findIsLyByConsumerId(Integer ConsumerId) {
		return this.agentMapper.findIsLyByConsumerId( ConsumerId);
	}

    public Result pageAgent(Agent agent) {
        PageHelper.startPage(agent.startToPageNum(),agent.getLimit());
        Page<Object> ob=this.agentMapper.queryPageList(agent);
        PageInfo<Object> pm = new PageInfo<Object>(ob);
        return new Result(Result.OK, pm);
    }


//	public  List<Map> findAgentById(Long id) {
//		return  agentMapper.findAgentById(id);
//	}

	public Agent findAgentById(Integer id) {
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
			if(consumer ==null ) return new Result(Result.ERROR, "审核失败，当前客商已是代理商");
			consumer.setIsAgent(2);
			consumer.setCommpayName(aa.getCommpayName());
			consumer.setName(aa.getPerson());
			consumer.setPhone(aa.getPhone());
			consumer.setAgentProvince(aa.getProvince());
			consumer.setAgentCity(aa.getCity());
			consumer.setAgentDistrict(aa.getDistrict());
			consumer.setAgentAddress(aa.getAddress());
			consumer.setTradingImg(aa.getTradingImg());
//			long consumerId = consumer.getId();
//			this.apiSessionUtil.saveOrUpdate(apiSessionUtil.getTokenStr(),consumer);
			consumerMapper.insertAgentToConsumer(consumer);

		}
		agentMapper.updateAgentStatus(aa);

		return new Result(Result.OK, "提交成功");
	}
}
