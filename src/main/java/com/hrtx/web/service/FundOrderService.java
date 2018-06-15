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

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FundOrderService extends BaseService {
	@Autowired private FundOrderMapper fundOrderMapper;
	@Autowired private FundDetailMapper fundDetailMapper;
	@Autowired private PinganService pinganService;
	@Autowired private ApiSessionUtil apiSessionUtil;

    /**
     * 支付订单下单
     * @param busi_type  业务类型
     * @param amt 支付金额
     * @param payer 付款方
     * @param order_name 订单描述
     * @param sourceId  业务来源编码
     * @return
     */
    public Result payAddOrder(String busi_type, int amt, String payer, String order_name, String sourceId) {
        return payAddOrder(sourceId+"", busi_type, amt, "", payer, order_name, FundOrder.THIRD_PAY_PINGANAPP, sourceId, "");
    }

	public Result payAddOrder(String contractno, String busi_type, int amt, String payee, String payer, String order_name, String third, String sourceId, String remark){
        if(!LockUtils.tryLock(contractno)) return new Result(Result.ERROR, "正在支付，请勿重复请求");
        try {
            FundOrder fundOrder = new FundOrder();
            fundOrder.setContractno(contractno);
            fundOrder = fundOrderMapper.selectOne(fundOrder);
            if(fundOrder != null && fundOrder.getStatus() == 3) return new Result(Result.ERROR, "已支付，请勿重复提交");
//            String contractno = "PAY"+Utils.randomNoByDateTime();
            fundOrder = new FundOrder(0l, busi_type, amt, payee, payer, 1, order_name, contractno, third, amt, remark, sourceId);
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
            LockUtils.unLock(contractno);
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

    public Result payRefund(String outNo, String remark){
        if(!LockUtils.tryLock(outNo)) return new Result(Result.ERROR, "退款中，请稍后再试");
        try {
            FundOrder fundOrder = new FundOrder();
            fundOrder.setContractno(outNo);
            fundOrder = fundOrderMapper.selectOne(fundOrder);
            if(fundOrder == null) return new Result(Result.ERROR, "退款订单不存在");
            if(fundOrder.getStatus() != 3) return new Result(Result.ERROR, "订单此状态不可退款");
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
