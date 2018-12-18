package com.hrtx.web.service;

import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.mapper.ConsumerMapper;
import com.hrtx.web.mapper.GoodsFocusMapper;
import com.hrtx.web.mapper.GoodsMapper;
import com.hrtx.web.mapper.NumMapper;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.GoodsFocus;
import com.hrtx.web.pojo.Num;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GoodsFocusService {

	@Autowired private GoodsFocusMapper goodsFocusMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;
    @Autowired
    private NumMapper numMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    /*
      当前用户对该商品的收藏记录
     */
    public List<Map> findGoodsFocusListByNumIdAndGId(Integer numId,Integer gId)
	{
	    //return goodsFocusMapper.findGoodsFocusListBydConsumerId(apiSessionUtil.getConsumer().getId());
        return goodsFocusMapper.finGoodsFocusListByGIdAndNumIdAndConsumerId(gId,numId,this.apiSessionUtil.getConsumer().getId());
	}

    /*
      当前用户对该商品的收藏记录
     */
    public List<Map> findGoodsFocusListByGId(Integer gId)
    {
        //return goodsFocusMapper.findGoodsFocusListBydConsumerId(apiSessionUtil.getConsumer().getId());
        return goodsFocusMapper.finGoodsFocusListByGIdAndConsumerId(gId,this.apiSessionUtil.getConsumer().getId());
    }

	/*
	 当前用户商品收藏记录列表
	 */
    public Result findGoodsFocusList()
    {
       // List<Map> list=goodsFocusMapper.findGoodsFocusListByConsumerId2(apiSessionUtil.getConsumer().getId());
        List<Map> list=goodsFocusMapper.findGoodsFocusListByConsumerId3(apiSessionUtil.getConsumer().getId());
        Integer numId=0;
        Integer gId=0;
        int numStatus=0;//状态 号码记录
        String urlImg="";
        int gStatus=0;//0 未上架，1上架，2 失效 tb_num.stauts<>2
        String gIsSale="";//是否上架1是0否
        for(Map map:list)
        {
            urlImg=SystemParam.get("domain-full") +map.get("gImg").toString();
            map.put("gImg",urlImg);
            numId=NumberUtils.toInt(map.get("numId").toString());
            gId=NumberUtils.toInt(map.get("gId").toString());
            if(gId>0)
            {
                Goods goods = goodsMapper.findGoodsInfo(gId);
                if(goods!=null&&StringUtils.isNotBlank(goods.getgIsSale()))
                {
                    gIsSale=goods.getgIsSale();
                    if(gIsSale.equals("0"))
                    {
                        map.put("gStatus",0);//0 未上架
                    }else if(gIsSale.equals("1"))
                    {
                        map.put("gStatus",1);//1上架
                    }
                }
            }
            if(numId>0)
            {
                Num number=numMapper.selectByPrimaryKey(numId);
                numStatus=NumberUtils.toInt(String.valueOf(number.getStatus()),0);
                if(numStatus!=2)
                {
                    gStatus=2;//非 2销售中为失效
                    map.put("gStatus",gStatus);
                }
            }
        }
        return new Result(Result.OK,list);
    }

    /*
	  添加商品收藏
	  GoodsFocus.isDel;//是否收藏  是0否1
	 */
	public Result goodsFocusEdit(GoodsFocus goodsFocus, HttpServletRequest request) {
            Goods goods=goodsMapper.findGoodsInfo(goodsFocus.getgId());//上架商品信息gActive
            if(goods!=null&&goodsFocus.getErISPack().equals(goods.getgIsPack()))
            {
                return new Result(Result.ERROR,"是否打包传参不符，请核对");
            }
            String goodsFocusStr="";
            goodsFocus.setAddIp(SessionUtil.getUserIp());
            goodsFocus.setConsumerId(apiSessionUtil.getConsumer().getId());
            List<Map> goodsFocusList=new ArrayList<Map>();
            if(goodsFocus.getErISPack()==0)//商品是否打包 erIsPack
            {
                goodsFocusList=goodsFocusMapper.finGoodsFocusListByGIdAndNumIdAndConsumerId(goodsFocus.getgId(),goodsFocus.getNumId(),this.apiSessionUtil.getConsumer().getId());
            }else if(goodsFocus.getErISPack()==1)
            {
                goodsFocusList=goodsFocusMapper.finGoodsFocusListByGIdAndConsumerId(goodsFocus.getgId(),this.apiSessionUtil.getConsumer().getId());
            }
            if (!goodsFocusList.isEmpty()&&goodsFocusList.size()>0) {
                goodsFocus.setId(NumberUtils.toInt(goodsFocusList.get(0).get("id").toString()));
                goodsFocus.setUpdateDate(new Date());
                goodsFocus.setIsDel(1);//取消收藏 否1
                goodsFocusMapper.goodsFocusEdit(goodsFocus);
                goodsFocusStr="取消成功";
            } else {
                List<GoodsFocus> list = new ArrayList<GoodsFocus>();
                goodsFocus.setId(goodsFocus.getGeneralId());
                goodsFocus.setAddDate(new Date());
                goodsFocus.setUpdateDate(new Date());
                goodsFocus.setIsDel(0);//添加收藏 是0
                list.add(goodsFocus);
                goodsFocusMapper.insertBatch(list);
                goodsFocusStr="收藏成功";
            }
			return new Result(Result.OK, goodsFocusStr);
	}

}
