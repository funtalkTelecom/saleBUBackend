package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.Num;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.service.NumService;
import com.hrtx.web.service.NumberService;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/lianghao")
public class LiangHaoController extends BaseReturn{
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private NumService numService;


    @RequestMapping("/lianghao-query")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_QUEYR})
    public ModelAndView lianghaoQuery(Number number){
        return new ModelAndView("admin/lianghao/lianghao-query");
    }

    @RequestMapping("/lianghao-list")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_QUEYR})
    public Result listNumber(NumPrice numPrice){
        numPrice.setPageNum(numPrice.startToPageNum());
        numPrice.setChannel(NumberUtils.toInt(String.valueOf(SystemParam.get("lianghao_channel"))));
        numPrice.setAgentId(NumberUtils.toLong(String.valueOf(SystemParam.get("lianghao_agent_id"))));
        PageInfo<Object> objectPageInfo = numService.queryNumPrice(numPrice);
        objectPageInfo =numService.queryFreeze(objectPageInfo);
        return new Result(Result.OK, objectPageInfo);
    }
    @PostMapping("/freeze-num")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_FREEZE})
    public Result freezeNum(Num num){
        return numService.freezeNum(num);
    }
}
