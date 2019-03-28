package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.hrtx.common.pay.PayClient;
import com.hrtx.common.pay.dto.*;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.annotation.NoRepeat;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.global.pinganUtils.TLinx2Util;
import com.hrtx.global.pinganUtils.YzffqUtil;
import com.hrtx.web.dto.PayRequest;
import com.hrtx.web.dto.PayResponse;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pay.YzfPayStrategy;
import com.hrtx.web.pojo.*;
import net.sf.json.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FundOrderService extends BaseService {
	@Autowired private FundOrderMapper fundOrderMapper;
	@Autowired private FundDetailMapper fundDetailMapper;
	@Autowired private ConsumerLogMapper consumerLogMapper;
	@Autowired private PinganService pinganService;
	@Autowired private ThirdPayService thirdPayService;
	@Autowired private OrderService orderService;
	@Autowired private AuctionDepositService auctionDepositService;
	@Autowired private ApiSessionUtil apiSessionUtil;
    @Autowired private ShareService shareService;
    @Autowired private OrderItemMapper orderItemMapper;

    /**
     * 平台订单支付（线下支付方式）
     * @param amt  金额（分）
     * @param payee 收款方
     * @param payer 付款方
     * @param orderName 订单描述
     * @param sourceId 订单号
     * @return
     */
    @NoRepeat
    public Result payOffLineOrder(int amt, String payee, String payer, String orderName, String sourceId) {
        return payAddOrder(FundOrder.BUSI_TYPE_PAYORDER, amt, payee, payer, orderName, FundOrder.THIRD_PAY_OFFLINE, sourceId, "");
    }

    /**
     * 平台订单支付（平安微信小程序支付方式）
     * @param amt 支付金额(分)
     * @param orderName 订单描述
     * @param sourceId  订单号
     * @return
     */
    @NoRepeat
    public Result payPinganWxxOrder(int amt, String orderName, String sourceId) {
        Result result = this.getPayer(2);
//        Result result = new Result(Result.OK, "o1F3M4sVzb7FUkxpgzGBinJWpnQA");
        if(result.getCode() != Result.OK) return result;
        return payAddOrder(FundOrder.BUSI_TYPE_PAYORDER, amt, "", String.valueOf(result.getData()), orderName, FundOrder.THIRD_PAY_PINGANAPP, sourceId, "");
    }

    /**
     * 保证金支付（平安微信小程序支付方式）
     * @param amt 支付金额(分)
     * @param orderName 订单描述
     * @param sourceId  保证金订单号
     * @return
     */
    public Result payPinganWxxDeposit(int amt, String orderName, String sourceId) {
        Result result = this.getPayer(2);
        if(result.getCode() == Result.ERROR) return result;
        return payAddOrder(FundOrder.BUSI_TYPE_PAYDEPOSIT, amt, "", String.valueOf(result.getData()), orderName, FundOrder.THIRD_PAY_PINGANAPP, sourceId, "");
    }

    /**
     * 平台订单支付（已支付分期支付方式）
     * @param amt 支付金额(分)
     * @param orderName 订单描述
     * @param sourceId  订单号
     * @return
     */
    @NoRepeat
    public Result payYzffqOrder(int amt, String orderName, String sourceId) {
//        Result result = this.getPayer(2);
        Result result = new Result(Result.OK, "o1F3M4sVzb7FUkxpgzGBinJWpnQA");
        if(result.getCode() != Result.OK) return result;
        return payAddOrder(FundOrder.BUSI_TYPE_PAYORDER, amt, "", String.valueOf(result.getData()), orderName, FundOrder.THIRD_PAY_YZFFQ, sourceId, "");
    }

    /**
     *
     * @param busiType 业务类型
     * @param amt  付款金额(分)
     * @param payee 收款方
     * @param payer 付款方
     * @param orderName 支付订单描述
     * @param third 支付方式
     * @param sourceId 业务来源编码
     * @param remark 备注
     * @return
     */
	private Result payAddOrder(String busiType, int amt, String payee, String payer, String orderName, String third, String sourceId, String remark){
	    String orderMark = busiType+"-"+sourceId;
        if(!LockUtils.tryLock(orderMark)) return new Result(Result.ERROR, "正在支付，请勿重复请求");
        try {
            if(!FundOrder.THIRD_PAY_OFFLINE.equals(third)) {
                Example example = new Example(FundOrder.class);
                example.createCriteria().andEqualTo("busi", busiType).andEqualTo("sourceId", sourceId).andIn("status",Arrays.asList(new Integer[]{3,5,7}));
                List fundOrders = fundOrderMapper.selectByExample(example);
//              if(true) return new Result(Result.ERROR, "订单已支付");
                if(fundOrders.size()>0) return new Result(Result.ERROR, "订单已支付");
            }
            String contractno = "PAY"+Utils.randomNoByDateTime();
            FundOrder fundOrder = new FundOrder(busiType, amt, payee, payer, 1, orderName, contractno, third, amt, remark, sourceId);
//            fundOrder.setId(fundOrder.getGeneralId());
            fundOrderMapper.insert(fundOrder);
            Integer req_user = apiSessionUtil.getConsumer() == null ? 0:apiSessionUtil.getConsumer().getId();
            FundDetail fundDetail = new FundDetail(fundOrder.getId(), contractno, SessionUtil.getUserIp(), req_user, new Date(), FundDetail.ORDER_ACT_TYPE_ADD, 1);
//            fundDetail.setId(fundDetail.getGeneralId());
            fundDetailMapper.insert(fundDetail);

            String notify_url = SystemParam.get("domain-full")+"/api/pingan-pay-result";
            String sub_appid = SystemParam.get("wxx_appid");
            Result result = null;
            try {
                if(FundOrder.THIRD_PAY_PINGANAPP.equals(third)) {
                    result=pinganService.payOrder(contractno,"WeixinOL",orderName,amt,amt,remark,notify_url,sub_appid,payer,"JSAPI");
//                    result=pinganService.payOrder(contractno,"Weixin",orderName,amt,amt,remark,notify_url,sub_appid,payer,"JSAPI");
                    if(result.getCode() == Result.OK) {
                        JSONObject json1=(JSONObject) result.getData();
                        String[] keys={"appId","nonceStr","timeStamp","signType","package","paySign"};
                        for (String key:keys) {
                            if(!json1.containsKey(key))return  result = new Result(Result.ERROR,"接口返回参数错误");
                        }
                        Map _map = new HashMap();
                        _map.put("appId",json1.getString("appId"));
                        _map.put("nonceStr",json1.getString("nonceStr"));
                        _map.put("timeStamp",json1.getString("timeStamp"));
                        _map.put("signType",json1.getString("signType"));
                        _map.put("package",json1.getString("package"));
                        _map.put("paySign",json1.getString("paySign"));
                        return result = new Result(Result.OK, _map);
                    }
                }
                if(FundOrder.THIRD_PAY_YZFFQ.equals(third)) {
                    thirdPayService.setThirdPayStrategy(YzfPayStrategy.getInstance());
                    PayResponse payResponse =thirdPayService.payOrder(new PayRequest(amt, payee, payer, contractno, contractno, null, orderName,
                            "YZF", amt, remark, SystemParam.get("domain-full")+"/api/yzffq-pay-result-jump?source_id="+sourceId, SystemParam.get("domain-full")+"/api/yzffq-pay-result"));
                    if(payResponse.getResCode() == Result.OK) {
                        JSONObject json1=(JSONObject) payResponse.getData();
                        Map _map = new HashMap();
                        _map.put("trade_qrcode",json1.getString("trade_qrcode"));
                        return result = new Result(Result.OK, _map);
                    }else {
                        return result = new Result(payResponse.getResCode(), payResponse.getResDesc());
                    }
                }
                if(FundOrder.THIRD_PAY_OFFLINE.equals(third)) {
                    result = new Result(Result.OK, "支付成功");
                }
                if(result == null) result = new Result(Result.ERROR,"第三方支付接口不存在");
                return result;
            } catch (Exception e) {
                log.error("请求未知异常", e);
                return result = new Result(Result.ERROR, "请求未知异常");
            } finally {
                fundDetail.setResCode(result.getCode());
                String desc = String.valueOf(result.getData());
                desc = desc.length() > 200 ? desc.substring(0, 199) : desc;
                fundDetail.setResDesc(desc);
                if(result.getCode() == Result.WARN) {//请求未知
                    fundDetail.setStatus(2);//请求中
                }
                if(result.getCode() == Result.OK) {//请求成功
                    if(FundOrder.THIRD_PAY_OFFLINE.equals(third)) {//线下支付
                        fundOrder.setStatus(3);//已支付
                    }else {
                        fundOrder.setStatus(2);//等待付款
                    }
                    fundDetail.setStatus(3);//请求结束
                }
                if(result.getCode() == Result.ERROR) {//请求失败
                    fundDetail.setStatus(4);//请求失败
                    fundOrder.setStatus(6);//已关闭
                }
                fundDetailMapper.updateByPrimaryKey(fundDetail);
                fundOrderMapper.updateByPrimaryKey(fundOrder);
            }
        } catch (Exception e) {
            log.error("下单请求前未知异常", e);
            return new Result(Result.ERROR, "请求前未知异常");
        } finally {
            LockUtils.unLock(orderMark);
        }

	}

    /**
     * 更新回调结果
     * @param params
     * @return
     */
    public Result pinganPayResult(Map<String,String> params) {
        log.info("接受到回掉参数："+params);
        if(!TLinx2Util.verifySign(params)) {
            return new Result(Result.ERROR, "验签失败");
        }
        int status = NumberUtils.toInt(params.get("status"));
        if(!ArrayUtils.contains(new int[]{1,4}, status)) return  new Result(Result.ERROR, "参数异常");
        if(status == 1) status = 3; //支付成功
        if(status == 4) status = 4; //支付失败（支付订单fundOrder 已取消）
        String out_no = params.get("out_no");
        this.updatePayCallback(out_no, status);
        return new Result(Result.OK, "success");
    }
    /**
     * 更新HR pay 回调结果
     * @param platrequest
     * @return
     */
    public Result hrPayResult(Map platrequest) {
        if(!platrequest.containsKey("order_no")||!platrequest.containsKey("status")) return new Result(Result.ERROR,"fail");
        String busi_type = FundOrder.BUSI_TYPE_PAYORDER;
        String busi_order_id = ObjectUtils.toString(platrequest.get("order_no"));
        int pay_status =NumberUtils.toInt(ObjectUtils.toString(platrequest.get("status")));; //状态 2等待付款，3已支付

        Result result= this.updateBusiPayResult(busi_type,NumberUtils.toInt(busi_order_id),pay_status==3);

        return result;
    }
    /**
     * 更新回调结果
     * @param params
     * @return
     */
    public Result yzffqPayResult(Map<String,String> params) {
        log.info("接受到回掉参数："+params);
        if(!YzffqUtil.verifySign(params)) {
            return new Result(Result.ERROR, "验签失败");
        }
        int status = NumberUtils.toInt(params.get("status"));
        if(!ArrayUtils.contains(new int[]{1,4}, status)) return  new Result(Result.ERROR, "参数异常");
        if(status == 1) status = 3; //支付成功
        if(status == 4) status = 4; //支付失败（支付订单fundOrder 已取消）
        String out_no = params.get("out_no");
        this.updatePayCallback(out_no, status);
        return new Result(Result.OK, "success");
    }

    private Result updatePayCallback(String out_no, Integer status) {
        FundDetail fundDetail = new FundDetail();
        fundDetail.setSerial(out_no);
        fundDetail.setActType(FundDetail.ORDER_ACT_TYPE_ADD);
        fundDetail = fundDetailMapper.selectOne(fundDetail);
        if(fundDetail == null) return new Result(Result.ERROR, "未找到订单");
        fundDetail.setStatus(5);//回掉完成
        Example example = new Example(FundDetail.class);
        example.createCriteria().andEqualTo("id",fundDetail.getId()).andIn("status", Arrays.asList(new Integer[]{2,3}));
        int count = fundDetailMapper.updateByExample(fundDetail, example);
        if(count != 1) throw new ServiceException("该状态订单不接受回调");

        FundOrder fundOrder = new FundOrder();
        fundOrder.setId(fundDetail.getFundOrderId());
        fundOrder = fundOrderMapper.selectByPrimaryKey(fundOrder);
        if(fundOrder == null) throw new ServiceException("未找到订单");

        fundOrder.setStatus(status);

        example = new Example(FundOrder.class);
        example.createCriteria().andEqualTo("id",fundOrder.getId()).andIn("status", Arrays.<Object>asList(new Integer[]{1,2}));
        count = fundOrderMapper.updateByExample(fundOrder, example);
        if(count != 1) throw new ServiceException("该状态订单不接受回调");
//        String payTime = params.get("pay_time");
        String busiType = fundOrder.getBusi();
        Integer orderId = NumberUtils.toInt(fundOrder.getSourceId());
        return this.updateBusiPayResult(busiType,orderId,status == 3);
    }

    /**
     * 支付结束后更新业务单据的状态及进行后续的操作
     * @param busi_type
     * @param busi_order_id
     * @param pay_result
     * @return
     */
    private Result updateBusiPayResult(String busi_type,int busi_order_id,boolean pay_result){
        Result result=null;
        try {
            if(FundOrder.BUSI_TYPE_PAYORDER.equals(busi_type)&&pay_result){
                result = orderService.payOrderSuccess(busi_order_id);
                if(result.getCode() == Result.OK) {
                    orderService.payDeliverOrder(busi_order_id);//发货成功与否不与支付结果挂钩
                    shareService.createOrderSettle(busi_order_id);//2019.3.26 结算费用
                }
            }else if(FundOrder.BUSI_TYPE_PAYDEPOSIT.equals(busi_type)){
                auctionDepositService.newAuctionDepositPay(busi_order_id, pay_result, Utils.getDate(0,"yyyyMMddHHmmss"));//payTime
                result=new Result(Result.OK,"success");
            }
        }catch (Exception e) {
            log.error(String.format("业务[%s]单据[%s]支付[%s]，更新失败",busi_type,busi_order_id,pay_result), e);
            result=new Result(Result.ERROR,"fail");
        }
        return result;
    }
    /**
     * 平台订单退款
     * @param sourceId 订单号
     * @param remark 退款备注
     * @return
     */
    public Result payOrderRefund(String sourceId, String remark){
        return payRefund(FundOrder.BUSI_TYPE_PAYORDER, sourceId, remark);
    }

    /**
     * 平台保证金退款
     * @param sourceId 保证金订单号
     * @param remark 退款备注
     * @return
     */
    public Result payDepositRefund(String sourceId, String remark){
        return payRefund(FundOrder.BUSI_TYPE_PAYDEPOSIT, sourceId, remark);
    }

    /**
     * 退款
     * @param busiType 业务类型
     * @param sourceId 业务编码
     * @param remark 退款备注
     * @return
     */
    private Result payRefund(String busiType, String sourceId, String remark){
        Example example = new Example(FundOrder.class);
        example.createCriteria().andEqualTo("busi", busiType).andEqualTo("sourceId", sourceId).andEqualTo("status", 3);
        List fundOrders = fundOrderMapper.selectByExample(example);
        if(fundOrders.size() != 1) return new Result(Result.ERROR, "退款订单不存在");
        FundOrder fundOrder = (FundOrder) fundOrders.get(0);
        String outNo = fundOrder.getContractno();
        if(!LockUtils.tryLock(outNo)) return new Result(Result.ERROR, "退款中，请稍后再试");
        try {
            Integer req_user = apiSessionUtil.getConsumer() == null ? 0:apiSessionUtil.getConsumer().getId();
            String contractno = "REFUND"+Utils.randomNoByDateTime();
            FundDetail fundDetail = new FundDetail(fundOrder.getId(), contractno, SessionUtil.getUserIp(), req_user, new Date(), FundDetail.ORDER_ACT_TYPE_REFUND, 1);
//            fundDetail.setId(fundDetail.getGeneralId());
            fundDetailMapper.insert(fundDetail);
            Result result = null;
            try {
                if(FundOrder.THIRD_PAY_PINGANAPP.equals(fundOrder.getThird())) {
                    result = pinganService.payRefund(outNo, contractno, fundOrder.getOrderName()+"退款", fundOrder.getAmt(), remark);
                }
                if(FundOrder.THIRD_PAY_YZFFQ.equals(fundOrder.getThird())) {
                    thirdPayService.setThirdPayStrategy(YzfPayStrategy.getInstance());
                    PayResponse payResponse = thirdPayService.payRefund(new PayRequest(fundOrder.getAmt(), "", "", contractno, contractno, outNo, "",
                            "YZF", 0, fundOrder.getOrderName()+"退款", "", ""));
                    if(payResponse.getResCode() == Result.OK) {
                        result = new Result(payResponse.getResCode(), payResponse.getData());
                    }else {
                        result = new Result(payResponse.getResCode(), payResponse.getResDesc());
                    }
                }
                if(result == null) result = new Result(Result.ERROR,"第三方支付接口不存在");
                return result;
            } catch (Exception e) {
                log.error("请求未知异常", e);
                return result = new Result(Result.ERROR, "请求未知异常");
            } finally {
                fundDetail.setResCode(result.getCode());
                String desc = String.valueOf(result.getData());
                desc = desc.length() > 1000 ? desc.substring(0, 999) : desc;
                fundDetail.setResDesc(desc);
                if(result.getCode() == Result.WARN) {//请求未知
                    fundDetail.setStatus(2);//请求中
                    fundOrder.setStatus(7);//退款中
                }
                if(result.getCode() == Result.OK) {//请求成功
                    fundDetail.setStatus(3);//请求结束
                    JSONObject json1=(JSONObject) result.getData();
                    String status = json1.getString("status");
                    if ("1".equals(status)) {
                        fundOrder.setStatus(5);//已退款
                        fundOrder.setActualAmt(0);
                    }
                }
                if(result.getCode() == Result.ERROR) {//请求失败
                    fundDetail.setStatus(4);//请求失败
                }
                fundDetailMapper.updateByPrimaryKey(fundDetail);
                fundOrderMapper.updateByPrimaryKey(fundOrder);
            }
        }catch (Exception e){
            log.error("退款请求前未知异常", e);
            return new Result(Result.ERROR, "请求前未知异常");
        }finally {
            LockUtils.unLock(outNo);
        }
    }

    /**
     * 获取付款方
     * @param loginType 登录方式
     * @return
     */
    public Result getPayer(int loginType){
        ConsumerLog consumerLog = new ConsumerLog();
        consumerLog.setUserId(apiSessionUtil.getConsumer() == null ? 0 : apiSessionUtil.getConsumer().getId());
        consumerLog.setStatus(1);
        consumerLog.setLoginType(loginType);
        consumerLog = consumerLogMapper.selectOne(consumerLog);
        if(consumerLog == null || StringUtils.isBlank(consumerLog.getOpenid())) return new Result(Result.ERROR, "未找到付款账户");
        return new Result(Result.OK, consumerLog.getOpenid());
    }

    /**
     * 查询业务订单支付成功总额
     * @param sourceId 订单号
     * @return result code == 200 data == 金额（分）   否则失败
     */
    public Result queryPayAmt(String sourceId) {
        Example example = new Example(FundOrder.class);
        example.createCriteria().andEqualTo("busi", FundOrder.BUSI_TYPE_PAYORDER).andEqualTo("status", 3).andEqualTo("sourceId", sourceId);
        List<FundOrder> list = fundOrderMapper.selectByExample(example);
        int amt = 0;
        for (FundOrder fundOrder:list) {
            amt = ((Double)Arith.add(amt, fundOrder.getAmt())).intValue();
        }
        return new Result(Result.OK, amt);
    }

    /**
     * 查询订单的支付信息
     * @param sourceId 订单号
     * @return result code == 200 已支付 其它 未支付 data= 1线上支付  2线下支付
     */
    public Result queryPayOrderInfo(String sourceId) {
        Example example = new Example(FundOrder.class);
        example.createCriteria().andEqualTo("busi", FundOrder.BUSI_TYPE_PAYORDER).andEqualTo("status", 3).andEqualTo("sourceId", sourceId);
        List<FundOrder> list = fundOrderMapper.selectByExample(example);
        if(list.size() <= 0) return new Result(Result.ERROR, "未支付");
        FundOrder fundOrder = list.get(0);
        if(FundOrder.THIRD_PAY_OFFLINE.equals(fundOrder.getThird())) return new Result(Result.OK, 2);
        return new Result(Result.OK, 1);
    }

    @NoRepeat
    public Result test(String a, Integer b, User u) {
        try {
            Thread.sleep(1000*5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(a+"----------"+u);
        return new Result(Result.OK, "success");
    }
    public Result payHrPayOrder(Order order) {
        String orderNo=String.valueOf(order.getOrderId());//订单号
        String payer=String.valueOf(order.getConsumer());//用户id
        Result result_openid = this.getPayer(2);
        if(result_openid.getCode() != Result.OK) return result_openid;
        String openid=String.valueOf(result_openid.getData());

        Example example = new Example(OrderItem.class);
        example.createCriteria().andEqualTo("orderId",order.getOrderId()).andGreaterThan("total",0);//订单大于0
        List<OrderItem> items = orderItemMapper.selectByExample(example);
        if(items.size()==0||items.size()>1) return new Result(Result.OK, "订单数据错误");
        int item_other_amt=Double.valueOf(Utils.mul(order.getShippingTotal()-order.getCommission(),100)).intValue();//每个子单时的额外费用
        int surplus_amt=0;
        String orderName=null;
        String payee=null;
        List<Map> payeeList= new ArrayList<>();
        for(int i=0;i<items.size();i++){
            OrderItem bean=items.get(i);
            int curr_item_other_amt=Double.valueOf(Arith.div(item_other_amt,items.size(),0)).intValue();//均摊到每个子单时的额外费用
            if(i==items.size()-1)curr_item_other_amt=item_other_amt-surplus_amt;
            else surplus_amt+=curr_item_other_amt;
            int item_amt=Double.valueOf(Utils.mul(bean.getTotal(),100)).intValue();
            log.info(String.format("订单[%s]子单[%s]支付给[%s]金额为[%s]分,分摊金额[%s]分",bean.getOrderId(),bean.getItemId(),bean.getSellerId(),item_amt,curr_item_other_amt));
            orderName=String.format("号码[%s]",bean.getNum());
            payee=String.valueOf(bean.getSellerId());
            Map map=new HashMap();
            map.put("payee",payee);
            map.put("item_amt",order.getTotal());
            map.put("item_id",order.getOrderId());
            payeeList.add(map);
        }
        return this.payHrPayOrder(orderNo,payer,payeeList,order.getTotal(),openid,orderName,Pay001.PAY_TRADE_TYPE_JSAPI,Pay001.PAY_MENTHOD_TYPE_1,Pay001.ORDER_TRADE_TYPE_1);
    }

    public Result payHrOrderSettle(int order_id,List<Map> payeeList) {
        String orderNo=String.valueOf("S"+order_id);//订单号
        Example example = new Example(OrderItem.class);
        example.createCriteria().andEqualTo("orderId",order_id).andGreaterThan("total",0);//订单大于0
        List<OrderItem> items = orderItemMapper.selectByExample(example);
        if(items.size()==0||items.size()>1) return new Result(Result.OK, "订单数据错误");
        String payer=null;//原订单收款方结算时则为订单付款
        double total=0d;
        for(int i=0;i<items.size();i++){
            OrderItem bean=items.get(i);
            payer=String.valueOf(bean.getSellerId());
        }
        for(Map map:payeeList){
            total=Utils.sum(NumberUtils.toDouble(ObjectUtils.toString(map.get("item_amt"))),total);
        }
        String orderName="订单结算";
        return this.payHrPayOrder(orderNo,payer,payeeList,total,null,orderName,null,Pay001.PAY_MENTHOD_TYPE_5,Pay001.ORDER_TRADE_TYPE_2);
    }
    public PayBase createPayBase() {
        String serial=Utils.randomNoByDateTime();
        return new PayBase(SystemParam.get("hr-pay-url"),serial,SystemParam.get("hr-pay-merid"),SystemParam.get("hr-pay-key"));
    }

    /**
     * 账户查询
     * @param account_no
     * @return
     */
    public Result payHrPayAccount(String account_no) {
        PayBase payBase=createPayBase();
        Pay006 pay006=new Pay006(payBase.getUrl(),payBase.getSerial(),payBase.getMerid(),payBase.getKey(),account_no);
        com.hrtx.common.dto.Result result=PayClient.callPay006(pay006);
        if(result.getCode()== com.hrtx.common.dto.Result.OK){
            return new Result(Result.OK,result.getData());
        }else{
            return new Result(result.getCode(),result.getDesc());
        }
    }

    /**
     * 账户提现到微信红包
     * @param account_no    提现账户
     * @param amt           提现金额(元)
     * @return
     */
    public Result payHrPayWithdrawToWx(String account_no,Double amt) {
        PayBase payBase=createPayBase();
        int withdrawType=Pay008.WITHDRAW_TYPE_1;
        String orderNo=this.fundOrderMapper.getId()+"";
        String orderName="余额提现";
        int w_amt=Double.valueOf(Utils.mul(amt,100)).intValue();//单位分
        String sub_appid = SystemParam.get("wxx_appid");
        Result result_openid=this.getPayer(2);
        if(result_openid.getCode()!=Result.OK)return result_openid;
        String mchWxOpenid=String.valueOf(result_openid.getData());//提现的发放用户
        Pay008 pay008=new Pay008(payBase.getUrl(),payBase.getSerial(),payBase.getMerid(),payBase.getKey()
                ,orderNo,account_no,orderName,w_amt,withdrawType,sub_appid,mchWxOpenid);
        com.hrtx.common.dto.Result result=PayClient.callPay008(pay008);
        if(result.getCode()== com.hrtx.common.dto.Result.OK){
            return new Result(Result.OK,result.getData());
        }else{
            return new Result(result.getCode(),result.getDesc());
        }
    }
    /**
     *
     * @param page_num
     * @param limit
     * @param account_no
     * @param order_no
     * @param start_date    yyyy-mm-dd 24:mi:ss
     * @param end_date      yyyy-mm-dd 24:mi:ss
     * @return
     */
    public Result payHrPayAccountDetail(int page_num,int limit,String order_no,String account_no,String start_date,String end_date) {
        PayBase payBase=createPayBase();
        Pay005 pay005 = new Pay005(payBase.getUrl(),payBase.getSerial(),payBase.getMerid(),payBase.getKey(),
                page_num,limit,order_no,account_no,start_date,end_date);
        com.hrtx.common.dto.Result result = PayClient.callPay005(pay005);
        if(result.getCode()== com.hrtx.common.dto.Result.OK){
            return new Result(Result.OK,result.getData());
        }else{
            return new Result(result.getCode(),result.getDesc());
        }
    }
    public Result payHrPayOrderSign(String order_id) {
        PayBase payBase=createPayBase();
        Pay007 pay007=new Pay007(payBase.getUrl(),payBase.getSerial(),payBase.getMerid(),payBase.getKey(),order_id,order_id);
        com.hrtx.common.dto.Result result=PayClient.callPay007(pay007);
        if(result.getCode()== com.hrtx.common.dto.Result.OK){
            return new Result(Result.OK,result.getData());
        }else{
            return new Result(result.getCode(),result.getDesc());
        }
    }

    /**
     * 支付
     * @param order_id  订单号
     * @param payer     付款方
     * @param payeeList     收款方
     * @param order_amt 订单金额 元
     * @param payer_openid  付款方openid（微信支付时需要）
     * @param orderName     订单名称
     * @param pay_trade_type    付款方式
     * @param pay_type          付款类型
     * @param order_trade_type  订单类型
     * @return
     */
    private Result payHrPayOrder(String order_id,String payer,List<Map> payeeList,double order_amt,String payer_openid,String orderName,String pay_trade_type,int pay_type,int order_trade_type) {
        PayBase payBase=createPayBase();
        String beforeUrl=null;//SystemParam.get("domain-full")+"api/pingan-pay-result";
        String afterUrl=SystemParam.get("domain-full")+"api/hr-pay-result";
        String subAppId = SystemParam.get("wxx_appid");
//        String serial=String.valueOf(order.getOrderId());//流水号，原样返回
        String orderNo=String.valueOf(order_id);//订单号
        int amt=Double.valueOf(Utils.mul(order_amt,100)).intValue();//单位分
        if(amt<0) return new Result(Result.ERROR,"订单金额错误");
        ArrayList<Pay001Payee> arrayList= new ArrayList<>();
        /*String payTradeType=Pay001.PAY_TRADE_TYPE_JSAPI;
        int pay_payType=Pay001.PAY_MENTHOD_TYPE_1;
        int orderTradeType=Pay001.ORDER_TRADE_TYPE_1;
        Example example = new Example(OrderItem.class);
        example.createCriteria().andEqualTo("orderId",order.getOrderId()).andGreaterThan("total",0);//订单大于0
        List<OrderItem> items = orderItemMapper.selectByExample(example);
        int item_other_amt=Double.valueOf(Utils.mul(order.getShippingTotal()-order.getCommission(),100)).intValue();//每个子单时的额外费用
        int surplus_amt=0;
        for(int i=0;i<items.size();i++){
            OrderItem bean=items.get(i);
            int curr_item_other_amt=Double.valueOf(Arith.div(item_other_amt,items.size(),0)).intValue();//均摊到每个子单时的额外费用
            if(i==items.size()-1)curr_item_other_amt=item_other_amt-surplus_amt;
            else surplus_amt+=curr_item_other_amt;
            int itme_amt=Double.valueOf(Utils.mul(bean.getTotal(),100)).intValue();
            log.info(String.format("订单[%s]子单[%s]支付给[%s]金额为[%s]元,分摊金额[%s]分",bean.getOrderId(),bean.getItemId(),bean.getSellerId(),bean.getTotal(),curr_item_other_amt));
            Pay001Payee pay001Payee=new Pay001Payee(String.valueOf(bean.getItemId()),String.valueOf(bean.getSellerId()),itme_amt+curr_item_other_amt);//收款方公司id  需确保不会存在
            arrayList.add(pay001Payee);
            orderName=String.format("号码[%s]",bean.getNum());
        }*/
        for(Map map:payeeList){
            int item_amt=Double.valueOf(Utils.mul(NumberUtils.toDouble(ObjectUtils.toString(map.get("item_amt"))),100)).intValue();//单位分
            Pay001Payee pay001Payee=new Pay001Payee(ObjectUtils.toString(map.get("item_id")),ObjectUtils.toString(map.get("payee")),item_amt);//收款方公司id  需确保不会存在
            arrayList.add(pay001Payee);
        }
        Pay001 pay001=new Pay001(payBase.getUrl(),payBase.getSerial(),payBase.getMerid(),payBase.getKey(), orderNo, payer,orderName,beforeUrl,afterUrl,subAppId,payer_openid,pay_trade_type,pay_type,amt,order_trade_type);
        pay001.setOrders(arrayList);
        com.hrtx.common.dto.Result result=PayClient.callPay001(pay001);
        if(result.getCode()== com.hrtx.common.dto.Result.OK){
            return new Result(Result.OK,result.getData());
        }else{
            return new Result(result.getCode(),result.getDesc());
        }
    }
}
