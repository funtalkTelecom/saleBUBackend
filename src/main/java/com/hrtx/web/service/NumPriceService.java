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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return new Result(Result.OK,"成功");
    }

    public Result saveAgentNumprices(NumPrice numPrice,String commpayName) {
        String pattern = "[1-9]\\d*.?\\d*|0.\\d*[1-9]\\d*";
        Pattern r = Pattern.compile(pattern);
        String s = numPrice.getPrice().toString();
        Matcher m = r.matcher(numPrice.getPrice().toString());
        if(!m.matches()) return new Result(Result.ERROR, "请输入两位正小数");
        Agent agent1 = new Agent();
        agent1.setId(numPrice.getAgentId());
        agent1.setCommpayName(commpayName);
        agent1.setStatus(2);
        agent1.setIsDel(0);
        Agent agent = agentMapper.selectOne(agent1);
        if(null==agent) return new Result(Result.ERROR, "代理商不存在");
        numPrice.setChannel(agent.getChannelId());
        Set<String> distinct  = new HashSet<String>(); //去重
        Set<String> noPrice = new HashSet<String>();//不能设置价格
        Set<String> set = new HashSet<String>();//已经有代理商价格
        String[] nums = numPrice.getResource().split("\n");
        for (String a :nums){//去重
            if(!distinct.contains(a)){
                distinct.add(a);
            }
        }
        for(String b:distinct){
            Integer integer = numPriceMapper.checkNumpriceCount(numPrice, b);
            if (integer==0){
                noPrice.add(b);
            }if(integer==2){
                set.add(b);
            }
        }
        if(noPrice.size()>0){
            for(String c:noPrice){
                if (distinct.contains(c)){
                    distinct.remove(c);
                }
            }
        }
        if(distinct.size()==0) return new Result(Result.ERROR, "无可更新价格的号码");
        if(set.size()>0){// 已经有代理商价格的更新isdel为1
            numPriceMapper.batchUpdataIsDel(set,numPrice);
        }
        numPriceMapper.insertBatchbyAgentId(distinct,numPrice);//所有符合条件的都新增一条数据
        if(noPrice.size()>0){
            String[] devOnlyIds = new String[noPrice.size()];
            //Set-->数组
            noPrice.toArray(devOnlyIds);
            String join = StringUtils.join(devOnlyIds, "\n");
            return new Result(Result.OTHER, join);
        }
	     return new Result(Result.OK, "成功");
    }


//	select a.price ,ifnull(b.price,a.price) from (
//	select np.price,np.resource from  tb_num_price np where np.resource="17130360000" and np.agent_id=-1 and np.is_del=0 and np.channel=1) a left join
//			(select np.price,np.resource from  tb_num_price np where np.resource="17130360000" and np.agent_id=1 and np.is_del=0 and np.channel=1) b on a.resource=b.resource;



}
