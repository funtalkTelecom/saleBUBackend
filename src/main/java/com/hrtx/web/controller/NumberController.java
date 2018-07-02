package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ExcelUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.pojo.Goods;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.pojo.User;
import com.hrtx.web.service.DictService;
import com.hrtx.web.service.NumberService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/number")
public class NumberController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private DictService dictService;
	@Autowired
	private NumberService numberService;

	@RequestMapping("/number-query")
	@Powers({PowerConsts.NUMBERMOUDULE_COMMON_QUEYR})
	public ModelAndView numberQuery(Number number){
		return new ModelAndView("admin/number/number-query");
	}

	@RequestMapping("/number-list")
	@Powers({PowerConsts.NUMBERMOUDULE_COMMON_QUEYR})
	public Result listNumber(Number number, HttpServletRequest request){
        Map param = getParam(request);
		return numberService.pageNumber(number, param);
	}

	@RequestMapping("/numRule-addTags")
	@Powers({PowerConsts.NUMBERMOUDULE_COMMON_ADDTAG})
	public void addTags(Number number, HttpServletRequest request){
		returnResult(numberService.addTags(number, request));
	}

	@RequestMapping("/numRule-clearTags")
	@Powers({PowerConsts.NUMBERMOUDULE_COMMON_ADDTAG})
	public void clearTags(Number number, HttpServletRequest request){
		returnResult(numberService.clearTags(number, request));
	}

	@RequestMapping("/number-export")
	public void numberExport(Number number, HttpServletRequest request, HttpServletResponse response){
//		int count = 200;
		JSONArray ja = new JSONArray();
		String isCurrentPage = request.getParameter("isCurrentPage");
        if("1".equals(isCurrentPage)) {
            number.setStart(Integer.parseInt(request.getParameter("start")));
            number.setLimit(15);
        }else{
            number.setStart(0);
            number.setLimit(999999999);
        }

        Map param = getParam(request);

		Map numStatus = getDictMap("num_status");
		Map with4s = getDictMap("with4");

		Map<String,String> headMap = new LinkedHashMap<String,String>();
		headMap.put("cityName", "地市名称");
		headMap.put("netType", "网络类型");
		headMap.put("numResource", "号码");
		headMap.put("numType", "靓号类型");
		headMap.put("numLevel", "级别");
		headMap.put("lowConsume", "最低消费");
		headMap.put("with4", "是否带4");//字典转换中文
		headMap.put("feature", "特征");
		headMap.put("sectionNo", "号段");
		headMap.put("moreDigit", "此号码最多的数字");
		headMap.put("seller", "供应商");
		headMap.put("buyer", "买家");
		headMap.put("iccid", "iccid");
		headMap.put("status", "状态");//字典转换中文
		headMap.put("teleType", "运营商类型");

		String title = "号码列表";
		Result result = numberService.pageNumber(number, param);
		if(result.getCode()==200) {
			PageInfo<Object> pm = (PageInfo<Object>) result.getData();
			List list = pm.getList();
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					JSONObject m = JSONObject.fromObject(list.get(i));
					m.put("with4", with4s.get(String.valueOf(m.get("with4"))));
					m.put("status", numStatus.get(String.valueOf(m.get("status"))));
                    list.set(i, m);
				}
			}
			ja = JSONArray.fromObject(list);

			ExcelUtil.downloadExcelFile(title, headMap, ja, response);
		}else{
			title = "导出列表异常";
			ExcelUtil.downloadExcelFile(title, headMap, ja, response);
		}
	}

    private Map getParam(HttpServletRequest request) {

        Map param = new HashMap();

        //标签
        String tags = request.getParameter("num_tags")==null?"": request.getParameter("num_tags");
        tags = "'"+ tags.replaceAll(",", "','") +"'";
        param.put("tags", tags);

        //号码(多号码)
        String numbers = request.getParameter("numbers")==null?"": request.getParameter("numbers");
        numbers = "'"+ numbers.replaceAll(",", "','") +"'";
        param.put("numbers", numbers);

        //号段
        String numberBlock = request.getParameter("numberBlock")==null?"": request.getParameter("numberBlock");
        param.put("numberBlock", numberBlock);

        //状态
        String qstatus = request.getParameter("qstatus")==null?"": request.getParameter("qstatus");
        param.put("qstatus", qstatus);

        //地市
        String city = request.getParameter("gSaleCity")==null?"": request.getParameter("gSaleCity");
        city = "'"+ city.replaceAll(",", "','") +"'";
        param.put("city", city);
        return param;
    }

    public Map getDictMap(String group){
		List list = dictService.findDictByGroup(group);
		Map map = new HashMap<>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				Map m = (Map) list.get(i);
				map.put(m.get("keyId"), m.get("keyValue"));
			}
		}
		return map;
	}
}
