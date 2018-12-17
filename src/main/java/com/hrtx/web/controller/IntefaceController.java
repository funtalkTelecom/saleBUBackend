package com.hrtx.web.controller;

import com.github.pagehelper.PageInfo;
import com.hrtx.config.advice.ServiceException;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.InterfaceResult;
import com.hrtx.global.CryptTool;
import com.hrtx.global.PowerConsts;
import com.hrtx.global.SessionUtil;
import com.hrtx.global.Utils;
import com.hrtx.web.pojo.InterfaceMerchant;
import com.hrtx.web.pojo.Num;
import com.hrtx.web.pojo.NumPrice;
import com.hrtx.web.pojo.ThirdRequest;
import com.hrtx.web.service.CityService;
import com.hrtx.web.service.DictService;
import com.hrtx.web.service.NumService;
import com.hrtx.web.service.ThirdRequestService;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/interface")
public class IntefaceController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Resource private ThirdRequestService thirdRequestService;
	@Resource private CityService cityService;
	@Resource private DictService dictService;
	@Resource private NumService numService;
	private static String res ="<?xml version='1.0'?><response><merid>%s</merid><appcode>%s</appcode><serial>%s</serial><sign>%s</sign><rescode>%s</rescode><resdesc>%s</resdesc><platresponse>%s</platresponse></response>";

	//获取返回报文
	public String getResultXml(String merid, String appcode, String serial, String respcode, String respdesc, String format, String key) {
		String resultxml = String.format(res, merid, appcode, serial, "%s", respcode, respdesc, format);
		Map<String, Object> map = (Map<String, Object>) parseXmlParam(resultxml).getMap();
		map.remove("data");
		map.remove("sign");
		String sign = CryptTool.getSign(map, key);
		return String.format(resultxml, sign);
	}

	//将入参报文解析成map
    public InterfaceResult parseXmlParam(String xml) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			Pattern p = Pattern.compile("<platrequest>[\\s\\S]*</platrequest>|<platrequest/>|<platresponse>[\\s\\S]*</platresponse>|<platresponse/>");
			Element root = Utils.prase_xml(xml);
			List<Element> list = root.elements();
			for (Element element : list) {
			    String name = element.getName();
			    if("platresponse".equals(name) || "platrequest".equals(name)) {
                    map.put("data", element);
					String a = "";
					Matcher m = p.matcher(xml);
					if(m.find())  a = m.group();
                    map.put(name, a.replaceAll("<platresponse>","").replaceAll("</platresponse>","")
                            .replaceAll("<platrequest>","").replaceAll("</platrequest>","")
                            .replaceAll("<platrequest/>","").replaceAll("<platresponse/>","")
                            .replaceAll("\\s*",""));
                }else {
                    map.put(element.getName(), element.getTextTrim());
                }
			}
		} catch (Exception e) {
            log.error("接口接收参数格式错误2", e);
			return new InterfaceResult("C0002", "接口接收参数格式错误");
		}
		return new InterfaceResult("00000", map);
	}
	@RequestMapping("/dispatch-command")
	@Powers({PowerConsts.NOLOGINPOWER})
	public String dispatchCommand(HttpServletRequest request) {
//		String xml = this.getParameter("xml");
		String xml = this.getParamBody(request);
		String resultxml = "", merid = "", appCode="", serial = "", key = "";
		if(StringUtils.isBlank(xml)) {
			resultxml = this.getResultXml(merid, appCode, serial, "C0001", "参数为空", "", key);
			return renderXml(resultxml);
		}
		try {
			log.info("接口接收到调入参数["+xml+"]");
			InterfaceResult result = this.parseXmlParam(xml);
			if(!"00000".equals(result.getCode())) {
				resultxml = this.getResultXml(merid, appCode, serial, result.getCode(), result.getDesc(), "", key);
				return renderXml(resultxml);
			}
			Map<String, Object> mapParam = (Map<String, Object>) result.getMap();
			serial = ObjectUtils.toString(mapParam.get("serial"));
			merid = ObjectUtils.toString(mapParam.get("merid"));
			appCode = ObjectUtils.toString(mapParam.get("appcode"));
			String sign = ObjectUtils.toString(mapParam.get("sign"));
			if(StringUtils.isBlank(serial) || StringUtils.isBlank(merid) || StringUtils.isBlank(appCode) || StringUtils.isBlank(sign)){
				resultxml = this.getResultXml(merid, appCode, serial, "C0003", "参数不完整", "", key);
				return renderXml(resultxml);
			}

			ThirdRequest inParam  = thirdRequestService.findThirdRequest(merid+serial);
			if(inParam != null) {
				resultxml = this.getResultXml(merid, appCode, serial, "C0008", "请求重复", "", key);
				return renderXml(resultxml);
			}
			InterfaceMerchant merchant = thirdRequestService.findMerchant(merid);
			if(merchant == null){
				resultxml = this.getResultXml(merid, appCode, serial, "C0004", "非法商户", "", key);
				return renderXml(resultxml);
			}
            String clientIp = SessionUtil.getUserIp();
            boolean isMatch = false;
            log.info("-------------请求ip:"+clientIp);
			key = merchant.getSecretKey();
			Object data = mapParam.get("data");
			String nativeSign = CryptTool.getSign(mapParam, key);
			if(StringUtils.isBlank(sign) || !sign.equals(nativeSign)){
				resultxml = this.getResultXml(merid, appCode, serial, "C0005", "签名出错", "", key);
				return renderXml(resultxml);
			}
            String[] ips = ObjectUtils.toString(merchant.getIp()).split(",");
            for (String ip:ips) {
                if(clientIp.matches(ip)) {
                    isMatch = true;
                    break;
                };
            }
			if(!isMatch){
				resultxml = this.getResultXml(merid, appCode, serial, "C0006", "无效IP", "", key);
				return renderXml(resultxml);
			}
			String[] appcodes = new String[]{"0101","0102","0103"};
			if(ArrayUtils.contains(appcodes, appCode)){
                mapParam.put("data",data);
                Class aClass = Class.forName("com.hrtx.global.InterfaceMethodValid");
                Method method = aClass.getMethod("valid"+appCode,new Class[]{Map.class});
                if(method != null) {
                    result=(InterfaceResult)method.invoke(aClass.newInstance(), new Object[]{mapParam});
                    if(!"00000".equals(result.getCode())) {
                        resultxml = this.getResultXml(merid, appCode, serial, result.getCode(), result.getDesc(), "", key);
                        return renderXml(resultxml);
                    }
                }
				Method me= this.getClass().getMethod("exeCommond"+appCode,new Class[]{Map.class});//使用反射执行目标方法
				result=(InterfaceResult) me.invoke(this, new Object[]{mapParam});//执行
				resultxml = this.getResultXml(merid, appCode, serial, result.getCode(), result.getDesc(), result.getPlatData(), key);
			}else{
				resultxml = this.getResultXml(merid, appCode, serial, "C0007", "无效的业务类型", "", key);
			}
			return renderXml(resultxml);
		} catch(InvocationTargetException e){
            try{
                throw e.getTargetException();
            }catch(ServiceException my){
                log.error("", my);
                resultxml = this.getResultXml(merid, appCode, serial,  "F0001", my.getMessage(), "", key);
                return renderXml(resultxml);
            }catch(Throwable my){
                log.error("", my);
                resultxml = this.getResultXml(merid, appCode, serial, "99999", "未知异常", "", key);
                return renderXml(resultxml);
            }
        } catch(ServiceException my){
            log.error("", my);
            resultxml = this.getResultXml(merid, appCode, serial,  "F0001", my.getMessage(), "", key);
            return renderXml(resultxml);
        } catch (Exception e){
			log.error("未知异常", e);
			resultxml = this.getResultXml(merid, appCode, serial, "99999", "未知异常", "", key);
			return renderXml(resultxml);
		} finally {
			log.info("接口返回结果["+resultxml+"]");
			thirdRequestService.addThirdRequest(merid+serial, xml, resultxml, "xml", appCode, "interface");
		}
	}


	public InterfaceResult exeCommond0102(Map<String, Object> mapParam){
		Element data = (Element) mapParam.get("data");
		StringBuffer sb = new StringBuffer();
		List<Map> citys = cityService.findCityByGrade(2);
		for (Map city:citys) {
			sb.append("<city><province_code>"+String.valueOf(city.get("pid"))+"</province_code><province>"+String.valueOf(city.get("pname"))+"</province><city_code>"+String.valueOf(city.get("id"))+"</city_code><city>"+String.valueOf(city.get("name"))+"</city></city>");
		}
		return new InterfaceResult("00000", "success", String.format("<req_no>"+data.elementText("req_no")+"</req_no><citys>%s</citys>", sb.toString()));
	}

	public InterfaceResult exeCommond0103(Map<String, Object> mapParam){
	    Element data = (Element) mapParam.get("data");
		StringBuffer sb = new StringBuffer();
        List<Map> feathers = dictService.findDictByGroup("FEATHER_TYPE");
        for (Map feather:feathers) {
			sb.append("<feather>"+String.valueOf(feather.get("keyValue"))+"</feather>");
		}
		return new InterfaceResult("00000", "success", String.format("<req_no>"+data.elementText("req_no")+"</req_no><feathers>%s</feathers>", sb.toString()));
	}

	public InterfaceResult exeCommond0101(Map<String, Object> mapParam){
	    Element data = (Element) mapParam.get("data");
		StringBuffer sb = new StringBuffer();
		String merid = ObjectUtils.toString(mapParam.get("merid"));
		InterfaceMerchant merchant = thirdRequestService.findMerchant(merid);
		int operator = NumberUtils.toInt(String.valueOf(data.elementText("operator")));
		int province_code = NumberUtils.toInt(String.valueOf(data.elementText("province_code")));
		int city_code = NumberUtils.toInt(String.valueOf(data.elementText("city_code")));
		String feature = ObjectUtils.toString(data.elementText("feature"));
		String pattern = ObjectUtils.toString(data.elementText("pattern"));
		int page_num = NumberUtils.toInt(String.valueOf(data.elementText("page_num")));
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
//			numPrice.setProvinceCode(1);
//			numPrice.setCityCode(44);
			numPrice.setSort(1);
		}

		long a = System.currentTimeMillis();
		List list = numService.queryNumPriceList(numPrice);
		log.info("----------------------------------------------------------耗时："+(System.currentTimeMillis()-a));
        for (Object object : list) {
			Map map = (Map) object;
			sb.append("<number><operator>"+map.get("net_type")+"</operator><province>"+map.get("province_name")+"</province><city>"+map.get("city_name")+"</city>" +
					"<feather>"+map.get("feature")+"</feather><mobile_number>"+map.get("resource")+"</mobile_number><price>"+map.get("price")+"</price></number>");
		}
		return new InterfaceResult("00000", "success", String.format("<req_no>"+data.elementText("req_no")+"</req_no><total>0</total><numbers>%s</numbers>", sb.toString()));

	}

}
