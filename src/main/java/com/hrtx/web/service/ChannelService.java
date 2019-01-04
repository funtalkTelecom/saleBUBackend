package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.dto.Result;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ChannelService {

	@Autowired private ChannelMapper channelMapper;
	@Autowired private NumPriceMapper numPriceMapper;


    public Result queryChannel(Channel channel) {
        PageHelper.startPage(channel.startToPageNum(),channel.getLimit());
        Page<Object> ob=channelMapper.queryPageList(channel);
        PageInfo<Object> pm = new PageInfo<Object>(ob);
        return new Result(Result.OK, pm);
    }

    public Result editChannel(Channel channel) {
        Channel c = channelMapper.selectByPrimaryKey(channel.getId());
        if(c==null) return new Result(Result.OK, "数据不存在");
        Channel cl = new Channel();
//        cl.setId(cl.getGeneralId());
        cl.setChannel(c.getChannel());
        cl.setChannelId(c.getChannelId());
        cl.setIsDel(0);
        cl.setRatioPrice(channel.getRatioPrice());
        channelMapper.insert(cl);
        c.setIsDel(1);
        channelMapper.updateByPrimaryKey(c);
        numPriceMapper.matchNumPrice(channel.getCorpId());
        return new Result(Result.OK, "成功");
    }

    public  List listChannel(){
        return  channelMapper.listChannel();
    }
}
