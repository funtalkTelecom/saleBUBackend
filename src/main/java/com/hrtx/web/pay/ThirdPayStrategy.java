package com.hrtx.web.pay;

import com.hrtx.web.dto.PayRequest;
import com.hrtx.web.dto.PayResponse;

public interface ThirdPayStrategy {

    /**
     * 支付
     * @param payRequest
     * @return
     */
    public PayResponse payOrder(PayRequest payRequest);

    /**
     * 退款
     * @param payRequest
     * @return
     */
    public PayResponse payRefund(PayRequest payRequest);

    /**
     * 取消订单
     * @param payRequest
     * @return
     */
    public PayResponse payCancel(PayRequest payRequest);

    /**
     * 根据条件查询支付订单
     * @param payRequest
     * @return
     */
    public PayResponse queryOrderList(PayRequest payRequest);

    /**
     *查询订单明细
     * @param orderNo
     * @return
     */
    public PayResponse queryOrderView(String orderNo);

    /**
     * 获取支付方式列表
     * @return
     */
    public PayResponse queryPayList();
}
