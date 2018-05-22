package com.hrtx.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.hrtx.web.service.CityService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;

@Controller
public class CommonController extends BaseReturn{
	@Resource
	private CityService cityService;

	@RequestMapping("query-city")
	@Powers( { PowerConsts.NOLOGINPOWER })
	public String queryCity(HttpServletRequest request) {
		String pid_=request.getParameter("pid");
		Object list=cityService.queryByPidList(NumberUtils.toInt(pid_,0));
		return renderJson(getJsonArray(list));
	}

	@RequestMapping("/{str}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public String pageModel(@PathVariable String str, HttpServletRequest request) {
        return "admin/"+str;
    }
	
	@RequestMapping("/{str1}/{str2}")
	@Powers({PowerConsts.NOLOGINPOWER})
	public String pageModel(@PathVariable String str1, @PathVariable String str2, HttpServletRequest request) {
		return "admin/"+str1+"/"+str2;
	}
	
}
