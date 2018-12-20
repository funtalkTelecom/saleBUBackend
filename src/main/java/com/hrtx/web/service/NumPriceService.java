package com.hrtx.web.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.pojo.NumPrice;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class NumPriceService {

	private Logger log = LoggerFactory.getLogger(NumPriceService.class);

	@Autowired private NumPriceMapper numPriceMapper;
	@Autowired private AgentMapper agentMapper;

	public Result queryNumPrice(NumPrice numPrice) {
//        if (numPrice.getAgentId()!=null&&numPrice.getChannel()==-1){
//            Agent agent = agentMapper.selectByPrimaryKey(numPrice.getAgentId());
//            numPrice.setChannel(agent.getChannelId());
//        }
		PageHelper.startPage(numPrice.startToPageNum(),numPrice.getLimit());
		Page<Object> ob=numPriceMapper.queryPageList(numPrice);
		PageInfo<Object> pm = new PageInfo<Object>(ob);
		return new Result(Result.OK, pm);
	}

    public Result queryAgentNumprice(NumPrice numPrice,String commpayName) {
        Agent agent1 = new Agent();
        agent1.setId(numPrice.getAgentId());
        agent1.setCommpayName(commpayName);
        agent1.setStatus(2);
        agent1.setIsDel(0);
        Agent agent = agentMapper.selectOne(agent1);
		if(null==agent) return new Result(Result.ERROR, "代理商不存在");
		numPrice.setChannel(agent.getChannelId());
		Map map=numPriceMapper.queryAgentNumprice(numPrice);
		return new Result(Result.OK, map);
    }

    public Result saveAgentNumprice(NumPrice numPrice, String commpayName) {
        Result result = this.queryAgentNumprice(numPrice, commpayName);
        if(result.getCode()!=200) return result;
        Map map = (Map)result.getData();
        if(map.size()==0) return new Result(Result.ERROR,"无此号码此代理商价格数据");
        BigDecimal angetPrict = new BigDecimal(String.valueOf(map.get("agentPrice")));
        if(angetPrict.compareTo(numPrice.getPrice())==0) return new Result(Result.ERROR,"价格相同无需修改");
        Agent agent = agentMapper.selectByPrimaryKey(numPrice.getAgentId());
        NumPrice numPrice1 = new NumPrice();
        numPrice1.setChannel(agent.getChannelId());
        numPrice1.setResource(numPrice.getResource());
        numPrice1.setAgentId(-1);
        numPrice1.setIsDel(0);
        NumPrice numPrice2 = numPriceMapper.selectOne(numPrice1);// 查出初始数据
        numPrice1.setAgentId(agent.getId());
        NumPrice numPrice3 = numPriceMapper.selectOne(numPrice1);// 查出代理数据
        if(numPrice3!=null){
            numPrice3.setIsDel(1);
            numPriceMapper.updateByPrimaryKey(numPrice3);
        }
        numPrice2.setAgentId(agent.getId());
        numPrice2.setPrice(numPrice.getPrice());
        numPrice2.setId(null);
        numPriceMapper.insert(numPrice2);
        return new Result(Result.ERROR,"成功");
    }


//	select a.price ,ifnull(b.price,a.price) from (
//	select np.price,np.resource from  tb_num_price np where np.resource="17130360000" and np.agent_id=-1 and np.is_del=0 and np.channel=1) a left join
//			(select np.price,np.resource from  tb_num_price np where np.resource="17130360000" and np.agent_id=1 and np.is_del=0 and np.channel=1) b on a.resource=b.resource;



}
