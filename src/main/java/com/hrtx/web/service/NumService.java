package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.dto.Result;
import com.hrtx.global.EgtPage;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.global.Utils;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.*;
import com.hrtx.web.pojo.Number;
import net.sf.json.JSONObject;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
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
	@Autowired private NumFreezeMapper numFreezeMapper;

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

    /**
     * 前端销售展示列表（分页(传pagenum 和 limit)，有总数）
     * @param numPrice
     * @return
     */
    public PageInfo<Object> queryNumPrice(NumPrice numPrice) {
//        PageHelper.startPage(numPrice.getPageNum(),numPrice.getLimit());
//        Page<Object> ob=this.numPriceMapper.queryPageList(numPrice);
        long total = numPriceMapper.countList(numPrice);
        EgtPage egtPage = new EgtPage(numPrice.getPageNum(), numPrice.getLimit(), total);
        egtPage.addAll(this.queryNumPriceList(numPrice));
        PageInfo<Object> pm = new PageInfo<Object>(egtPage);
        return pm;
    }

    /**
     * 前端销售展示列表（分页，无总数）
     * @param numPrice
     * @return
     */
    public List queryNumPriceList(NumPrice numPrice) {
        numPrice.setStart((numPrice.getPageNum()-1)*numPrice.getLimit());
        return numPriceMapper.queryList(numPrice);
    }

    public Result freezeNum(Num num) {
        int isFreeze = NumberUtils.toInt(String.valueOf(num.getIsFreeze()));
        if((isFreeze !=1 && isFreeze != 0)) return new Result(Result.ERROR, "参数异常");
        Num num1 = numMapper.selectByPrimaryKey(num.getId());
        if (num1==null) return new Result(Result.ERROR, "靓号不存在");
        if(num1.getIsFreeze()==isFreeze) {
            return new Result(Result.ERROR, "被人抢先了");
        }
        if(isFreeze==0){
            if(numFreezeMapper.queryFreeze(num1.getId())!=SessionUtil.getUserId()){
                return new Result(Result.ERROR, "非冻结人无法解冻");
            }
        }
        NumFreeze numFreeze = new NumFreeze();
        numFreeze.setId(numFreeze.getGeneralId());
        numFreeze.setAddDate(new Date());
        numFreeze.setAddUser(SessionUtil.getUserId());
        numFreeze.setNumId(num1.getId());
        numFreeze.setNumResource(num1.getNumResource());
        numFreeze.setIsFreeze(num.getIsFreeze());
        numFreezeMapper.insert(numFreeze);
        num1.setIsFreeze(num.getIsFreeze());
        int count = numMapper.updateByPrimaryKey(num1);
        if(count != 1) return new Result(Result.ERROR, "提交失败");
        return new Result(Result.OK, "提交成功");
    }

    public PageInfo<Object> queryFreeze(PageInfo<Object> objectPageInfo) {
        List<Object> list = objectPageInfo.getList();
        for (int i = 0; i < list.size(); i++) {
            JSONObject m = JSONObject.fromObject(list.get(i));
            Integer integer = NumberUtils.toInt(String.valueOf(m.get("is_freeze")));
            if(integer==1){
                long numId = NumberUtils.toLong(String.valueOf(m.get("id")));
                Long addUser=numFreezeMapper.queryFreeze(numId);
                m.put("addUser",addUser );
                list.set(i, m);
            }
        }
        return objectPageInfo;
    }

    /*
	 号码冻结>30分钟系统自动解冻
	 */
    @Scheduled(fixedRate=6000)
    public void unFreezeSystem() {
        if(!"true".equals(SystemParam.get("numFreeze_timer"))) return;
        log.info("开始执行.....冻结号码>30分钟,系统自动解冻......定时器");
        List<Map> list=this.numMapper.findNumFreezeList();
        if(list.size()>0)
        {
            for(Map map :list)
            {
                long id = NumberUtils.toLong(String.valueOf(map.get("id")));
                Num num = numMapper.selectByPrimaryKey(id);
                NumFreeze numFreeze = new NumFreeze();
                numFreeze.setId(numFreeze.getGeneralId());
                numFreeze.setAddDate(new Date());
                numFreeze.setNumId(num.getId());
                numFreeze.setNumResource(num.getNumResource());
                numFreeze.setIsFreeze(0);
                numFreezeMapper.insert(numFreeze);
                num.setIsFreeze(0);
                numMapper.updateByPrimaryKey(num);
                log.info("号码冻结>30分钟系统自动解冻,numId:"+id);
            }
        }
    }

    public NumPrice getNumPrice(Long id) {
        return numPriceMapper.selectByPrimaryKey(id);
    }

    public Long queryFreeze(Long numId) {
        return numFreezeMapper.queryFreeze(numId);
    }

    public long matchNumPrice(){
        return numPriceMapper.matchNumPrice();
    }
}
