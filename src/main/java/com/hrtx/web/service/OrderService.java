package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.NoRepeat;
import com.hrtx.dto.Result;
import com.hrtx.dto.StorageInterfaceRequest;
import com.hrtx.global.*;
import com.hrtx.web.dto.StorageInterfaceResponse;
import com.hrtx.web.mapper.IccidMapper;
import com.hrtx.web.mapper.NumMapper;
import com.hrtx.web.mapper.OrderItemMapper;
import com.hrtx.web.mapper.OrderMapper;
import com.hrtx.web.pojo.Corporation;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.pojo.OrderItem;
import com.hrtx.web.pojo.User;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class OrderService extends BaseService {
    @Autowired private OrderMapper orderMapper;
    @Autowired private OrderItemMapper orderItemMapper;
    @Autowired private NumMapper numMapper;
    @Autowired private IccidMapper iccidMapper;
    @Autowired private FundOrderService fundOrderService;
    @Autowired private OrderService orderService;

    //订单业务类型 //1白卡 2普号 3普靓  4超靓
    private String[] goodsTypes = new String[]{"1","2","3","4"};

    public Result pageOrder(Order order) {
        User user = SessionUtil.getUser();
        if(user==null) return new Result(Result.ERROR, "未获取到用户");
        Corporation corporation = (Corporation) SessionUtil.getSession().getAttribute("corporation");
        PageHelper.startPage(order.getPageNum(),order.getLimit());
        Page<Object> ob=this.orderMapper.queryPageList(order, corporation.getId());
        PageInfo<Object> pm = new PageInfo<Object>(ob);
        return new Result(Result.OK, pm);
    }

    public Order findOrderById(Long id) {
        Order order = orderMapper.findOrderInfo(id);
        return order;
    }

    /**
     * 发货
     * @param orderId
     * @return
     */
    @NoRepeat
    public Result payDeliverOrder(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null) return new Result(Result.ERROR, "订单不存在");
        if(order.getIsDel() == 1 || order.getStatus() != 2) return new Result(Result.ERROR, "订单状态异常");
        String goodsType = order.getSkuGoodsType();
        if(!ArrayUtils.contains(goodsTypes, goodsType)) return new Result(Result.ERROR, "订单业务类型不存在，终止发货。");
        if(!"1".equals(goodsType)) {//非白卡
            //更新号码
            int notFreezeCount = 0;
            List<Map> nums = orderItemMapper.queryOrderNums(orderId);
            if(nums.size() == 0) return new Result(Result.ERROR, "订单未找到可更新号码");
            for (Map num:nums) {
                int status = NumberUtils.toInt(ObjectUtils.toString(num.get("status")));
                if(status != 3) notFreezeCount++;
            }
            if(notFreezeCount > 0) return new Result(Result.ERROR, "号码处于非冻结数量["+notFreezeCount+"]，状态异常");
            numMapper.batchUpdateDpk(order.getConsumer(), order.getConsumerName(), nums);
        }

        if("3".equals(goodsType)) {//普靓
            order.setStatus(4);//待配卡
            orderMapper.updateByPrimaryKey(order);
            return new Result(Result.OK, "订单下未找到需要发货的产品");
        }

        Example example = new Example(OrderItem.class);
        example.createCriteria().andEqualTo("orderId", order.getOrderId()).andEqualTo("isShipment", 1);
        List<OrderItem> items = orderItemMapper.selectByExample(example);
        List commodities = new ArrayList();
        for (OrderItem orderItem:items) {
            commodities.add(CommonMap.create("item_id",orderItem.getItemId()).put("companystock_id", orderItem.getCompanystockId()).put("quantity", orderItem.getQuantity()).getData());
        }
        Map param = CommonMap.create("receiver", order.getPersonName()).put("phone",order.getPersonTel()).put("address",order.getAddress())
                .put("receiver_company", order.getConsumerName()).put("remark",order.getConment()).put("callback_url",SystemParam.get("domain-full")+"/order/deliver-order-callback")
                .put("commodities", commodities).put("order_id",order.getOrderId()).getData();
        Result result = StorageApiCallUtil.storageApiCall(param, "HK0004");
        if(result.getCode() != Result.OK) return result;
        StorageInterfaceResponse storageInterfaceResponse = StorageInterfaceResponse.create(String.valueOf(result.getData()), SystemParam.get("key"));
        if("00000".equals(storageInterfaceResponse.getCode())) {
            order.setStatus(3);//待配货
            order.setNoticeShipmentDate(new Date());
            orderMapper.updateByPrimaryKey(order);
        }else {
            return new Result(Result.ERROR, "发货失败["+storageInterfaceResponse.getDesc()+"]");
        }
        return new Result(Result.OK, "发货成功");
    }

    /**
     * 订单支付成功
     * @param orderId
     * @return
     */
    @NoRepeat
    public Result payOrderSuccess(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null) return new Result(Result.ERROR, "订单不存在");
        if(order.getIsDel() == 1 || order.getStatus() != 1) return new Result(Result.ERROR, "订单状态异常");
        order.setPayDate(new Date());
        order.setStatus(2);
        orderMapper.updateByPrimaryKey(order);
        return new Result(Result.OK, "success");
    }

    /**
     * 更新仓库回填完成的发货信息
     * @param storageInterfaceRequest
     * @return
     */
    @NoRepeat
    public Result updateDeliverCallbackInfo(StorageInterfaceRequest storageInterfaceRequest) {
        Map platrequest = (Map) storageInterfaceRequest.getPlatrequest();
        long orderId = NumberUtils.toLong(ObjectUtils.toString(platrequest.get("order_id")));
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null) return new Result(Result.ERROR, "订单不存在");
        if(order.getStatus() != 3) return new Result(Result.ERROR, "订单状态异常");
        order.setExpressId(ObjectUtils.toString(platrequest.get("logistic_type")));
        order.setExpressName(ObjectUtils.toString(platrequest.get("logistic_name")));
        order.setExpressNumber(ObjectUtils.toString(platrequest.get("logistic_id")));
        try {
            order.setDeliverDate(Utils.stringToDate(ObjectUtils.toString(platrequest.get("refill_date")), "yyyy-MM-dd HH:mm:ss"));
        } catch (ParseException e) {
            log.error("解析回填时间异常", e);
        }
        order.setPickupDate(new Date());
        order.setStatus(4);//待配卡

        /**插入仓库回调回来的条码**/
        List<Map> commodities = (List) platrequest.get("commodities");
        List allImeis = new ArrayList();
        for (Map commodity:commodities) {
            List imeis = (List) commodity.get("imeis");
            String item_id = ObjectUtils.toString(commodity.get("item_id"));
            allImeis.add(CommonMap.create("iccids",imeis).put("itemId", item_id));
        }
        iccidMapper.deleteTempByBatchNum(orderId);
        int insertCount = iccidMapper.batchInsertTemp(allImeis, orderId);
        /**插入仓库回调回来的条码**/
        orderMapper.updateByPrimaryKey(order);
        return new Result(Result.OK, order);
    }

    /**
     * 订单付款操作
     * @param orderId
     * @return
     */
    @NoRepeat
    public Result payOrder(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null) return new Result(Result.ERROR, "订单不存在");
        if(order.getStatus() !=1 || order.getIsDel() != 0) return new Result(Result.ERROR, "订单状态异常");
        return fundOrderService.payPinganWxxOrder(((Double)Arith.mul(order.getTotal(), 100)).intValue(), "支付号卡订单", String.valueOf(orderId));
    }

    /**
     * 更新订单为待签收
     * @param orderId
     */
    public Result updateDqx(Long orderId) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(order == null) return new Result(Result.ERROR, "订单不存在");
        order.setStatus(5);
        orderMapper.updateByPrimaryKey(order);
        return new Result(Result.OK, "success");
    }

    /**
     * 线下付款
     * @param order
     * @param request
     * @return
     */
    public Result receipt(Order order, HttpServletRequest request) {
        //付款账号
        String payAccount = request.getParameter("payAccount");
        if(StringUtils.isBlank(payAccount)) return new Result(Result.ERROR, "付款账号不能为空");
        //收款账号
        String receivableAccount = request.getParameter("receivableAccount");
        if(StringUtils.isBlank(receivableAccount)) return new Result(Result.ERROR, "收款账号不能为空");
        //应收
        String receivable = request.getParameter("receivable");
        if(StringUtils.isBlank(receivable)) return new Result(Result.ERROR, "应收不能为空");
        if(receivable.split("\\.")[1].length()>2) return new Result(Result.ERROR, "请填写不要超过2位小数的数字");
        //实收
        String receipts = request.getParameter("receipts");
        if(StringUtils.isBlank(receipts)) return new Result(Result.ERROR, "实收不能为空");
        if(receipts.split("\\.")[1].length()>2) return new Result(Result.ERROR, "请填写不要超过2位小数的数字");
        int receivableInt = 0;
        int receiptsInt = 0;
        try {
            receivableInt = ((Double)Arith.add(Double.parseDouble(receivable)*100, 0.0)).intValue();
            receiptsInt = ((Double)Arith.add(Double.parseDouble(receipts)*100, 0.0)).intValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return new Result(Result.ERROR, "请输入有效数字");
        }

        Result result = fundOrderService.queryPayAmt(String.valueOf(order.getOrderId()));
        //已支付金额(分)
        int aamt = 0;
        if(result.getCode()==200){
            aamt = Integer.parseInt(String.valueOf(result.getData()));
        }else{
            return new Result(Result.ERROR, "查询已支付金额失败");
        }
        //本次收款金额
        int amt = receiptsInt;
        if(amt+aamt > receivableInt) return new Result(Result.ERROR, "本次实收金额加已支付金额不能大于应收金额");
        result = fundOrderService.payOffLineOrder(amt, receivableAccount, payAccount, order.getOrderId()+"线下支付", String.valueOf(order.getOrderId()));
        if(result.getCode()==200){
            if(amt+aamt == receivableInt){
                result = orderService.payOrderSuccess(order.getOrderId());
                if(result.getCode()==200) {
                    result = orderService.payDeliverOrder(order.getOrderId());
                    if(result.getCode()==200){
                        return new Result(Result.OK, "收款成功");
                    }else{
                        return new Result(Result.ERROR, "调用发货失败\n" + result.getData());
                    }
                }else{
                    return new Result(Result.ERROR, "调用支付成功失败\n" + result.getData());
                }
            }
            return new Result(Result.ERROR, result.getData());
        }else{
            return new Result(Result.ERROR, result.getData());
        }
    }
}

