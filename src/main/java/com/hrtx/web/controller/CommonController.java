package com.hrtx.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hrtx.dto.Result;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.DictService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hrtx.config.annotation.Powers;
import com.hrtx.global.PowerConsts;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.acl.Group;

@Controller
public class CommonController extends BaseReturn{
	@Resource
	private CityService cityService;
	@Resource
	private DictService dictService;

    @RequestMapping("query-city")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object queryCity(HttpServletRequest request) {
        String pid_=request.getParameter("pid");
        Object list=cityService.queryByPidList(NumberUtils.toInt(pid_,0));
        return list;
    }

    @RequestMapping("query-city-ztree")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object queryCityZtree(HttpServletRequest request) {
        String pid_=request.getParameter("pid");
        String isopen=request.getParameter("isopen");
        Object list=cityService.queryByPidListForZtree(NumberUtils.toInt(pid_,0), isopen);
        return list;
    }

    @GetMapping("/api/citys")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object ApiCitys(HttpServletRequest request) {
        Object list= null;
        try {
            list = cityService.queryCitys();
        } catch (Exception e) {
            log.error("获取地市异常",e);
            return new Result(Result.ERROR,"暂时无法获取地市");
        }
        return new Result(Result.OK,list);
    }

    @RequestMapping("query-third-city")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object queryThirdCity(HttpServletRequest request) {
        String third=request.getParameter("third");
        Object list=cityService.queryByThird(third);
        return list;
    }

    @RequestMapping("dict-query-group")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object dictQueryGroup(HttpServletRequest request) {
        String group=request.getParameter("group");
        Object list = dictService.findDictByGroup(group);
        return list;
    }

    @RequestMapping("type-group-dict")
    @Powers( { PowerConsts.NOLOGINPOWER })
    @ResponseBody
    public Object typeGroupDict(HttpServletRequest request) {
        String group=request.getParameter("group");
        Object list = dictService.findDictByTypeGroup(group);
        return list;
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

    @RequestMapping("/get-img/{str:.+}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public void getImg(@PathVariable String str, HttpServletResponse response) {
        this.downLoadImg(str, response);
    }

    @RequestMapping("/get-img/{str}/{str1:.+}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public void getImg1(@PathVariable String str, @PathVariable String str1, HttpServletResponse response) {
        this.downLoadImg(str+"/"+str1, response);
    }

    @RequestMapping("/get-img/{str}/{str1}/{str2:.+}")
    @Powers({PowerConsts.NOLOGINPOWER})
    public void getImg2(@PathVariable String str, @PathVariable String str1,  @PathVariable String str2, HttpServletResponse response) {
        this.downLoadImg(str+"/"+str1+"/"+str2, response);
    }
}
