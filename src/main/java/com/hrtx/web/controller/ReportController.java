//package com.hrtx.web.controller;
//
//import com.hrtx.config.annotation.Powers;
//import com.hrtx.global.PowerConsts;
//import com.hrtx.global.SessionUtil;
//import com.hrtx.global.Utils;
//import com.hrtx.web.service.ReportService;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.math.NumberUtils;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.lang.reflect.InvocationTargetException;
//import java.util.*;
//
//@Controller
//public class ReportController extends BaseReturn {
//    @Resource private ReportService reportService;
//
//	/**
//	 * 通用报表界面
//	 */
//	@RequestMapping("search-common")
//	@Powers( {PowerConsts.REPORTMODULE_COMMON_QUERY })
//	public String searchCommon(HttpServletRequest request){
//		this.reportService.searchCommonList(request);
//		return "report/searchCommon";
//	}
//
//	/**
//	 * 通用报表(子报表)界面
//	 */
//	@RequestMapping("query-common")
//	@Powers( {PowerConsts.REPORTMODULE_COMMON_QUERY })
//	public String queryCommon(HttpServletRequest request){
//		String id = request.getParameter("id");
//		Map<String, Object> map = ReportConfig.get(id);
//		request.setAttribute("id",id);
//		request.setAttribute("map", map);
//		return "report/commonReport";
//	}
//	/**
//	 * 通用报表(子报表)数据加载
//	 * @return
//	 */
//	@RequestMapping("query-common-list")
//	@Powers( {PowerConsts.REPORTMODULE_COMMON_QUERY })
//	public String queryCommonList(HttpServletRequest request) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//		String id = request.getParameter("id");
//		Map<String, Object> report = ReportConfig.get(id);
//		com.hrtx.web.dto.Result result = this.validateCommonReport(request, report);
//		if(result.getCode() != com.hrtx.web.dto.Result.OK) return returnResult(result);
//		PageModel pageModel =this.reportService.queryCommonList(request);
//		return returnJson(pageModel);
//	}
//
//	/**
//	 * 通用报表(子报表)报表
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("common-to-excel")
//	@Powers( {PowerConsts.REPORTMODULE_COMMON_EXPORT })
//	public final String commonToExcel(HttpServletRequest request) throws Exception {
//		Map<String, Object> map = ReportConfig.get(request.getParameter("id"));
//		com.hrtx.web.dto.Result result = this.validateCommonReport(request, map);
//		if(result.getCode() != com.hrtx.web.dto.Result.OK){
//			request.setAttribute("message", result.getData());
//			return "error";
//		}
//		String fileName = (String)map.get("name")+new Date().getTime()+".xls";
//		Object[] str = (Object[]) map.get("title");
//		List<Object> list =reportService.commonToExcel(request, map);
//		list.add(0,str);
//		List<List<?>> list1 = new ArrayList<>();
//		list1.add(list);
//		Utils.export(fileName, list1, null, null, new String[]{(String)map.get("name")});
//		return null;
//	}
//
//	/**
//	 * 通用报表(子报表)报表
//	 * @return
//	 * @throws Exception
//	 */
//	@RequestMapping("common-to-excel-thread")
//	@Powers( {PowerConsts.REPORTMODULE_COMMON_EXPORT })
//	public String commonToExcelThread(HttpServletRequest request) throws Exception {
//		Map<String, Object> map = ReportConfig.get(request.getParameter("id"));
//		com.hrtx.web.dto.Result result = this.validateCommonReport(request, map);
//		if(result.getCode() != com.hrtx.web.dto.Result.OK){
//			return returnResult(result);
//		}
//        String fileName = (String)map.get("name")+new Date().getTime()+".xls";
//        Object[] str = (Object[]) map.get("title");
//        List<Object> list =reportService.commonToExcel(request, map);
//        list.add(0,str);
//        List<List<?>> list1 = new ArrayList<>();
//        list1.add(list);
//        Map<String, Object> pmap = new HashMap<>();
//        pmap.put("report_name",map.get("name"));
//        pmap.put("params",this.getParamMap(request));
//        pmap.put("fileName",fileName);
//        pmap.put("rootPath",request.getServletContext().getRealPath("/"));
//        pmap.put("list",list1);
//        return returnResult(reportService.commonToExcelThread(pmap));
//	}
//
//
//    private com.hrtx.web.dto.Result validateCommonReport(HttpServletRequest request, Map<String, Object> report) {
//		if(!SessionUtil.hasPower(NumberUtils.toInt(request.getParameter("id")))) return new com.hrtx.web.dto.Result(com.hrtx.web.dto.Result.ERROR, "没有权限");
//		List<Map<String, Object>> conditions = (List<Map<String, Object>>) report.get("conditions");
//		if(conditions.size()>0){
//			for(Map<String, Object> m : conditions){
//				String required = (String) (m.get("is_required") == null ? "0" : m.get("is_required"));
//				String name = (String) m.get("name");
//				String par1 = (String) (m.get("par") == null ? (m.get("par1") == null ? "0" : m.get("par1")) : m.get("par"));
//				String par2 = (String) (m.get("par") == null ? (m.get("par2") == null ? "0" : m.get("par2")) : m.get("par"));
//				String v1 = request.getParameter(par1), v2 = request.getParameter(par2);
//				if(required.equals("1")){
//					if(StringUtils.isBlank(v1) || StringUtils.isBlank(v2)){
//						log.error("参数["+name+"]必填，传进来的值["+v1+"---"+v2+"]不能为空");
//						return new com.hrtx.web.dto.Result(com.hrtx.web.dto.Result.ERROR, "参数["+name+"]必填");
//					}
//				}
//			}
//		}
//		return new com.hrtx.web.dto.Result(com.hrtx.web.dto.Result.OK, "");
//	}
//
//}
