package com.hrtx.web.controller;

import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Corporation;
import com.hrtx.web.service.CorporationService;
import com.hrtx.web.service.SmsService;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/corp")
public class CorporationController extends BaseReturn{
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired private SmsService smsService;
    @Autowired private CorporationService corporationService;

    //////////////////商家结算//////////////////
    /**
     * 商家结算页面
     */
    @GetMapping("/settle-index")
    @Powers({PowerConsts.FINANCE_CORP})
    public ModelAndView corpSettleIndex(){
        return new ModelAndView("admin/account/corp-settle");
    }

    /**
     *  统计订单数据
     */
    @GetMapping("/count-order-data")
    @Powers({PowerConsts.FINANCE_CORP})
    public Result countOrderData(){
        Object o=this.corporationService.queryCorpBusiData();
        return new Result(Result.OK,o);
    }
    /**
     * 商家收支明细
     */
    @RequestMapping("/income/list")
    @Powers({PowerConsts.FINANCE_CORP})
    public Object queryCorpIncomeList(Corporation corporation,HttpServletRequest request){
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        Object o=this.corporationService.financeList(startDate,endDate,corporation);
        return o;
    }
    /**
     * 商家收支导出
     */
    @GetMapping("/income/list/excel")
    @Powers({PowerConsts.FINANCE_CORP})
    public Result excelCorpIncomeList(HttpServletRequest request){
        String startDate=request.getParameter("startDate");
        String endDate=request.getParameter("endDate");
        return null;
    }

    /**
     * 提现
     */
    @PostMapping("/finance-withdraw")
    @Powers({PowerConsts.FINANCE_CORP})
    public Result financeWithdraw(HttpServletRequest request){
        int accountId=NumberUtils.toInt(request.getParameter("accountId"));
        double withDrawAmt=NumberUtils.toDouble(request.getParameter("withDrawAmt"));
        String check_code=request.getParameter("smsCode");
        Result result=smsService.checkKey(SmsService.req_type_pc,check_code);
        if(result.getCode()!=Result.OK)return result;
        return this.corporationService.financeWithdraw(accountId,withDrawAmt);
//        return new Result(Result.OK,"提现成功");
    }
    /**
     * 提现进度
     */
    @GetMapping("/finance-withdraw-progresss")
    @Powers({PowerConsts.FINANCE_CORP})
    public Result financeWithdrawProgresss(){
        return new Result(Result.OK,"");
    }


}
