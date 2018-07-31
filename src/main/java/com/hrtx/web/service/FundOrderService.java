package com.hrtx.web.service;

import com.github.abel533.entity.Example;
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
        Result result = this.getPayer(2);
//        Result result = new Result(Result.OK, "o1F3M4sVzb7FUkxpgzGBinJWpnQA");
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
            FundOrder fundOrder = new FundOrder(0l, busiType, amt, payee, payer, 1, orderName, contractno, third, amt, remark, sourceId);
            fundOrder.setId(fundOrder.getGeneralId());
            fundOrderMapper.insert(fundOrder);
            Long req_user = apiSessionUtil.getConsumer() == null ? 0l:apiSessionUtil.getConsumer().getId();
            FundDetail fundDetail = new FundDetail(0l, fundOrder.getId(), contractno, SessionUtil.getUserIp(), req_user, new Date(), FundDetail.ORDER_ACT_TYPE_ADD, 1);
            fundDetail.setId(fundDetail.getGeneralId());
            fundDetailMapper.insert(fundDetail);

            String notify_url = SystemParam.get("domain-full")+"/pingan-pay-result";
            String sub_appid = SystemParam.get("wxx_appid");
            Result result = null;
            try {
                if(FundOrder.THIRD_PAY_PINGANAPP.equals(third)) {
                    result=pinganService.payOrder(contractno,"WeixinOL",orderName,amt,amt,remark,notify_url,sub_appid,payer,"JSAPI");
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
                            "YZF", amt, remark, SystemParam.get("domain-full")+"/api/yzffq-pay-result-jump", SystemParam.get("domain-full")+"/api/yzffq-pay-result"));
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
        Long orderId = NumberUtils.toLong(fundOrder.getSourceId());
        Result result = null;
        if(FundOrder.BUSI_TYPE_PAYORDER.equals(busiType)) {// 订单支付完成回调
            if(status == 3) {//支付成功
                try {
                    result = orderService.payOrderSuccess(orderId);
                    if(result.getCode() == Result.OK) {
                        result = orderService.payDeliverOrder(orderId);
                    }
                }catch (Exception e) {
                    log.error("支付完成，发货失败", e);
                }
            }
        }
        if(FundOrder.BUSI_TYPE_PAYDEPOSIT.equals(busiType)) {//保证金支付完成 回调
            try{
                auctionDepositService.newAuctionDepositPay(orderId, status == 3 ? true : false, Utils.getDate(0,"yyyyMMddHHmmss"));//payTime
            }catch (Exception e) {
                log.error("支付完成更新保证金回调信息异常", e);
            }
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
            Long req_user = apiSessionUtil.getConsumer() == null ? 0l:apiSessionUtil.getConsumer().getId();
            String contractno = "REFUND"+Utils.randomNoByDateTime();
            FundDetail fundDetail = new FundDetail(0l, fundOrder.getId(), contractno, SessionUtil.getUserIp(), req_user, new Date(), FundDetail.ORDER_ACT_TYPE_REFUND, 1);
            fundDetail.setId(fundDetail.getGeneralId());
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
                    result = new Result(payResponse.getResCode(), payResponse.getResDesc());
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
    private Result getPayer(int loginType){
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
}
