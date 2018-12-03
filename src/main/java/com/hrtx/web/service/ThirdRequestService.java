package com.hrtx.web.service;

import com.github.abel533.entity.Example;
import com.hrtx.web.dto.PayRequest;
import com.hrtx.web.dto.PayResponse;
import com.hrtx.web.mapper.InterfaceMerchantMapper;
import com.hrtx.web.mapper.ThirdRequestMapper;
import com.hrtx.web.pay.ThirdPayStrategy;
import com.hrtx.web.pojo.InterfaceMerchant;
import com.hrtx.web.pojo.ThirdRequest;
import com.hrtx.web.pojo.ThirdRequestWithBLOBs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ThirdRequestService {
    @Resource private ThirdRequestMapper thirdRequestMapper;
    @Resource private InterfaceMerchantMapper interfaceMerchantMapper;
	private Logger log = LoggerFactory.getLogger(ThirdRequestService.class);

    public void addThirdRequest(String serial, String request, String response, String contentType, String appCode, String thirdType) {
        thirdRequestMapper.insert(new ThirdRequestWithBLOBs(contentType, appCode, thirdType, serial, "", new Date(), request, response));
    }

    public ThirdRequest findThirdRequest(String serial) {
        Example example = new Example(ThirdRequest.class);
        example.createCriteria().andEqualTo("requestSerial", serial);
        List<ThirdRequestWithBLOBs> list = thirdRequestMapper.selectByExample(example);
        if(list.size() == 1) return list.get(0);
        return null;
    }

    public InterfaceMerchant findMerchant(String merid) {
        Example example = new Example(InterfaceMerchant.class);
        example.createCriteria().andEqualTo("code", merid);
        List<InterfaceMerchant> list = interfaceMerchantMapper.selectByExample(example);
        if(list.size() == 1) return list.get(0);
        return null;
    }
}
