package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.Constants;
import com.hrtx.global.ExcelUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Channel;
import com.hrtx.web.pojo.Dict;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.service.AgentService;
import com.hrtx.web.service.ChannelService;
import com.hrtx.web.service.DictService;
import com.hrtx.web.service.NumPriceService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/numprice")
public class NumPriceController extends BaseReturn{
    public final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private NumPriceService numPriceService;



    @RequestMapping("/numprice-query")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_QUEYR})
    public ModelAndView numpriceQuery( HttpServletRequest request){
        request.setAttribute("channel",  Constants.contantsToList("CHANNEL_ID"));
        return new ModelAndView("admin/numprice/numprice-query");
    }

    @RequestMapping("/numprice-list")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_QUEYR})
    public Result numpriceList(NumPrice numPrice){
        return numPriceService.queryNumPrice(numPrice);
    }



    @RequestMapping("/query-agent-numprice")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_QUEYR})
    public Result queryAgentNumprice(NumPrice numPrice,String commpayName){
        return numPriceService.queryAgentNumprice(numPrice,commpayName);
    }


    @RequestMapping("/save-agent-numprice")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_EDIT})
    public Result saveAgentNumprice(NumPrice numPrice,String commpayName){
        return numPriceService.saveAgentNumprice(numPrice,commpayName);
    }

    @RequestMapping("/save-agent-numprices")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_EDIT})
    public Result saveAgentNumprices(NumPrice numPrice,String commpayName){
        if(StringUtils.isBlank(numPrice.getPrice().toString())) return new Result(Result.ERROR, "请输入价格");
        if(StringUtils.isBlank(commpayName)) return new Result(Result.ERROR, "请输入代理商");
        if(StringUtils.isBlank(numPrice.getResource())) return new Result(Result.ERROR, "请输入号码");
        return numPriceService.saveAgentNumprices(numPrice,commpayName);
    }

    @RequestMapping("/numprice-export")
    @Powers({PowerConsts.NUMPRICEMOUDULE_COMMON_QUEYR})
    public void numberExport(NumPrice numPrice, HttpServletRequest request, HttpServletResponse response){
//		int count = 200;
        JSONArray ja = new JSONArray();
        String isCurrentPage = request.getParameter("isCurrentPage");
        if("1".equals(isCurrentPage)) {
            numPrice.setStart(Integer.parseInt(request.getParameter("start")));
            numPrice.setLimit(15);
        }else{
            numPrice.setStart(0);
            numPrice.setLimit(0);
//            numPrice.setLimit(Integer.parseInt(request.getParameter("total")));
        }


        Map<String,String> headMap = new LinkedHashMap<String,String>();
        headMap.put("cityName", "地市名称");
        headMap.put("resource", "号码");
        headMap.put("netType", "运营商");
        headMap.put("channel", "渠道");
        headMap.put("price", "价格");
        headMap.put("lowConsume", "最低消费");
        headMap.put("numLevel", "级别");
        headMap.put("commpayName", "代理商");
        headMap.put("addDate", "添加时间");

        String title = "号码价格列表";
        Result result = numPriceService.queryNumPrice(numPrice);
        if(result.getCode()==200) {
            PageInfo<Object> pm = (PageInfo<Object>) result.getData();
            ja = JSONArray.fromObject(pm.getList());

            ExcelUtil.downloadExcelFile(title, headMap, ja, response);
        }else{
            title = "导出列表异常";
            ExcelUtil.downloadExcelFile(title, headMap, ja, response);
        }
    }

}
