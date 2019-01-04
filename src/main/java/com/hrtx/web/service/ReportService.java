//package com.hrtx.web.service;
//
//
//import com.github.pagehelper.Page;
//import com.github.pagehelper.PageHelper;
//import com.github.pagehelper.PageInfo;
//import com.hrtx.dto.Result;
//import com.hrtx.global.*;
//import com.hrtx.web.dto.Menu;
//import com.hrtx.web.dto.Result;
//import com.hrtx.web.pojo.Corporation;
//import com.hrtx.web.pojo.DownloadFile;
//import com.hrtx.web.pojo.User;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang.ObjectUtils;
//import org.apache.commons.lang.StringUtils;
//import org.apache.commons.lang.math.NumberUtils;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import javax.servlet.http.HttpServletRequest;
//import java.io.File;
//import java.lang.reflect.InvocationTargetException;
//import java.util.*;
//
//@Service
//public  class ReportService {
//
//    public Result queryCommonList(HttpServletRequest request) {
//        List<Object> params = new ArrayList<Object>();
//        Map<String, Object> report = ReportConfig.get(request.getParameter("id"));
//        StringBuffer sql = new StringBuffer((String) report.get("show_sql"));
//        String orderby = (String) report.get("orderby");
//        String groupby = (String) report.get("groupby");
//        List<Map<String, Object>> conditions = (List<Map<String, Object>>) report.get("conditions");
//        Map<String, Object> permission = (Map<String, Object>) report.get("permissions");
//        if(conditions.size()>0){
//            for(Map<String, Object> m : conditions){
//                String type = (String) m.get("type");
//                if("input".equals(type) || "select".equals(type)) {
//                    String code = (String) m.get("code");
//                    String key = (String) m.get("par");
//                    String value = request.getParameter(key);
//                    String scope = (String) m.get("scope");
//                    String is_all = (String) m.get("is_all");
//                    if(StringUtils.isBlank(scope)){
//                        if(StringUtils.isNotBlank(value)){
//                            if("1".equals(is_all)){
//                                sql.append(" and "+code+" = ?");
//                                params.add(value.trim());
//                            }else{
//                                sql.append(" and "+code+" like ?");
//                                params.add("%"+value.trim()+"%");
//                            }
//                        }
//                    }else{
//                        String t1 = "";
//                        if(StringUtils.isNotBlank(value)){
//                            if("1".equals(is_all)){
//                                t1= " and "+code+" = '"+value.trim()+"'";
//                            }else{
//                                t1= " and "+code+" like '%"+value.trim()+"%'";
//                            }
//                        }
//                        sql = new StringBuffer(sql.toString().replaceFirst("@", t1));
//                    }
//                }
//                if("date".equals(type)){
//                    String code = (String) m.get("code");
//                    String key1 = (String) m.get("par1");
//                    String value1 = request.getParameter(key1);
//                    String key2 = (String) m.get("par2");
//                    String value2 = request.getParameter(key2);
//                    String scope = (String) m.get("scope");
//                    if(StringUtils.isBlank(scope)){
//                        if(StringUtils.isNotBlank(value1)){
//                            sql.append(" and to_date("+code+",'yyyy-mm-dd hh24:mi:ss')>= to_date(?,'yyyy-mm-dd')");
//                            params.add(value1);
//                        }
//                        if(StringUtils.isNotBlank(value2)){
//                            sql.append(" and to_date("+code+",'yyyy-mm-dd hh24:mi:ss')< to_date(?,'yyyy-mm-dd')+1");
//                            params.add(value2);
//                        }
//                    }else{//为中间时间查询条件
//                        String[] codes = code.split(";");
//                        String[] scopes = scope.split(";");
//                        for (int i = 0; i < codes.length; i++) {
//                            String t1 = "";
//                            String c = codes[i], s = scopes[i];
//                            if(StringUtils.isNotBlank(value1) && ("1".equals(s) || "3".equals(s))){
//                                t1 += " and to_date("+c+",'yyyy-mm-dd hh24:mi:ss')>= to_date('"+value1+"','yyyy-mm-dd')";
//                            }
//                            if(StringUtils.isNotBlank(value2) && ("2".equals(s) || "3".equals(s))){
//                                t1 += " and to_date("+c+",'yyyy-mm-dd hh24:mi:ss')< to_date('"+value2+"','yyyy-mm-dd')+1";
//                            }
//                            if(StringUtils.isNotBlank(value1) && "4".equals(s)){
//                                t1 += " and trunc(to_date("+c+",'yyyy-mm-dd hh24:mi:ss'))= trunc(to_date('"+value1+"','yyyy-mm-dd'))";
//                            }
//                            if(StringUtils.isNotBlank(value2) && "5".equals(s)){
//                                t1 += " and trunc(to_date("+c+",'yyyy-mm-dd hh24:mi:ss'))= trunc(to_date('"+value2+"','yyyy-mm-dd'))";
//                            }
//                            sql = new StringBuffer(sql.toString().replaceFirst("@", t1));
//                        }
//                    }
//                }
//            }
//        }
//        if(!SessionUtil.hasPower(PowerConsts.SYSTEM_ADMIN_ALL)) {
//            if(permission != null) {
//                String filter_corp_key = ObjectUtils.toString(permission.get("filter_corp_key"));
//                String[] filter_corp_keys = filter_corp_key.split(",");
//                if(filter_corp_keys.length > 0) {
//                    Class<Corporation> aClass = Corporation.class;
//                    for (int i = 0; i < filter_corp_keys.length; i++) {
//                        String[] keyArray = filter_corp_keys[i].split("\\|");
//                        if(keyArray.length == 2) {
//                            sql.append(" and "+keyArray[0]+" = ?");
//                            params.add(aClass.getMethod(keyArray[1]).invoke(SessionUtil.getCorporation()));
//                        }
//                    }
//
//                }
//                String filter_user_key = ObjectUtils.toString(permission.get("filter_user_key"));
//                String[] filter_user_keys = filter_user_key.split(",");
//                if(filter_user_keys.length > 0) {
//                    Class<User> aClass = User.class;
//                    for (int i = 0; i < filter_user_keys.length; i++) {
//                        String[] keyArray = filter_user_keys[i].split("\\|");
//                        if(keyArray.length == 2) {
//                            sql.append(" and "+keyArray[0]+" = ?");
//                            params.add(aClass.getMethod(keyArray[1]).invoke(SessionUtil.getUser()));
//                        }
//                    }
//
//                }
//            }
//        }
//        if(StringUtils.isNotBlank(groupby)){
//            sql.append(" "+groupby);
//        }
//        if(StringUtils.isNotBlank(orderby)){
//            sql.append(" "+orderby);
//        }
//        Object logsql = report.get("log");
//        if(logsql == null) {
//            log.info("报表执行sql："+sql.toString());
//            report.put("log", "已打印");
//        }
//        int start = NumberUtils.toInt(request.getParameter("start"));
//        int limit = NumberUtils.toInt(request.getParameter("limit"), 15);
//        return this.pageBySQl(sql.toString(), start, limit, params.toArray());
//
//        PageHelper.startPage(dict.startToPageNum(),dict.getLimit());
//        Page<Object> ob=this.dictMapper.queryPageList(dict);
//        PageInfo<Object> pm = new PageInfo<Object>(ob);
//        return new Result(Result.OK, pm);
//    }
//
//    public void searchCommonList(HttpServletRequest request) {
//        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//        List<Menu> menus = (List<Menu>) SessionUtil.getSession().getAttribute("commonReports");
//        for (Menu value : menus) {
//            if (value.getPid() == PowerConsts.REPORTMODULE.getId()) {
//                if (value.getId() == PowerConsts.REPORTMODULE_COMMON_QUERY.getId() || value.getId() == PowerConsts.REPORTMODULE_COMMON_EXPORT.getId())
//                    continue;
//                Map<String, Object> map = new HashMap<String, Object>();
//                map.put("name", value.getName());
//                map.put("url", value.getUrl());
//                list.add(map);
//            }
//        }
//        request.setAttribute("list", list);
//    }
//
//    public List<Object> commonToExcel(HttpServletRequest request, Map<String, Object> map) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        List<Object> list =this.reportDao.commonToExcel(request, map);
//        return list;
//    }
//
//    public Result commonToExcelThread(final Map<String, Object> map) {
//        String reportName = String.valueOf(map.get("report_name"));
//        String params = String.valueOf(map.get("params"));
//        String fileName = String.valueOf(map.get("fileName"));
//        DownloadFile downloadFile = new DownloadFile(reportName, SessionUtil.getUserId(), new Date(), null, params, fileName, "", 0);
//        reportDao.add(downloadFile);
//        map.put("downloadFile",downloadFile);
//        new CommonThread("线程方式数据导出",map) {
//            @Override
//            public void run() {
//                try {
//                    Map<String, Object> map1 = this.getMap();
//                    String rootPath = (String) map1.get("rootPath");
//                    String filePath = rootPath+File.separator+"upload/report/";
//                    String fileName = String.valueOf(map1.get("fileName"));
//                    List<List<?>> list = (List<List<?>>) map1.get("list");
//                    DownloadFile downloadFile1 = (DownloadFile) map1.get("downloadFile");
//                    Utils.createExportFile(filePath+fileName, list, null, null, new String[]{downloadFile1.getReportName()});
//                    downloadFile1.setCompleteDate(new Date());
//                    downloadFile1.setFileName(fileName);
//                    downloadFile1.setFilePath(filePath);
//                    downloadFile1.setStatus(1);
//                    reportDao.update(downloadFile1);
//                } catch (Exception e) {
//                    log.error("", e);
//                }
//            }
//        }.start();
//        try{
//            int save_file_date = NumberUtils.toInt(SystemParam.get("save_file_date"), 7);
//            List<Map> files = reportDao.queryExpireFile(save_file_date);
//            for (Map file:files) {
//                int id = NumberUtils.toInt(String.valueOf(file.get("ID")));
//                try{
//                    DownloadFile downloadFile1 = (DownloadFile) reportDao.get(DownloadFile.class, id);
//                    File dfile = new File(downloadFile1.getFilePath()+downloadFile1.getFileName());
//                    if(dfile.exists()) FileUtils.forceDelete(dfile);
//                    downloadFile1.setStatus(2);
//                    reportDao.update(downloadFile1);
//                }catch (Exception e){
//                    log.error("", e);
//                    continue;
//                }
//            }
//        }catch (Exception e){
//            log.info("删除过期文件失败", e);
//        }
//        return new Result(Result.OK,"您的导出申请已提交，请稍后到菜单<a href='query-common?id=1000'>[报表管理-导出文件]”</a>下载");
//    }
//}
//
