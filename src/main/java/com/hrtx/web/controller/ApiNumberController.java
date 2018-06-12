package com.hrtx.web.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hrtx.config.annotation.Powers;
import com.hrtx.dto.Result;
import com.hrtx.global.ApiSessionUtil;
import com.hrtx.global.PowerConsts;
import com.hrtx.web.mapper.NumberMapper;
import com.hrtx.web.pojo.Number;
import com.hrtx.web.pojo.User;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiNumberController extends BaseReturn{

	public final Logger log = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private NumberMapper numberMapper;
	@Autowired
	private ApiSessionUtil apiSessionUtil;

	@GetMapping("/number")
    @Powers(PowerConsts.NOLOGINPOWER)
	@ResponseBody
	public Result numberList(Number number, HttpServletRequest request){
		PageInfo<Object> pm = null;
		try {
			number.setPageNum(request.getParameter("pageNum")==null?1: Integer.parseInt(request.getParameter("pageNum")));
			number.setLimit(request.getParameter("limit")==null?15: Integer.parseInt(request.getParameter("limit")));
			String tags = request.getParameter("tags")==null?"": request.getParameter("tags");
			tags = "'"+ tags.replaceAll(",", "','") +"'";

			PageHelper.startPage(number.getPageNum(),number.getLimit());
			Page<Object> ob=this.numberMapper.queryPageListApi(tags);
			if(ob!=null && ob.size()>0){
				//处理号码,生成号码块字段(numBlock)
				for (int i = 0; i < ob.size(); i++) {
					Map obj= (Map) ob.get(i);
					StringBuffer num = new StringBuffer((String) obj.get("numResource"));
					num.insert(3, ",");
					num.insert(8, ",");
					obj.put("numBlock", num.toString().split(","));
				}
			}
			pm = new PageInfo<Object>(ob);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			pm = new PageInfo<Object>(null);
			return new Result(Result.ERROR, pm);
		}

		return new Result(Result.OK, pm);
	}
}
