package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.pojo.InterfaceMerchant;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.service.NumService;
import com.hrtx.web.service.ThirdRequestService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/stress-test")
public class StressTestController extends BaseReturn{
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired private ThirdRequestService thirdRequestService;
    @Autowired private NumService numService;


    @RequestMapping("/query-hk")
    @Powers({PowerConsts.NOLOGINPOWER})
    public Result qeruyHk(HttpServletRequest request){
        long a = System.currentTimeMillis();
        String merid = ObjectUtils.toString(request.getParameter("merid"));
        InterfaceMerchant merchant = thirdRequestService.findMerchant(merid);
        if(merchant == null) return new Result(Result.ERROR, "商户不存在");
        int operator = NumberUtils.toInt(request.getParameter("operator"));
        int province_code = NumberUtils.toInt(request.getParameter("province_code"));
        int city_code = NumberUtils.toInt(request.getParameter("city_code"));
        String feature = ObjectUtils.toString(request.getParameter("feature"));
        String pattern = ObjectUtils.toString(request.getParameter("pattern"), "???????????");
        int page_num = NumberUtils.toInt(request.getParameter("page_num"), 1);
        NumPrice numPrice = new NumPrice();
        numPrice.setLimit(30);
        numPrice.setStart((page_num-1)*numPrice.getLimit());
        numPrice.setNetType(operator == 1 ? "电信":(operator == 2 ? "联通" : (operator == 3 ? "移动" : "")));
        numPrice.setProvinceCode(province_code);
        numPrice.setCityCode(city_code);
        numPrice.setFeature(feature);
        numPrice.setTemp(pattern.replaceAll("\\?","_"));
        numPrice.setAgentId(merchant.getCorpId());
        numPrice.setChannel(merchant.getChanel());

        if("???????????".equals(pattern) && operator == 0 && province_code == 0 && city_code == 0 && StringUtils.isBlank(feature)) {
            numPrice.setSort(1);
        }
        List list = numService.queryNumPriceList(numPrice);
        log.info("----------------查询号码耗时【"+(System.currentTimeMillis()-a)+"】ms");
        return new Result(Result.OK, list);
    }
}
