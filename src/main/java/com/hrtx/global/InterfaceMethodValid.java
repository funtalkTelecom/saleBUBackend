package com.hrtx.global;

import com.hrtx.dto.InterfaceResult;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;

import java.util.Map;

public class InterfaceMethodValid {
    public InterfaceResult valid0102(Map<String, Object> mapParam) {
        Element platrequest = (Element) mapParam.get("data");
        if(platrequest == null) return new InterfaceResult("F0001", "未找到主体参数", null);
        String req_no = platrequest.elementText("req_no");
        if(StringUtils.isBlank(req_no)) return new InterfaceResult("F0001", "订单号未填写", null);
        return new InterfaceResult("00000", "success", null);
    }

    public InterfaceResult valid0103(Map<String, Object> mapParam) {
        Element platrequest = (Element) mapParam.get("data");
        if(platrequest == null) return new InterfaceResult("F0001", "未找到主体参数", null);
        String req_no = platrequest.elementText("req_no");
        if(StringUtils.isBlank(req_no)) return new InterfaceResult("F0001", "订单号未填写", null);
        return new InterfaceResult("00000", "success", null);
    }

    public InterfaceResult valid0101(Map<String, Object> mapParam) {
        Element platrequest = (Element) mapParam.get("data");
        if(platrequest == null) return new InterfaceResult("F0001", "未找到主体参数", null);
        String req_no = platrequest.elementText("req_no");
        if(StringUtils.isBlank(req_no)) return new InterfaceResult("F0001", "订单号未填写", null);
        int page_num = NumberUtils.toInt(platrequest.elementText("page_num"));
        if(page_num<=0) return new InterfaceResult("F0001", "page_num未填写", null);
        int operator = NumberUtils.toInt(platrequest.elementText("operator"));
        if(operator > 0 && !ArrayUtils.contains(new int[]{1,2,3}, operator))  return new InterfaceResult("F0001", "operator非法", null);
        String pattern = ObjectUtils.toString(platrequest.elementText("pattern"));
        if(StringUtils.isNotBlank(pattern) && !pattern.matches("^[0-9\\?]{11}$"))  return new InterfaceResult("F0001", "pattern非法", null);
        return new InterfaceResult("00000", "success", null);
    }

}
