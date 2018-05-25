package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.System;
import com.hrtx.web.service.SystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import java.util.*;

@RestController
@RequestMapping("/system")
public class SystemController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SystemService systemService;

	@RequestMapping("/system-query")
	@Powers({PowerConsts.SYSMOUDULE_COMMON_QUEYR})
	public ModelAndView systemQuery(System system){
		return new ModelAndView("admin/system/system-query");
	}

	@RequestMapping("/system-list")
	@Powers({PowerConsts.SYSMOUDULE_COMMON_QUEYR})
	public Result listSystem(System system){
		return systemService.pageSystem(system);
	}

	@RequestMapping("/system-info")
	@ResponseBody
	@Powers({PowerConsts.SYSMOUDULE_COMMON_QUEYR})
	public Map systemInfo(System system){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", Result.OK);
		map.put("data", systemService.findSystemById(system.getId()));
		return map;
	}

	@RequestMapping("/system-edit")
	@Powers({PowerConsts.SYSMOUDULE_COMMON_EDIT})
	public void systemEdit(System system){
		returnResult(systemService.systemEdit(system));
	}

	@RequestMapping("/system-delete")
	@Powers({PowerConsts.SYSMOUDULE_COMMON_DELETE})
	public void systemDelete(System system){
		returnResult(systemService.systemDelete(system));
	}

	@RequestMapping("/system-audit")
	@Powers({PowerConsts.SYSMOUDULE_COMMON_AUDIT})
	public void systemAudit(System system){
		returnResult(systemService.systemAudit(system));
	}
}
