package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.service.ChannelService;
import com.hrtx.web.service.DictService;
import com.hrtx.web.service.NumService;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/channel")
public class ChannelController extends BaseReturn{
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private DictService dictService;
    @Resource
    private ChannelService channelService;


    @RequestMapping("/channel-query")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_QUEYR})
    public ModelAndView channelQuery(){
        return new ModelAndView("admin/channel/channel-query");
    }

    @RequestMapping("/channel-list")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_QUEYR})
    public Result channelList(Channel channel){
        return channelService.queryChannel(channel);
    }

    @RequestMapping("/feather-list")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_QUEYR})
    public Result featherList(){
        Dict dict = new Dict();
        dict.setKeyGroup("FEATHER_TYPE");
        dict.setIsDel(1);
        return dictService.pageDict(dict);
    }

    @PostMapping("/channel-edit")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_EDIT})
    public Result editChannel(Channel channel){
        return channelService.editChannel(channel);
    }
    @PostMapping("/feather-edit")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_EDIT})
    public Result editFeather(Dict dict){
        return dictService.editFeather(dict);
    }
}
