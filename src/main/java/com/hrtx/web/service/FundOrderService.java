package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.dto.Result;
import com.hrtx.global.*;
import com.hrtx.global.pinganUtils.TLinx2Util;
import com.hrtx.web.mapper.*;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.pojo.FundDetail;
import com.hrtx.web.pojo.FundOrder;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FundOrderService extends BaseService {
	@Autowired private FundOrderMapper fundOrderMapper;
	@Autowired private FundDetailMapper fundDetailMapper;
	@Autowired private PinganService pinganService;
	@Autowired private ApiSessionUtil apiSessionUtil;

    /**
     * 平台订单支付（平安微信小程序支付方式）
     * @param amt 支付金额
     * @param payer 付款方
     * @param order_name 订单描述
     * @param sourceId  订单号
     * @return
     */
    public Result payPinganWxxOrder(int amt, String payer, String order_name, String sourceId) {
        return payAddOrder(FundOrder.BUSI_TYPE_PAYORDER, amt, "", payer, order_name, FundOrder.THIRD_PAY_PINGANAPP, sourceId, "");
    }

    /**
     * 保证金支付（平安微信小程序支付方式）
     * @param amt 支付金额
     * @param payer 付款方
     * @param order_name 订单描述
     * @param sourceId  保证金订单号
     * @return
     */
    public Result payPinganWxxDeposit(int amt, String payer, String order_name, String sourceId) {
        return payAddOrder(FundOrder.BUSI_TYPE_PAYDEPOSIT, amt, "", payer, order_name, FundOrder.THIRD_PAY_PINGANAPP, sourceId, "");
    }

    /**
     *
     * @param busi_type 业务类型
     * @param amt  付款金额
     * @param payee 收款方
     * @param payer 付款方
     * @param order_name 支付订单描述
     * @param third 支付方式
     * @param sourceId 业务来源编码
     * @param remark 备注
     * @return
     */
	public Result payAddOrder(String busi_type, int amt, String payee, String payer, String order_name, String third, String sourceId, String remark){
	    String orderMark = busi_type+"-"+sourceId;
        if(!LockUtils.tryLock(orderMark)) return new Result(Result.ERROR, "正在支付，请勿重复请求");
        try {
            Example example = new Example(FundOrder.class);
            example.createCriteria().andEqualTo("busi", busi_type).andEqualTo("sourceId", sourceId).andIn("status",Arrays.asList(new int[]{3,5,7}));
            List fundOrders = fundOrderMapper.selectByExample(example);
            if(fundOrders.size()>0) return new Result(Result.ERROR, "订单已支付");
            String contractno = "PAY"+Utils.randomNoByDateTime();
            FundOrder fundOrder = new FundOrder(0l, busi_type, amt, payee, payer, 1, order_name, contractno, third, amt, remark, sourceId);
            fundOrder.setId(fundOrder.getGeneralId());
            fundOrderMapper.insert(fundOrder);
            Long req_user = apiSessionUtil.getUser() == null ? 0l:apiSessionUtil.getUser().getId();
            FundDetail fundDetail = new FundDetail(0l, fundOrder.getId(), contractno, SessionUtil.getUserIp(), req_user, new Date(), FundDetail.ORDER_ACT_TYPE_ADD, 1);
            fundDetail.setId(fundDetail.getGeneralId());
            fundDetailMapper.insert(fundDetail);

            String notify_url = SystemParam.get("pay_call_back_url");
            String sub_appid = SystemParam.get("wxx_appid");
            Result result = null;
            try {
                if(FundOrder.THIRD_PAY_PINGANAPP.equals(third)) {
                    result=pinganService.payOrder(contractno,"WeixinOL",order_name,amt,amt,remark,notify_url,sub_appid,payer,"JSAPI");
                    if(result.getCode() == Result.OK) {
                        JSONObject json1=(JSONObject) result.getData();
                        String[] keys={"appid","noncestr","timestamp","signType","package","paysign"};
                        for (String key:keys) {
                            if(!json1.containsKey(key))return  result = new Result(Result.ERROR,"接口返回参数错误");
                        }
                        Map _map = new HashMap();
                        _map.put("appId",json1.getString("appid"));
                        _map.put("nonceStr",json1.getString("noncestr"));
                        _map.put("timeStamp",json1.getString("timestamp"));
                        _map.put("signType",json1.getString("signType"));
                        _map.put("package",json1.getString("package"));
                        _map.put("paySign",json1.getString("paysign"));
                        return result = new Result(Result.OK, _map);
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
                desc = desc.length() > 200 ? desc.substring(0, 199) : desc;
                fundDetail.setResDesc(desc);
                if(result.getCode() == Result.WARN) {//请求未知
                    fundDetail.setStatus(2);//请求中
                }
                if(result.getCode() == Result.OK) {//请求成功
                    fundDetail.setStatus(3);//请求结束
                    fundOrder.setStatus(2);//等待付款
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
        String out_no = params.get("out_no");
        FundDetail fundDetail = new FundDetail();
        fundDetail.setSerial(out_no);
        fundDetail.setAct_type(FundDetail.ORDER_ACT_TYPE_ADD);
        fundDetail = fundDetailMapper.selectOne(fundDetail);
        if(fundDetail == null) return new Result(Result.ERROR, "未找到订单");
        fundDetail.setStatus(5);//回掉完成
        Example example = new Example(FundDetail.class);
        example.createCriteria().andEqualTo("id",fundDetail.getId()).andIn("status", Arrays.<Object>asList(new int[]{2,3}));
        int count = fundDetailMapper.updateByExample(fundDetail, example);
        if(count != 1) throw new ServiceException("该状态订单不接受回调");

        FundOrder fundOrder = new FundOrder();
        fundOrder.setId(fundDetail.getFund_order_id());
        fundOrder = fundOrderMapper.selectByPrimaryKey(fundOrder);
        if(fundOrder == null) throw new ServiceException("未找到订单");
        String status = params.get("status");
        if("1".equals(status)) {
            fundOrder.setStatus(3);//已支付
        }else if("4".equals(status)) {
            fundOrder.setStatus(4);//已取消
        }
        example = new Example(FundOrder.class);
        example.createCriteria().andEqualTo("id",fundOrder.getId()).andIn("status", Arrays.<Object>asList(new int[]{1,2}));
        count = fundOrderMapper.updateByExample(fundOrder, example);
        if(count != 1) throw new ServiceException("该状态订单不接受回调");
        return new Result(Result.OK, "success");
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
     * @param busi_type 业务类型
     * @param sourceId 业务编码
     * @param remark 退款备注
     * @return
     */
    public Result payRefund(String busi_type, String sourceId, String remark){
        Example example = new Example(FundOrder.class);
        example.createCriteria().andEqualTo("busi", busi_type).andEqualTo("sourceId", sourceId).andEqualTo("status", 3);
        List fundOrders = fundOrderMapper.selectByExample(example);
        if(fundOrders.size() != 1) return new Result(Result.ERROR, "退款订单不存在");
        FundOrder fundOrder = (FundOrder) fundOrders.get(0);
        String outNo = fundOrder.getContractno();
        if(!LockUtils.tryLock(outNo)) return new Result(Result.ERROR, "退款中，请稍后再试");
        try {
            Long req_user = apiSessionUtil.getUser() == null ? 0l:apiSessionUtil.getUser().getId();
            String contractno = "REFUND"+Utils.randomNoByDateTime();
            FundDetail fundDetail = new FundDetail(0l, fundOrder.getId(), contractno, SessionUtil.getUserIp(), req_user, new Date(), FundDetail.ORDER_ACT_TYPE_REFUND, 1);
            fundDetail.setId(fundDetail.getGeneralId());
            fundDetailMapper.insert(fundDetail);
            Result result = null;
            try {
                if(FundOrder.THIRD_PAY_PINGANAPP.equals(fundOrder.getThird())) {
                    result = pinganService.payRefund(outNo, contractno, fundOrder.getOrder_name()+"退款", fundOrder.getAmt(), remark);
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
                    fundOrder.setStatus(7);//退款中
                }
                if(result.getCode() == Result.OK) {//请求成功
                    fundDetail.setStatus(3);//请求结束
                    JSONObject json1=(JSONObject) result.getData();
                    String status = json1.getString("status");
                    if ("1".equals(status)) {
                        fundOrder.setStatus(5);//已退款
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
}
