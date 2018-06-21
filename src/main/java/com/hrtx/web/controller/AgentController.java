package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.pojo.Consumer;
import com.hrtx.web.service.AgentService;
import com.hrtx.web.service.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

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
	@Autowired private ApiSessionUtil apiSessionUtil;
    @PostMapping(value = "/api/save-or-update-agent")
    @Powers({PowerConsts.NOLOGINPOWER})
	public Result SaveOrUpdateAgent(@RequestParam(value="id",required=false) Long id,
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
	@Powers({PowerConsts.NOLOGINPOWER})
	public Result SaveAgentLeyu(@RequestParam(value="loginName",required=false) String loginName,
									@RequestParam(value="pwd",required=false) String pwd) {
		return agentService.SaveAgentLeyu(loginName,pwd);
	}

	@GetMapping("/api/query-agent_by_consumerid")
	@Powers({PowerConsts.NOLOGINPOWER})
	@ResponseBody
	public Result listAgent(){
		Consumer consumer= this.apiSessionUtil.getConsumer();
		long consumerId =consumer.getId();
//		String  a = "1007534495353536512";
//		long consumerId =Long.valueOf(a);
		List list = agentService.findAgentListByaddConsumerId(consumerId);
		if(list.size()==0) return  new Result(Result.OK,list);
		Map map = (Map) list.get(0);
		map.put("tradingImgUrl", SystemParam.get("domain-full") + "get-img/trading_url/1000/" +map.get("trading_img").toString());
		return new Result(Result.OK, map);
	}


	@RequestMapping("/agent/query-agent")
	@Powers({PowerConsts.AGENTMOUDULE_COMMON_QUEYR})
	public ModelAndView queryMeal(Agent agent){
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
}
