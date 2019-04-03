package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.ChannelMapper;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.service.AgentService;
import com.hrtx.web.service.ChannelService;
import com.hrtx.web.service.ConsumerService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AgentController {

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AgentService agentService;
	@Autowired
	ChannelService channelService;
	@Autowired private ApiSessionUtil apiSessionUtil;
	@Autowired private ConsumerService consumerService;
	@Autowired
	SessionUtil sessionUtil;
    @PostMapping(value = "/api/save-or-update-agent")
    @Powers({PowerConsts.NOPOWER})
	public Result SaveOrUpdateAgent(@RequestParam(value="id",required=false) Integer id,
								   @RequestParam(value="commpayName",required=false) String commpayName,
								   @RequestParam(value="person",required=false) String person,
								   @RequestParam(value="phone",required=false) String phone,
								   @RequestParam(value="province",required=false) Long province,
								   @RequestParam(value="city",required=false) Long city,
								   @RequestParam(value="district",required=false) Long district,
								   @RequestParam(value="address",required=false) String address,
									@RequestParam(name = "file",required = false) MultipartFile file) {
		return agentService.SaveOrUpdateAgent(id,commpayName,person,phone,province,city,district,address,file);
	}

	@PutMapping(value = "/api/save-agent-leyu")
	@Powers({PowerConsts.NOPOWER})
	public Result SaveAgentLeyu(@RequestParam(value="loginName",required=false) String loginName,
									@RequestParam(value="pwd",required=false) String pwd) {
		return agentService.SaveAgentLeyu(loginName,pwd);
	}

	@GetMapping("/api/query-agent_by_consumerid")
	@Powers({PowerConsts.NOPOWER})
	@ResponseBody
	public Result listAgent(){
		Consumer consumer= this.apiSessionUtil.getConsumer();
		int consumerId =consumer.getId();
		Consumer consumer2=new Consumer();
		consumer2.setId(consumer.getId());
		Consumer consumer1=this.consumerService.getConsumerById(consumer2);
//		String  a = "1006420771322462209";
//		long consumerId =Long.valueOf(a);
		Map map = new HashMap();
		List listly = agentService.findIsLyByConsumerId(consumerId);
		if(listly.size()>0){
			map = (Map) listly.get(0);
			map.put("isAgent","true");
            map.put("tradingImgUrl", SystemParam.get("domain-full") + "/get-img/trading_url/1000/" +ObjectUtils.toString(map.get("trading_img")));
		}else {
			List list = agentService.findAgentListByaddConsumerId(consumerId);
			if(list.size()==0){
				map.put("isAgent","false");
			}else {
				map = (Map) list.get(0);
				map.put("isAgent","true");
                map.put("tradingImgUrl", SystemParam.get("domain-full") + "/get-img/trading_url/1000/" +ObjectUtils.toString(map.get("trading_img")));
			}
		}
		map.put("partnerCheck",consumer1.getPartnerCheck());//合伙人审核结果
		map.put("isPartner",consumer1.getIsPartner());//是否申请了合伙人
		map.put("testUser",StringUtils.equals(consumer1.getCommpayName(),"测试")?"1":"0");//临时借用
		return new Result(Result.OK, map);
	}


	@RequestMapping("/agent/query-agent")
	@Powers({PowerConsts.AGENTMOUDULE_COMMON_QUEYR})
	public ModelAndView queryMeal(Agent agent,HttpServletRequest request){
		request.setAttribute("channelList", channelService.listChannel());
		return new ModelAndView("admin/agent/query-agent");
	}

	@RequestMapping("/agent/list-agent")
	@Powers({PowerConsts.AGENTMOUDULE_COMMON_QUEYR})
	public Result listAgent(Agent agent){
		return agentService.pageAgent(agent);
	}


	@PostMapping("/find-agent-by-id")
	@ResponseBody
	@Powers({PowerConsts.AGENTMOUDULE_COMMON_CHECK})
	public Map findAgentById(Agent agent){
		Map<String, Object> map = new HashMap<String, Object>();
		Agent ag=agentService.findAgentById(agent.getId());
		map.put("code", Result.OK);
		map.put("data", ag);
		return map;
	}
	@PostMapping("/check-agent")
	@ResponseBody
	@Powers({PowerConsts.AGENTMOUDULE_COMMON_CHECK})
	public Result checkAgent(Agent agent){
		return agentService.checkAgent(agent);
	}

	@PostMapping("/update-agent")
	@ResponseBody
	@Powers({PowerConsts.AGENTMOUDULE_COMMON_UPDATE})
	public Result updateAgent(Agent agent){
//		return agentService.updateAgentChannel(agent.getIds(),agent.getChannelId());
		return null;
	}

	@PostMapping("/agent/query-agent-by-CName")
	@Powers({PowerConsts.NOPOWER})
	public Result queryAgentByCName(Agent agent){
		return agentService.queryAgentByCName(agent);
	}
}
