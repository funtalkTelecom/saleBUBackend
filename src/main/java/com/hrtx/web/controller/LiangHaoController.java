package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.LockUtils;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.SystemParam;
import com.hrtx.web.pojo.Num;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.pojo.Order;
import com.hrtx.web.service.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lianghao")
public class LiangHaoController extends BaseReturn{
    public final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private NumService numService;
    @Resource private ApiMealService apiMealService;
    @Resource private ApiOrderService apiOrderService;
    @Resource private DictService dictService;


    @RequestMapping("/lianghao-query")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_QUEYR})
    public ModelAndView lianghaoQuery(Number number){
        return new ModelAndView("admin/lianghao/lianghao-query");
    }

    @RequestMapping("/lianghao-list")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_QUEYR})
    public Result listNumber(NumPrice numPrice){
        numPrice.setPageNum(numPrice.startToPageNum());
        numPrice.setChannel(NumberUtils.toInt(String.valueOf(SystemParam.get("lianghao_channel"))));
        numPrice.setAgentId(NumberUtils.toLong(String.valueOf(SystemParam.get("lianghao_agent_id"))));
        PageInfo<Object> objectPageInfo = numService.queryNumPrice(numPrice);
        objectPageInfo =numService.queryFreeze(objectPageInfo);
        return new Result(Result.OK, objectPageInfo);
    }
    @PostMapping("/freeze-num")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_FREEZE})
    public Result freezeNum(Num num){
        return numService.freezeNum(num);
    }

    @GetMapping("/add-order")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_ADD})
    public ModelAndView addOrder(NumPrice numPrice, HttpServletRequest request){
        numPrice = numService.getNumPrice(numPrice.getId());
        if(numPrice == null) return new ModelAndView("admin/error-page").addObject("errormsg", "号码未找到");
        Result result = apiMealService.mealListForNum(numPrice.getNumId()+"", request);
        List mealList = new ArrayList();
        if(result.getCode() == Result.OK) mealList = (List) result.getData();
        return new ModelAndView("admin/lianghao/lianghao-add-order")
                .addObject("numPrice", numPrice)
                .addObject("types", dictService.findDictByGroup("phone_consumer_id_type"))
                .addObject("mealList", mealList);
    }

    @PostMapping("/add-order")
    @Powers({PowerConsts.LIANGHAOMOUDULE_COMMON_ADD})
    public Result addOrder(HttpServletRequest request){
        //第三方订单号、手机号码、套餐名称、BOSS开户工号、客户名称、客户证件类型、客户证件编码、邮寄联系人、邮寄联系电话、邮寄地址
        Result result = this.vaildAddOrder(request);
        if(result.getCode() != Result.OK) return  result;
        String id = request.getParameter("id");
        if(!LockUtils.tryLock("kfadd"+id)) return new Result(Result.ERROR, "此号码下单中，请稍后再试!");
        try {
            NumPrice numPrice = numService.getNumPrice(NumberUtils.toLong(id));
            if(numPrice == null) return new Result(Result.ERROR, "未找到号码");
            //判断是否冻结
            Long fuser = numService.queryFreeze(numPrice.getNumId());
            if(fuser != null && !fuser.equals(SessionUtil.getUserId())) return new Result(Result.ERROR, "此号码已冻结不可下单");

            Map<String, String> param = new HashMap<>();
            param.put("user-agent", request.getHeader("user-agent"));
            param.put("type", "2");
            param.put("skuid", numPrice.getSkuId()+"");
            param.put("numid", numPrice.getNumId()+"");
            param.put("numcount", "1");
            param.put("mealid", request.getParameter("mealId"));
            param.put("bossNum", request.getParameter("bossNum"));
            param.put("phoneConsumer", request.getParameter("phoneConsumer"));
            param.put("phoneConsumerIdType", request.getParameter("phoneConsumerIdType"));
            param.put("phoneConsumerIdNum", request.getParameter("phoneConsumerIdNum"));
            param.put("personName", request.getParameter("personName"));
            param.put("personTel", request.getParameter("personTel"));
            param.put("address", request.getParameter("address"));
            param.put("thirdOrder", request.getParameter("thirdOrder"));
            param.put("conment", request.getParameter("conment"));

            return apiOrderService.createOrder(param, null);
        }finally {
            LockUtils.unLock("kfadd"+id);
        }
    }

    private Result vaildAddOrder(HttpServletRequest request) {
        if(NumberUtils.toLong(request.getParameter("mealId")) == 0) return new Result(Result.ERROR,"请选择套餐");
        if(StringUtils.isBlank(request.getParameter("thirdOrder"))) return new Result(Result.ERROR,"请填写第三方订单号");
        if(StringUtils.isBlank(request.getParameter("bossNum"))) return new Result(Result.ERROR,"请填写BOSS开户工号");
        if(StringUtils.isBlank(request.getParameter("phoneConsumer"))) return new Result(Result.ERROR,"请填写客户名称");
        if(NumberUtils.toInt(request.getParameter("phoneConsumerIdType")) == 0) return new Result(Result.ERROR,"请填写客户证件类型");
        if(StringUtils.isBlank(request.getParameter("phoneConsumerIdNum"))) return new Result(Result.ERROR,"请填写客户证件编码");
        if(StringUtils.isBlank(request.getParameter("personName"))) return new Result(Result.ERROR,"请填写邮寄联系人");
        if(StringUtils.isBlank(request.getParameter("personTel"))) return new Result(Result.ERROR,"请填写邮寄联系电话");
        if(StringUtils.isBlank(request.getParameter("address"))) return new Result(Result.ERROR,"请填写邮寄地址");
        return new Result(Result.OK, "");
    }
}
