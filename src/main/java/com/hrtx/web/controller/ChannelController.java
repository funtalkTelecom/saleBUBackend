package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
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

    /**
     *渠道系数列表
     * @return
     */
    @RequestMapping("/channel-list")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_QUEYR})
    public Result channelList(Channel channel){
        return channelService.queryChannel(channel);
    }

    /**
     *价格规则列表
     * @return
     */
    @RequestMapping("/feather-price-list")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_QUEYR})
    public Result featherPriceList(){
        Dict dict = new Dict();
        dict.setKeyGroup("feather_price");
        dict.setIsDel(1);
        dict.setCorpId(SessionUtil.getUser().getCorpId());
        return dictService.pageDict(dict);
    }
    /**
     *查询规则列表
     * @return
     */
    @RequestMapping("/feather-type-list")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_QUEYR})
    public Result featherTypeList(){
        Dict dict = new Dict();
        dict.setKeyGroup("FEATHER_TYPE");
        dict.setIsDel(1);
        return dictService.pageDict(dict);
    }

    /**
     *号码标签列表
     * @return
     */
    @RequestMapping("/num-tags-list")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_QUEYR})
    public Result numTagsList(){
        Dict dict = new Dict();
        dict.setKeyGroup("num_tags");
        dict.setIsDel(1);
        return dictService.pageDict(dict);
    }

    /**
     *渠道系数修改
     * @return
     */
    @PostMapping("/channel-edit")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_EDIT})
    public Result editChannel(Channel channel){
        return channelService.editChannel(channel);
    }

    /**
     *价格规则新增和修改
     * @return
     */
    @PostMapping("/feather-price-edit")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_EDIT,PowerConsts.CHANNELMOUDULE_COMMON_ADD})
    public Result editFeatherPrice(Dict dict){
        return dictService.editFeatherPrice(dict);
    }
    /**
     *新增查询规则
     * @return
     */
    @PostMapping("/add-feather-type")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_ADD})
    public Result addFeatherType(Dict dict){
        return dictService.addFeatherType(dict,"FEATHER_TYPE");
    }


    /**
     *价格规则，查询规则，号码标签删除
     * @return
     */
    @RequestMapping("/feather-delete")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_DELETE})
    public Result featherDelete(Dict dict){
        return dictService.featherDelete(dict);
    }
    /**
     *新增号码标签
     * @return
     */
    @PostMapping("/add-num-tags")
    @Powers({PowerConsts.CHANNELMOUDULE_COMMON_ADD})
    public Result addNumTags(Dict dict){
        return dictService.addFeatherType(dict,"num_tags");
    }


}
