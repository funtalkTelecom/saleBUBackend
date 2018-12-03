package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.dto.Result;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class NumService {

	private Logger log = LoggerFactory.getLogger(NumService.class);
	@Autowired private OrderMapper orderMapper;
	@Autowired private IccidMapper iccidMapper;
	@Autowired private OrderItemMapper orderItemMapper;
	@Autowired private NumMapper numMapper;
	@Autowired private MealMapper mealMapper;
	@Autowired private NumPriceMapper numPriceMapper;

    /**
     * 绑卡
     * @param orderId
     * @return
     */
    public Result blindNum(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null) return new Result(Result.ERROR, "绑卡订单不存在");
        if(order.getIsDel() == 1 || order.getStatus() != 4) return new Result(Result.ERROR, "订单状态异常");
        int insertCount = 0;
        List<Map> items = iccidMapper.queryTempItemsByBatchNum(orderId);
        for (Map item:items) {
            int count = NumberUtils.toInt(ObjectUtils.toString(item.get("count")));
            insertCount += count;
        }
        int noFundCount = iccidMapper.batchInsertNoFund(order.getConsumer(), orderId);
        //回调卡中有["+noFundCount+"]个在卡库中未找到
        if(noFundCount != 0) throw new ServiceException("回调卡中有["+noFundCount+"]个在卡库中未找到");

        int updateCount  = iccidMapper.batchUpdate(order.getConsumer(), orderId);
        log.info("得到本地卡和回调卡的匹配信息[回调数量"+insertCount+", 未找到数量"+noFundCount+", 更新数量"+updateCount+"]");
        if(updateCount != insertCount) throw new ServiceException("卡库中更新数量与回调数量不一致，数据异常");
        String goodsType = order.getSkuGoodsType();
        if("2".equals(goodsType) || "4".equals(goodsType)) {//普号 或者 超靓
            //绑卡
            for (Map item:items) {
                int count = NumberUtils.toInt(ObjectUtils.toString(item.get("count")));
                long item_id = NumberUtils.toLong(ObjectUtils.toString(item.get("itemId")));
                OrderItem orderItem = orderItemMapper.selectByPrimaryKey(item_id);
                if(orderItem == null) throw new ServiceException("未找到仓库回调的itemId["+item_id+"]");
                if(orderItem.getIsShipment() != 1) throw new ServiceException("仓库回调的itemId["+item_id+"]在平台为不需发货，数据异常");
                Example example = new Example(OrderItem.class);
                example.createCriteria().andEqualTo("pItemId", item_id);
                List<OrderItem> list = orderItemMapper.selectByExample(example);
                if(count != list.size()) throw new ServiceException("仓库回调的itemId["+item_id+"]数量["+count+"]与平台数量["+list.size()+"]不一致");
                List errors = iccidMapper.matchOrderItem(item_id);
                if(errors.size() > 0) throw new ServiceException("itemId["+item_id+"]回调匹配存在号段不一致或号码状态异常");
                //捆绑
                example = new Example(Iccid.class);
                example.createCriteria().andEqualTo("orderId", item_id);
                List<Iccid> iccids = iccidMapper.selectByExample(example);
                if(iccids.size() != list.size())  throw new ServiceException("itemId["+item_id+"]iccid与号码数量不一致");
                for (int i = 0; i <list.size() ; i++) {
                    OrderItem item1 = list.get(i);
                    Iccid iccid = iccids.get(i);
                    Num num = new Num();
                    num.setId(item1.getNumId());
                    num.setIccidId(iccid.getId());
                    num.setIccid(iccid.getIccid());
                    num.setStatus(5);
                    if("4".equals(goodsType)) {//超靓
                        if(orderItem.getMealId() == null) throw new ServiceException("订单中未找到套餐");
                        num.setMealMid(orderItem.getMealId());
                    }
                    if("2".equals(goodsType)) {//谱号
                        List<Meal> meals = mealMapper.getMealListByNum(String.valueOf(item1.getNumId()));
                        if(meals.size()<=0) throw new ServiceException("未找到普号的基础套餐");
                        num.setMealMid(meals.get(0).getMid());
                    }
                    numMapper.updateByPrimaryKeySelective(num);
                    iccid.setDealStatus("2");
                    iccidMapper.updateByPrimaryKeySelective(iccid);
                }
            }
        }
        return new Result(Result.OK, "success");
    }

    public PageInfo<Object> queryNumPrice(NumPrice numPrice) {
        PageHelper.startPage(numPrice.getPageNum(),numPrice.getLimit());
        Page<Object> ob=this.numPriceMapper.queryPageList(numPrice);
        PageInfo<Object> pm = new PageInfo<Object>(ob);
        return pm;
    }
}
