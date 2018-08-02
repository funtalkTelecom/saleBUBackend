package com.hrtx.web.service;

import com.hrtx.web.dto.PayRequest;
import com.hrtx.web.dto.PayResponse;
import com.hrtx.web.mapper.ThirdRequestMapper;
import com.hrtx.web.pay.ThirdPayStrategy;
import com.hrtx.web.pay.YzfPayStrategy;
import com.hrtx.web.pojo.ThirdRequestWithBLOBs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ThirdPayService implements ThirdPayStrategy {

	private Logger log = LoggerFactory.getLogger(ThirdPayService.class);
	private ThirdPayStrategy thirdPayStrategy;

	@Autowired private ThirdRequestMapper thirdRequestMapper;

    public void setThirdPayStrategy(ThirdPayStrategy thirdPayStrategy) {
        this.thirdPayStrategy = thirdPayStrategy;
    }

    @Override
    public PayResponse payOrder(PayRequest payRequest) {
        PayResponse payResponse = thirdPayStrategy.payOrder(payRequest);
        thirdRequestMapper.insert(new ThirdRequestWithBLOBs(payResponse.getContentType(), payResponse.getInterfaceName(), payResponse.getThirdType(),
                payRequest.getSerial(), payRequest.getOrderNo(), new Date(), payResponse.getRequestStr(), payResponse.getResponseStr()));
        return payResponse;
    }

    @Override
    public PayResponse payRefund(PayRequest payRequest) {
        PayResponse payResponse = thirdPayStrategy.payRefund(payRequest);
        thirdRequestMapper.insert(new ThirdRequestWithBLOBs(payResponse.getContentType(), payResponse.getInterfaceName(), payResponse.getThirdType(),
                payRequest.getSerial(), payRequest.getOrderNo(), new Date(), payResponse.getRequestStr(), payResponse.getResponseStr()));
        return payResponse;
    }

    @Override
    public PayResponse payCancel(PayRequest payRequest) {
        PayResponse payResponse = thirdPayStrategy.payCancel(payRequest);
        thirdRequestMapper.insert(new ThirdRequestWithBLOBs(payResponse.getContentType(), payResponse.getInterfaceName(), payResponse.getThirdType(),
                payRequest.getSerial(), payRequest.getOrderNo(), new Date(), payResponse.getRequestStr(), payResponse.getResponseStr()));
        return payResponse;
    }

    @Override
    public PayResponse queryOrderList(PayRequest payRequest) {
        return thirdPayStrategy.queryOrderList(payRequest);
    }

    @Override
    public PayResponse queryOrderView(String orderNo) {
        return thirdPayStrategy.queryOrderView(orderNo);
    }

    @Override
    public PayResponse queryPayList() {
        return thirdPayStrategy.queryPayList();
    }
}
