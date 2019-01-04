package com.hrtx.global;

import net.sf.json.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class ReportConfig {
	private final static Logger log = LoggerFactory.getLogger(ReportConfig.class);

	private final static Map<String, Map<String, Object>> reportsConfig = new HashMap<String, Map<String, Object>>();

	/**
	 * 加载配置文件
	 * @return
	 * @throws URISyntaxException
	 * @throws DocumentException
	 */
	public final static void load() throws Exception {
		reportsConfig.clear();
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new File(ReportConfig.class.getResource("/report_config.xml").toURI()));
		Element root = doc.getRootElement();
		List<Element> reports = root.elements("report");
		List<List<Object>> reportPermissions = new ArrayList<List<Object>>();
		for (Element report : reports) {
			String key = report.elementText("id").trim();
			int export_type = NumberUtils.toInt(report.elementText("export_type"), 0);
			String not_auto_load = report.elementText("not_auto_load") == null ? "0":report.elementText("not_auto_load").trim();
			String sql = report.elementText("sql");
			String orderby = ObjectUtils.toString(report.elementText("orderby"));
			String groupby = ObjectUtils.toString(report.elementText("groupby"));
			String export = report.elementText("export") == null ? "":report.elementText("export");
			Map<String, Object> map = (Map<String, Object>) reportsConfig.get(key);
			if(map == null){
				map = new HashMap<String, Object>();
				reportsConfig.put(key, map);
			}
			map.put("id", key); //编号
			map.put("export_type", export_type); //到处类型
			map.put("not_auto_load", not_auto_load); //编号
			String name = report.elementText("name")==null ? "未命名":report.elementText("name").trim();
			map.put("name", name);      //报表名字
			map.put("remark", StringUtils.trimToEmpty(report.elementText("remark")));      //报表备注
			StringBuffer show_sql = new StringBuffer("select ");
			StringBuffer export_sql = new StringBuffer("select ");
			List<Element> fields = report.elements("field");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> cm = new ArrayList<Map<String, Object>>();
			List<String> title = new ArrayList<String>();
			List<Integer> totalCol = new ArrayList<Integer>();
			Map<String, Object> paramObj = new HashMap<String, Object>();
			int i = 0, k = 0;
			for (Element field : fields) {
				String code = field.elementText("code");
				String param = field.elementText("name") == null ? "未命名":field.elementText("name").trim();
				String alias = field.elementText("alias") == null ? "ALIAS_RC_"+i:field.elementText("alias").trim().toUpperCase();
				String type = field.elementText("type") == null ? "input":field.elementText("type").trim();
				String is_show = field.elementText("is_show") == null ? "1":field.elementText("is_show").trim();
				String is_export = field.elementText("is_export") == null ? "1":field.elementText("is_export").trim();
				String is_condition = field.elementText("is_condition") == null ? "0":field.elementText("is_condition").trim();
				i++;
				if("1".equals(is_show)) {
					show_sql.append(" "+code+" "+alias+",");
					Map<String, Object> c = new HashMap<String, Object>();
					c.put("header", param);
					c.put("dataIndex", alias);
					cm.add(c);
				}
				if("1".equals(is_export)) {
					export_sql.append(" "+code+" "+alias+",");
					title.add(param);
					int is_total = field.elementText("is_total") == null ? 0 : NumberUtils.toInt(field.elementText("is_total").trim());
					if(is_total == 1){
						totalCol.add(k);
					}
					k++;
				}
				if("1".equals(is_condition)) {
					Map<String, Object> c = new HashMap<String, Object>();
					c.put("code", code);
					c.put("name", param);
					c.put("type", type);
					c.put("scope", field.elementText("scope") == null ? "":field.elementText("scope").trim());
					String is_required = field.elementText("is_required") == null ? "0":field.elementText("is_required").trim();
					//存在必填条件时    强制转为默认不加载数据
					if(is_required.equals("1")) map.put("not_auto_load", "1");
					c.put("is_required",is_required);
					if("date".equals(type)){
						c.put("par1", alias+"_start");
						c.put("par2", alias+"_end");
					}else if("select".equals(type)){
						String is_all = field.elementText("is_all") == null ? "1":field.elementText("is_all").trim();
						c.put("par", alias);
						c.put("is_all",is_all);
						String [] queryVal = StringUtils.isBlank(ObjectUtils.toString(field.elementText("query_val"))) ? null:field.elementText("query_val").trim().split("#");
						List<Map<String, String>> query_vals = new ArrayList<Map<String,String>>();
						if(queryVal != null){
							for (int j = 0; j < queryVal.length; j++) {
								Map<String, String> t = new HashMap<String, String>();
								t.put("val", queryVal[j]);
								query_vals.add(t);
							}
						}
						c.put("query_vals",query_vals);
					}else {
						String is_all = field.elementText("is_all") == null ? "0":field.elementText("is_all").trim();
						c.put("par", alias);
						c.put("is_all",is_all);
					}
					list.add(c);
				}
			}
			Map<String, Object> p = new HashMap<String, Object>();
			Element permission = report.element("permission");
			if(permission != null) {
				String filter_corp_key = permission.elementText("filter_corp_key");
				String filter_user_key = permission.elementText("filter_user_key");
				p.put("filter_corp_key",filter_corp_key);
				p.put("filter_user_key",filter_user_key);
			}
			for(Map<String, Object> c : cm){
				c.put("width", 100/cm.size()+"%");
			}
			map.put("show_sql", show_sql.substring(0, show_sql.length()-1)+" "+sql); //显示sql
			map.put("export_sql", export_sql.substring(0, export_sql.length()-1)+" "+sql); //导出sql
			map.put("orderby", orderby); //导出sql
			map.put("groupby", groupby); //导出sql
			map.put("conditions", list);  //查询条件
			map.put("permissions", p);  //权限集合
			map.put("export", export);  //可导出的角色集合
			map.put("cm", cm);  //表格显示模型
			map.put("title", title.toArray());  //导出标题
			map.put("totalCol", totalCol);  //需要合并的列

			List<Object> param = new ArrayList<Object>();
			param.add(key); param.add(name); param.add("query-common?id="+key);
			reportPermissions.add(param);
		}
//		PermissionAllocateService permissionAllocateService = (PermissionAllocateService) ContextUtils.getContext().getBean("permissionAllocateServiceImpl");
//		permissionAllocateService.initCommonReportPermission(reportPermissions);
		log.info("报表配置重载成功【"+getJSON()+"】");
	}

	/**
	 * 获取参数值
	 * @param key
	 * @return
	 */
	public final static Map<String, Object> get(String key){
		Map<String, Object> map = reportsConfig.get(key);
		return map;
	}

	/**
	 * 获取JSON的配置参数
	 * @return
	 */
	public final static String getJSON(){
		return JSONObject.fromObject(reportsConfig).toString();
	}

	/*

	 */
	public static void main(String[] args) {
		String[] sqls = new String[]{
				"select t.id, t.report_name, to_char(t.add_date,'yyyy-mm-dd hh24:mi:ss'), to_char(t.complete_date,'yyyy-mm-dd hh24:mi:ss'), decode(t.status,1,'已生成','未生成'), FROMHR "
		};
		for (int j = 0; j < sqls.length; j++) {
			String sql = sqls[j];
			String upperSql = sql.toUpperCase();
			Object[] fields = escape(sql.substring(upperSql.indexOf("SELECT")+6, upperSql.indexOf("FROMHR"))).split(",");
			String from = sql.substring(upperSql.indexOf("FROMHR")+7);
			System.out.println("<report>");
			System.out.println("	<id>"+new SimpleDateFormat("yyyyMMdd").format(new Date())+StringUtils.leftPad((j+1)+"", 2, "0")+"</id>");
			System.out.println("	<name>报表名称</name>");
			for (int i = 0, len = fields.length; i < len; i++) {
				String field = (String) fields[i];
				String[] fieldNames = field.trim().split(" ");
				String alias = fieldNames[fieldNames.length-1];//别名
				String fieldnode = "	<field>"+
						"<code>"+fieldNames[0]+"</code>"+
						"<name>"+alias+"</name> "+
						"</field>";
				System.out.println(fieldnode);
			}
			System.out.println("	<sql>");
			System.out.println("	FROM " +from);
			System.out.println("	</sql>");
			System.out.println("	<groupby></groupby>");
			System.out.println("	<orderby></orderby>");
			System.out.println("</report>");
		}
	}

	public static String escape(String str){
		if(str==null || str.length() == 0)return "";
		Stack<Character> stack = new Stack<Character>();
		int sz = str.length();
		char[] chs = new char[sz];
		int count = 0;
		for (int i = 0; i < sz; i++) {
			char c = str.charAt(i);
			if (c == '(') {
				stack.push(c);
			}else if(c==',' && !stack.isEmpty()){
				c = ' ';
			}else if(c==')' && !stack.isEmpty()){
				stack.pop();
			}
			chs[count++]=c;
		}
		return new String(chs, 0, count);
	}
}
