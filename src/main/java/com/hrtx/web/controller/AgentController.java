package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Agent;
import com.hrtx.web.service.AgentService;
import com.hrtx.web.service.ConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class AgentController {

	public final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AgentService agentService;
    @PutMapping(value = "/api/save-or-update-agent")
    @Powers({PowerConsts.NOLOGINPOWER})
	public Result SaveOrUpdateAgent(@RequestParam(value="id",required=false) Long id,
								   @RequestParam(value="commpayName",required=false) String commpayName,
								   @RequestParam(value="person",required=false) String person,
								   @RequestParam(value="phone",required=false) String phone,
								   @RequestParam(value="province",required=false) Long province,
								   @RequestParam(value="city",required=false) Long city,
								   @RequestParam(value="district",required=false) Long district,
								   @RequestParam(value="address",required=false) String address,
								   @RequestParam(value="tradingImg",required=false) String tradingImg) {
		return agentService.SaveOrUpdateAgent(id,commpayName,person,phone,province,city,district,address,tradingImg);
	}

	@GetMapping("/api/agent-list/")
	@Powers({PowerConsts.NOLOGINPOWER})
	@ResponseBody
	public Result listAgent(Agent agent, @PathVariable("addConsumerId") String addConsumerId){
		return agentService.findAgentListByaddConsumerId(Long.valueOf(addConsumerId));
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
}
