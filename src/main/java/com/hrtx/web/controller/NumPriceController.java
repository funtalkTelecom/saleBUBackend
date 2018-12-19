package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Channel;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.service.AgentService;
import com.hrtx.web.service.ChannelService;
import com.hrtx.web.service.DictService;
import com.hrtx.web.service.NumPriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/numprice")
public class NumPriceController extends BaseReturn{
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NumPriceService numPriceService;



    @RequestMapping("/numprice-query")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_QUEYR})
    public ModelAndView numpriceQuery( HttpServletRequest request){
        request.setAttribute("channel",  Constants.contantsToList("CHANNEL_ID"));
        return new ModelAndView("admin/numprice/numprice-query");
    }

    @RequestMapping("/numprice-list")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_QUEYR})
    public Result numpriceList(NumPrice numPrice){
        return numPriceService.queryNumPrice(numPrice);
    }



    @RequestMapping("/query-agent-numprice")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_QUEYR})
    public Result queryAgentNumprice(NumPrice numPrice,String commpayName){
        return numPriceService.queryAgentNumprice(numPrice,commpayName);
    }

    @RequestMapping("/save-agent-numprice")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_EDIT})
    public Result saveAgentNumprice(NumPrice numPrice,String commpayName){
        return numPriceService.saveAgentNumprice(numPrice,commpayName);
    }

}
